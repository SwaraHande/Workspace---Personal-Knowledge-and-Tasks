package com.workspace.app.ui.dialogs;

import com.workspace.app.app.AppContext;
import com.workspace.app.model.Page;
import com.workspace.app.ui.theme.Theme;
import com.workspace.app.ui.theme.ThemeManager;
import com.workspace.app.util.Debouncer;
import com.workspace.app.util.IconFactory;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.*;
import java.util.List;

public class QuickSearchDialog extends JDialog {
    private final AppContext context;
    private final JTextField searchField = new JTextField();
    private final DefaultListModel<Page> listModel = new DefaultListModel<>();
    private final JList<Page> resultList = new JList<>(listModel);
    private final Debouncer searchDebouncer = new Debouncer(120);

    public QuickSearchDialog(Frame parent, AppContext context) {
        super(parent, "Quick Search", false); // Non-modal so outside clicks can be detected!
        this.context = context;

        setSize(560, 420);
        setLocationRelativeTo(parent);
        setUndecorated(true);
        setLayout(new BorderLayout());

        buildUI();

        // Dismiss when clicking anywhere outside the dialog window
        addWindowFocusListener(new WindowAdapter() {
            @Override
            public void windowLostFocus(WindowEvent e) {
                dispose();
            }
        });

        // Focus search input when window opens
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowOpened(WindowEvent e) {
                searchField.requestFocusInWindow();
            }
        });
    }

    private void buildUI() {
        Theme theme = ThemeManager.getTheme();
        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(theme.bgCard);
        root.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(theme.border, 1),
                BorderFactory.createEmptyBorder(0, 0, 0, 0)
        ));

        // Top Search Input
        JPanel top = new JPanel(new BorderLayout(10, 0));
        top.setOpaque(false);
        top.setBorder(new EmptyBorder(12, 16, 12, 16));

        JLabel icon = new JLabel(IconFactory.createSearchIcon(16, theme.textSecondary));
        top.add(icon, BorderLayout.WEST);

        searchField.setFont(Theme.FONT_BODY.deriveFont(15f));
        searchField.setBorder(null);
        searchField.setOpaque(false);
        searchField.setForeground(theme.textPrimary);
        searchField.setCaretColor(theme.textPrimary);
        searchField.putClientProperty("JTextField.placeholderText", "Search pages, to-dos, or notes...");

        searchField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_DOWN) {
                    if (!listModel.isEmpty()) {
                        int next = (resultList.getSelectedIndex() + 1) % listModel.size();
                        resultList.setSelectedIndex(next);
                        resultList.ensureIndexIsVisible(next);
                    }
                } else if (e.getKeyCode() == KeyEvent.VK_UP) {
                    if (!listModel.isEmpty()) {
                        int prev = (resultList.getSelectedIndex() - 1 + listModel.size()) % listModel.size();
                        resultList.setSelectedIndex(prev);
                        resultList.ensureIndexIsVisible(prev);
                    }
                } else if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    openSelected();
                } else if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
                    dispose();
                }
            }

            @Override
            public void keyReleased(KeyEvent e) {
                if (e.getKeyCode() != KeyEvent.VK_DOWN && e.getKeyCode() != KeyEvent.VK_UP
                        && e.getKeyCode() != KeyEvent.VK_ENTER && e.getKeyCode() != KeyEvent.VK_ESCAPE) {
                    searchDebouncer.debounce(() -> doSearch(searchField.getText()));
                }
            }
        });
        top.add(searchField, BorderLayout.CENTER);
        root.add(top, BorderLayout.NORTH);

        // Results List
        resultList.setOpaque(false);
        resultList.setCellRenderer(new SearchResultCellRenderer());
        resultList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        resultList.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() >= 1) {
                    openSelected();
                }
            }
        });

        JScrollPane scroll = new JScrollPane(resultList);
        scroll.setBorder(BorderFactory.createMatteBorder(1, 0, 1, 0, theme.border));
        scroll.setOpaque(false);
        scroll.getViewport().setOpaque(false);
        root.add(scroll, BorderLayout.CENTER);

        // Footer
        JPanel footer = new JPanel(new BorderLayout());
        footer.setOpaque(false);
        footer.setBorder(new EmptyBorder(8, 16, 8, 16));
        JLabel tip = new JLabel("Use ↑ / ↓ to navigate • Enter to open • ESC or click outside to dismiss");
        tip.setFont(Theme.FONT_SMALL);
        tip.setForeground(theme.textMuted);
        footer.add(tip, BorderLayout.WEST);
        root.add(footer, BorderLayout.SOUTH);

        add(root, BorderLayout.CENTER);

        // Initial populate
        doSearch("");
    }

    private void doSearch(String query) {
        SwingUtilities.invokeLater(() -> {
            listModel.clear();
            List<Page> results = context.getSearchService().search(context.getCurrentWorkspace().getId(), query);
            for (Page p : results) {
                listModel.addElement(p);
            }
            if (!listModel.isEmpty()) {
                resultList.setSelectedIndex(0);
            }
        });
    }

    private void openSelected() {
        Page selected = resultList.getSelectedValue();
        if (selected != null) {
            context.setActivePage(selected);
            dispose();
        }
    }

    private static class SearchResultCellRenderer extends DefaultListCellRenderer {
        @Override
        public Component getListCellRendererComponent(JList<?> list, Object value, int index,
                                                      boolean isSelected, boolean cellHasFocus) {
            JPanel p = new JPanel(new BorderLayout(8, 0));
            Theme theme = ThemeManager.getTheme();
            p.setOpaque(true);
            p.setBackground(isSelected ? theme.bgActive : theme.bgCard);
            p.setBorder(new EmptyBorder(8, 14, 8, 14));

            if (value instanceof Page page) {
                JLabel icon = new JLabel(IconFactory.createDocumentIcon(15, theme.textSecondary));
                p.add(icon, BorderLayout.WEST);

                JLabel title = new JLabel(page.getTitle());
                title.setFont(Theme.FONT_BODY);
                title.setForeground(theme.textPrimary);
                p.add(title, BorderLayout.CENTER);

                if (page.isFavorite()) {
                    JLabel fav = new JLabel(IconFactory.createStarIcon(true, 14, new Color(0xF5, 0xA6, 0x23)));
                    p.add(fav, BorderLayout.EAST);
                }
            }
            return p;
        }
    }
}
