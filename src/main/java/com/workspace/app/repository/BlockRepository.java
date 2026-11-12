package com.workspace.app.repository;

import com.workspace.app.config.DatabaseConfig;
import com.workspace.app.model.BlockType;
import com.workspace.app.model.PageBlock;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class BlockRepository {
    private static final Logger logger = LoggerFactory.getLogger(BlockRepository.class);

    public List<PageBlock> findByPageId(String pageId) {
        List<PageBlock> blocks = new ArrayList<>();
        String sql = "SELECT id, page_id, block_type, content, properties, position, parent_block_id, " +
                "created_at, updated_at FROM page_blocks WHERE page_id = ? ORDER BY position ASC, created_at ASC";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, pageId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    blocks.add(mapRow(rs));
                }
            }
        } catch (SQLException e) {
            logger.error("Error finding blocks for page {}: {}", pageId, e.getMessage());
        }
        return blocks;
    }

    public void save(PageBlock block) {
        String sql = "INSERT INTO page_blocks (id, page_id, block_type, content, properties, position, " +
                "parent_block_id, updated_at) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, CURRENT_TIMESTAMP) " +
                "ON CONFLICT (id) DO UPDATE SET " +
                "block_type = EXCLUDED.block_type, content = EXCLUDED.content, " +
                "properties = EXCLUDED.properties, position = EXCLUDED.position, " +
                "parent_block_id = EXCLUDED.parent_block_id, updated_at = CURRENT_TIMESTAMP";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, block.getId());
            ps.setString(2, block.getPageId());
            ps.setString(3, block.getBlockType().name());
            ps.setString(4, block.getContent());
            ps.setString(5, block.getProperties());
            ps.setInt(6, block.getPosition());
            ps.setString(7, block.getParentBlockId());
            ps.executeUpdate();
        } catch (SQLException e) {
            logger.error("Error saving block: {}", e.getMessage());
            throw new RuntimeException(e);
        }
    }

    /**
     * Atomically replaces or synchronizes all blocks for a page
     */
    public void saveAllForPage(String pageId, List<PageBlock> blocks) {
        String deleteSql = "DELETE FROM page_blocks WHERE page_id = ?";
        String insertSql = "INSERT INTO page_blocks (id, page_id, block_type, content, properties, position, " +
                "parent_block_id, updated_at) VALUES (?, ?, ?, ?, ?, ?, ?, CURRENT_TIMESTAMP)";

        try (Connection conn = DatabaseConfig.getConnection()) {
            conn.setAutoCommit(false);
            try {
                try (PreparedStatement delStmt = conn.prepareStatement(deleteSql)) {
                    delStmt.setString(1, pageId);
                    delStmt.executeUpdate();
                }

                try (PreparedStatement insStmt = conn.prepareStatement(insertSql)) {
                    for (int i = 0; i < blocks.size(); i++) {
                        PageBlock b = blocks.get(i);
                        b.setPosition(i);
                        insStmt.setString(1, b.getId());
                        insStmt.setString(2, pageId);
                        insStmt.setString(3, b.getBlockType().name());
                        insStmt.setString(4, b.getContent());
                        insStmt.setString(5, b.getProperties());
                        insStmt.setInt(6, i);
                        insStmt.setString(7, b.getParentBlockId());
                        insStmt.addBatch();
                    }
                    insStmt.executeBatch();
                }

                conn.commit();
            } catch (SQLException e) {
                conn.rollback();
                logger.error("Failed batch saving blocks for page {}: {}", pageId, e.getMessage());
                throw e;
            } finally {
                conn.setAutoCommit(true);
            }
        } catch (SQLException e) {
            logger.error("Transaction error in saveAllForPage: {}", e.getMessage());
            throw new RuntimeException(e);
        }
    }

    public void delete(String blockId) {
        String sql = "DELETE FROM page_blocks WHERE id = ?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, blockId);
            ps.executeUpdate();
        } catch (SQLException e) {
            logger.error("Error deleting block: {}", e.getMessage());
        }
    }

    private PageBlock mapRow(ResultSet rs) throws SQLException {
        PageBlock b = new PageBlock();
        b.setId(rs.getString("id"));
        b.setPageId(rs.getString("page_id"));
        String typeStr = rs.getString("block_type");
        try {
            b.setBlockType(BlockType.valueOf(typeStr));
        } catch (Exception e) {
            b.setBlockType(BlockType.PARAGRAPH);
        }
        b.setContent(rs.getString("content"));
        b.setProperties(rs.getString("properties"));
        b.setPosition(rs.getInt("position"));
        b.setParentBlockId(rs.getString("parent_block_id"));
        Timestamp cat = rs.getTimestamp("created_at");
        if (cat != null) b.setCreatedAt(cat.toInstant());
        Timestamp uat = rs.getTimestamp("updated_at");
        if (uat != null) b.setUpdatedAt(uat.toInstant());
        return b;
    }
}
