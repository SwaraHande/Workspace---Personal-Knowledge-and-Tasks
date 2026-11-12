package com.workspace.app.repository;

import com.workspace.app.config.DatabaseConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.stream.Collectors;

public class DatabaseMigration {
    private static final Logger logger = LoggerFactory.getLogger(DatabaseMigration.class);

    private static final String[] MIGRATION_FILES = {
            "V1__initial_schema.sql",
            "V2__seed_data.sql"
    };

    public static void runMigrations() {
        logger.info("Starting database migrations check...");
        try (Connection conn = DatabaseConfig.getConnection()) {
            ensureMigrationTable(conn);

            for (String file : MIGRATION_FILES) {
                if (!isMigrationApplied(conn, file)) {
                    applyMigration(conn, file);
                } else {
                    logger.debug("Migration {} is already applied.", file);
                }
            }
            logger.info("Database migrations completed successfully.");
        } catch (Exception e) {
            logger.error("Database migration failed: {}", e.getMessage(), e);
            throw new RuntimeException("Migration error: " + e.getMessage(), e);
        }
    }

    private static void ensureMigrationTable(Connection conn) throws Exception {
        String sql = "CREATE TABLE IF NOT EXISTS schema_version (" +
                "version VARCHAR(100) PRIMARY KEY, " +
                "applied_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP" +
                ")";
        try (Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
        }
    }

    private static boolean isMigrationApplied(Connection conn, String version) throws Exception {
        String sql = "SELECT COUNT(*) FROM schema_version WHERE version = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, version);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        }
        return false;
    }

    private static void applyMigration(Connection conn, String filename) throws Exception {
        String resourcePath = "/db/migration/" + filename;
        logger.info("Applying migration: {}", filename);

        try (InputStream is = DatabaseMigration.class.getResourceAsStream(resourcePath)) {
            if (is == null) {
                throw new IllegalStateException("Migration script not found on classpath: " + resourcePath);
            }
            String script = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8))
                    .lines().collect(Collectors.joining("\n"));

            conn.setAutoCommit(false);
            try {
                try (Statement stmt = conn.createStatement()) {
                    stmt.execute(script);
                }

                String recordSql = "INSERT INTO schema_version (version) VALUES (?)";
                try (PreparedStatement ps = conn.prepareStatement(recordSql)) {
                    ps.setString(1, filename);
                    ps.executeUpdate();
                }

                conn.commit();
                logger.info("Successfully applied migration {}", filename);
            } catch (Exception e) {
                conn.rollback();
                throw e;
            } finally {
                conn.setAutoCommit(true);
            }
        }
    }
}
