package com.workspace.app.model;

import java.time.Instant;

public class Workspace {
    private String id;
    private String name;
    private String icon;
    private String description;
    private String ownerId;
    private Instant createdAt;
    private Instant updatedAt;

    public Workspace() {}

    public Workspace(String id, String name, String icon, String description, String ownerId) {
        this.id = id;
        this.name = name;
        this.icon = icon != null ? icon : "🚀";
        this.description = description;
        this.ownerId = ownerId;
        this.createdAt = Instant.now();
        this.updatedAt = Instant.now();
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getIcon() { return icon; }
    public void setIcon(String icon) { this.icon = icon; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getOwnerId() { return ownerId; }
    public void setOwnerId(String ownerId) { this.ownerId = ownerId; }

    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }

    public Instant getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(Instant updatedAt) { this.updatedAt = updatedAt; }

    @Override
    public String toString() {
        return (icon != null ? icon + " " : "") + name;
    }
}
