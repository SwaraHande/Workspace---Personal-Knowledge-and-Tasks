package com.workspace.app.ui.editor;

import com.workspace.app.model.BlockType;
import com.workspace.app.model.PageBlock;
import com.workspace.app.ui.components.HoverButton;
import com.workspace.app.ui.theme.Theme;
import com.workspace.app.ui.theme.ThemeManager;
import com.workspace.app.util.IconFactory;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.awt.datatransfer.StringSelection;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.function.Consumer;

public class BlockEditorComponent extends JPanel {
    private final PageBlock block;
    private final Runnable onChange;
    private final Consumer<BlockEditorComponent> onAddNext;
    private final Consumer<BlockEditorComponent> onDelete;
    private final Consumer<BlockEditorComponent> onFocusPrevious;
    private final Consumer<BlockEditorComponent> onFocusNext;

    private JComponent editorInput;
    private JCheckBox todoCheckBox;
    private HoverButton toggleBtn;
    private boolean toggleExpanded = true;
    private JPanel toggleContentPanel;

    public BlockEditorComponent(PageBlock block,
                                Runnable onChange,
                                Consumer<BlockEditorComponent> onAddNext,
                                Consumer<BlockEditorComponent> onDelete,
                                Consumer<BlockEditorComponent> onFocusPrevious,
                                Consumer<BlockEditorComponent> onFocusNext) {
        this.block = block;
        this.onChange = onChange;
        this.onAddNext = onAddNext;
        this.onDelete = onDelete;
        this.onFocusPrevious = onFocusPrevious;
        this.onFocusNext = onFocusNext;

        setLayout(new BorderLayout(6, 0));
        setOpaque(false);
        setBorder(new EmptyBorder(2, 0, 2, 0));

        buildUI();
    }

