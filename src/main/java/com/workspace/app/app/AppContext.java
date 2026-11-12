package com.workspace.app.app;

import com.workspace.app.model.Page;
import com.workspace.app.model.UserSettings;
import com.workspace.app.model.Workspace;
import com.workspace.app.repository.*;
import com.workspace.app.service.*;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class AppContext {
    private static AppContext instance;

    private final WorkspaceService workspaceService;
    private final PageService pageService;
    private final BlockService blockService;
    private final SearchService searchService;
    private final DatabaseEntityService databaseEntityService;
    private final ImportExportService importExportService;
    private final CommentRepository commentRepository;
    private final TemplateRepository templateRepository;
    private final UserSettingsRepository userSettingsRepository;

    private Workspace currentWorkspace;
    private Page activePage;
    private UserSettings userSettings;

    // Listeners for reactive updates
    private final List<Consumer<Page>> pageSelectionListeners = new ArrayList<>();
    private final List<Runnable> treeRefreshListeners = new ArrayList<>();
    private final List<Consumer<Workspace>> workspaceChangeListeners = new ArrayList<>();

    public AppContext() {
        WorkspaceRepository wsRepo = new WorkspaceRepository();
        PageRepository pageRepo = new PageRepository();
        BlockRepository blockRepo = new BlockRepository();
        DatabaseViewRepository dbRepo = new DatabaseViewRepository();

        this.workspaceService = new WorkspaceService(wsRepo);
        this.pageService = new PageService(pageRepo, blockRepo);
        this.blockService = new BlockService(blockRepo);
        this.searchService = new SearchService(pageRepo);
        this.databaseEntityService = new DatabaseEntityService(dbRepo);
        this.importExportService = new ImportExportService();
        this.commentRepository = new CommentRepository();
        this.templateRepository = new TemplateRepository();
        this.userSettingsRepository = new UserSettingsRepository();

        initDefaults();
    }

    private void initDefaults() {
        List<Workspace> workspaces = workspaceService.getAllWorkspaces();
        if (!workspaces.isEmpty()) {
            this.currentWorkspace = workspaces.get(0);
        }
        this.userSettings = userSettingsRepository.findByUserId("user_default")
                .orElseGet(UserSettings::new);
    }

    public static synchronized AppContext getInstance() {
        if (instance == null) {
            instance = new AppContext();
        }
        return instance;
    }

    public WorkspaceService getWorkspaceService() { return workspaceService; }
    public PageService getPageService() { return pageService; }
    public BlockService getBlockService() { return blockService; }
    public SearchService getSearchService() { return searchService; }
    public DatabaseEntityService getDatabaseEntityService() { return databaseEntityService; }
    public ImportExportService getImportExportService() { return importExportService; }
    public CommentRepository getCommentRepository() { return commentRepository; }
    public TemplateRepository getTemplateRepository() { return templateRepository; }
    public UserSettingsRepository getUserSettingsRepository() { return userSettingsRepository; }

    public Workspace getCurrentWorkspace() { return currentWorkspace; }
    public void setCurrentWorkspace(Workspace currentWorkspace) {
        this.currentWorkspace = currentWorkspace;
        for (Consumer<Workspace> l : workspaceChangeListeners) l.accept(currentWorkspace);
        notifyTreeRefresh();
    }

    public Page getActivePage() { return activePage; }
    public void setActivePage(Page activePage) {
        this.activePage = activePage;
        for (Consumer<Page> listener : pageSelectionListeners) {
            listener.accept(activePage);
        }
    }

    public UserSettings getUserSettings() { return userSettings; }
    public void saveUserSettings(UserSettings settings) {
        this.userSettings = settings;
        userSettingsRepository.save(settings);
    }

    public void addPageSelectionListener(Consumer<Page> listener) {
        pageSelectionListeners.add(listener);
    }

    public void addTreeRefreshListener(Runnable listener) {
        treeRefreshListeners.add(listener);
    }

    public void addWorkspaceChangeListener(Consumer<Workspace> listener) {
        workspaceChangeListeners.add(listener);
    }

    public void notifyTreeRefresh() {
        for (Runnable r : treeRefreshListeners) {
            r.run();
        }
    }
}
