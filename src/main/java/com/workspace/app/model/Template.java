package com.workspace.app.model;

import java.time.Instant;

public class Template {
    private String id;
    private String name;
    private String description;
    private String category;
    private String icon;
    private String contentJson;
    private boolean isBuiltin;
    private Instant createdAt;

    public Template() {
        this.icon = "📋";
        this.createdAt = Instant.now();
    }

    public Template(String id, String name, String description, String category, String icon, String contentJson, boolean isBuiltin) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.category = category;
        this.icon = icon != null ? icon : "📋";
        this.contentJson = contentJson;
        this.isBuiltin = isBuiltin;
        this.createdAt = Instant.now();
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public String getIcon() { return icon; }
    public void setIcon(String icon) { this.icon = icon; }

    public String getContentJson() { return contentJson; }
    public void setContentJson(String contentJson) { this.contentJson = contentJson; }

    public boolean isBuiltin() { return isBuiltin; }
    public void setBuiltin(boolean builtin) { isBuiltin = builtin; }

    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }

    @Override
    public String toString() {
        return (icon != null ? icon + " " : "") + name;
    }
}
