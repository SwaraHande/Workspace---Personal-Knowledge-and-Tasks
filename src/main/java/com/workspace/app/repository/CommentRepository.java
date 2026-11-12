package com.workspace.app.repository;

import com.workspace.app.config.DatabaseConfig;
import com.workspace.app.model.Comment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CommentRepository {
    private static final Logger logger = LoggerFactory.getLogger(CommentRepository.class);

    public List<Comment> findByPageId(String pageId) {
        List<Comment> list = new ArrayList<>();
        String sql = "SELECT id, page_id, author_name, content, is_resolved, created_at, updated_at " +
                "FROM comments WHERE page_id = ? ORDER BY created_at ASC";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, pageId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Comment c = new Comment();
                    c.setId(rs.getString("id"));
                    c.setPageId(rs.getString("page_id"));
                    c.setAuthorName(rs.getString("author_name"));
                    c.setContent(rs.getString("content"));
                    c.setResolved(rs.getBoolean("is_resolved"));
                    Timestamp cat = rs.getTimestamp("created_at");
                    if (cat != null) c.setCreatedAt(cat.toInstant());
                    list.add(c);
                }
            }
        } catch (SQLException e) {
            logger.error("Error finding comments for page {}: {}", pageId, e.getMessage());
        }
        return list;
    }

    public void save(Comment comment) {
        String sql = "INSERT INTO comments (id, page_id, author_name, content, is_resolved, updated_at) " +
                "VALUES (?, ?, ?, ?, ?, CURRENT_TIMESTAMP) " +
                "ON CONFLICT (id) DO UPDATE SET content = EXCLUDED.content, is_resolved = EXCLUDED.is_resolved, " +
                "updated_at = CURRENT_TIMESTAMP";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, comment.getId());
            ps.setString(2, comment.getPageId());
            ps.setString(3, comment.getAuthorName());
            ps.setString(4, comment.getContent());
            ps.setBoolean(5, comment.isResolved());
            ps.executeUpdate();
        } catch (SQLException e) {
            logger.error("Error saving comment: {}", e.getMessage());
        }
    }

    public void delete(String commentId) {
        String sql = "DELETE FROM comments WHERE id = ?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, commentId);
            ps.executeUpdate();
        } catch (SQLException e) {
            logger.error("Error deleting comment: {}", e.getMessage());
        }
    }
}
