package com.workspace.app.model;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

public class Page {
    private String id;
    private String workspaceId;
    private String parentId;
    private String title;
    private String icon;
    private String coverImage;
    private boolean isFavorite;
    private boolean isArchived;
    private boolean inTrash;
    private int position;
    private Instant createdAt;
    private Instant updatedAt;

    // In-memory children for tree representation
    private List<Page> children = new ArrayList<>();

    public Page() {
        this.title = "Untitled";
        this.icon = "📄";
        this.createdAt = Instant.now();
        this.updatedAt = Instant.now();
    }

    public Page(String id, String workspaceId, String parentId, String title, String icon) {
        this();
        this.id = id;
        this.workspaceId = workspaceId;
        this.parentId = parentId;
        this.title = title != null && !title.isBlank() ? title : "Untitled";
        this.icon = icon != null ? icon : "📄";
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getWorkspaceId() { return workspaceId; }
    public void setWorkspaceId(String workspaceId) { this.workspaceId = workspaceId; }

    public String getParentId() { return parentId; }
    public void setParentId(String parentId) { this.parentId = parentId; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getIcon() { return icon; }
    public void setIcon(String icon) { this.icon = icon; }

    public String getCoverImage() { return coverImage; }
    public void setCoverImage(String coverImage) { this.coverImage = coverImage; }

    public boolean isFavorite() { return isFavorite; }
    public void setFavorite(boolean favorite) { isFavorite = favorite; }

    public boolean isArchived() { return isArchived; }
    public void setArchived(boolean archived) { isArchived = archived; }

    public boolean isInTrash() { return inTrash; }
    public void setInTrash(boolean inTrash) { this.inTrash = inTrash; }

    public int getPosition() { return position; }
    public void setPosition(int position) { this.position = position; }

    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }

    public Instant getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(Instant updatedAt) { this.updatedAt = updatedAt; }

    public List<Page> getChildren() { return children; }
    public void setChildren(List<Page> children) { this.children = children; }

    @Override
    public String toString() {
        return (icon != null ? icon + " " : "") + (title != null && !title.isEmpty() ? title : "Untitled");
    }
}
