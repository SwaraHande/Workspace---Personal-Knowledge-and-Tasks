package com.workspace.app.ui;

import com.workspace.app.app.AppContext;
import com.workspace.app.model.Page;
import com.workspace.app.ui.dialogs.*;
import com.workspace.app.ui.editor.PageEditorPanel;
import com.workspace.app.ui.sidebar.SidebarPanel;
import com.workspace.app.ui.theme.Theme;
import com.workspace.app.ui.theme.ThemeManager;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

public class MainFrame extends JFrame {
    private final AppContext context;

    private SidebarPanel sidebarPanel;
    private PageEditorPanel editorPanel;
    private JSplitPane mainSplitPane;

    public MainFrame(AppContext context) {
        this.context = context;

        setTitle("Workspace — Personal Knowledge & Tasks");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1200, 800);
        setMinimumSize(new Dimension(800, 500));
        setLocationRelativeTo(null);

        buildUI();
        registerGlobalShortcuts();

        // Listen for active page changes
        context.addPageSelectionListener(this::onPageSelected);

        // Load active page if already set
        if (context.getActivePage() != null) {
            editorPanel.loadPage(context.getActivePage());
        }
    }

    private void buildUI() {
        mainSplitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        mainSplitPane.setDividerSize(1);
        mainSplitPane.setBorder(null);

        sidebarPanel = new SidebarPanel(
                context,
                this::openSearch,
                this::openTemplates,
                this::openSettings,
                this::openTrash
        );
        mainSplitPane.setLeftComponent(sidebarPanel);
        mainSplitPane.setDividerLocation(260);

        editorPanel = new PageEditorPanel(context, this::openExport);
        mainSplitPane.setRightComponent(editorPanel);

        getContentPane().setLayout(new BorderLayout());
        getContentPane().add(mainSplitPane, BorderLayout.CENTER);
    }

    private void onPageSelected(Page page) {
        if (page != null) {
            setTitle(page.getTitle() + " — Workspace");
            editorPanel.loadPage(page);
        }
    }

    private void registerGlobalShortcuts() {
        JRootPane root = getRootPane();

        // Ctrl+N: New Page
        root.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(
                KeyStroke.getKeyStroke(KeyEvent.VK_N, Toolkit.getDefaultToolkit().getMenuShortcutKeyMaskEx()), "newPage"
        );
        root.getActionMap().put("newPage", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                sidebarPanel.createNewTopLevelPage();
            }
        });

        // Ctrl+P: Quick Search
        root.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(
                KeyStroke.getKeyStroke(KeyEvent.VK_P, Toolkit.getDefaultToolkit().getMenuShortcutKeyMaskEx()), "quickSearch"
        );
        root.getActionMap().put("quickSearch", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                openSearch();
            }
        });

        // Ctrl+F: Quick Search (Alternative)
        root.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(
                KeyStroke.getKeyStroke(KeyEvent.VK_F, Toolkit.getDefaultToolkit().getMenuShortcutKeyMaskEx()), "quickSearchF"
        );
        root.getActionMap().put("quickSearchF", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                openSearch();
            }
        });

        // Ctrl+S: Save Immediately
        root.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(
                KeyStroke.getKeyStroke(KeyEvent.VK_S, Toolkit.getDefaultToolkit().getMenuShortcutKeyMaskEx()), "saveNow"
        );
        root.getActionMap().put("saveNow", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                editorPanel.saveNow();
            }
        });
    }

    public void openSearch() {
        QuickSearchDialog dialog = new QuickSearchDialog(this, context);
        dialog.setVisible(true);
    }

    public void openTemplates() {
        TemplatesDialog dialog = new TemplatesDialog(this, context);
        dialog.setVisible(true);
    }

    public void openSettings() {
        SettingsDialog dialog = new SettingsDialog(this, context);
        dialog.setVisible(true);
    }

    public void openTrash() {
        TrashDialog dialog = new TrashDialog(this, context);
        dialog.setVisible(true);
    }

    public void openExport() {
        if (editorPanel.getCurrentPage() != null) {
            ExportDialog dialog = new ExportDialog(this, context, editorPanel.getCurrentPage(), editorPanel.getBlocks());
            dialog.setVisible(true);
        }
    }
}
