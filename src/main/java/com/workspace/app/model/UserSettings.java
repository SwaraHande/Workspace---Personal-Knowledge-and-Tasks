package com.workspace.app.model;

import java.time.Instant;

public class UserSettings {
    private String userId;
    private String theme;
    private int fontSize;
    private int autosaveInterval;
    private boolean sidebarCollapsed;
    private Instant updatedAt;

    public UserSettings() {
        this.userId = "user_default";
        this.theme = "DARK";
        this.fontSize = 14;
        this.autosaveInterval = 1500;
        this.sidebarCollapsed = false;
        this.updatedAt = Instant.now();
    }

    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }

    public String getTheme() { return theme; }
    public void setTheme(String theme) { this.theme = theme; }

    public int getFontSize() { return fontSize; }
    public void setFontSize(int fontSize) { this.fontSize = fontSize; }

    public int getAutosaveInterval() { return autosaveInterval; }
    public void setAutosaveInterval(int autosaveInterval) { this.autosaveInterval = autosaveInterval; }

    public boolean isSidebarCollapsed() { return sidebarCollapsed; }
    public void setSidebarCollapsed(boolean sidebarCollapsed) { this.sidebarCollapsed = sidebarCollapsed; }

    public Instant getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(Instant updatedAt) { this.updatedAt = updatedAt; }
}
