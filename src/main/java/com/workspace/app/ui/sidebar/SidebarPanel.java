package com.workspace.app.ui.sidebar;

import com.workspace.app.app.AppContext;
import com.workspace.app.model.Page;
import com.workspace.app.model.Workspace;
import com.workspace.app.ui.components.HoverButton;
import com.workspace.app.ui.theme.Theme;
import com.workspace.app.ui.theme.ThemeManager;
import com.workspace.app.util.IconFactory;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.tree.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;

public class SidebarPanel extends JPanel {
    private final AppContext context;
    private final Runnable onOpenSearch;
    private final Runnable onOpenTemplates;
    private final Runnable onOpenSettings;
    private final Runnable onOpenTrash;

    private JTree pageTree;
    private DefaultTreeModel treeModel;
    private DefaultMutableTreeNode rootNode;
    private JPanel favoritesPanel;
    private JLabel workspaceLabel;
    private boolean isUpdatingTree = false;

    public SidebarPanel(AppContext context,
                        Runnable onOpenSearch,
                        Runnable onOpenTemplates,
                        Runnable onOpenSettings,
                        Runnable onOpenTrash) {
        this.context = context;
        this.onOpenSearch = onOpenSearch;
        this.onOpenTemplates = onOpenTemplates;
        this.onOpenSettings = onOpenSettings;
        this.onOpenTrash = onOpenTrash;

        setLayout(new BorderLayout());
        setPreferredSize(new Dimension(240, 800));
        setMinimumSize(new Dimension(180, 400));

        buildUI();

        context.addTreeRefreshListener(this::reloadTree);
        context.addWorkspaceChangeListener(ws -> {
            updateWorkspaceLabel();
            reloadTree();
        });
        ThemeManager.addThemeChangeListener(this::applyThemeStyles);
        applyThemeStyles();
    }

