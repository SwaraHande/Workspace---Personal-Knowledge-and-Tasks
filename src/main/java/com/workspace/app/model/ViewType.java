package com.workspace.app.model;

public enum ViewType {
    TABLE("Table"),
    BOARD("Board"),
    CALENDAR("Calendar"),
    LIST("List"),
    GALLERY("Gallery");

    private final String displayName;

    ViewType(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
