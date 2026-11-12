package com.workspace.app.ui.editor;

import com.workspace.app.app.AppContext;
import com.workspace.app.model.Page;
import com.workspace.app.ui.components.HoverButton;
import com.workspace.app.ui.components.StatusIndicator;
import com.workspace.app.ui.theme.Theme;
import com.workspace.app.ui.theme.ThemeManager;
import com.workspace.app.util.IconFactory;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;

public class PageHeaderPanel extends JPanel {
    private final AppContext context;
    private final StatusIndicator statusIndicator;
    private final Runnable onToggleComments;
    private final Runnable onExport;
    private final Runnable onManualSave;
    private final Runnable onTitleChange;

    private Page page;
    private JLabel breadcrumbLabel;
    private HoverButton iconBtn;
    private JTextField titleField;
    private HoverButton favBtn;
    private HoverButton saveBtn;
    private HoverButton commentsBtn;
    private HoverButton exportBtn;

    public PageHeaderPanel(AppContext context,
                           StatusIndicator statusIndicator,
                           Runnable onToggleComments,
                           Runnable onExport,
                           Runnable onManualSave,
                           Runnable onTitleChange) {
        this.context = context;
        this.statusIndicator = statusIndicator;
        this.onToggleComments = onToggleComments;
        this.onExport = onExport;
        this.onManualSave = onManualSave;
        this.onTitleChange = onTitleChange;

        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setOpaque(false);
        setBorder(new EmptyBorder(12, 32, 12, 32));

        buildUI();
    }

