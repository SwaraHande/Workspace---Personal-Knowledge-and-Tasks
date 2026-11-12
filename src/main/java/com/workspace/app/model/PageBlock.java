package com.workspace.app.model;

import java.time.Instant;

public class PageBlock {
    private String id;
    private String pageId;
    private BlockType blockType;
    private String content;
    private String properties;
    private int position;
    private String parentBlockId;
    private Instant createdAt;
    private Instant updatedAt;

    public PageBlock() {
        this.blockType = BlockType.PARAGRAPH;
        this.content = "";
        this.properties = "{}";
        this.createdAt = Instant.now();
        this.updatedAt = Instant.now();
    }

    public PageBlock(String id, String pageId, BlockType blockType, String content, int position) {
        this();
        this.id = id;
        this.pageId = pageId;
        this.blockType = blockType != null ? blockType : BlockType.PARAGRAPH;
        this.content = content != null ? content : "";
        this.position = position;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getPageId() { return pageId; }
    public void setPageId(String pageId) { this.pageId = pageId; }

    public BlockType getBlockType() { return blockType; }
    public void setBlockType(BlockType blockType) { this.blockType = blockType; }

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }

    public String getProperties() { return properties; }
    public void setProperties(String properties) { this.properties = properties != null ? properties : "{}"; }

    public int getPosition() { return position; }
    public void setPosition(int position) { this.position = position; }

    public String getParentBlockId() { return parentBlockId; }
    public void setParentBlockId(String parentBlockId) { this.parentBlockId = parentBlockId; }

    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }

    public Instant getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(Instant updatedAt) { this.updatedAt = updatedAt; }

    // Helpers for properties JSON
    public boolean isChecked() {
        return properties != null && properties.contains("\"checked\":true");
    }

    public void setChecked(boolean checked) {
        if (properties == null || properties.isEmpty() || properties.equals("{}")) {
            this.properties = "{\"checked\":" + checked + "}";
        } else if (properties.contains("\"checked\":")) {
            this.properties = properties.replaceAll("\"checked\":\\s*(true|false)", "\"checked\":" + checked);
        } else {
            this.properties = properties.substring(0, properties.length() - 1) + ",\"checked\":" + checked + "}";
        }
    }
}
