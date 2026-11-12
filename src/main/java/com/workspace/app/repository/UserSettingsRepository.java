package com.workspace.app.repository;

import com.workspace.app.config.DatabaseConfig;
import com.workspace.app.model.UserSettings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.util.Optional;

public class UserSettingsRepository {
    private static final Logger logger = LoggerFactory.getLogger(UserSettingsRepository.class);

    public Optional<UserSettings> findByUserId(String userId) {
        String sql = "SELECT user_id, theme, font_size, autosave_interval, sidebar_collapsed, updated_at " +
                "FROM user_settings WHERE user_id = ?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    UserSettings s = new UserSettings();
                    s.setUserId(rs.getString("user_id"));
                    s.setTheme(rs.getString("theme"));
                    s.setFontSize(rs.getInt("font_size"));
                    s.setAutosaveInterval(rs.getInt("autosave_interval"));
                    s.setSidebarCollapsed(rs.getBoolean("sidebar_collapsed"));
                    return Optional.of(s);
                }
            }
        } catch (SQLException e) {
            logger.error("Error finding settings for user {}: {}", userId, e.getMessage());
        }
        return Optional.empty();
    }

    public void save(UserSettings settings) {
        String sql = "INSERT INTO user_settings (user_id, theme, font_size, autosave_interval, sidebar_collapsed, updated_at) " +
                "VALUES (?, ?, ?, ?, ?, CURRENT_TIMESTAMP) " +
                "ON CONFLICT (user_id) DO UPDATE SET theme = EXCLUDED.theme, font_size = EXCLUDED.font_size, " +
                "autosave_interval = EXCLUDED.autosave_interval, sidebar_collapsed = EXCLUDED.sidebar_collapsed, " +
                "updated_at = CURRENT_TIMESTAMP";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, settings.getUserId());
            ps.setString(2, settings.getTheme());
            ps.setInt(3, settings.getFontSize());
            ps.setInt(4, settings.getAutosaveInterval());
            ps.setBoolean(5, settings.isSidebarCollapsed());
            ps.executeUpdate();
        } catch (SQLException e) {
            logger.error("Error saving user settings: {}", e.getMessage());
        }
    }
}
