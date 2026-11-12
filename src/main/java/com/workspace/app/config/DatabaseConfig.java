package com.workspace.app.config;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

public class DatabaseConfig {
    private static final Logger logger = LoggerFactory.getLogger(DatabaseConfig.class);
    private static HikariDataSource dataSource;

    public static synchronized DataSource getDataSource() {
        if (dataSource == null || dataSource.isClosed()) {
            initDataSource();
        }
        return dataSource;
    }

    private static void initDataSource() {
        String host = AppConfig.getDbHost();
        int port = AppConfig.getDbPort();
        String dbName = AppConfig.getDbName();
        String user = AppConfig.getDbUser();
        String password = AppConfig.getDbPassword();
        int maxPool = AppConfig.getDbMaxPoolSize();

        String jdbcUrl = String.format("jdbc:postgresql://%s:%d/%s", host, port, dbName);
        logger.info("Initializing HikariCP pool for PostgreSQL at {}:{}", host, port);

        HikariConfig config = new HikariConfig();
        config.setJdbcUrl(jdbcUrl);
        config.setUsername(user);
        config.setPassword(password);
        config.setMaximumPoolSize(maxPool);
        config.setMinimumIdle(2);
        config.setIdleTimeout(30000);
        config.setConnectionTimeout(10000);
        config.setMaxLifetime(1800000);
        config.setPoolName("WorkspaceHikariPool");

        // Helpful diagnostic properties
        config.addDataSourceProperty("ApplicationName", "NotionWorkspaceApp");
        config.addDataSourceProperty("reWriteBatchedInserts", "true");

        try {
            dataSource = new HikariDataSource(config);
            logger.info("HikariCP DataSource initialized successfully");
        } catch (Exception e) {
            logger.error("Failed to initialize DataSource: {}", e.getMessage());
            throw new RuntimeException("Database connection initialization failed: " + e.getMessage(), e);
        }
    }

    public static Connection getConnection() throws SQLException {
        return getDataSource().getConnection();
    }

    public static boolean testConnection() {
        try (Connection conn = getConnection()) {
            return conn.isValid(3);
        } catch (Exception e) {
            logger.error("Database connection test failed: {}", e.getMessage());
            return false;
        }
    }

    public static synchronized void shutdown() {
        if (dataSource != null && !dataSource.isClosed()) {
            logger.info("Closing HikariCP DataSource...");
            dataSource.close();
        }
    }
}
