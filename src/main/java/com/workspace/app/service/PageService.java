package com.workspace.app.service;

import com.workspace.app.model.BlockType;
import com.workspace.app.model.Page;
import com.workspace.app.model.PageBlock;
import com.workspace.app.repository.BlockRepository;
import com.workspace.app.repository.PageRepository;

import java.util.*;

public class PageService {
    private final PageRepository pageRepository;
    private final BlockRepository blockRepository;

    public PageService(PageRepository pageRepository, BlockRepository blockRepository) {
        this.pageRepository = pageRepository;
        this.blockRepository = blockRepository;
    }

    /**
     * Builds and returns root-level pages with nested children populated.
     */
    public List<Page> getPageTree(String workspaceId) {
        List<Page> allPages = pageRepository.findActiveByWorkspace(workspaceId);
        Map<String, Page> pageMap = new HashMap<>();
        List<Page> rootPages = new ArrayList<>();

        for (Page p : allPages) {
            p.getChildren().clear();
            pageMap.put(p.getId(), p);
        }

        for (Page p : allPages) {
            if (p.getParentId() != null && pageMap.containsKey(p.getParentId())) {
                pageMap.get(p.getParentId()).getChildren().add(p);
            } else {
                rootPages.add(p);
            }
        }
        return rootPages;
    }

    public List<Page> getFavorites(String workspaceId) {
        return pageRepository.findFavorites(workspaceId);
    }

    public List<Page> getTrash(String workspaceId) {
        return pageRepository.findTrash(workspaceId);
    }

    public Optional<Page> getPageById(String id) {
        return pageRepository.findById(id);
    }

    public Page createPage(String workspaceId, String parentId, String title, String icon) {
        String id = "page_" + UUID.randomUUID().toString().substring(0, 8);
        Page page = new Page(id, workspaceId, parentId, title, icon);
        pageRepository.save(page);

        // Add a default introductory paragraph block so the user can start typing immediately
        PageBlock initialBlock = new PageBlock("b_" + UUID.randomUUID().toString().substring(0, 8), id, BlockType.PARAGRAPH, "", 0);
        blockRepository.save(initialBlock);

        return page;
    }

    public Page duplicatePage(String sourcePageId) {
        Optional<Page> opt = pageRepository.findById(sourcePageId);
        if (opt.isEmpty()) return null;

        Page source = opt.get();
        String newId = "page_" + UUID.randomUUID().toString().substring(0, 8);
        Page clone = new Page(newId, source.getWorkspaceId(), source.getParentId(), source.getTitle() + " (Copy)", source.getIcon());
        pageRepository.save(clone);

        List<PageBlock> sourceBlocks = blockRepository.findByPageId(sourcePageId);
        List<PageBlock> clonedBlocks = new ArrayList<>();
        for (int i = 0; i < sourceBlocks.size(); i++) {
            PageBlock sb = sourceBlocks.get(i);
            PageBlock cb = new PageBlock("b_" + UUID.randomUUID().toString().substring(0, 8), newId, sb.getBlockType(), sb.getContent(), i);
            cb.setProperties(sb.getProperties());
            clonedBlocks.add(cb);
        }
        blockRepository.saveAllForPage(newId, clonedBlocks);

        return clone;
    }

    public void updateTitleAndIcon(String pageId, String title, String icon) {
        pageRepository.updateTitleAndIcon(pageId, title, icon);
    }

    public void toggleFavorite(String pageId, boolean isFavorite) {
        pageRepository.setFavorite(pageId, isFavorite);
    }

    public void moveToTrash(String pageId) {
        pageRepository.moveToTrash(pageId);
    }

    public void restoreFromTrash(String pageId) {
        pageRepository.restoreFromTrash(pageId);
    }

    public void deletePermanently(String pageId) {
        pageRepository.deletePermanently(pageId);
    }

    public void movePage(String pageId, String targetParentId, int targetPosition) {
        Optional<Page> opt = pageRepository.findById(pageId);
        if (opt.isPresent()) {
            Page p = opt.get();
            // Prevent circular parenting
            if (targetParentId != null && targetParentId.equals(pageId)) return;
            p.setParentId(targetParentId);
            p.setPosition(targetPosition);
            pageRepository.save(p);
        }
    }
}
