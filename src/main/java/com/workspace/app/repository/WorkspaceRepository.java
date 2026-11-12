package com.workspace.app.repository;

import com.workspace.app.config.DatabaseConfig;
import com.workspace.app.model.Workspace;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class WorkspaceRepository {
    private static final Logger logger = LoggerFactory.getLogger(WorkspaceRepository.class);

    public List<Workspace> findAll() {
        List<Workspace> list = new ArrayList<>();
        String sql = "SELECT id, name, icon, description, owner_id, created_at, updated_at FROM workspaces ORDER BY created_at ASC";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                Workspace ws = mapRow(rs);
                list.add(ws);
            }
        } catch (SQLException e) {
            logger.error("Error fetching workspaces: {}", e.getMessage());
        }
        return list;
    }

    public Optional<Workspace> findById(String id) {
        String sql = "SELECT id, name, icon, description, owner_id, created_at, updated_at FROM workspaces WHERE id = ?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapRow(rs));
                }
            }
        } catch (SQLException e) {
            logger.error("Error fetching workspace by id: {}", e.getMessage());
        }
        return Optional.empty();
    }

    public void save(Workspace ws) {
        String sql = "INSERT INTO workspaces (id, name, icon, description, owner_id, updated_at) " +
                "VALUES (?, ?, ?, ?, ?, CURRENT_TIMESTAMP) " +
                "ON CONFLICT (id) DO UPDATE SET name = EXCLUDED.name, icon = EXCLUDED.icon, " +
                "description = EXCLUDED.description, updated_at = CURRENT_TIMESTAMP";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, ws.getId());
            ps.setString(2, ws.getName());
            ps.setString(3, ws.getIcon());
            ps.setString(4, ws.getDescription());
            ps.setString(5, ws.getOwnerId());
            ps.executeUpdate();
        } catch (SQLException e) {
            logger.error("Error saving workspace: {}", e.getMessage());
            throw new RuntimeException(e);
        }
    }

    public void delete(String id) {
        String sql = "DELETE FROM workspaces WHERE id = ?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            logger.error("Error deleting workspace: {}", e.getMessage());
            throw new RuntimeException(e);
        }
    }

    private Workspace mapRow(ResultSet rs) throws SQLException {
        Workspace ws = new Workspace();
        ws.setId(rs.getString("id"));
        ws.setName(rs.getString("name"));
        ws.setIcon(rs.getString("icon"));
        ws.setDescription(rs.getString("description"));
        ws.setOwnerId(rs.getString("owner_id"));
        Timestamp cat = rs.getTimestamp("created_at");
        if (cat != null) ws.setCreatedAt(cat.toInstant());
        Timestamp uat = rs.getTimestamp("updated_at");
        if (uat != null) ws.setUpdatedAt(uat.toInstant());
        return ws;
    }
}
