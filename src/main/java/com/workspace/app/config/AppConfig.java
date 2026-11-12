package com.workspace.app.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class AppConfig {
    private static final Logger logger = LoggerFactory.getLogger(AppConfig.class);
    private static final Map<String, String> envMap = new HashMap<>();

    static {
        loadEnvFile();
    }

    private static void loadEnvFile() {
        File envFile = new File(".env");
        if (envFile.exists() && envFile.isFile()) {
            try (BufferedReader reader = new BufferedReader(new FileReader(envFile))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    line = line.trim();
                    if (line.isEmpty() || line.startsWith("#")) continue;
                    int eqIndex = line.indexOf('=');
                    if (eqIndex > 0) {
                        String key = line.substring(0, eqIndex).trim();
                        String value = line.substring(eqIndex + 1).trim();
                        envMap.put(key, value);
                    }
                }
                logger.info("Loaded configuration from .env file");
            } catch (IOException e) {
                logger.warn("Could not read .env file: {}", e.getMessage());
            }
        }
    }

    public static String get(String key, String defaultValue) {
        // Priority: 1. System environment variable, 2. .env file, 3. default
        String envVal = System.getenv(key);
        if (envVal != null && !envVal.isBlank()) {
            return envVal;
        }
        String localVal = envMap.get(key);
        if (localVal != null && !localVal.isBlank()) {
            return localVal;
        }
        return defaultValue;
    }

    public static int getInt(String key, int defaultValue) {
        String val = get(key, null);
        if (val == null) return defaultValue;
        try {
            return Integer.parseInt(val);
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }

    public static String getDbHost() {
        return get("DB_HOST", "localhost");
    }

    public static int getDbPort() {
        return getInt("DB_PORT", 5432);
    }

    public static String getDbName() {
        return get("DB_NAME", "workspace_db");
    }

    public static String getDbUser() {
        return get("DB_USER", "postgres");
    }

    public static String getDbPassword() {
        return get("DB_PASSWORD", "");
    }

    public static int getDbMaxPoolSize() {
        return getInt("DB_MAX_POOL_SIZE", 10);
    }

    public static String getAppTheme() {
        return get("APP_THEME", "DARK");
    }

    public static int getAutosaveIntervalMs() {
        return getInt("APP_AUTOSAVE_INTERVAL_MS", 1500);
    }
}
