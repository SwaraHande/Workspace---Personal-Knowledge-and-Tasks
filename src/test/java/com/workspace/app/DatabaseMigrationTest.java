package com.workspace.app;

import com.workspace.app.config.DatabaseConfig;
import com.workspace.app.model.Page;
import com.workspace.app.model.PageBlock;
import com.workspace.app.model.Workspace;
import com.workspace.app.repository.BlockRepository;
import com.workspace.app.repository.DatabaseMigration;
import com.workspace.app.repository.PageRepository;
import com.workspace.app.repository.WorkspaceRepository;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class DatabaseMigrationTest {

    @BeforeAll
    static void setUpDatabase() {
        try {
            assertTrue(DatabaseConfig.testConnection(), "PostgreSQL should be reachable");
            DatabaseMigration.runMigrations();
        } catch (Exception e) {
            fail("Migration failed: " + e.getMessage());
        }
    }

    @Test
    void testWorkspaceAndPagesSeeded() {
        WorkspaceRepository wsRepo = new WorkspaceRepository();
        List<Workspace> workspaces = wsRepo.findAll();
        assertFalse(workspaces.isEmpty(), "Workspaces should not be empty");

        PageRepository pageRepo = new PageRepository();
        List<Page> pages = pageRepo.findActiveByWorkspace(workspaces.get(0).getId());
        assertFalse(pages.isEmpty(), "Sample pages should be loaded");

        BlockRepository blockRepo = new BlockRepository();
        List<PageBlock> blocks = blockRepo.findByPageId(pages.get(0).getId());
        assertFalse(blocks.isEmpty(), "Blocks should be present for welcome page");
    }
}
