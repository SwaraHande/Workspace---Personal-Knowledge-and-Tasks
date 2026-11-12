package com.workspace.app.ui.editor;

import com.workspace.app.model.BlockType;
import com.workspace.app.ui.theme.Theme;
import com.workspace.app.ui.theme.ThemeManager;
import com.workspace.app.util.IconFactory;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class SlashCommandPopup extends JPopupMenu {
    private final Consumer<BlockType> onSelect;
    private final DefaultListModel<BlockType> listModel = new DefaultListModel<>();
    private final JList<BlockType> blockList = new JList<>(listModel);
    private final JTextField filterField = new JTextField();

    public SlashCommandPopup(Consumer<BlockType> onSelect) {
        this.onSelect = onSelect;
        setLayout(new BorderLayout());
        setPreferredSize(new Dimension(300, 280));

        buildUI();
    }

    private void buildUI() {
        Theme theme = ThemeManager.getTheme();

        // Search / Filter Header
        JPanel top = new JPanel(new BorderLayout());
        top.setOpaque(false);
        top.setBorder(new EmptyBorder(6, 8, 6, 8));

        filterField.putClientProperty("JTextField.placeholderText", "Type to filter blocks...");
        filterField.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, theme.border));
        filterField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    selectCurrent();
                } else if (e.getKeyCode() == KeyEvent.VK_DOWN) {
                    int next = (blockList.getSelectedIndex() + 1) % listModel.size();
                    blockList.setSelectedIndex(next);
                    blockList.ensureIndexIsVisible(next);
                } else if (e.getKeyCode() == KeyEvent.VK_UP) {
                    int prev = (blockList.getSelectedIndex() - 1 + listModel.size()) % listModel.size();
                    blockList.setSelectedIndex(prev);
                    blockList.ensureIndexIsVisible(prev);
                } else if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
                    setVisible(false);
                } else {
                    filter(filterField.getText());
                }
            }
        });
        top.add(filterField, BorderLayout.CENTER);
        add(top, BorderLayout.NORTH);

        // Block types list
        populateList("");
        blockList.setCellRenderer(new BlockTypeCellRenderer());
        blockList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        blockList.setSelectedIndex(0);
        blockList.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                if (e.getClickCount() >= 1) {
                    selectCurrent();
                }
            }
        });

        JScrollPane scroll = new JScrollPane(blockList);
        scroll.setBorder(null);
        add(scroll, BorderLayout.CENTER);
    }

    public void showPopup(Component invoker, int x, int y) {
        filterField.setText("");
        populateList("");
        blockList.setSelectedIndex(0);
        show(invoker, x, y);
        SwingUtilities.invokeLater(filterField::requestFocusInWindow);
    }

    private void populateList(String query) {
        listModel.clear();
        String q = query.toLowerCase().trim();
        for (BlockType bt : BlockType.values()) {
            if (q.isEmpty() || bt.getLabel().toLowerCase().contains(q) || bt.name().toLowerCase().contains(q)) {
                listModel.addElement(bt);
            }
        }
        if (!listModel.isEmpty()) {
            blockList.setSelectedIndex(0);
        }
    }

    private void filter(String query) {
        populateList(query);
    }

    private void selectCurrent() {
        BlockType selected = blockList.getSelectedValue();
        if (selected != null) {
            setVisible(false);
            onSelect.accept(selected);
        }
    }

    private static class BlockTypeCellRenderer extends DefaultListCellRenderer {
        @Override
        public Component getListCellRendererComponent(JList<?> list, Object value, int index,
                                                      boolean isSelected, boolean cellHasFocus) {
            JPanel panel = new JPanel(new BorderLayout(8, 0));
            Theme theme = ThemeManager.getTheme();
            panel.setOpaque(true);
            panel.setBackground(isSelected ? theme.bgHover : theme.bgCard);
            panel.setBorder(new EmptyBorder(6, 10, 6, 10));

            if (value instanceof BlockType bt) {
                JLabel iconLbl = new JLabel(IconFactory.createTextIcon(bt.getIconText(), 16, isSelected ? theme.accent : theme.textPrimary));
                panel.add(iconLbl, BorderLayout.WEST);

                JPanel textPanel = new JPanel();
                textPanel.setLayout(new BoxLayout(textPanel, BoxLayout.Y_AXIS));
                textPanel.setOpaque(false);

                JLabel title = new JLabel(bt.getLabel());
                title.setFont(Theme.FONT_BODY.deriveFont(Font.BOLD, 13f));
                title.setForeground(theme.textPrimary);

                JLabel desc = new JLabel(bt.getDescription());
                desc.setFont(Theme.FONT_SMALL);
                desc.setForeground(theme.textSecondary);

                textPanel.add(title);
                textPanel.add(desc);
                panel.add(textPanel, BorderLayout.CENTER);
            }
            return panel;
        }
    }
}
