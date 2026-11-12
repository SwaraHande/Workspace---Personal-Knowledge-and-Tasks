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
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;
import java.util.Map;

public class CalendarViewPanel extends JPanel {
    private final AppContext context;
    private final DatabaseDefinition database;

    private YearMonth currentMonth = YearMonth.now();
    private JPanel gridPanel;
    private JLabel monthTitle;

    public CalendarViewPanel(AppContext context, DatabaseDefinition database) {
        this.context = context;
        this.database = database;

        setLayout(new BorderLayout(0, 8));
        setOpaque(false);

        buildUI();
    }

    private void buildUI() {
        Theme theme = ThemeManager.getTheme();

        // Top Navigation
        JPanel nav = new JPanel(new BorderLayout());
        nav.setOpaque(false);
        monthTitle = new JLabel(currentMonth.getMonth().name() + " " + currentMonth.getYear());
        monthTitle.setFont(Theme.FONT_BODY.deriveFont(Font.BOLD, 14f));
        monthTitle.setForeground(theme.textPrimary);
        nav.add(monthTitle, BorderLayout.WEST);

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 4, 0));
        btnPanel.setOpaque(false);
        HoverButton prevBtn = new HoverButton(IconFactory.createChevronLeftIcon(12, theme.textSecondary));
        prevBtn.setPreferredSize(new Dimension(24, 24));
        prevBtn.setToolTipText("Previous month");
        prevBtn.addActionListener(e -> {
            currentMonth = currentMonth.minusMonths(1);
            reload();
        });
        HoverButton nextBtn = new HoverButton(IconFactory.createChevronRightIcon(12, theme.textSecondary));
        nextBtn.setPreferredSize(new Dimension(24, 24));
        nextBtn.setToolTipText("Next month");
        nextBtn.addActionListener(e -> {
            currentMonth = currentMonth.plusMonths(1);
            reload();
        });
        btnPanel.add(prevBtn);
        btnPanel.add(nextBtn);
        nav.add(btnPanel, BorderLayout.EAST);
        add(nav, BorderLayout.NORTH);

        gridPanel = new JPanel(new GridLayout(0, 7, 4, 4));
        gridPanel.setOpaque(false);
        add(gridPanel, BorderLayout.CENTER);

        reload();
    }

    public void reload() {
        gridPanel.removeAll();
        Theme theme = ThemeManager.getTheme();
        monthTitle.setText(currentMonth.getMonth().name() + " " + currentMonth.getYear());

        String[] days = {"Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun"};
        for (String d : days) {
            JLabel header = new JLabel(d, SwingConstants.CENTER);
            header.setFont(Theme.FONT_SMALL.deriveFont(Font.BOLD));
            header.setForeground(theme.textSecondary);
            gridPanel.add(header);
        }

        List<DatabaseRow> rows = context.getDatabaseEntityService().getRows(database.getId());

        LocalDate firstDay = currentMonth.atDay(1);
        int dayOfWeek = firstDay.getDayOfWeek().getValue(); // 1 = Mon, 7 = Sun

        for (int i = 1; i < dayOfWeek; i++) {
            JPanel blank = new JPanel();
            blank.setOpaque(false);
            gridPanel.add(blank);
        }

        int lengthOfMonth = currentMonth.lengthOfMonth();
        for (int day = 1; day <= lengthOfMonth; day++) {
            LocalDate date = currentMonth.atDay(day);
            JPanel cell = new JPanel(new BorderLayout());
            cell.setOpaque(true);
            cell.setBackground(theme.bgCard);
            cell.setBorder(BorderFactory.createLineBorder(theme.border));

            JLabel dayNum = new JLabel(" " + day);
            dayNum.setFont(Theme.FONT_SMALL);
            dayNum.setForeground(theme.textSecondary);
            cell.add(dayNum, BorderLayout.NORTH);

            JPanel itemsPanel = new JPanel();
            itemsPanel.setLayout(new BoxLayout(itemsPanel, BoxLayout.Y_AXIS));
            itemsPanel.setOpaque(false);

            String dateStr = date.toString();
            for (DatabaseRow row : rows) {
                Map<String, Object> map = row.getValues();
                if (map != null && dateStr.equals(map.get("Due Date"))) {
                    String name = map.containsKey("Task Name") ? map.get("Task Name").toString() : "Task";
                    JLabel badge = new JLabel(" • " + name);
                    badge.setFont(Theme.FONT_SMALL.deriveFont(10f));
                    badge.setForeground(theme.accent);
                    itemsPanel.add(badge);
                }
            }

            cell.add(itemsPanel, BorderLayout.CENTER);
            gridPanel.add(cell);
        }

        revalidate();
        repaint();
    }
}
