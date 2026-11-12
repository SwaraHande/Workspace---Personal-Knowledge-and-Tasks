package com.workspace.app.model;

public enum PropertyType {
    TEXT("Text"),
    NUMBER("Number"),
    SELECT("Select"),
    MULTI_SELECT("Multi-select"),
    DATE("Date"),
    CHECKBOX("Checkbox"),
    URL("URL");

    private final String displayName;

    PropertyType(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
