package com.workspace.app.ui.components;

import com.workspace.app.ui.theme.Theme;
import com.workspace.app.ui.theme.ThemeManager;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class HoverButton extends JButton {
    private boolean isHovered = false;
    private Color customBg = null;

    public HoverButton(String text) {
        super(text);
        init();
    }

    public HoverButton(Icon icon) {
        super(icon);
        init();
    }

    public HoverButton(String text, Icon icon) {
        super(text, icon);
        init();
    }

    private void init() {
        setContentAreaFilled(false);
        setBorderPainted(false);
        setFocusPainted(false);
        setOpaque(false);
        setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        setFont(Theme.FONT_BODY);
        setHorizontalAlignment(SwingConstants.CENTER);
        setIconTextGap(6);
        setMargin(new Insets(4, 10, 4, 10));

        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                if (!isHovered) {
                    isHovered = true;
                    repaint();
                }
            }

            @Override
            public void mouseExited(MouseEvent e) {
                if (isHovered) {
                    isHovered = false;
                    repaint();
                }
            }
        });
    }

    public void setCustomBg(Color color) {
        this.customBg = color;
        repaint();
    }

    @Override
    public Dimension getPreferredSize() {
        if (isPreferredSizeSet()) {
            return super.getPreferredSize();
        }
        Dimension d = super.getPreferredSize();
        // Ensure buttons have comfortable padding and are never clipped
        return new Dimension(Math.max(d.width + 12, 28), Math.max(d.height, 28));
    }

    @Override
    protected void paintComponent(Graphics g) {
        Theme theme = ThemeManager.getTheme();
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        if (customBg != null) {
            g2.setColor(customBg);
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), 6, 6);
        } else if (isHovered) {
            g2.setColor(theme.bgHover);
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), 6, 6);
        }

        g2.dispose();
        super.paintComponent(g);
    }
}
