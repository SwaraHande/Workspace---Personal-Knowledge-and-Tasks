package com.workspace.app.app;

import com.workspace.app.config.AppConfig;
import com.workspace.app.config.DatabaseConfig;
import com.workspace.app.repository.DatabaseMigration;
import com.workspace.app.ui.MainFrame;
import com.workspace.app.ui.theme.ThemeManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;

public class Main {
    private static final Logger logger = LoggerFactory.getLogger(Main.class);

    public static void main(String[] args) {
        logger.info("Starting Notion-Inspired Workspace Application...");

        // 1. Initialize UI theme first so dialogs look modern
        ThemeManager.initialize(AppConfig.getAppTheme());

        // 2. Run Database Migrations
        try {
            DatabaseMigration.runMigrations();
        } catch (Exception e) {
            logger.error("Failed to connect or migrate PostgreSQL database: {}", e.getMessage());
            JOptionPane.showMessageDialog(
                    null,
                    "Could not connect to PostgreSQL database:\n" + e.getMessage() +
                            "\n\nPlease ensure PostgreSQL is running and check your .env configuration.\n" +
                            "Host: " + AppConfig.getDbHost() + ", Port: " + AppConfig.getDbPort() + ", DB: " + AppConfig.getDbName(),
                    "Database Connection Error",
                    JOptionPane.ERROR_MESSAGE
            );
            System.exit(1);
        }

        // 3. Launch UI on Event Dispatch Thread
        SwingUtilities.invokeLater(() -> {
            try {
                AppContext context = AppContext.getInstance();
                MainFrame frame = new MainFrame(context);
                frame.setVisible(true);
                logger.info("Main application window launched successfully.");
            } catch (Exception e) {
                logger.error("Failed to launch UI: {}", e.getMessage(), e);
                JOptionPane.showMessageDialog(null, "Application Launch Error: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        // Add shutdown hook for clean pool disposal
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            logger.info("Application shutting down, releasing resources...");
            DatabaseConfig.shutdown();
        }));
    }
}
