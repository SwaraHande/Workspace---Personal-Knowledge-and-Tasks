package com.workspace.app.ui.components;

import com.workspace.app.ui.theme.Theme;
import com.workspace.app.ui.theme.ThemeManager;

import javax.swing.*;
import java.awt.*;

public class StatusIndicator extends JLabel {
    public enum State {
        SAVED("Saved", new Color(0x34, 0xC7, 0x59)),
        SAVING("Saving...", new Color(0xFF, 0x95, 0x00)),
        ERROR("Save Error", new Color(0xFF, 0x3B, 0x30));

        final String text;
        final Color color;

        State(String text, Color color) {
            this.text = text;
            this.color = color;
        }
    }

    private State state = State.SAVED;

    public StatusIndicator() {
        setFont(Theme.FONT_SMALL);
        setState(State.SAVED);
    }

    public void setState(State state) {
        this.state = state;
        setText(state.text);
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        Theme theme = ThemeManager.getTheme();
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // Draw dot
        g2.setColor(state.color);
        g2.fillOval(2, (getHeight() - 8) / 2, 8, 8);

        setForeground(theme.textSecondary);
        g2.dispose();

        // Offset text so it doesn't overlap the dot
        Graphics gText = g.create(14, 0, getWidth() - 14, getHeight());
        super.paintComponent(gText);
        gText.dispose();
    }
}
