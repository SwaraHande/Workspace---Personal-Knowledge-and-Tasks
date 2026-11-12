package com.workspace.app;

import com.workspace.app.model.BlockType;
import com.workspace.app.model.DatabaseProperty;
import com.workspace.app.model.DatabaseRow;
import com.workspace.app.model.Page;
import com.workspace.app.model.PageBlock;
import com.workspace.app.model.PropertyType;
import com.workspace.app.service.ImportExportService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

public class ImportExportServiceTest {
    private ImportExportService service;

    @BeforeEach
    void setUp() {
        service = new ImportExportService();
    }

    @Test
    void testExportPageToMarkdown() {
        Page page = new Page("p1", "ws1", null, "Project Roadmap", "🚀");
        List<PageBlock> blocks = new ArrayList<>();
        blocks.add(new PageBlock("b1", "p1", BlockType.HEADING_1, "Milestone 1", 0));
        blocks.add(new PageBlock("b2", "p1", BlockType.BULLET_LIST, "Feature A", 1));
        PageBlock todo = new PageBlock("b3", "p1", BlockType.TODO, "Testing task", 2);
        todo.setChecked(true);
        blocks.add(todo);

        String md = service.exportPageToMarkdown(page, blocks);
        assertNotNull(md);
        assertTrue(md.contains("# Project Roadmap"));
        assertTrue(md.contains("# Milestone 1"));
        assertTrue(md.contains("- Feature A"));
        assertTrue(md.contains("- [x] Testing task"));
    }

    @Test
    void testParseMarkdownToBlocks() {
        String md = "# Main Title\n## Sub heading\n- [ ] Task 1\n- Item A\n> A quote";
        List<PageBlock> blocks = service.parseMarkdownToBlocks("page_123", md);

        assertEquals(5, blocks.size());
        assertEquals(BlockType.HEADING_1, blocks.get(0).getBlockType());
        assertEquals("Main Title", blocks.get(0).getContent());
        assertEquals(BlockType.HEADING_2, blocks.get(1).getBlockType());
        assertEquals(BlockType.TODO, blocks.get(2).getBlockType());
        assertFalse(blocks.get(2).isChecked());
        assertEquals(BlockType.BULLET_LIST, blocks.get(3).getBlockType());
        assertEquals(BlockType.QUOTE, blocks.get(4).getBlockType());
    }

    @Test
    void testExportDatabaseToCsv() {
        List<DatabaseProperty> props = List.of(
                new DatabaseProperty("pr1", "db1", "Task", PropertyType.TEXT, 0),
                new DatabaseProperty("pr2", "db1", "Status", PropertyType.SELECT, 1)
        );

        DatabaseRow row = new DatabaseRow("r1", "db1", "{}", 0);
        Map<String, Object> values = new HashMap<>();
        values.put("Task", "Setup Database");
        values.put("Status", "Done");
        row.setValues(values);

        String csv = service.exportDatabaseToCsv(props, List.of(row));
        assertTrue(csv.contains("\"Task\",\"Status\""));
        assertTrue(csv.contains("\"Setup Database\",\"Done\""));
    }
}
