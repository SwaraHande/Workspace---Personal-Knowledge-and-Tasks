package com.workspace.app.model;

import java.time.Instant;

public class DatabaseProperty {
    private String id;
    private String databaseId;
    private String name;
    private PropertyType propertyType;
    private String options; // JSON array of options for SELECT/MULTI_SELECT
    private int position;
    private Instant createdAt;

    public DatabaseProperty() {
        this.propertyType = PropertyType.TEXT;
        this.options = "[]";
        this.createdAt = Instant.now();
    }

    public DatabaseProperty(String id, String databaseId, String name, PropertyType propertyType, int position) {
        this();
        this.id = id;
        this.databaseId = databaseId;
        this.name = name;
        this.propertyType = propertyType;
        this.position = position;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getDatabaseId() { return databaseId; }
    public void setDatabaseId(String databaseId) { this.databaseId = databaseId; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public PropertyType getPropertyType() { return propertyType; }
    public void setPropertyType(PropertyType propertyType) { this.propertyType = propertyType; }

    public String getOptions() { return options; }
    public void setOptions(String options) { this.options = options != null ? options : "[]"; }

    public int getPosition() { return position; }
    public void setPosition(int position) { this.position = position; }

    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }
}
