package com.workspace.app.model;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

public class DatabaseDefinition {
    private String id;
    private String workspaceId;
    private String pageId;
    private String title;
    private String description;
    private Instant createdAt;
    private Instant updatedAt;

    private List<DatabaseProperty> properties = new ArrayList<>();
    private List<DatabaseView> views = new ArrayList<>();

    public DatabaseDefinition() {
        this.createdAt = Instant.now();
        this.updatedAt = Instant.now();
    }

    public DatabaseDefinition(String id, String workspaceId, String pageId, String title) {
        this();
        this.id = id;
        this.workspaceId = workspaceId;
        this.pageId = pageId;
        this.title = title;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getWorkspaceId() { return workspaceId; }
    public void setWorkspaceId(String workspaceId) { this.workspaceId = workspaceId; }

    public String getPageId() { return pageId; }
    public void setPageId(String pageId) { this.pageId = pageId; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }

    public Instant getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(Instant updatedAt) { this.updatedAt = updatedAt; }

    public List<DatabaseProperty> getProperties() { return properties; }
    public void setProperties(List<DatabaseProperty> properties) { this.properties = properties; }

    public List<DatabaseView> getViews() { return views; }
    public void setViews(List<DatabaseView> views) { this.views = views; }
}
