package com.workspace.app;

import com.workspace.app.model.BlockType;
import com.workspace.app.model.Page;
import com.workspace.app.model.PageBlock;
import com.workspace.app.model.Workspace;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class ModelAndHierarchyTest {

    @Test
    void testPageBlockPropertiesAndTodo() {
        PageBlock block = new PageBlock("b1", "p1", BlockType.TODO, "Complete task", 0);
        assertFalse(block.isChecked());

        block.setChecked(true);
        assertTrue(block.isChecked());
        assertTrue(block.getProperties().contains("\"checked\":true"));

        block.setChecked(false);
        assertFalse(block.isChecked());
        assertTrue(block.getProperties().contains("\"checked\":false"));
    }

    @Test
    void testPageHierarchyModel() {
        Page root = new Page("root_id", "ws_1", null, "Parent Page", "📁");
        Page child1 = new Page("c1", "ws_1", "root_id", "Child 1", "📄");
        Page child2 = new Page("c2", "ws_1", "root_id", "Child 2", "📄");

        root.getChildren().add(child1);
        root.getChildren().add(child2);

        assertEquals(2, root.getChildren().size());
        assertEquals("root_id", child1.getParentId());
        assertEquals("root_id", child2.getParentId());
    }

    @Test
    void testWorkspaceDefaults() {
        Workspace ws = new Workspace("ws_1", "Engineering", null, "Dev workspace", "user_1");
        assertEquals("🚀", ws.getIcon());
        assertEquals("Engineering", ws.getName());
    }
}
