package com.workspace.app.ui.editor;

import com.workspace.app.app.AppContext;
import com.workspace.app.model.*;
import com.workspace.app.ui.components.HoverButton;
import com.workspace.app.ui.components.StatusIndicator;
import com.workspace.app.ui.database.DatabaseContainerPanel;
import com.workspace.app.ui.theme.Theme;
import com.workspace.app.ui.theme.ThemeManager;
import com.workspace.app.util.Debouncer;
import com.workspace.app.util.IconFactory;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class PageEditorPanel extends JPanel {
    private final AppContext context;
    private final Runnable onExportRequested;

    private Page currentPage;
    private final List<PageBlock> blocks = new ArrayList<>();
    private final List<BlockEditorComponent> blockComponents = new ArrayList<>();

    private PageHeaderPanel headerPanel;
    private JPanel blocksContainer;
    private JScrollPane scrollPane;
    private JPanel commentsPanel;
    private boolean commentsVisible = false;

    private final StatusIndicator statusIndicator = new StatusIndicator();
    private final Debouncer autosaveDebouncer = new Debouncer(1500);

    public PageEditorPanel(AppContext context, Runnable onExportRequested) {
        this.context = context;
        this.onExportRequested = onExportRequested;

        setLayout(new BorderLayout());
        setOpaque(true);

        buildUI();
        applyThemeStyles();
        ThemeManager.addThemeChangeListener(this::applyThemeStyles);
    }

    private void buildUI() {
        // Document Panel that strictly tracks viewport width to prevent horizontal overflow
        ScrollableDocumentPanel documentPanel = new ScrollableDocumentPanel();
        documentPanel.setLayout(new BoxLayout(documentPanel, BoxLayout.Y_AXIS));
        documentPanel.setOpaque(false);

        headerPanel = new PageHeaderPanel(
                context,
                statusIndicator,
                this::toggleComments,
                onExportRequested,
                this::saveNow,
                this::onTitleChanged
        );
        documentPanel.add(headerPanel);

        // Blocks container
        blocksContainer = new JPanel();
        blocksContainer.setLayout(new BoxLayout(blocksContainer, BoxLayout.Y_AXIS));
        blocksContainer.setOpaque(false);
        blocksContainer.setBorder(new EmptyBorder(0, 32, 60, 32));
        documentPanel.add(blocksContainer);

        scrollPane = new JScrollPane(documentPanel);
        scrollPane.setBorder(null);
        scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.getVerticalScrollBar().setUnitIncrement(24);
        scrollPane.getVerticalScrollBar().setBlockIncrement(64);
        add(scrollPane, BorderLayout.CENTER);

        // Right comments sidebar (initially collapsed)
        commentsPanel = new JPanel(new BorderLayout());
        commentsPanel.setPreferredSize(new Dimension(280, 600));
        commentsPanel.setVisible(false);
        add(commentsPanel, BorderLayout.EAST);
    }

    public void loadPage(Page page) {
        if (page == null) return;

        // If it's already the current page loaded, only update header without wiping editor
        if (currentPage != null && currentPage.getId().equals(page.getId())) {
            headerPanel.setPage(page);
            return;
        }

        this.currentPage = page;
        autosaveDebouncer.cancel();
        headerPanel.setPage(page);

        // Scroll to top when loading a new page
        SwingUtilities.invokeLater(() -> scrollPane.getVerticalScrollBar().setValue(0));

        statusIndicator.setState(StatusIndicator.State.SAVING);
        SwingWorker<List<PageBlock>, Void> worker = new SwingWorker<>() {
            @Override
            protected List<PageBlock> doInBackground() {
                return context.getBlockService().getBlocksForPage(page.getId());
            }

            @Override
            protected void done() {
                try {
                    blocks.clear();
                    blocks.addAll(get());
                    renderBlocks(false);
                    statusIndicator.setState(StatusIndicator.State.SAVED);
                    SwingUtilities.invokeLater(() -> scrollPane.getVerticalScrollBar().setValue(0));
                } catch (Exception e) {
                    statusIndicator.setState(StatusIndicator.State.ERROR);
                }
            }
        };
        worker.execute();

        reloadComments();
    }

    private void renderBlocks(boolean preserveScroll) {
        int savedScroll = preserveScroll ? scrollPane.getVerticalScrollBar().getValue() : 0;

        blocksContainer.removeAll();
        blockComponents.clear();

        // Check if there is an associated Database for this page
        Optional<DatabaseDefinition> dbOpt = context.getDatabaseEntityService().getDatabaseForPage(currentPage.getId());
        if (dbOpt.isPresent()) {
            DatabaseContainerPanel dbPanel = new DatabaseContainerPanel(context, dbOpt.get());
            blocksContainer.add(dbPanel);
            blocksContainer.add(Box.createVerticalStrut(16));
        }

        // Render each block
        for (int i = 0; i < blocks.size(); i++) {
            PageBlock b = blocks.get(i);
            b.setPosition(i);
            BlockEditorComponent comp = new BlockEditorComponent(
                    b,
                    this::triggerAutosave,
                    this::handleAddNextBlock,
                    this::handleDeleteBlock,
                    this::handleFocusPrevious,
                    this::handleFocusNext
            );
            blockComponents.add(comp);
            blocksContainer.add(comp);
        }

        // Bottom click-to-add blank block area
        JPanel bottomEmptySpace = new JPanel();
        bottomEmptySpace.setOpaque(false);
        bottomEmptySpace.setPreferredSize(new Dimension(100, 60));
        bottomEmptySpace.setMaximumSize(new Dimension(Integer.MAX_VALUE, 60));
        bottomEmptySpace.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                if (blockComponents.isEmpty() || !blocks.get(blocks.size() - 1).getContent().isEmpty()) {
                    addNewBlockAtEnd();
                }
            }
        });
        blocksContainer.add(bottomEmptySpace);

        blocksContainer.revalidate();
        blocksContainer.repaint();

        if (preserveScroll) {
            SwingUtilities.invokeLater(() -> scrollPane.getVerticalScrollBar().setValue(savedScroll));
        }
    }

    private void handleAddNextBlock(BlockEditorComponent current) {
        int index = blockComponents.indexOf(current);
        int insertPos = (index >= 0) ? index + 1 : blocks.size();

        PageBlock newBlock = context.getBlockService().createBlockAfter(currentPage.getId(), BlockType.PARAGRAPH, insertPos);
        blocks.add(insertPos, newBlock);

        renderBlocks(true);
        triggerAutosave();

        if (insertPos < blockComponents.size()) {
            blockComponents.get(insertPos).focusInput();
        }
    }

    private void handleDeleteBlock(BlockEditorComponent current) {
        if (blocks.size() <= 1) return; // Keep at least one block

        int index = blockComponents.indexOf(current);
        if (index >= 0) {
            blocks.remove(index);
            renderBlocks(true);
            triggerAutosave();

            int focusIdx = Math.max(0, index - 1);
            if (focusIdx < blockComponents.size()) {
                blockComponents.get(focusIdx).focusInput();
            }
        }
    }

    private void handleFocusPrevious(BlockEditorComponent current) {
        int idx = blockComponents.indexOf(current);
        if (idx > 0) {
            blockComponents.get(idx - 1).focusInput();
        }
    }

    private void handleFocusNext(BlockEditorComponent current) {
        int idx = blockComponents.indexOf(current);
        if (idx >= 0 && idx < blockComponents.size() - 1) {
            blockComponents.get(idx + 1).focusInput();
        }
    }

    private void addNewBlockAtEnd() {
        PageBlock newBlock = context.getBlockService().createBlockAfter(currentPage.getId(), BlockType.PARAGRAPH, blocks.size());
        blocks.add(newBlock);
        renderBlocks(true);
        triggerAutosave();
        if (!blockComponents.isEmpty()) {
            blockComponents.get(blockComponents.size() - 1).focusInput();
        }
    }

    public void triggerAutosave() {
        statusIndicator.setState(StatusIndicator.State.SAVING);
        autosaveDebouncer.debounce(this::saveNow);
    }

    private void onTitleChanged() {
        triggerAutosave();
        // Update tree label only
        context.notifyTreeRefresh();
    }

    public void saveNow() {
        if (currentPage == null) return;

        // Persist page title & icon
        context.getPageService().updateTitleAndIcon(currentPage.getId(), currentPage.getTitle(), currentPage.getIcon());

        // Persist blocks in background SwingWorker
        SwingWorker<Void, Void> worker = new SwingWorker<>() {
            @Override
            protected Void doInBackground() {
                context.getBlockService().saveAll(currentPage.getId(), new ArrayList<>(blocks));
                return null;
            }

            @Override
            protected void done() {
                try {
                    get();
                    statusIndicator.setState(StatusIndicator.State.SAVED);
                } catch (Exception e) {
                    statusIndicator.setState(StatusIndicator.State.ERROR);
                }
            }
        };
        worker.execute();
    }

    private void toggleComments() {
        commentsVisible = !commentsVisible;
        commentsPanel.setVisible(commentsVisible);
        if (commentsVisible) reloadComments();
        revalidate();
        repaint();
    }

    private void reloadComments() {
        if (currentPage == null) return;
        commentsPanel.removeAll();
        Theme theme = ThemeManager.getTheme();
        commentsPanel.setBackground(theme.bgCard);
        commentsPanel.setBorder(BorderFactory.createMatteBorder(0, 1, 0, 0, theme.border));

        // Header
        JPanel head = new JPanel(new BorderLayout());
        head.setOpaque(false);
        head.setBorder(new EmptyBorder(12, 12, 12, 12));
        JLabel title = new JLabel("Page Discussion");
        title.setFont(Theme.FONT_BODY.deriveFont(Font.BOLD));
        title.setForeground(theme.textPrimary);
        head.add(title, BorderLayout.WEST);

        HoverButton closeBtn = new HoverButton(IconFactory.createCloseIcon(12, theme.textSecondary));
        closeBtn.setPreferredSize(new Dimension(24, 24));
        closeBtn.setToolTipText("Close discussion panel");
        closeBtn.addActionListener(e -> toggleComments());
        head.add(closeBtn, BorderLayout.EAST);
        commentsPanel.add(head, BorderLayout.NORTH);

        // Comments list
        JPanel list = new JPanel();
        list.setLayout(new BoxLayout(list, BoxLayout.Y_AXIS));
        list.setOpaque(false);
        list.setBorder(new EmptyBorder(8, 12, 8, 12));

        List<Comment> comments = context.getCommentRepository().findByPageId(currentPage.getId());
        for (Comment c : comments) {
            JPanel card = new JPanel(new BorderLayout(4, 4));
            card.setOpaque(true);
            card.setBackground(theme.bgHover);
            card.setBorder(new EmptyBorder(8, 8, 8, 8));

            JLabel author = new JLabel(c.getAuthorName());
            author.setFont(Theme.FONT_SMALL.deriveFont(Font.BOLD));
            author.setForeground(theme.textPrimary);
            card.add(author, BorderLayout.NORTH);

            JTextArea body = new JTextArea(c.getContent());
            body.setFont(Theme.FONT_SMALL);
            body.setWrapStyleWord(true);
            body.setLineWrap(true);
            body.setEditable(false);
            body.setOpaque(false);
            body.setForeground(theme.textSecondary);
            card.add(body, BorderLayout.CENTER);

            list.add(card);
            list.add(Box.createVerticalStrut(8));
        }

        JScrollPane scroll = new JScrollPane(list);
        scroll.setBorder(null);
        scroll.setOpaque(false);
        scroll.getViewport().setOpaque(false);
        commentsPanel.add(scroll, BorderLayout.CENTER);

        // Add Comment Input
        JPanel inputPanel = new JPanel(new BorderLayout(6, 0));
        inputPanel.setOpaque(false);
        inputPanel.setBorder(new EmptyBorder(8, 12, 12, 12));

        JTextField input = new JTextField();
        input.putClientProperty("JTextField.placeholderText", "Write a comment...");
        inputPanel.add(input, BorderLayout.CENTER);

        HoverButton sendBtn = new HoverButton("Post");
        sendBtn.addActionListener(e -> {
            String text = input.getText().trim();
            if (!text.isEmpty()) {
                String id = "cmt_" + java.util.UUID.randomUUID().toString().substring(0, 8);
                Comment comment = new Comment(id, currentPage.getId(), "Swara", text);
                context.getCommentRepository().save(comment);
                input.setText("");
                reloadComments();
            }
        });
        inputPanel.add(sendBtn, BorderLayout.EAST);
        commentsPanel.add(inputPanel, BorderLayout.SOUTH);

        commentsPanel.revalidate();
        commentsPanel.repaint();
    }

    public Page getCurrentPage() {
        return currentPage;
    }

    public List<PageBlock> getBlocks() {
        return blocks;
    }

    private void applyThemeStyles() {
        Theme theme = ThemeManager.getTheme();
        setBackground(theme.bgMain);
        if (scrollPane != null) {
            scrollPane.getViewport().setBackground(theme.bgMain);
        }
        repaint();
    }

    /**
     * Custom panel implementing Scrollable to force matching viewport width.
     * This permanently prevents horizontal scrollbars and eliminates overflow.
     */
    private static class ScrollableDocumentPanel extends JPanel implements Scrollable {
        @Override
        public Dimension getPreferredScrollableViewportSize() {
            return getPreferredSize();
        }

        @Override
        public int getScrollableUnitIncrement(Rectangle visibleRect, int orientation, int direction) {
            return 24;
        }

        @Override
        public int getScrollableBlockIncrement(Rectangle visibleRect, int orientation, int direction) {
            return 64;
        }

        @Override
        public boolean getScrollableTracksViewportWidth() {
            return true; // Strict viewport width tracking!
        }

        @Override
        public boolean getScrollableTracksViewportHeight() {
            return false;
        }
    }
}
