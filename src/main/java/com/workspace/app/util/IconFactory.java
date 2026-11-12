package com.workspace.app.util;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.*;

public class IconFactory {

    public static Icon createSearchIcon(int size, Color color) {
        return new Icon() {
            @Override
            public void paintIcon(Component c, Graphics g, int x, int y) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(color != null ? color : Color.GRAY);
                g2.setStroke(new BasicStroke(1.5f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));

                int r = size * 6 / 10;
                int pad = 2;
                g2.drawOval(x + pad, y + pad, r, r);
                int startX = x + pad + r - 1;
                int startY = y + pad + r - 1;
                g2.drawLine(startX, startY, x + size - pad, y + size - pad);

                g2.dispose();
            }

            @Override public int getIconWidth() { return size; }
            @Override public int getIconHeight() { return size; }
        };
    }

    public static Icon createDiscussionIcon(int size, Color color) {
        return new Icon() {
            @Override
            public void paintIcon(Component c, Graphics g, int x, int y) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(color != null ? color : Color.GRAY);
                g2.setStroke(new BasicStroke(1.4f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));

                int w = size - 3;
                int h = size * 7 / 10;
                // Speech bubble body
                g2.drawRoundRect(x + 1, y + 1, w, h, 4, 4);

                // Small pointer tail at bottom-left
                Path2D tail = new Path2D.Float();
                tail.moveTo(x + 4, y + 1 + h);
                tail.lineTo(x + 3, y + size - 1);
                tail.lineTo(x + 8, y + 1 + h);
                g2.draw(tail);

                // Inside text lines
                g2.setStroke(new BasicStroke(1.0f));
                g2.drawLine(x + 4, y + 4, x + w - 2, y + 4);
                g2.drawLine(x + 4, y + 7, x + w - 5, y + 7);

                g2.dispose();
            }

            @Override public int getIconWidth() { return size; }
            @Override public int getIconHeight() { return size; }
        };
    }

    public static Icon createDocumentIcon(int size, Color color) {
        return new Icon() {
            @Override
            public void paintIcon(Component c, Graphics g, int x, int y) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(color != null ? color : Color.GRAY);
                g2.setStroke(new BasicStroke(1.4f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));

                int w = size * 7 / 10;
                int h = size - 3;
                int x0 = x + (size - w) / 2;
                int y0 = y + 1;
                int fold = 4;

                Path2D p = new Path2D.Float();
                p.moveTo(x0, y0);
                p.lineTo(x0 + w - fold, y0);
                p.lineTo(x0 + w, y0 + fold);
                p.lineTo(x0 + w, y0 + h);
                p.lineTo(x0, y0 + h);
                p.closePath();
                g2.draw(p);

                // Folded corner
                g2.drawLine(x0 + w - fold, y0, x0 + w - fold, y0 + fold);
                g2.drawLine(x0 + w - fold, y0 + fold, x0 + w, y0 + fold);

                // Document lines
                g2.setStroke(new BasicStroke(1.0f));
                g2.drawLine(x0 + 3, y0 + 6, x0 + w - 3, y0 + 6);
                g2.drawLine(x0 + 3, y0 + 9, x0 + w - 4, y0 + 9);

                g2.dispose();
            }

            @Override public int getIconWidth() { return size; }
            @Override public int getIconHeight() { return size; }
        };
    }

    public static Icon createSettingsIcon(int size, Color color) {
        return new Icon() {
            @Override
            public void paintIcon(Component c, Graphics g, int x, int y) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(color != null ? color : Color.GRAY);
                g2.setStroke(new BasicStroke(1.4f));

                int cx = x + size / 2;
                int cy = y + size / 2;
                int r = size / 3;
                g2.drawOval(cx - r, cy - r, r * 2, r * 2);

                // 6 spokes
                for (int i = 0; i < 6; i++) {
                    double angle = i * Math.PI / 3;
                    int x1 = (int) (cx + (r - 1) * Math.cos(angle));
                    int y1 = (int) (cy + (r - 1) * Math.sin(angle));
                    int x2 = (int) (cx + (r + 3) * Math.cos(angle));
                    int y2 = (int) (cy + (r + 3) * Math.sin(angle));
                    g2.drawLine(x1, y1, x2, y2);
                }

                g2.dispose();
            }

            @Override public int getIconWidth() { return size; }
            @Override public int getIconHeight() { return size; }
        };
    }

    public static Icon createTrashIcon(int size, Color color) {
        return new Icon() {
            @Override
            public void paintIcon(Component c, Graphics g, int x, int y) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(color != null ? color : Color.GRAY);
                g2.setStroke(new BasicStroke(1.4f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));

                int w = size * 6 / 10;
                int h = size * 7 / 10;
                int x0 = x + (size - w) / 2;
                int y0 = y + size * 2 / 10;

                // Lid
                g2.drawLine(x0 - 2, y0, x0 + w + 2, y0);
                g2.drawLine(x + size / 2 - 2, y0 - 2, x + size / 2 + 2, y0 - 2);

                // Body
                g2.drawRoundRect(x0, y0, w, h, 2, 2);
                // Slits
                g2.setStroke(new BasicStroke(1.0f));
                g2.drawLine(x0 + w / 3, y0 + 3, x0 + w / 3, y0 + h - 3);
                g2.drawLine(x0 + w * 2 / 3, y0 + 3, x0 + w * 2 / 3, y0 + h - 3);

                g2.dispose();
            }

            @Override public int getIconWidth() { return size; }
            @Override public int getIconHeight() { return size; }
        };
    }

    public static Icon createTemplateIcon(int size, Color color) {
        return new Icon() {
            @Override
            public void paintIcon(Component c, Graphics g, int x, int y) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(color != null ? color : Color.GRAY);
                g2.setStroke(new BasicStroke(1.4f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));

                int w = size * 7 / 10;
                int h = size - 3;
                int x0 = x + (size - w) / 2;
                int y0 = y + 2;

                // Board
                g2.drawRoundRect(x0, y0, w, h, 3, 3);
                // Clip on top
                g2.fillRect(x0 + w / 3, y0 - 2, w / 3, 3);
                // Items
                g2.setStroke(new BasicStroke(1.0f));
                g2.drawLine(x0 + 3, y0 + 5, x0 + w - 3, y0 + 5);
                g2.drawLine(x0 + 3, y0 + 8, x0 + w - 3, y0 + 8);
                g2.drawLine(x0 + 3, y0 + 11, x0 + w - 5, y0 + 11);

                g2.dispose();
            }

            @Override public int getIconWidth() { return size; }
            @Override public int getIconHeight() { return size; }
        };
    }

    public static Icon createSaveIcon(int size, Color color) {
        return new Icon() {
            @Override
            public void paintIcon(Component c, Graphics g, int x, int y) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(color != null ? color : Color.GRAY);
                g2.setStroke(new BasicStroke(1.4f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));

                int w = size - 3;
                int h = size - 3;
                int x0 = x + 1;
                int y0 = y + 1;
                int cut = 3;

                Path2D p = new Path2D.Float();
                p.moveTo(x0, y0);
                p.lineTo(x0 + w - cut, y0);
                p.lineTo(x0 + w, y0 + cut);
                p.lineTo(x0 + w, y0 + h);
                p.lineTo(x0, y0 + h);
                p.closePath();
                g2.draw(p);

                // Top notch
                g2.drawRect(x0 + 3, y0, w - 6, 4);
                // Bottom label
                g2.drawRect(x0 + 2, y0 + h - 5, w - 4, 5);

                g2.dispose();
            }

            @Override public int getIconWidth() { return size; }
            @Override public int getIconHeight() { return size; }
        };
    }

    public static Icon createStarIcon(boolean filled, int size, Color color) {
        return new Icon() {
            @Override
            public void paintIcon(Component c, Graphics g, int x, int y) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(color != null ? color : new Color(0xF5, 0xA6, 0x23));

                int rOut = size / 2 - 1;
                int rIn = rOut * 4 / 10;
                int cx = x + size / 2;
                int cy = y + size / 2;

                Path2D star = new Path2D.Float();
                for (int i = 0; i < 10; i++) {
                    double angle = -Math.PI / 2 + i * Math.PI / 5;
                    int r = (i % 2 == 0) ? rOut : rIn;
                    double px = cx + r * Math.cos(angle);
                    double py = cy + r * Math.sin(angle);
                    if (i == 0) star.moveTo(px, py);
                    else star.lineTo(px, py);
                }
                star.closePath();

                if (filled) {
                    g2.fill(star);
                } else {
                    g2.setStroke(new BasicStroke(1.3f));
                    g2.draw(star);
                }

                g2.dispose();
            }

            @Override public int getIconWidth() { return size; }
            @Override public int getIconHeight() { return size; }
        };
    }

    public static Icon createDragHandleIcon(int size, Color color) {
        return new Icon() {
            @Override
            public void paintIcon(Component c, Graphics g, int x, int y) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(color != null ? color : Color.GRAY);

                int dot = 2;
                int col1 = x + 3;
                int col2 = x + 7;
                int y0 = y + (size - 10) / 2;

                for (int i = 0; i < 3; i++) {
                    int dy = y0 + i * 4;
                    g2.fillOval(col1, dy, dot, dot);
                    g2.fillOval(col2, dy, dot, dot);
                }

                g2.dispose();
            }

            @Override public int getIconWidth() { return 12; }
            @Override public int getIconHeight() { return size; }
        };
    }

    public static Icon createTableIcon(int size, Color color) {
        return new Icon() {
            @Override
            public void paintIcon(Component c, Graphics g, int x, int y) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(color != null ? color : Color.GRAY);
                g2.setStroke(new BasicStroke(1.3f));

                int s = size - 2;
                g2.drawRect(x + 1, y + 1, s, s);
                g2.drawLine(x + 1, y + 1 + s / 3, x + 1 + s, y + 1 + s / 3);
                g2.drawLine(x + 1 + s / 2, y + 1, x + 1 + s / 2, y + 1 + s);

                g2.dispose();
            }

            @Override public int getIconWidth() { return size; }
            @Override public int getIconHeight() { return size; }
        };
    }

    public static Icon createBoardIcon(int size, Color color) {
        return new Icon() {
            @Override
            public void paintIcon(Component c, Graphics g, int x, int y) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(color != null ? color : Color.GRAY);
                g2.setStroke(new BasicStroke(1.2f));

                int colW = (size - 6) / 3;
                int h = size - 2;
                for (int i = 0; i < 3; i++) {
                    g2.drawRoundRect(x + 1 + i * (colW + 2), y + 1, colW, h, 2, 2);
                }

                g2.dispose();
            }

            @Override public int getIconWidth() { return size; }
            @Override public int getIconHeight() { return size; }
        };
    }

    public static Icon createCalendarIcon(int size, Color color) {
        return new Icon() {
            @Override
            public void paintIcon(Component c, Graphics g, int x, int y) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(color != null ? color : Color.GRAY);
                g2.setStroke(new BasicStroke(1.3f));

                int s = size - 2;
                g2.drawRoundRect(x + 1, y + 2, s, s - 1, 2, 2);
                g2.drawLine(x + 1, y + 5, x + 1 + s, y + 5);

                // Rings
                g2.drawLine(x + 3, y, x + 3, y + 3);
                g2.drawLine(x + s - 2, y, x + s - 2, y + 3);

                g2.dispose();
            }

            @Override public int getIconWidth() { return size; }
            @Override public int getIconHeight() { return size; }
        };
    }

    public static Icon createExportIcon(int size, Color color) {
        return new Icon() {
            @Override
            public void paintIcon(Component c, Graphics g, int x, int y) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(color != null ? color : Color.GRAY);
                g2.setStroke(new BasicStroke(1.3f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));

                int w = size - 3;
                int h = size - 3;
                int x0 = x + 1;
                int y0 = y + 1;

                // Box with open top
                Path2D box = new Path2D.Float();
                box.moveTo(x0, y0 + h / 3);
                box.lineTo(x0, y0 + h);
                box.lineTo(x0 + w, y0 + h);
                box.lineTo(x0 + w, y0 + h / 3);
                g2.draw(box);

                // Arrow pointing up
                int cx = x0 + w / 2;
                g2.drawLine(cx, y0 + h - 3, cx, y0);
                g2.drawLine(cx - 3, y0 + 3, cx, y0);
                g2.drawLine(cx + 3, y0 + 3, cx, y0);

                g2.dispose();
            }

            @Override public int getIconWidth() { return size; }
            @Override public int getIconHeight() { return size; }
        };
    }

    public static Icon createPlusIcon(int size, Color color) {
        return new Icon() {
            @Override
            public void paintIcon(Component c, Graphics g, int x, int y) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(color != null ? color : Color.GRAY);
                g2.setStroke(new BasicStroke(1.6f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));

                int pad = 2;
                int midX = x + size / 2;
                int midY = y + size / 2;
                g2.drawLine(x + pad, midY, x + size - pad, midY);
                g2.drawLine(midX, y + pad, midX, y + size - pad);

                g2.dispose();
            }

            @Override public int getIconWidth() { return size; }
            @Override public int getIconHeight() { return size; }
        };
    }

    public static Icon createChevronDownIcon(int size, Color color) {
        return new Icon() {
            @Override
            public void paintIcon(Component c, Graphics g, int x, int y) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                Color drawColor = color != null ? color : (c != null ? c.getForeground() : Color.GRAY);
                g2.setColor(drawColor);
                g2.setStroke(new BasicStroke(1.6f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));

                int midX = x + size / 2;
                int midY = y + size / 2;
                int arm = Math.max(3, size / 3);
                g2.drawLine(midX - arm, midY - 2, midX, midY + 2);
                g2.drawLine(midX, midY + 2, midX + arm, midY - 2);

                g2.dispose();
            }

            @Override public int getIconWidth() { return size; }
            @Override public int getIconHeight() { return size; }
        };
    }

    public static Icon createCloseIcon(int size, Color color) {
        return new Icon() {
            @Override
            public void paintIcon(Component c, Graphics g, int x, int y) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                Color drawColor = color != null ? color : (c != null ? c.getForeground() : Color.GRAY);
                g2.setColor(drawColor);
                g2.setStroke(new BasicStroke(1.6f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));

                int pad = Math.max(2, size / 4);
                g2.drawLine(x + pad, y + pad, x + size - pad, y + size - pad);
                g2.drawLine(x + size - pad, y + pad, x + pad, y + size - pad);

                g2.dispose();
            }

            @Override public int getIconWidth() { return size; }
            @Override public int getIconHeight() { return size; }
        };
    }

    public static Icon createChevronLeftIcon(int size, Color color) {
        return new Icon() {
            @Override
            public void paintIcon(Component c, Graphics g, int x, int y) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                Color drawColor = color != null ? color : (c != null ? c.getForeground() : Color.GRAY);
                g2.setColor(drawColor);
                g2.setStroke(new BasicStroke(1.6f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));

                int midX = x + size / 2;
                int midY = y + size / 2;
                int arm = Math.max(3, size / 3);
                g2.drawLine(midX + 2, midY - arm, midX - 2, midY);
                g2.drawLine(midX - 2, midY, midX + 2, midY + arm);

                g2.dispose();
            }

            @Override public int getIconWidth() { return size; }
            @Override public int getIconHeight() { return size; }
        };
    }

    public static Icon createChevronRightIcon(int size, Color color) {
        return new Icon() {
            @Override
            public void paintIcon(Component c, Graphics g, int x, int y) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                Color drawColor = color != null ? color : (c != null ? c.getForeground() : Color.GRAY);
                g2.setColor(drawColor);
                g2.setStroke(new BasicStroke(1.6f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));

                int midX = x + size / 2;
                int midY = y + size / 2;
                int arm = Math.max(3, size / 3);
                g2.drawLine(midX - 2, midY - arm, midX + 2, midY);
                g2.drawLine(midX + 2, midY, midX - 2, midY + arm);

                g2.dispose();
            }

            @Override public int getIconWidth() { return size; }
            @Override public int getIconHeight() { return size; }
        };
    }

    public static Icon createArrowRightIcon(int size, Color color) {
        return new Icon() {
            @Override
            public void paintIcon(Component c, Graphics g, int x, int y) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                Color drawColor = color != null ? color : (c != null ? c.getForeground() : Color.GRAY);
                g2.setColor(drawColor);
                g2.setStroke(new BasicStroke(1.6f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));

                int midY = y + size / 2;
                int pad = Math.max(2, size / 5);
                int startX = x + pad;
                int endX = x + size - pad;
                g2.drawLine(startX, midY, endX, midY);
                int head = Math.max(3, size / 3);
                g2.drawLine(endX - head, midY - head, endX, midY);
                g2.drawLine(endX - head, midY + head, endX, midY);

                g2.dispose();
            }

            @Override public int getIconWidth() { return size; }
            @Override public int getIconHeight() { return size; }
        };
    }

    public static Icon createTextIcon(String text, int size, Color fgColor) {
        // Fallback for emojis and Unicode glyphs that commonly render as squares
        if (text == null || text.isBlank() || text.equals("📄") || text.equals("📝")) {
            return createDocumentIcon(size, fgColor);
        }
        if (text.equals("💬")) {
            return createDiscussionIcon(size, fgColor);
        }
        if (text.equals("🔍")) {
            return createSearchIcon(size, fgColor);
        }
        if (text.equals("📋")) {
            return createTemplateIcon(size, fgColor);
        }
        if (text.equals("⚙️") || text.equals("⚙")) {
            return createSettingsIcon(size, fgColor);
        }
        if (text.equals("🗑️") || text.equals("🗑")) {
            return createTrashIcon(size, fgColor);
        }
        if (text.equals("⭐") || text.equals("★")) {
            return createStarIcon(true, size, fgColor);
        }
        if (text.equals("☆")) {
            return createStarIcon(false, size, fgColor);
        }
        if (text.equals("⊞")) {
            return createTableIcon(size, fgColor);
        }
        if (text.equals("▾") || text.equals("▼")) {
            return createChevronDownIcon(size, fgColor);
        }
        if (text.equals("◀") || text.equals("<")) {
            return createChevronLeftIcon(size, fgColor);
        }
        if (text.equals("▶") || text.equals(">")) {
            return createChevronRightIcon(size, fgColor);
        }
        if (text.equals("✕") || text.equals("×")) {
            return createCloseIcon(size, fgColor);
        }
        if (text.equals("→") || text.equals("->")) {
            return createArrowRightIcon(size, fgColor);
        }

        return new Icon() {
            @Override
            public void paintIcon(Component c, Graphics g, int x, int y) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
                Font font = new Font("Segoe UI", Font.PLAIN, size - 2);
                g2.setFont(font);
                g2.setColor(fgColor);
                FontMetrics fm = g2.getFontMetrics();
                int textX = x + (size - fm.stringWidth(text)) / 2;
                int textY = y + ((size - fm.getHeight()) / 2) + fm.getAscent();
                g2.drawString(text, textX, textY);
                g2.dispose();
            }

            @Override public int getIconWidth() { return size; }
            @Override public int getIconHeight() { return size; }
        };
    }
}
