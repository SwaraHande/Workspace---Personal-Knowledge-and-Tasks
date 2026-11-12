package com.workspace.app.model;

import java.time.Instant;

public class DatabaseView {
    private String id;
    private String databaseId;
    private String name;
    private ViewType viewType;
    private String filterConfig;
    private String sortConfig;
    private Instant createdAt;

    public DatabaseView() {
        this.viewType = ViewType.TABLE;
        this.filterConfig = "{}";
        this.sortConfig = "{}";
        this.createdAt = Instant.now();
    }

    public DatabaseView(String id, String databaseId, String name, ViewType viewType) {
        this();
        this.id = id;
        this.databaseId = databaseId;
        this.name = name;
        this.viewType = viewType != null ? viewType : ViewType.TABLE;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getDatabaseId() { return databaseId; }
    public void setDatabaseId(String databaseId) { this.databaseId = databaseId; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public ViewType getViewType() { return viewType; }
    public void setViewType(ViewType viewType) { this.viewType = viewType; }

    public String getFilterConfig() { return filterConfig; }
    public void setFilterConfig(String filterConfig) { this.filterConfig = filterConfig != null ? filterConfig : "{}"; }

    public String getSortConfig() { return sortConfig; }
    public void setSortConfig(String sortConfig) { this.sortConfig = sortConfig != null ? sortConfig : "{}"; }

    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }
}
