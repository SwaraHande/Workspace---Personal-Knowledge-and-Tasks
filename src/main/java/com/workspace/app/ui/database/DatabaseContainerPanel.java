package com.workspace.app.ui.database;

import com.workspace.app.app.AppContext;
import com.workspace.app.model.DatabaseDefinition;
import com.workspace.app.model.ViewType;
import com.workspace.app.ui.components.HoverButton;
import com.workspace.app.ui.theme.Theme;
import com.workspace.app.ui.theme.ThemeManager;
import com.workspace.app.util.IconFactory;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class DatabaseContainerPanel extends JPanel {
    private final AppContext context;
    private final DatabaseDefinition database;

    private ViewType currentView = ViewType.TABLE;
    private TableViewPanel tableView;
    private BoardViewPanel boardView;
    private CalendarViewPanel calendarView;

    private JPanel viewCards;
    private CardLayout cardLayout;
    private HoverButton tableTab;
    private HoverButton boardTab;
    private HoverButton calendarTab;

    public DatabaseContainerPanel(AppContext context, DatabaseDefinition database) {
        this.context = context;
        this.database = database;

        setLayout(new BorderLayout(0, 10));
        setOpaque(false);
        setBorder(new EmptyBorder(12, 0, 12, 0));

        buildUI();
    }

    private void buildUI() {
        Theme theme = ThemeManager.getTheme();

        // Top Header: Database Title + View Switcher Tabs
        JPanel topHeader = new JPanel(new BorderLayout(8, 0));
        topHeader.setOpaque(false);

        JLabel dbTitle = new JLabel("Database: " + database.getTitle(), IconFactory.createTableIcon(16, theme.textPrimary), SwingConstants.LEFT);
        dbTitle.setFont(Theme.FONT_H2);
        dbTitle.setForeground(theme.textPrimary);
        topHeader.add(dbTitle, BorderLayout.WEST);

        // View Tabs
        JPanel tabs = new JPanel(new FlowLayout(FlowLayout.LEFT, 4, 0));
        tabs.setOpaque(false);

        tableTab = new HoverButton("Table", IconFactory.createTableIcon(13, theme.textSecondary));
        tableTab.addActionListener(e -> switchView(ViewType.TABLE));

        boardTab = new HoverButton("Board", IconFactory.createBoardIcon(13, theme.textSecondary));
        boardTab.addActionListener(e -> switchView(ViewType.BOARD));

        calendarTab = new HoverButton("Calendar", IconFactory.createCalendarIcon(13, theme.textSecondary));
        calendarTab.addActionListener(e -> switchView(ViewType.CALENDAR));

        tabs.add(tableTab);
        tabs.add(boardTab);
        tabs.add(calendarTab);
        topHeader.add(tabs, BorderLayout.CENTER);

        add(topHeader, BorderLayout.NORTH);

        // Center View Switcher
        cardLayout = new CardLayout();
        viewCards = new JPanel(cardLayout);
        viewCards.setOpaque(false);

        Runnable onDataChanged = () -> {
            if (tableView != null) tableView.reload();
            if (boardView != null) boardView.reload();
            if (calendarView != null) calendarView.reload();
        };

        tableView = new TableViewPanel(context, database, onDataChanged);
        boardView = new BoardViewPanel(context, database, onDataChanged);
        calendarView = new CalendarViewPanel(context, database);

        viewCards.add(tableView, ViewType.TABLE.name());
        viewCards.add(boardView, ViewType.BOARD.name());
        viewCards.add(calendarView, ViewType.CALENDAR.name());

        add(viewCards, BorderLayout.CENTER);

        updateTabStyles();
    }

    private void switchView(ViewType type) {
        this.currentView = type;
        cardLayout.show(viewCards, type.name());
        updateTabStyles();
    }

    private void updateTabStyles() {
        Theme theme = ThemeManager.getTheme();
        tableTab.setCustomBg(currentView == ViewType.TABLE ? theme.bgActive : null);
        boardTab.setCustomBg(currentView == ViewType.BOARD ? theme.bgActive : null);
        calendarTab.setCustomBg(currentView == ViewType.CALENDAR ? theme.bgActive : null);
        repaint();
    }
}
