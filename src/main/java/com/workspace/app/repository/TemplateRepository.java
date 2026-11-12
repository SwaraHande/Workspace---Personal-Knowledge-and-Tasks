package com.workspace.app.repository;

import com.workspace.app.config.DatabaseConfig;
import com.workspace.app.model.Template;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class TemplateRepository {
    private static final Logger logger = LoggerFactory.getLogger(TemplateRepository.class);

    public List<Template> findAll() {
        List<Template> list = new ArrayList<>();
        String sql = "SELECT id, name, description, category, icon, content_json, is_builtin, created_at " +
                "FROM templates ORDER BY is_builtin DESC, name ASC";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                Template t = new Template();
                t.setId(rs.getString("id"));
                t.setName(rs.getString("name"));
                t.setDescription(rs.getString("description"));
                t.setCategory(rs.getString("category"));
                t.setIcon(rs.getString("icon"));
                t.setContentJson(rs.getString("content_json"));
                t.setBuiltin(rs.getBoolean("is_builtin"));
                list.add(t);
            }
        } catch (SQLException e) {
            logger.error("Error finding templates: {}", e.getMessage());
        }
        return list;
    }

    public void save(Template template) {
        String sql = "INSERT INTO templates (id, name, description, category, icon, content_json, is_builtin) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?) " +
                "ON CONFLICT (id) DO UPDATE SET name = EXCLUDED.name, description = EXCLUDED.description, " +
                "category = EXCLUDED.category, icon = EXCLUDED.icon, content_json = EXCLUDED.content_json";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, template.getId());
            ps.setString(2, template.getName());
            ps.setString(3, template.getDescription());
            ps.setString(4, template.getCategory());
            ps.setString(5, template.getIcon());
            ps.setString(6, template.getContentJson());
            ps.setBoolean(7, template.isBuiltin());
            ps.executeUpdate();
        } catch (SQLException e) {
            logger.error("Error saving template: {}", e.getMessage());
        }
    }
}
