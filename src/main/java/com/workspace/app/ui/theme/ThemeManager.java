package com.workspace.app.ui.theme;

import com.formdev.flatlaf.FlatDarkLaf;
import com.formdev.flatlaf.FlatLightLaf;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class ThemeManager {
    private static final Logger logger = LoggerFactory.getLogger(ThemeManager.class);
    private static Theme currentTheme;
    private static final List<Runnable> themeChangeListeners = new ArrayList<>();

    public static void initialize(String preferredTheme) {
        boolean dark = "DARK".equalsIgnoreCase(preferredTheme);
        applyLookAndFeel(dark);
        currentTheme = new Theme(dark);
    }

    public static Theme getTheme() {
        if (currentTheme == null) {
            currentTheme = new Theme(true);
        }
        return currentTheme;
    }

    public static boolean isDark() {
        return getTheme().isDark;
    }

    public static void setTheme(boolean dark, Component rootComponent) {
        currentTheme = new Theme(dark);
        applyLookAndFeel(dark);

        if (rootComponent != null) {
            SwingUtilities.updateComponentTreeUI(rootComponent);
        }

        for (Runnable listener : themeChangeListeners) {
            listener.run();
        }
    }

    private static void applyLookAndFeel(boolean dark) {
        try {
            if (dark) {
                FlatDarkLaf.setup();
            } else {
                FlatLightLaf.setup();
            }

            // Customize global FlatLaf properties for Notion feel
            UIManager.put("Button.arc", 6);
            UIManager.put("Component.arc", 6);
            UIManager.put("ProgressBar.arc", 6);
            UIManager.put("TextComponent.arc", 6);
            UIManager.put("ScrollBar.showButtons", false);
            UIManager.put("ScrollBar.width", 10);
            UIManager.put("Tree.showDefaultIcons", false);
            UIManager.put("PopupMenu.borderColor", dark ? new Color(0x35, 0x35, 0x35) : new Color(0xE8, 0xE7, 0xE4));
        } catch (Exception e) {
            logger.warn("Failed to set FlatLaf Look and Feel: {}", e.getMessage());
        }
    }

    public static void addThemeChangeListener(Runnable listener) {
        themeChangeListeners.add(listener);
    }
}
