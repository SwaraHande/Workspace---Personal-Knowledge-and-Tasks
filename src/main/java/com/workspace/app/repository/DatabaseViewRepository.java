package com.workspace.app.repository;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.workspace.app.config.DatabaseConfig;
import com.workspace.app.model.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.util.*;

public class DatabaseViewRepository {
    private static final Logger logger = LoggerFactory.getLogger(DatabaseViewRepository.class);
    private static final ObjectMapper objectMapper = new ObjectMapper();

    public Optional<DatabaseDefinition> findByPageId(String pageId) {
        String sql = "SELECT id, workspace_id, page_id, title, description, created_at, updated_at " +
                "FROM database_definitions WHERE page_id = ?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, pageId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    DatabaseDefinition db = mapDatabase(rs);
                    db.setProperties(findProperties(db.getId()));
                    db.setViews(findViews(db.getId()));
                    return Optional.of(db);
                }
            }
        } catch (SQLException e) {
            logger.error("Error finding database definition by page id: {}", e.getMessage());
        }
        return Optional.empty();
    }

    public List<DatabaseProperty> findProperties(String databaseId) {
        List<DatabaseProperty> list = new ArrayList<>();
        String sql = "SELECT id, database_id, name, property_type, options, position, created_at " +
                "FROM database_properties WHERE database_id = ? ORDER BY position ASC";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, databaseId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    DatabaseProperty p = new DatabaseProperty();
                    p.setId(rs.getString("id"));
                    p.setDatabaseId(rs.getString("database_id"));
                    p.setName(rs.getString("name"));
                    try {
                        p.setPropertyType(PropertyType.valueOf(rs.getString("property_type")));
                    } catch (Exception e) {
                        p.setPropertyType(PropertyType.TEXT);
                    }
                    p.setOptions(rs.getString("options"));
                    p.setPosition(rs.getInt("position"));
                    list.add(p);
                }
            }
        } catch (SQLException e) {
            logger.error("Error finding properties for db {}: {}", databaseId, e.getMessage());
        }
        return list;
    }

    public List<DatabaseRow> findRows(String databaseId) {
        List<DatabaseRow> list = new ArrayList<>();
        String sql = "SELECT id, database_id, cell_values, position, created_at, updated_at " +
                "FROM database_rows WHERE database_id = ? ORDER BY position ASC, created_at ASC";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, databaseId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    DatabaseRow r = new DatabaseRow();
                    r.setId(rs.getString("id"));
                    r.setDatabaseId(rs.getString("database_id"));
                    String json = rs.getString("cell_values");
                    r.setCellValuesJson(json);
                    r.setPosition(rs.getInt("position"));
                    try {
                        Map<String, Object> map = objectMapper.readValue(json, new TypeReference<>() {});
                        r.setValues(map);
                    } catch (Exception e) {
                        r.setValues(new HashMap<>());
                    }
                    list.add(r);
                }
            }
        } catch (SQLException e) {
            logger.error("Error finding rows for db {}: {}", databaseId, e.getMessage());
        }
        return list;
    }

    public List<DatabaseView> findViews(String databaseId) {
        List<DatabaseView> list = new ArrayList<>();
        String sql = "SELECT id, database_id, name, view_type, filter_config, sort_config, created_at " +
                "FROM database_views WHERE database_id = ? ORDER BY created_at ASC";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, databaseId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    DatabaseView v = new DatabaseView();
                    v.setId(rs.getString("id"));
                    v.setDatabaseId(rs.getString("database_id"));
                    v.setName(rs.getString("name"));
                    try {
                        v.setViewType(ViewType.valueOf(rs.getString("view_type")));
                    } catch (Exception e) {
                        v.setViewType(ViewType.TABLE);
                    }
                    v.setFilterConfig(rs.getString("filter_config"));
                    v.setSortConfig(rs.getString("sort_config"));
                    list.add(v);
                }
            }
        } catch (SQLException e) {
            logger.error("Error finding views for db {}: {}", databaseId, e.getMessage());
        }
        return list;
    }

    public void saveDatabase(DatabaseDefinition db) {
        String sql = "INSERT INTO database_definitions (id, workspace_id, page_id, title, description, updated_at) " +
                "VALUES (?, ?, ?, ?, ?, CURRENT_TIMESTAMP) " +
                "ON CONFLICT (id) DO UPDATE SET title = EXCLUDED.title, description = EXCLUDED.description, updated_at = CURRENT_TIMESTAMP";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, db.getId());
            ps.setString(2, db.getWorkspaceId());
            ps.setString(3, db.getPageId());
            ps.setString(4, db.getTitle());
            ps.setString(5, db.getDescription());
            ps.executeUpdate();
        } catch (SQLException e) {
            logger.error("Error saving database: {}", e.getMessage());
            throw new RuntimeException(e);
        }
    }

    public void saveRow(DatabaseRow row) {
        try {
            if (row.getValues() != null) {
                row.setCellValuesJson(objectMapper.writeValueAsString(row.getValues()));
            }
        } catch (Exception e) {
            logger.error("Error serializing row cell values: {}", e.getMessage());
        }

        String sql = "INSERT INTO database_rows (id, database_id, cell_values, position, updated_at) " +
                "VALUES (?, ?, ?, ?, CURRENT_TIMESTAMP) " +
                "ON CONFLICT (id) DO UPDATE SET cell_values = EXCLUDED.cell_values, position = EXCLUDED.position, updated_at = CURRENT_TIMESTAMP";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, row.getId());
            ps.setString(2, row.getDatabaseId());
            ps.setString(3, row.getCellValuesJson());
            ps.setInt(4, row.getPosition());
            ps.executeUpdate();
        } catch (SQLException e) {
            logger.error("Error saving row: {}", e.getMessage());
            throw new RuntimeException(e);
        }
    }

    public void deleteRow(String rowId) {
        String sql = "DELETE FROM database_rows WHERE id = ?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, rowId);
            ps.executeUpdate();
        } catch (SQLException e) {
            logger.error("Error deleting row: {}", e.getMessage());
        }
    }

    public void saveProperty(DatabaseProperty prop) {
        String sql = "INSERT INTO database_properties (id, database_id, name, property_type, options, position) " +
                "VALUES (?, ?, ?, ?, ?, ?) " +
                "ON CONFLICT (id) DO UPDATE SET name = EXCLUDED.name, property_type = EXCLUDED.property_type, " +
                "options = EXCLUDED.options, position = EXCLUDED.position";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, prop.getId());
            ps.setString(2, prop.getDatabaseId());
            ps.setString(3, prop.getName());
            ps.setString(4, prop.getPropertyType().name());
            ps.setString(5, prop.getOptions());
            ps.setInt(6, prop.getPosition());
            ps.executeUpdate();
        } catch (SQLException e) {
            logger.error("Error saving property: {}", e.getMessage());
            throw new RuntimeException(e);
        }
    }

    private DatabaseDefinition mapDatabase(ResultSet rs) throws SQLException {
        DatabaseDefinition db = new DatabaseDefinition();
        db.setId(rs.getString("id"));
        db.setWorkspaceId(rs.getString("workspace_id"));
        db.setPageId(rs.getString("page_id"));
        db.setTitle(rs.getString("title"));
        db.setDescription(rs.getString("description"));
        return db;
    }
}
