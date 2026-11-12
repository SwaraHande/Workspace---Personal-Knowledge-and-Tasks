package com.workspace.app.model;

public enum BlockType {
    PARAGRAPH("Text", "Plain text block", "T"),
    HEADING_1("Heading 1", "Large section heading", "H1"),
    HEADING_2("Heading 2", "Medium section heading", "H2"),
    HEADING_3("Heading 3", "Small section heading", "H3"),
    BULLET_LIST("Bulleted list", "Simple bulleted list", "•"),
    NUMBERED_LIST("Numbered list", "List with numbers", "1."),
    TODO("To-do list", "Track tasks with a checkbox", "[✓]"),
    TOGGLE("Toggle list", "Toggles can hide and show content", "▶"),
    QUOTE("Quote", "Capture a quote", "\""),
    DIVIDER("Divider", "Visually divide blocks", "―"),
    CODE("Code", "Capture a code snippet", "<>"),
    CALLOUT("Callout", "Make writing stand out", "!"),
    TABLE("Table", "Insert a database table view", "[#]"),
    IMAGE("Image", "Insert or link an image", "IMG"),
    LINK_BOOKMARK("Web bookmark", "Save a link as a visual bookmark", "URL");

    private final String label;
    private final String description;
    private final String iconText;

    BlockType(String label, String description, String iconText) {
        this.label = label;
        this.description = description;
        this.iconText = iconText;
    }

    public String getLabel() {
        return label;
    }

    public String getDescription() {
        return description;
    }

    public String getIconText() {
        return iconText;
    }
}
