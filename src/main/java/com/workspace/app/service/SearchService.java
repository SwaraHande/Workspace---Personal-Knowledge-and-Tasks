package com.workspace.app.service;

import com.workspace.app.model.Page;
import com.workspace.app.repository.PageRepository;

import java.util.List;

public class SearchService {
    private final PageRepository pageRepository;

    public SearchService(PageRepository pageRepository) {
        this.pageRepository = pageRepository;
    }

    public List<Page> search(String workspaceId, String query) {
        if (query == null || query.trim().isEmpty()) {
            return pageRepository.findActiveByWorkspace(workspaceId);
        }
        return pageRepository.searchPages(workspaceId, query.trim());
    }
}
