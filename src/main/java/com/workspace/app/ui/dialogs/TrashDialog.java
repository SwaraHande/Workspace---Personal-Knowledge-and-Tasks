package com.workspace.app.ui.dialogs;

import com.workspace.app.app.AppContext;
import com.workspace.app.model.Page;
import com.workspace.app.ui.components.HoverButton;
import com.workspace.app.ui.theme.Theme;
import com.workspace.app.ui.theme.ThemeManager;
import com.workspace.app.util.IconFactory;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.List;

public class TrashDialog extends JDialog {
    private final AppContext context;
    private final DefaultListModel<Page> listModel = new DefaultListModel<>();
    private final JList<Page> trashList = new JList<>(listModel);

    public TrashDialog(Frame parent, AppContext context) {
        super(parent, "Trash & Archive", true);
        this.context = context;

        setSize(550, 420);
        setLocationRelativeTo(parent);
        setLayout(new BorderLayout());

        buildUI();
        reload();
    }

    private void buildUI() {
        Theme theme = ThemeManager.getTheme();

        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(theme.bgMain);

        // Header
        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);
        header.setBorder(new EmptyBorder(16, 20, 12, 20));

        JLabel title = new JLabel("Trash");
        title.setFont(Theme.FONT_TITLE.deriveFont(20f));
        title.setForeground(theme.textPrimary);
        header.add(title, BorderLayout.WEST);
        root.add(header, BorderLayout.NORTH);

        // List
        trashList.setOpaque(false);
        trashList.setCellRenderer(new TrashCellRenderer());
        trashList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        JScrollPane scroll = new JScrollPane(trashList);
        scroll.setBorder(BorderFactory.createMatteBorder(1, 0, 1, 0, theme.border));
        scroll.setOpaque(false);
        scroll.getViewport().setOpaque(false);
        root.add(scroll, BorderLayout.CENTER);

        // Bottom Actions: Restore, Delete Permanently, Close
        JPanel bottomBar = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 12));
        bottomBar.setBackground(theme.bgCard);

        HoverButton restoreBtn = new HoverButton("Restore");
        restoreBtn.addActionListener(e -> {
            Page selected = trashList.getSelectedValue();
            if (selected != null) {
                context.getPageService().restoreFromTrash(selected.getId());
                context.notifyTreeRefresh();
                reload();
            }
        });

        HoverButton deleteForeverBtn = new HoverButton("Delete Permanently");
        deleteForeverBtn.setForeground(new Color(0xFF, 0x3B, 0x30));
        deleteForeverBtn.addActionListener(e -> {
            Page selected = trashList.getSelectedValue();
            if (selected != null) {
                int confirm = JOptionPane.showConfirmDialog(this,
                        "Permanently delete '" + selected.getTitle() + "'?\nThis cannot be undone.",
                        "Confirm Permanent Deletion", JOptionPane.YES_NO_OPTION);
                if (confirm == JOptionPane.YES_OPTION) {
                    context.getPageService().deletePermanently(selected.getId());
                    context.notifyTreeRefresh();
                    reload();
                }
            }
        });

        HoverButton closeBtn = new HoverButton("Close");
        closeBtn.addActionListener(e -> dispose());

        bottomBar.add(restoreBtn);
        bottomBar.add(deleteForeverBtn);
        bottomBar.add(closeBtn);
        root.add(bottomBar, BorderLayout.SOUTH);

        add(root, BorderLayout.CENTER);
    }

    private void reload() {
        listModel.clear();
        List<Page> trash = context.getPageService().getTrash(context.getCurrentWorkspace().getId());
        for (Page p : trash) {
            listModel.addElement(p);
        }
    }

    private static class TrashCellRenderer extends DefaultListCellRenderer {
        @Override
        public Component getListCellRendererComponent(JList<?> list, Object value, int index,
                                                      boolean isSelected, boolean cellHasFocus) {
            JPanel p = new JPanel(new BorderLayout(8, 0));
            Theme theme = ThemeManager.getTheme();
            p.setOpaque(true);
            p.setBackground(isSelected ? theme.bgActive : theme.bgMain);
            p.setBorder(new EmptyBorder(8, 16, 8, 16));

            if (value instanceof Page page) {
                JLabel icon = new JLabel(IconFactory.createTextIcon(page.getIcon(), 16, theme.textPrimary));
                p.add(icon, BorderLayout.WEST);

                JLabel title = new JLabel(page.getTitle());
                title.setFont(Theme.FONT_BODY);
                title.setForeground(theme.textPrimary);
                p.add(title, BorderLayout.CENTER);
            }
            return p;
        }
    }
}
