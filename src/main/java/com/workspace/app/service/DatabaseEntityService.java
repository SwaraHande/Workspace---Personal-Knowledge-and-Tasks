package com.workspace.app.service;

import com.workspace.app.model.*;
import com.workspace.app.repository.DatabaseViewRepository;

import java.util.*;

public class DatabaseEntityService {
    private final DatabaseViewRepository databaseViewRepository;

    public DatabaseEntityService(DatabaseViewRepository databaseViewRepository) {
        this.databaseViewRepository = databaseViewRepository;
    }

    public Optional<DatabaseDefinition> getDatabaseForPage(String pageId) {
        return databaseViewRepository.findByPageId(pageId);
    }

    public List<DatabaseProperty> getProperties(String databaseId) {
        return databaseViewRepository.findProperties(databaseId);
    }

    public List<DatabaseRow> getRows(String databaseId) {
        return databaseViewRepository.findRows(databaseId);
    }

    public List<DatabaseView> getViews(String databaseId) {
        return databaseViewRepository.findViews(databaseId);
    }

    public DatabaseDefinition createDatabase(String workspaceId, String pageId, String title) {
        String dbId = "db_" + UUID.randomUUID().toString().substring(0, 8);
        DatabaseDefinition db = new DatabaseDefinition(dbId, workspaceId, pageId, title);
        databaseViewRepository.saveDatabase(db);

        // Add standard properties: Name, Status, Priority
        DatabaseProperty p1 = new DatabaseProperty("prop_" + UUID.randomUUID().toString().substring(0, 8), dbId, "Name", PropertyType.TEXT, 0);
        DatabaseProperty p2 = new DatabaseProperty("prop_" + UUID.randomUUID().toString().substring(0, 8), dbId, "Status", PropertyType.SELECT, 1);
        p2.setOptions("[\"Todo\",\"In Progress\",\"Done\"]");
        DatabaseProperty p3 = new DatabaseProperty("prop_" + UUID.randomUUID().toString().substring(0, 8), dbId, "Priority", PropertyType.SELECT, 2);
        p3.setOptions("[\"High\",\"Medium\",\"Low\"]");

        databaseViewRepository.saveProperty(p1);
        databaseViewRepository.saveProperty(p2);
        databaseViewRepository.saveProperty(p3);

        return db;
    }

    public DatabaseRow addRow(String databaseId, Map<String, Object> values, int position) {
        String rowId = "row_" + UUID.randomUUID().toString().substring(0, 8);
        DatabaseRow row = new DatabaseRow(rowId, databaseId, "{}", position);
        row.setValues(values);
        databaseViewRepository.saveRow(row);
        return row;
    }

    public void updateRow(DatabaseRow row) {
        databaseViewRepository.saveRow(row);
    }

    public void deleteRow(String rowId) {
        databaseViewRepository.deleteRow(rowId);
    }

    public void addProperty(DatabaseProperty prop) {
        databaseViewRepository.saveProperty(prop);
    }
}