    private void buildUI() {
        removeAll();
        Theme theme = ThemeManager.getTheme();

        // Left Handle Gutter - fixed size, always present, never triggers layout shifts on hover
        JPanel handleGutter = new JPanel(new FlowLayout(FlowLayout.RIGHT, 1, 2));
        handleGutter.setOpaque(false);
        handleGutter.setPreferredSize(new Dimension(36, 24));

        HoverButton addBtn = new HoverButton(IconFactory.createPlusIcon(10, theme.textMuted));
        addBtn.setToolTipText("Click to add block below");
        addBtn.setPreferredSize(new Dimension(16, 20));
        addBtn.setMargin(new Insets(2, 2, 2, 2));
        addBtn.addActionListener(e -> {
            SlashCommandPopup popup = new SlashCommandPopup(newType -> {
                block.setBlockType(newType);
                buildUI();
                revalidate();
                repaint();
                onChange.run();
                onAddNext.accept(this);
            });
            popup.showPopup(addBtn, 0, addBtn.getHeight());
        });

        HoverButton menuBtn = new HoverButton(IconFactory.createDragHandleIcon(12, theme.textMuted));
        menuBtn.setToolTipText("Block options");
        menuBtn.setPreferredSize(new Dimension(16, 20));
        menuBtn.setMargin(new Insets(2, 2, 2, 2));
        menuBtn.addActionListener(e -> showBlockOptionsMenu(menuBtn));

        handleGutter.add(addBtn);
        handleGutter.add(menuBtn);
        add(handleGutter, BorderLayout.WEST);

        // Center Content depending on block type
        JPanel contentPanel = new JPanel(new BorderLayout(6, 0));
        contentPanel.setOpaque(false);

        switch (block.getBlockType()) {
            case HEADING_1 -> {
                JTextArea ta = createWrappingArea(Theme.FONT_H1, false);
                editorInput = ta;
                contentPanel.add(ta, BorderLayout.CENTER);
            }
            case HEADING_2 -> {
                JTextArea ta = createWrappingArea(Theme.FONT_H2, false);
                editorInput = ta;
                contentPanel.add(ta, BorderLayout.CENTER);
            }
            case HEADING_3 -> {
                JTextArea ta = createWrappingArea(Theme.FONT_H3, false);
                editorInput = ta;
                contentPanel.add(ta, BorderLayout.CENTER);
            }
            case BULLET_LIST -> {
                JLabel bullet = new JLabel("• ");
                bullet.setFont(Theme.FONT_H2);
                bullet.setForeground(theme.textSecondary);
                contentPanel.add(bullet, BorderLayout.WEST);
                JTextArea ta = createWrappingArea(Theme.FONT_BODY, false);
                editorInput = ta;
                contentPanel.add(ta, BorderLayout.CENTER);
            }
            case NUMBERED_LIST -> {
                JLabel num = new JLabel((block.getPosition() + 1) + ". ");
                num.setFont(Theme.FONT_BODY.deriveFont(Font.BOLD));
                num.setForeground(theme.textSecondary);
                contentPanel.add(num, BorderLayout.WEST);
                JTextArea ta = createWrappingArea(Theme.FONT_BODY, false);
                editorInput = ta;
                contentPanel.add(ta, BorderLayout.CENTER);
            }
            case TODO -> {
                todoCheckBox = new JCheckBox();
                todoCheckBox.setOpaque(false);
                todoCheckBox.setSelected(block.isChecked());
                todoCheckBox.addActionListener(e -> {
                    block.setChecked(todoCheckBox.isSelected());
                    updateTodoStrikethrough();
                    onChange.run();
                });
                contentPanel.add(todoCheckBox, BorderLayout.WEST);
                JTextArea ta = createWrappingArea(Theme.FONT_BODY, false);
                editorInput = ta;
                updateTodoStrikethrough();
                contentPanel.add(ta, BorderLayout.CENTER);
            }
            case TOGGLE -> {
                toggleBtn = new HoverButton(IconFactory.createChevronRightIcon(10, theme.textSecondary));
                toggleBtn.setPreferredSize(new Dimension(22, 22));
                toggleBtn.addActionListener(e -> {
                    toggleExpanded = !toggleExpanded;
                    toggleBtn.setIcon(toggleExpanded ? IconFactory.createChevronDownIcon(10, theme.textSecondary) : IconFactory.createChevronRightIcon(10, theme.textSecondary));
                    if (toggleContentPanel != null) {
                        toggleContentPanel.setVisible(toggleExpanded);
                    }
                    revalidate();
                    repaint();
                });
                contentPanel.add(toggleBtn, BorderLayout.WEST);
                JTextArea ta = createWrappingArea(Theme.FONT_BODY.deriveFont(Font.BOLD), false);
                editorInput = ta;
                contentPanel.add(ta, BorderLayout.CENTER);
            }
            case QUOTE -> {
                JPanel quotePanel = new JPanel(new BorderLayout(8, 0));
                quotePanel.setOpaque(false);
                quotePanel.setBorder(BorderFactory.createMatteBorder(0, 3, 0, 0, theme.textPrimary));
                JTextArea ta = createWrappingArea(Theme.FONT_BODY.deriveFont(Font.ITALIC), false);
                ta.setBorder(new EmptyBorder(2, 8, 2, 0));
                editorInput = ta;
                quotePanel.add(ta, BorderLayout.CENTER);
                contentPanel.add(quotePanel, BorderLayout.CENTER);
            }
            case CALLOUT -> {
                JPanel calloutCard = new JPanel(new BorderLayout(8, 0));
                calloutCard.setOpaque(true);
                calloutCard.setBackground(theme.bgCallout);
                calloutCard.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(theme.border, 1, true),
                        new EmptyBorder(8, 12, 8, 12)
                ));
                JLabel iconLbl = new JLabel(IconFactory.createTemplateIcon(16, theme.accent));
                calloutCard.add(iconLbl, BorderLayout.WEST);
                JTextArea ta = createWrappingArea(Theme.FONT_BODY, false);
                editorInput = ta;
                calloutCard.add(ta, BorderLayout.CENTER);
                contentPanel.add(calloutCard, BorderLayout.CENTER);
            }
            case CODE -> {
                JPanel codeBox = new JPanel(new BorderLayout());
                codeBox.setOpaque(true);
                codeBox.setBackground(theme.bgCode);
                codeBox.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(theme.border, 1, true),
                        new EmptyBorder(6, 10, 6, 10)
                ));

                JPanel codeHeader = new JPanel(new BorderLayout());
                codeHeader.setOpaque(false);
                JLabel langLbl = new JLabel("Code");
                langLbl.setFont(Theme.FONT_SMALL);
                langLbl.setForeground(theme.textSecondary);
                codeHeader.add(langLbl, BorderLayout.WEST);

                HoverButton copyBtn = new HoverButton("Copy");
                copyBtn.setFont(Theme.FONT_SMALL);
                copyBtn.addActionListener(e -> {
                    String text = block.getContent();
                    Toolkit.getDefaultToolkit().getSystemClipboard().setContents(new StringSelection(text), null);
                    copyBtn.setText("Copied!");
                    Timer t = new Timer(1500, evt -> copyBtn.setText("Copy"));
                    t.setRepeats(false);
                    t.start();
                });
                codeHeader.add(copyBtn, BorderLayout.EAST);
                codeBox.add(codeHeader, BorderLayout.NORTH);

                JTextArea ta = createWrappingArea(Theme.FONT_CODE, true);
                ta.setBackground(theme.bgCode);
                editorInput = ta;
                codeBox.add(ta, BorderLayout.CENTER);
                contentPanel.add(codeBox, BorderLayout.CENTER);
            }
            case DIVIDER -> {
                JPanel dividerPanel = new JPanel(new BorderLayout());
                dividerPanel.setOpaque(false);
                dividerPanel.setBorder(new EmptyBorder(10, 0, 10, 0));
                JSeparator sep = new JSeparator();
                sep.setForeground(theme.border);
                dividerPanel.add(sep, BorderLayout.CENTER);
                contentPanel.add(dividerPanel, BorderLayout.CENTER);
                editorInput = dividerPanel;
            }
            default -> { // PARAGRAPH
                JTextArea ta = createWrappingArea(Theme.FONT_BODY, false);
                editorInput = ta;
                contentPanel.add(ta, BorderLayout.CENTER);
            }
        }

        add(contentPanel, BorderLayout.CENTER);
    }

    private JTextArea createWrappingArea(Font font, boolean allowEnterNewline) {
        JTextArea ta = new JTextArea(block.getContent());
        ta.setFont(font);
        ta.setLineWrap(true);
        ta.setWrapStyleWord(true);
        ta.setOpaque(false);
        ta.setBorder(new EmptyBorder(2, 2, 2, 2));
        ta.setForeground(ThemeManager.getTheme().textPrimary);
        ta.setCaretColor(ThemeManager.getTheme().textPrimary);

        ta.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e) { syncText(); }
            public void removeUpdate(DocumentEvent e) { syncText(); }
            public void changedUpdate(DocumentEvent e) { syncText(); }
            private void syncText() {
                block.setContent(ta.getText());
                onChange.run();
            }
        });

        ta.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER && !e.isShiftDown() && !allowEnterNewline) {
                    e.consume();
                    onAddNext.accept(BlockEditorComponent.this);
                } else if (e.getKeyCode() == KeyEvent.VK_BACK_SPACE && ta.getText().isEmpty()) {
                    e.consume();
                    onDelete.accept(BlockEditorComponent.this);
                } else if (e.getKeyCode() == KeyEvent.VK_UP && ta.getCaretPosition() == 0) {
                    e.consume();
                    onFocusPrevious.accept(BlockEditorComponent.this);
                } else if (e.getKeyCode() == KeyEvent.VK_DOWN && ta.getCaretPosition() == ta.getText().length()) {
                    e.consume();
                    onFocusNext.accept(BlockEditorComponent.this);
                }
            }

            @Override
            public void keyReleased(KeyEvent e) {
                if (e.getKeyChar() == '/' && ta.getText().trim().equals("/")) {
                    SlashCommandPopup popup = new SlashCommandPopup(newType -> {
                        ta.setText("");
                        block.setBlockType(newType);
                        buildUI();
                        revalidate();
                        repaint();
                        focusInput();
                        onChange.run();
                    });
                    popup.showPopup(ta, 0, ta.getHeight());
                }
            }
        });

        return ta;
    }

    private void updateTodoStrikethrough() {
        if (editorInput instanceof JTextArea ta) {
            if (block.isChecked()) {
                ta.setForeground(ThemeManager.getTheme().textMuted);
            } else {
                ta.setForeground(ThemeManager.getTheme().textPrimary);
            }
        }
    }

    private void showBlockOptionsMenu(Component invoker) {
        JPopupMenu menu = new JPopupMenu();

        JMenuItem deleteItem = new JMenuItem("Delete Block");
        deleteItem.addActionListener(e -> onDelete.accept(this));

        JMenuItem dupItem = new JMenuItem("Duplicate Block");
        dupItem.addActionListener(e -> onAddNext.accept(this));

        JMenu turnIntoMenu = new JMenu("Turn into...");
        for (BlockType bt : BlockType.values()) {
            JMenuItem itm = new JMenuItem(bt.getLabel());
            itm.addActionListener(e -> {
                block.setBlockType(bt);
                buildUI();
                revalidate();
                repaint();
                onChange.run();
            });
            turnIntoMenu.add(itm);
        }

        menu.add(turnIntoMenu);
        menu.add(dupItem);
        menu.addSeparator();
        menu.add(deleteItem);

        menu.show(invoker, 0, invoker.getHeight());
    }

    public void focusInput() {
        if (editorInput instanceof JTextArea ta) {
            ta.requestFocusInWindow();
            ta.setCaretPosition(ta.getText().length());
        }
    }

    public PageBlock getBlock() {
        return block;
    }
}
