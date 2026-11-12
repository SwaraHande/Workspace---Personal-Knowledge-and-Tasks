package com.workspace.app.model;

import java.time.Instant;

public class Comment {
    private String id;
    private String pageId;
    private String authorName;
    private String content;
    private boolean isResolved;
    private Instant createdAt;
    private Instant updatedAt;

    public Comment() {
        this.createdAt = Instant.now();
        this.updatedAt = Instant.now();
    }

    public Comment(String id, String pageId, String authorName, String content) {
        this();
        this.id = id;
        this.pageId = pageId;
        this.authorName = authorName != null ? authorName : "Anonymous";
        this.content = content;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getPageId() { return pageId; }
    public void setPageId(String pageId) { this.pageId = pageId; }

    public String getAuthorName() { return authorName; }
    public void setAuthorName(String authorName) { this.authorName = authorName; }

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }

    public boolean isResolved() { return isResolved; }
    public void setResolved(boolean resolved) { isResolved = resolved; }

    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }

    public Instant getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(Instant updatedAt) { this.updatedAt = updatedAt; }
}
