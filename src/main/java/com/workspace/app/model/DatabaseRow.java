package com.workspace.app.model;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

public class DatabaseRow {
    private String id;
    private String databaseId;
    private String cellValuesJson;
    private int position;
    private Instant createdAt;
    private Instant updatedAt;

    // Parsed map representation of values
    private Map<String, Object> values = new HashMap<>();

    public DatabaseRow() {
        this.cellValuesJson = "{}";
        this.createdAt = Instant.now();
        this.updatedAt = Instant.now();
    }

    public DatabaseRow(String id, String databaseId, String cellValuesJson, int position) {
        this();
        this.id = id;
        this.databaseId = databaseId;
        this.cellValuesJson = cellValuesJson != null ? cellValuesJson : "{}";
        this.position = position;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getDatabaseId() { return databaseId; }
    public void setDatabaseId(String databaseId) { this.databaseId = databaseId; }

    public String getCellValuesJson() { return cellValuesJson; }
    public void setCellValuesJson(String cellValuesJson) { this.cellValuesJson = cellValuesJson != null ? cellValuesJson : "{}"; }

    public int getPosition() { return position; }
    public void setPosition(int position) { this.position = position; }

    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }

    public Instant getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(Instant updatedAt) { this.updatedAt = updatedAt; }

    public Map<String, Object> getValues() { return values; }
    public void setValues(Map<String, Object> values) { this.values = values; }
}
