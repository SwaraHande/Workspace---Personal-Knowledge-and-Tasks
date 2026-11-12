package com.workspace.app.ui.theme;

import java.awt.Color;
import java.awt.Font;

public class Theme {
    public static final Font FONT_TITLE = new Font("Segoe UI", Font.BOLD, 28);
    public static final Font FONT_H1 = new Font("Segoe UI", Font.BOLD, 22);
    public static final Font FONT_H2 = new Font("Segoe UI", Font.BOLD, 18);
    public static final Font FONT_H3 = new Font("Segoe UI", Font.BOLD, 15);
    public static final Font FONT_BODY = new Font("Segoe UI", Font.PLAIN, 14);
    public static final Font FONT_SMALL = new Font("Segoe UI", Font.PLAIN, 12);
    public static final Font FONT_CODE = new Font("Consolas", Font.PLAIN, 13);

    public final boolean isDark;
    public final Color bgMain;
    public final Color bgSidebar;
    public final Color bgCard;
    public final Color bgHover;
    public final Color bgActive;
    public final Color bgCode;
    public final Color bgCallout;
    public final Color border;
    public final Color textPrimary;
    public final Color textSecondary;
    public final Color textMuted;
    public final Color accent;
    public final Color tagGreen;
    public final Color tagYellow;
    public final Color tagRed;
    public final Color tagBlue;

    public Theme(boolean isDark) {
        this.isDark = isDark;
        if (isDark) {
            bgMain = new Color(0x19, 0x19, 0x19);
            bgSidebar = new Color(0x20, 0x20, 0x20);
            bgCard = new Color(0x26, 0x26, 0x26);
            bgHover = new Color(0x2D, 0x2D, 0x2D);
            bgActive = new Color(0x37, 0x37, 0x37);
            bgCode = new Color(0x28, 0x28, 0x28);
            bgCallout = new Color(0x2B, 0x29, 0x23);
            border = new Color(0x35, 0x35, 0x35);
            textPrimary = new Color(0xEA, 0xEA, 0xEA);
            textSecondary = new Color(0xA0, 0x9F, 0x9C);
            textMuted = new Color(0x73, 0x72, 0x6E);
            accent = new Color(0x23, 0x83, 0xE2);
            tagGreen = new Color(0x2E, 0x62, 0x3B);
            tagYellow = new Color(0x89, 0x63, 0x2A);
            tagRed = new Color(0x78, 0x2E, 0x2F);
            tagBlue = new Color(0x28, 0x45, 0x6C);
        } else {
            bgMain = new Color(0xFF, 0xFF, 0xFF);
            bgSidebar = new Color(0xF7, 0xF7, 0xF5);
            bgCard = new Color(0xFB, 0xFB, 0xFA);
            bgHover = new Color(0xEF, 0xEF, 0xEE);
            bgActive = new Color(0xE3, 0xE2, 0xE0);
            bgCode = new Color(0xF2, 0xF1, 0xEE);
            bgCallout = new Color(0xFB, 0xF7, 0xEE);
            border = new Color(0xE8, 0xE7, 0xE4);
            textPrimary = new Color(0x37, 0x35, 0x2F);
            textSecondary = new Color(0x78, 0x77, 0x74);
            textMuted = new Color(0x9B, 0x9A, 0x97);
            accent = new Color(0x23, 0x83, 0xE2);
            tagGreen = new Color(0xDB, 0xED, 0xDB);
            tagYellow = new Color(0xFD, 0xF5, 0xD8);
            tagRed = new Color(0xFF, 0xE2, 0xDD);
            tagBlue = new Color(0xD3, 0xE5, 0xEF);
        }
    }
}
