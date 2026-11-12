package com.workspace.app.ui.database;

import com.workspace.app.app.AppContext;
import com.workspace.app.model.DatabaseDefinition;
import com.workspace.app.model.DatabaseRow;
import com.workspace.app.ui.components.HoverButton;
import com.workspace.app.ui.theme.Theme;
import com.workspace.app.ui.theme.ThemeManager;
import com.workspace.app.util.IconFactory;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BoardViewPanel extends JPanel {
    private final AppContext context;
    private final DatabaseDefinition database;
    private final Runnable onDataChanged;

    private static final String[] STATUS_COLUMNS = {"Todo", "In Progress", "Done"};

    public BoardViewPanel(AppContext context, DatabaseDefinition database, Runnable onDataChanged) {
        this.context = context;
        this.database = database;
        this.onDataChanged = onDataChanged;

        setLayout(new BorderLayout());
        setOpaque(false);

        reload();
    }

    public void reload() {
        removeAll();
        Theme theme = ThemeManager.getTheme();

        List<DatabaseRow> rows = context.getDatabaseEntityService().getRows(database.getId());

        JPanel columnsContainer = new JPanel(new GridLayout(1, STATUS_COLUMNS.length, 12, 0));
        columnsContainer.setOpaque(false);
        columnsContainer.setBorder(new EmptyBorder(8, 0, 8, 0));

        for (String status : STATUS_COLUMNS) {
            JPanel colPanel = new JPanel(new BorderLayout(0, 8));
            colPanel.setOpaque(true);
            colPanel.setBackground(theme.bgCard);
            colPanel.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(theme.border, 1, true),
                    new EmptyBorder(10, 10, 10, 10)
            ));

            // Column Header
            JPanel header = new JPanel(new BorderLayout());
            header.setOpaque(false);

            long count = rows.stream().filter(r -> {
                Object val = r.getValues() != null ? r.getValues().get("Status") : null;
                return status.equalsIgnoreCase(val != null ? val.toString() : "");
            }).count();

            JLabel title = new JLabel(status.toUpperCase() + "  " + count);
            title.setFont(Theme.FONT_SMALL.deriveFont(Font.BOLD, 11f));
            title.setForeground(theme.textSecondary);
            header.add(title, BorderLayout.WEST);

            colPanel.add(header, BorderLayout.NORTH);

            // Cards list
            JPanel cardsList = new JPanel();
            cardsList.setLayout(new BoxLayout(cardsList, BoxLayout.Y_AXIS));
            cardsList.setOpaque(false);

            for (DatabaseRow row : rows) {
                Map<String, Object> values = row.getValues();
                Object rowStatus = values != null ? values.get("Status") : null;
                if (status.equalsIgnoreCase(rowStatus != null ? rowStatus.toString() : "")) {
                    cardsList.add(createCard(row, status));
                    cardsList.add(Box.createVerticalStrut(8));
                }
            }

            JScrollPane colScroll = new JScrollPane(cardsList);
            colScroll.setBorder(null);
            colScroll.setOpaque(false);
            colScroll.getViewport().setOpaque(false);
            colPanel.add(colScroll, BorderLayout.CENTER);

            // Add card button
            HoverButton addCardBtn = new HoverButton("New", IconFactory.createPlusIcon(11, theme.textPrimary));
            addCardBtn.setFont(Theme.FONT_SMALL);
            addCardBtn.addActionListener(e -> {
                Map<String, Object> vals = new HashMap<>();
                vals.put("Task Name", "New Task");
                vals.put("Status", status);
                vals.put("Priority", "Medium");
                context.getDatabaseEntityService().addRow(database.getId(), vals, rows.size());
                reload();
                if (onDataChanged != null) onDataChanged.run();
            });
            colPanel.add(addCardBtn, BorderLayout.SOUTH);

            columnsContainer.add(colPanel);
        }

        add(columnsContainer, BorderLayout.CENTER);
        revalidate();
        repaint();
    }

    private JPanel createCard(DatabaseRow row, String currentStatus) {
        Theme theme = ThemeManager.getTheme();
        JPanel card = new JPanel(new BorderLayout(4, 6));
        card.setOpaque(true);
        card.setBackground(theme.bgMain);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(theme.border, 1, true),
                new EmptyBorder(8, 10, 8, 10)
        ));

        Map<String, Object> values = row.getValues();
        String taskName = values != null && values.containsKey("Task Name") ? values.get("Task Name").toString() : "Untitled";
        String priority = values != null && values.containsKey("Priority") ? values.get("Priority").toString() : "";
        String due = values != null && values.containsKey("Due Date") ? values.get("Due Date").toString() : "";

        JLabel nameLbl = new JLabel(taskName);
        nameLbl.setFont(Theme.FONT_BODY.deriveFont(Font.BOLD, 13f));
        nameLbl.setForeground(theme.textPrimary);
        card.add(nameLbl, BorderLayout.NORTH);

        // Tags panel
        JPanel tagsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 4, 0));
        tagsPanel.setOpaque(false);

        if (!priority.isEmpty()) {
            Color tagBg = "High".equalsIgnoreCase(priority) ? theme.tagRed :
                    ("Medium".equalsIgnoreCase(priority) ? theme.tagYellow : theme.tagBlue);
            Color tagFg = theme.isDark ? theme.textPrimary : Color.DARK_GRAY;
            JLabel prioLbl = new JLabel(" " + priority + " ");
            prioLbl.setFont(Theme.FONT_SMALL.deriveFont(Font.BOLD, 10f));
            prioLbl.setOpaque(true);
            prioLbl.setBackground(tagBg);
            prioLbl.setForeground(tagFg);
            tagsPanel.add(prioLbl);
        }

        if (!due.isEmpty()) {
            JLabel dueLbl = new JLabel(due, IconFactory.createCalendarIcon(12, theme.textSecondary), SwingConstants.LEFT);
            dueLbl.setFont(Theme.FONT_SMALL);
            dueLbl.setForeground(theme.textSecondary);
            tagsPanel.add(dueLbl);
        }
        card.add(tagsPanel, BorderLayout.CENTER);

        // Quick status shift button
        JPanel actions = new JPanel(new FlowLayout(FlowLayout.RIGHT, 2, 0));
        actions.setOpaque(false);
        HoverButton moveBtn = new HoverButton(IconFactory.createArrowRightIcon(12, theme.textSecondary));
        moveBtn.setPreferredSize(new Dimension(24, 24));
        moveBtn.setToolTipText("Move to next status");
        moveBtn.addActionListener(e -> {
            int nextIdx = (java.util.Arrays.asList(STATUS_COLUMNS).indexOf(currentStatus) + 1) % STATUS_COLUMNS.length;
            String nextStatus = STATUS_COLUMNS[nextIdx];
            if (row.getValues() == null) row.setValues(new HashMap<>());
            row.getValues().put("Status", nextStatus);
            context.getDatabaseEntityService().updateRow(row);
            reload();
            if (onDataChanged != null) onDataChanged.run();
        });
        actions.add(moveBtn);
        card.add(actions, BorderLayout.EAST);

        return card;
    }
}
