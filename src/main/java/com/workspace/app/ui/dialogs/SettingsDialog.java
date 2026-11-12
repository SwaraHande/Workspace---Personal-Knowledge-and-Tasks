package com.workspace.app.ui.dialogs;

import com.workspace.app.app.AppContext;
import com.workspace.app.config.AppConfig;
import com.workspace.app.config.DatabaseConfig;
import com.workspace.app.model.UserSettings;
import com.workspace.app.ui.components.HoverButton;
import com.workspace.app.ui.theme.Theme;
import com.workspace.app.ui.theme.ThemeManager;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class SettingsDialog extends JDialog {
    private final AppContext context;

    public SettingsDialog(Frame parent, AppContext context) {
        super(parent, "Settings & Preferences", true);
        this.context = context;

        setSize(520, 460);
        setLocationRelativeTo(parent);
        setLayout(new BorderLayout());

        buildUI();
    }

    private void buildUI() {
        Theme theme = ThemeManager.getTheme();
        JPanel root = new JPanel();
        root.setLayout(new BoxLayout(root, BoxLayout.Y_AXIS));
        root.setBackground(theme.bgMain);
        root.setBorder(new EmptyBorder(20, 24, 20, 24));

        JLabel title = new JLabel("Settings");
        title.setFont(Theme.FONT_TITLE.deriveFont(22f));
        title.setForeground(theme.textPrimary);
        root.add(title);
        root.add(Box.createVerticalStrut(16));

        // 1. Appearance / Theme
        JPanel themeSection = createSection("Appearance");
        JRadioButton darkRb = new JRadioButton("Dark theme (Obsidian)");
        JRadioButton lightRb = new JRadioButton("Light theme");
        ButtonGroup bg = new ButtonGroup();
        bg.add(darkRb);
        bg.add(lightRb);

        if (ThemeManager.isDark()) {
            darkRb.setSelected(true);
        } else {
            lightRb.setSelected(true);
        }

        darkRb.addActionListener(e -> {
            ThemeManager.setTheme(true, getOwner());
            UserSettings s = context.getUserSettings();
            s.setTheme("DARK");
            context.saveUserSettings(s);
            dispose();
        });

        lightRb.addActionListener(e -> {
            ThemeManager.setTheme(false, getOwner());
            UserSettings s = context.getUserSettings();
            s.setTheme("LIGHT");
            context.saveUserSettings(s);
            dispose();
        });

        darkRb.setOpaque(false);
        lightRb.setOpaque(false);
        darkRb.setForeground(theme.textPrimary);
        lightRb.setForeground(theme.textPrimary);
        themeSection.add(darkRb);
        themeSection.add(lightRb);
        root.add(themeSection);
        root.add(Box.createVerticalStrut(16));

        // 2. Database Connection Diagnostics
        JPanel dbSection = createSection("PostgreSQL Database");
        JLabel dbInfo = new JLabel("Connected to: " + AppConfig.getDbHost() + ":" + AppConfig.getDbPort() + "/" + AppConfig.getDbName());
        dbInfo.setFont(Theme.FONT_SMALL);
        dbInfo.setForeground(theme.textSecondary);
        dbSection.add(dbInfo);
        dbSection.add(Box.createVerticalStrut(6));

        JLabel testStatus = new JLabel("Status: Connected");
        testStatus.setFont(Theme.FONT_SMALL.deriveFont(Font.BOLD));
        testStatus.setForeground(new Color(0x34, 0xC7, 0x59));

        HoverButton testBtn = new HoverButton("Test Connection");
        testBtn.addActionListener(e -> {
            boolean ok = DatabaseConfig.testConnection();
            if (ok) {
                testStatus.setText("Status: Connection healthy (PostgreSQL OK)");
                testStatus.setForeground(new Color(0x34, 0xC7, 0x59));
            } else {
                testStatus.setText("Status: Connection failed");
                testStatus.setForeground(new Color(0xFF, 0x3B, 0x30));
            }
        });

        JPanel dbActions = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 4));
        dbActions.setOpaque(false);
        dbActions.add(testBtn);
        dbActions.add(Box.createHorizontalStrut(10));
        dbActions.add(testStatus);
        dbSection.add(dbActions);
        root.add(dbSection);
        root.add(Box.createVerticalStrut(16));

        // 3. Keyboard Shortcuts Reference
        JPanel shortcutsSection = createSection("Keyboard Shortcuts");
        String[][] shortcuts = {
                {"Ctrl + N", "Create new page"},
                {"Ctrl + P / Ctrl + F", "Quick search pages"},
                {"Ctrl + S", "Save immediately"},
                {"/ (slash)", "Open block commands menu"},
                {"Enter", "New block below"},
                {"Backspace (on empty)", "Delete block"}
        };
        for (String[] sc : shortcuts) {
            JPanel row = new JPanel(new BorderLayout());
            row.setOpaque(false);
            JLabel keyLbl = new JLabel(sc[0]);
            keyLbl.setFont(Theme.FONT_CODE.deriveFont(12f));
            keyLbl.setForeground(theme.accent);
            JLabel descLbl = new JLabel(sc[1]);
            descLbl.setFont(Theme.FONT_SMALL);
            descLbl.setForeground(theme.textSecondary);
            row.add(keyLbl, BorderLayout.WEST);
            row.add(descLbl, BorderLayout.EAST);
            shortcutsSection.add(row);
            shortcutsSection.add(Box.createVerticalStrut(2));
        }
        root.add(shortcutsSection);

        add(new JScrollPane(root), BorderLayout.CENTER);

        // Close button
        JPanel bottom = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 10));
        bottom.setBackground(theme.bgCard);
        HoverButton closeBtn = new HoverButton("Close");
        closeBtn.addActionListener(e -> dispose());
        bottom.add(closeBtn);
        add(bottom, BorderLayout.SOUTH);
    }

    private JPanel createSection(String title) {
        Theme theme = ThemeManager.getTheme();
        JPanel p = new JPanel();
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
        p.setOpaque(false);
        p.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, theme.border),
                new EmptyBorder(0, 0, 12, 0)
        ));

        JLabel lbl = new JLabel(title);
        lbl.setFont(Theme.FONT_BODY.deriveFont(Font.BOLD, 14f));
        lbl.setForeground(theme.textPrimary);
        p.add(lbl);
        p.add(Box.createVerticalStrut(8));
        return p;
    }
}
