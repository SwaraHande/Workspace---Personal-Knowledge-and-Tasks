package com.workspace.app.repository;

import com.workspace.app.config.DatabaseConfig;
import com.workspace.app.model.Page;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class PageRepository {
    private static final Logger logger = LoggerFactory.getLogger(PageRepository.class);

    public List<Page> findActiveByWorkspace(String workspaceId) {
        List<Page> list = new ArrayList<>();
        String sql = "SELECT id, workspace_id, parent_id, title, icon, cover_image, is_favorite, " +
                "is_archived, in_trash, position, created_at, updated_at " +
                "FROM pages WHERE workspace_id = ? AND in_trash = FALSE AND is_archived = FALSE " +
                "ORDER BY position ASC, created_at ASC";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, workspaceId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapRow(rs));
                }
            }
        } catch (SQLException e) {
            logger.error("Error finding pages for workspace: {}", e.getMessage());
        }
        return list;
    }

    public List<Page> findFavorites(String workspaceId) {
        List<Page> list = new ArrayList<>();
        String sql = "SELECT id, workspace_id, parent_id, title, icon, cover_image, is_favorite, " +
                "is_archived, in_trash, position, created_at, updated_at " +
                "FROM pages WHERE workspace_id = ? AND is_favorite = TRUE AND in_trash = FALSE " +
                "ORDER BY updated_at DESC";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, workspaceId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapRow(rs));
                }
            }
        } catch (SQLException e) {
            logger.error("Error finding favorite pages: {}", e.getMessage());
        }
        return list;
    }

    public List<Page> findTrash(String workspaceId) {
        List<Page> list = new ArrayList<>();
        String sql = "SELECT id, workspace_id, parent_id, title, icon, cover_image, is_favorite, " +
                "is_archived, in_trash, position, created_at, updated_at " +
                "FROM pages WHERE workspace_id = ? AND in_trash = TRUE " +
                "ORDER BY updated_at DESC";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, workspaceId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapRow(rs));
                }
            }
        } catch (SQLException e) {
            logger.error("Error finding trash pages: {}", e.getMessage());
        }
        return list;
    }

    public Optional<Page> findById(String id) {
        String sql = "SELECT id, workspace_id, parent_id, title, icon, cover_image, is_favorite, " +
                "is_archived, in_trash, position, created_at, updated_at " +
                "FROM pages WHERE id = ?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapRow(rs));
                }
            }
        } catch (SQLException e) {
            logger.error("Error finding page by id: {}", e.getMessage());
        }
        return Optional.empty();
    }

    public void save(Page page) {
        String sql = "INSERT INTO pages (id, workspace_id, parent_id, title, icon, cover_image, " +
                "is_favorite, is_archived, in_trash, position, updated_at) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, CURRENT_TIMESTAMP) " +
                "ON CONFLICT (id) DO UPDATE SET workspace_id = EXCLUDED.workspace_id, " +
                "parent_id = EXCLUDED.parent_id, title = EXCLUDED.title, icon = EXCLUDED.icon, " +
                "cover_image = EXCLUDED.cover_image, is_favorite = EXCLUDED.is_favorite, " +
                "is_archived = EXCLUDED.is_archived, in_trash = EXCLUDED.in_trash, " +
                "position = EXCLUDED.position, updated_at = CURRENT_TIMESTAMP";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, page.getId());
            ps.setString(2, page.getWorkspaceId());
            ps.setString(3, page.getParentId());
            ps.setString(4, page.getTitle());
            ps.setString(5, page.getIcon());
            ps.setString(6, page.getCoverImage());
            ps.setBoolean(7, page.isFavorite());
            ps.setBoolean(8, page.isArchived());
            ps.setBoolean(9, page.isInTrash());
            ps.setInt(10, page.getPosition());
            ps.executeUpdate();
        } catch (SQLException e) {
            logger.error("Error saving page: {}", e.getMessage());
            throw new RuntimeException(e);
        }
    }

    public void updateTitleAndIcon(String id, String title, String icon) {
        String sql = "UPDATE pages SET title = ?, icon = ?, updated_at = CURRENT_TIMESTAMP WHERE id = ?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, title);
            ps.setString(2, icon);
            ps.setString(3, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            logger.error("Error updating page title: {}", e.getMessage());
        }
    }

    public void setFavorite(String id, boolean favorite) {
        String sql = "UPDATE pages SET is_favorite = ?, updated_at = CURRENT_TIMESTAMP WHERE id = ?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setBoolean(1, favorite);
            ps.setString(2, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            logger.error("Error setting page favorite: {}", e.getMessage());
        }
    }

    public void moveToTrash(String id) {
        String sql = "UPDATE pages SET in_trash = TRUE, updated_at = CURRENT_TIMESTAMP WHERE id = ?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            logger.error("Error moving page to trash: {}", e.getMessage());
        }
    }

    public void restoreFromTrash(String id) {
        String sql = "UPDATE pages SET in_trash = FALSE, updated_at = CURRENT_TIMESTAMP WHERE id = ?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            logger.error("Error restoring page: {}", e.getMessage());
        }
    }

    public void deletePermanently(String id) {
        String sql = "DELETE FROM pages WHERE id = ?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            logger.error("Error permanently deleting page: {}", e.getMessage());
        }
    }

    public List<Page> searchPages(String workspaceId, String query) {
        List<Page> list = new ArrayList<>();
        String sql = "SELECT p.id, p.workspace_id, p.parent_id, p.title, p.icon, p.cover_image, " +
                "p.is_favorite, p.is_archived, p.in_trash, p.position, p.created_at, p.updated_at " +
                "FROM pages p " +
                "LEFT JOIN page_blocks b ON p.id = b.page_id " +
                "WHERE p.workspace_id = ? AND p.in_trash = FALSE AND (" +
                "   p.title ILIKE ? OR b.content ILIKE ?" +
                ") " +
                "GROUP BY p.id " +
                "ORDER BY p.updated_at DESC LIMIT 30";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            String wildcard = "%" + query + "%";
            ps.setString(1, workspaceId);
            ps.setString(2, wildcard);
            ps.setString(3, wildcard);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapRow(rs));
                }
            }
        } catch (SQLException e) {
            logger.error("Error searching pages: {}", e.getMessage());
        }
        return list;
    }

    private Page mapRow(ResultSet rs) throws SQLException {
        Page page = new Page();
        page.setId(rs.getString("id"));
        page.setWorkspaceId(rs.getString("workspace_id"));
        page.setParentId(rs.getString("parent_id"));
        page.setTitle(rs.getString("title"));
        page.setIcon(rs.getString("icon"));
        page.setCoverImage(rs.getString("cover_image"));
        page.setFavorite(rs.getBoolean("is_favorite"));
        page.setArchived(rs.getBoolean("is_archived"));
        page.setInTrash(rs.getBoolean("in_trash"));
        page.setPosition(rs.getInt("position"));
        Timestamp cat = rs.getTimestamp("created_at");
        if (cat != null) page.setCreatedAt(cat.toInstant());
        Timestamp uat = rs.getTimestamp("updated_at");
        if (uat != null) page.setUpdatedAt(uat.toInstant());
        return page;
    }
}