    private void buildUI() {
        Theme theme = ThemeManager.getTheme();

        // TOP SECTION: Workspace Selector & Quick Nav
        JPanel topPanel = new JPanel();
        topPanel.setLayout(new BoxLayout(topPanel, BoxLayout.Y_AXIS));
        topPanel.setOpaque(false);
        topPanel.setBorder(new EmptyBorder(12, 12, 8, 12));

        // Workspace header
        JPanel wsHeader = new JPanel(new BorderLayout());
        wsHeader.setOpaque(false);
        workspaceLabel = new JLabel("Workspace");
        workspaceLabel.setFont(Theme.FONT_BODY.deriveFont(Font.BOLD, 14f));
        workspaceLabel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        updateWorkspaceLabel();

        HoverButton wsMenuBtn = new HoverButton(IconFactory.createChevronDownIcon(12, theme.textSecondary));
        wsMenuBtn.setPreferredSize(new Dimension(24, 24));
        wsMenuBtn.setToolTipText("Switch workspace");
        wsMenuBtn.addActionListener(e -> showWorkspaceSwitcher(wsMenuBtn));
        workspaceLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                showWorkspaceSwitcher(wsMenuBtn);
            }
        });
        wsHeader.add(workspaceLabel, BorderLayout.CENTER);
        wsHeader.add(wsMenuBtn, BorderLayout.EAST);
        topPanel.add(wsHeader);

        topPanel.add(Box.createVerticalStrut(10));

        // Quick Navigation buttons with clean vector icons
        HoverButton searchBtn = new HoverButton("Quick Search", IconFactory.createSearchIcon(14, theme.textSecondary));
        searchBtn.setHorizontalAlignment(SwingConstants.LEFT);
        searchBtn.setToolTipText("Search pages & blocks (Ctrl+P / Ctrl+F)");
        searchBtn.addActionListener(e -> onOpenSearch.run());

        HoverButton tmplBtn = new HoverButton("Templates", IconFactory.createTemplateIcon(14, theme.textSecondary));
        tmplBtn.setHorizontalAlignment(SwingConstants.LEFT);
        tmplBtn.setToolTipText("Create page from template");
        tmplBtn.addActionListener(e -> onOpenTemplates.run());

        HoverButton settingsBtn = new HoverButton("Settings", IconFactory.createSettingsIcon(14, theme.textSecondary));
        settingsBtn.setHorizontalAlignment(SwingConstants.LEFT);
        settingsBtn.setToolTipText("Preferences, theme, database status");
        settingsBtn.addActionListener(e -> onOpenSettings.run());

        HoverButton trashBtn = new HoverButton("Trash", IconFactory.createTrashIcon(14, theme.textSecondary));
        trashBtn.setHorizontalAlignment(SwingConstants.LEFT);
        trashBtn.setToolTipText("View archived & deleted pages");
        trashBtn.addActionListener(e -> onOpenTrash.run());

        topPanel.add(searchBtn);
        topPanel.add(Box.createVerticalStrut(2));
        topPanel.add(tmplBtn);
        topPanel.add(Box.createVerticalStrut(2));
        topPanel.add(settingsBtn);
        topPanel.add(Box.createVerticalStrut(2));
        topPanel.add(trashBtn);
        topPanel.add(Box.createVerticalStrut(12));

        // CENTER SECTION: Favorites + Pages Tree
        JPanel centerPanel = new JPanel();
        centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.Y_AXIS));
        centerPanel.setOpaque(false);

        // Favorites header
        JLabel favTitle = new JLabel("FAVORITES");
        favTitle.setFont(Theme.FONT_SMALL.deriveFont(Font.BOLD, 10f));
        favTitle.setBorder(new EmptyBorder(4, 12, 4, 12));
        centerPanel.add(favTitle);

        favoritesPanel = new JPanel();
        favoritesPanel.setLayout(new BoxLayout(favoritesPanel, BoxLayout.Y_AXIS));
        favoritesPanel.setOpaque(false);
        favoritesPanel.setBorder(new EmptyBorder(0, 8, 8, 8));
        centerPanel.add(favoritesPanel);

        // Pages header with (+) button
        JPanel pagesHeader = new JPanel(new BorderLayout());
        pagesHeader.setOpaque(false);
        pagesHeader.setBorder(new EmptyBorder(4, 12, 4, 8));
        JLabel pagesTitle = new JLabel("PAGES");
        pagesTitle.setFont(Theme.FONT_SMALL.deriveFont(Font.BOLD, 10f));
        pagesHeader.add(pagesTitle, BorderLayout.WEST);

        HoverButton addPageQuick = new HoverButton(IconFactory.createPlusIcon(12, theme.textSecondary));
        addPageQuick.setToolTipText("Add a top-level page (Ctrl+N)");
        addPageQuick.setPreferredSize(new Dimension(24, 24));
        addPageQuick.addActionListener(e -> createNewTopLevelPage());
        pagesHeader.add(addPageQuick, BorderLayout.EAST);
        centerPanel.add(pagesHeader);

        // Page JTree
        rootNode = new DefaultMutableTreeNode("Root");
        treeModel = new DefaultTreeModel(rootNode);
        pageTree = new JTree(treeModel);
        pageTree.setRootVisible(false);
        pageTree.setShowsRootHandles(true);
        pageTree.setOpaque(false);
        pageTree.setCellRenderer(new PageTreeCellRenderer());
        pageTree.setRowHeight(28);

        // Click selection
        pageTree.addTreeSelectionListener(e -> {
            if (isUpdatingTree) return; // Prevent loop during programmatic tree reload
            DefaultMutableTreeNode selected = (DefaultMutableTreeNode) pageTree.getLastSelectedPathComponent();
            if (selected != null && selected.getUserObject() instanceof Page page) {
                context.setActivePage(page);
            }
        });

        // Context menu on right click
        pageTree.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                if (SwingUtilities.isRightMouseButton(e)) {
                    int row = pageTree.getClosestRowForLocation(e.getX(), e.getY());
                    pageTree.setSelectionRow(row);
                    DefaultMutableTreeNode node = (DefaultMutableTreeNode) pageTree.getLastSelectedPathComponent();
                    if (node != null && node.getUserObject() instanceof Page p) {
                        showContextMenu(p, e.getX(), e.getY());
                    }
                }
            }
        });

        JScrollPane treeScroll = new JScrollPane(pageTree);
        treeScroll.setBorder(null);
        treeScroll.setOpaque(false);
        treeScroll.getViewport().setOpaque(false);
        treeScroll.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        centerPanel.add(treeScroll);

        // BOTTOM BAR: New Page Button
        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.setOpaque(false);
        bottomPanel.setBorder(new EmptyBorder(8, 12, 12, 12));

        HoverButton bottomNewPage = new HoverButton("New page", IconFactory.createPlusIcon(12, theme.textPrimary));
        bottomNewPage.setFont(Theme.FONT_BODY.deriveFont(Font.BOLD, 13f));
        bottomNewPage.addActionListener(e -> createNewTopLevelPage());
        bottomPanel.add(bottomNewPage, BorderLayout.CENTER);

        add(topPanel, BorderLayout.NORTH);
        add(centerPanel, BorderLayout.CENTER);
        add(bottomPanel, BorderLayout.SOUTH);

        reloadTree();
    }

    public void reloadTree() {
        Workspace ws = context.getCurrentWorkspace();
        if (ws == null) return;

        isUpdatingTree = true;
        try {
            // Reload favorites
            favoritesPanel.removeAll();
            List<Page> favorites = context.getPageService().getFavorites(ws.getId());
            for (Page fav : favorites) {
                HoverButton favBtn = new HoverButton(fav.getTitle(), IconFactory.createDocumentIcon(13, Color.GRAY));
                favBtn.setHorizontalAlignment(SwingConstants.LEFT);
                favBtn.addActionListener(e -> context.setActivePage(fav));
                favoritesPanel.add(favBtn);
                favoritesPanel.add(Box.createVerticalStrut(2));
            }
            if (favorites.isEmpty()) {
                JLabel emptyFav = new JLabel("No favorited pages");
                emptyFav.setFont(Theme.FONT_SMALL);
                emptyFav.setForeground(ThemeManager.getTheme().textMuted);
                emptyFav.setBorder(new EmptyBorder(2, 4, 2, 4));
                favoritesPanel.add(emptyFav);
            }
            favoritesPanel.revalidate();
            favoritesPanel.repaint();

            // Reload page tree
            rootNode.removeAllChildren();
            List<Page> treePages = context.getPageService().getPageTree(ws.getId());
            for (Page p : treePages) {
                DefaultMutableTreeNode node = buildTreeNode(p);
                rootNode.add(node);
            }
            treeModel.reload();

            for (int i = 0; i < pageTree.getRowCount(); i++) {
                pageTree.expandRow(i);
            }

            if (context.getActivePage() != null) {
                selectPageInTree(rootNode, context.getActivePage().getId());
            } else if (!treePages.isEmpty()) {
                context.setActivePage(treePages.get(0));
            }
        } finally {
            isUpdatingTree = false;
        }
    }

    private DefaultMutableTreeNode buildTreeNode(Page page) {
        DefaultMutableTreeNode node = new DefaultMutableTreeNode(page);
        for (Page child : page.getChildren()) {
            node.add(buildTreeNode(child));
        }
        return node;
    }

    private boolean selectPageInTree(DefaultMutableTreeNode current, String pageId) {
        if (current.getUserObject() instanceof Page p && p.getId().equals(pageId)) {
            pageTree.setSelectionPath(new TreePath(current.getPath()));
            return true;
        }
        for (int i = 0; i < current.getChildCount(); i++) {
            if (selectPageInTree((DefaultMutableTreeNode) current.getChildAt(i), pageId)) {
                return true;
            }
        }
        return false;
    }

    private void showContextMenu(Page page, int x, int y) {
        JPopupMenu menu = new JPopupMenu();

        JMenuItem addSub = new JMenuItem("Add Subpage");
        addSub.addActionListener(e -> {
            Page sub = context.getPageService().createPage(page.getWorkspaceId(), page.getId(), "Untitled Subpage", "📄");
            reloadTree();
            context.setActivePage(sub);
        });

        JMenuItem duplicate = new JMenuItem("Duplicate");
        duplicate.addActionListener(e -> {
            Page clone = context.getPageService().duplicatePage(page.getId());
            reloadTree();
            if (clone != null) context.setActivePage(clone);
        });

        JMenuItem favItem = new JMenuItem(page.isFavorite() ? "Remove from Favorites" : "Add to Favorites");
        favItem.addActionListener(e -> {
            context.getPageService().toggleFavorite(page.getId(), !page.isFavorite());
            reloadTree();
        });

        JMenuItem rename = new JMenuItem("Rename");
        rename.addActionListener(e -> {
            String newTitle = JOptionPane.showInputDialog(this, "Enter new page title:", page.getTitle());
            if (newTitle != null && !newTitle.trim().isEmpty()) {
                context.getPageService().updateTitleAndIcon(page.getId(), newTitle.trim(), page.getIcon());
                page.setTitle(newTitle.trim());
                reloadTree();
            }
        });

        JMenuItem trash = new JMenuItem("Move to Trash");
        trash.addActionListener(e -> {
            int confirm = JOptionPane.showConfirmDialog(this,
                    "Move '" + page.getTitle() + "' to Trash?",
                    "Confirm Delete", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                context.getPageService().moveToTrash(page.getId());
                reloadTree();
            }
        });

        menu.add(addSub);
        menu.add(duplicate);
        menu.add(favItem);
        menu.add(rename);
        menu.addSeparator();
        menu.add(trash);

        menu.show(pageTree, x, y);
    }

    private void showWorkspaceSwitcher(Component invoker) {
        JPopupMenu popup = new JPopupMenu();
        List<Workspace> workspaces = context.getWorkspaceService().getAllWorkspaces();
        for (Workspace ws : workspaces) {
            JMenuItem item = new JMenuItem(ws.getName());
            item.addActionListener(e -> context.setCurrentWorkspace(ws));
            popup.add(item);
        }
        popup.addSeparator();
        JMenuItem createWs = new JMenuItem("+ Create New Workspace");
        createWs.addActionListener(e -> {
            String name = JOptionPane.showInputDialog(this, "Workspace Name:", "Create Workspace", JOptionPane.PLAIN_MESSAGE);
            if (name != null && !name.trim().isEmpty()) {
                Workspace created = context.getWorkspaceService().createWorkspace(name.trim(), "🚀");
                context.setCurrentWorkspace(created);
            }
        });
        popup.add(createWs);
        popup.show(invoker, 0, invoker.getHeight());
    }

    private void updateWorkspaceLabel() {
        Workspace ws = context.getCurrentWorkspace();
        if (ws != null) {
            workspaceLabel.setText(ws.getName());
        }
    }

    public void createNewTopLevelPage() {
        Workspace ws = context.getCurrentWorkspace();
        if (ws != null) {
            Page newPage = context.getPageService().createPage(ws.getId(), null, "Untitled", "📄");
            reloadTree();
            context.setActivePage(newPage);
        }
    }

    private void applyThemeStyles() {
        Theme theme = ThemeManager.getTheme();
        setBackground(theme.bgSidebar);
        setBorder(BorderFactory.createMatteBorder(0, 0, 0, 1, theme.border));
        repaint();
    }

    private static class PageTreeCellRenderer extends DefaultTreeCellRenderer {
        @Override
        public Component getTreeCellRendererComponent(JTree tree, Object value, boolean sel,
                                                      boolean expanded, boolean leaf, int row, boolean hasFocus) {
            JLabel label = (JLabel) super.getTreeCellRendererComponent(tree, value, sel, expanded, leaf, row, hasFocus);
            Theme theme = ThemeManager.getTheme();

            if (value instanceof DefaultMutableTreeNode node && node.getUserObject() instanceof Page page) {
                label.setText(" " + page.getTitle());
                label.setIcon(IconFactory.createDocumentIcon(14, sel ? theme.accent : theme.textSecondary));
                label.setFont(Theme.FONT_BODY);
            }

            label.setOpaque(sel);
            label.setBackground(sel ? theme.bgActive : theme.bgSidebar);
            label.setForeground(sel ? theme.textPrimary : theme.textSecondary);
            label.setBorder(new EmptyBorder(2, 6, 2, 6));
            return label;
        }
    }
}