    private void buildUI() {
        Theme theme = ThemeManager.getTheme();

        // Top Action & Breadcrumb Bar
        JPanel topBar = new JPanel(new BorderLayout(12, 0));
        topBar.setOpaque(false);
        topBar.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));

        breadcrumbLabel = new JLabel("Workspace / Untitled");
        breadcrumbLabel.setFont(Theme.FONT_SMALL);
        breadcrumbLabel.setForeground(theme.textSecondary);
        topBar.add(breadcrumbLabel, BorderLayout.WEST);

        // Right tools: Save Button, Status Indicator, Favorite, Comments, Export
        JPanel toolsPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 6, 0));
        toolsPanel.setOpaque(false);

        // Explicit Save button - prominent and fully visible
        saveBtn = new HoverButton("Save", IconFactory.createSaveIcon(14, theme.textPrimary));
        saveBtn.setFont(Theme.FONT_SMALL.deriveFont(Font.BOLD));
        saveBtn.setToolTipText("Save immediately (Ctrl+S)");
        saveBtn.addActionListener(e -> onManualSave.run());
        toolsPanel.add(saveBtn);

        // Status indicator right beside save button
        statusIndicator.setPreferredSize(new Dimension(85, 28));
        toolsPanel.add(statusIndicator);

        favBtn = new HoverButton(IconFactory.createStarIcon(false, 15, theme.textSecondary));
        favBtn.setToolTipText("Toggle favorite");
        favBtn.addActionListener(e -> {
            if (page != null) {
                boolean newFav = !page.isFavorite();
                page.setFavorite(newFav);
                context.getPageService().toggleFavorite(page.getId(), newFav);
                updateFavIcon(newFav);
                context.notifyTreeRefresh();
            }
        });
        toolsPanel.add(favBtn);

        commentsBtn = new HoverButton("Discussion", IconFactory.createDiscussionIcon(14, theme.textSecondary));
        commentsBtn.setFont(Theme.FONT_SMALL);
        commentsBtn.setToolTipText("Open page discussion");
        commentsBtn.addActionListener(e -> onToggleComments.run());
        toolsPanel.add(commentsBtn);

        exportBtn = new HoverButton("Export", IconFactory.createExportIcon(14, theme.textSecondary));
        exportBtn.setFont(Theme.FONT_SMALL);
        exportBtn.setToolTipText("Export as Markdown, HTML, or Text");
        exportBtn.addActionListener(e -> onExport.run());
        toolsPanel.add(exportBtn);

        topBar.add(toolsPanel, BorderLayout.EAST);
        add(topBar);

        add(Box.createVerticalStrut(12));

        // Page Icon & Title
        JPanel titlePanel = new JPanel(new BorderLayout(12, 0));
        titlePanel.setOpaque(false);
        titlePanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 50));

        iconBtn = new HoverButton(IconFactory.createDocumentIcon(28, theme.textPrimary));
        iconBtn.setToolTipText("Click to choose an icon");
        iconBtn.setPreferredSize(new Dimension(42, 42));
        iconBtn.addActionListener(e -> showIconPicker(iconBtn));
        titlePanel.add(iconBtn, BorderLayout.WEST);

        titleField = new JTextField("Untitled");
        titleField.setFont(Theme.FONT_TITLE.deriveFont(24f));
        titleField.setOpaque(false);
        titleField.setBorder(null);
        titleField.setForeground(theme.textPrimary);
        titleField.setCaretColor(theme.textPrimary);
        titleField.putClientProperty("JTextField.placeholderText", "Untitled");

        titleField.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e) { syncTitle(); }
            public void removeUpdate(DocumentEvent e) { syncTitle(); }
            public void changedUpdate(DocumentEvent e) { syncTitle(); }
            private void syncTitle() {
                if (page != null) {
                    page.setTitle(titleField.getText().trim());
                    onTitleChange.run();
                }
            }
        });

        titlePanel.add(titleField, BorderLayout.CENTER);
        add(titlePanel);

        add(Box.createVerticalStrut(10));
    }

    public void setPage(Page page) {
        this.page = page;
        if (page != null) {
            String wsName = context.getCurrentWorkspace() != null ? context.getCurrentWorkspace().getName() : "Workspace";
            breadcrumbLabel.setText(wsName + "  /  " + page.getTitle());
            titleField.setText(page.getTitle());
            updateFavIcon(page.isFavorite());
        }
    }

    private void updateFavIcon(boolean isFavorite) {
        Theme theme = ThemeManager.getTheme();
        if (isFavorite) {
            favBtn.setIcon(IconFactory.createStarIcon(true, 15, new Color(0xF5, 0xA6, 0x23)));
        } else {
            favBtn.setIcon(IconFactory.createStarIcon(false, 15, theme.textSecondary));
        }
    }

    private void showIconPicker(Component invoker) {
        JPopupMenu popup = new JPopupMenu();
        JPanel grid = new JPanel(new GridLayout(2, 4, 4, 4));
        grid.setOpaque(false);
        grid.setBorder(new EmptyBorder(6, 6, 6, 6));

        String[] sampleNames = {"Document", "Star", "Template", "Discussion", "Search", "Settings", "Table", "Save"};
        Icon[] sampleIcons = {
                IconFactory.createDocumentIcon(18, Color.GRAY),
                IconFactory.createStarIcon(true, 18, new Color(0xF5, 0xA6, 0x23)),
                IconFactory.createTemplateIcon(18, Color.GRAY),
                IconFactory.createDiscussionIcon(18, Color.GRAY),
                IconFactory.createSearchIcon(18, Color.GRAY),
                IconFactory.createSettingsIcon(18, Color.GRAY),
                IconFactory.createTableIcon(18, Color.GRAY),
                IconFactory.createSaveIcon(18, Color.GRAY)
        };

        for (int i = 0; i < sampleNames.length; i++) {
            Icon ic = sampleIcons[i];
            JButton btn = new JButton(ic);
            btn.setContentAreaFilled(false);
            btn.setFocusPainted(false);
            btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            btn.addActionListener(e -> {
                iconBtn.setIcon(ic);
                popup.setVisible(false);
            });
            grid.add(btn);
        }
        popup.add(grid);
        popup.show(invoker, 0, invoker.getHeight());
    }
}
