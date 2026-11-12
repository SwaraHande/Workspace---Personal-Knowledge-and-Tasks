package com.workspace.app.service;

import com.workspace.app.model.*;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class ImportExportService {

    public String exportPageToMarkdown(Page page, List<PageBlock> blocks) {
        StringBuilder sb = new StringBuilder();
        sb.append("# ").append(page.getTitle()).append("\n\n");

        for (PageBlock block : blocks) {
            String c = block.getContent() != null ? block.getContent() : "";
            switch (block.getBlockType()) {
                case HEADING_1 -> sb.append("# ").append(c).append("\n\n");
                case HEADING_2 -> sb.append("## ").append(c).append("\n\n");
                case HEADING_3 -> sb.append("### ").append(c).append("\n\n");
                case BULLET_LIST -> sb.append("- ").append(c).append("\n");
                case NUMBERED_LIST -> sb.append("1. ").append(c).append("\n");
                case TODO -> sb.append(block.isChecked() ? "- [x] " : "- [ ] ").append(c).append("\n");
                case QUOTE -> sb.append("> ").append(c).append("\n\n");
                case CALLOUT -> sb.append("> 💡 ").append(c).append("\n\n");
                case CODE -> sb.append("```\n").append(c).append("\n```\n\n");
                case DIVIDER -> sb.append("---\n\n");
                default -> sb.append(c).append("\n\n");
            }
        }
        return sb.toString();
    }

    public String exportPageToText(Page page, List<PageBlock> blocks) {
        StringBuilder sb = new StringBuilder();
        sb.append(page.getTitle().toUpperCase()).append("\n");
        sb.append("=".repeat(Math.max(20, page.getTitle().length()))).append("\n\n");

        for (PageBlock block : blocks) {
            String c = block.getContent() != null ? block.getContent() : "";
            switch (block.getBlockType()) {
                case BULLET_LIST -> sb.append(" • ").append(c).append("\n");
                case TODO -> sb.append(block.isChecked() ? " [X] " : " [ ] ").append(c).append("\n");
                case DIVIDER -> sb.append("----------------------------\n");
                default -> sb.append(c).append("\n\n");
            }
        }
        return sb.toString();
    }

    public String exportPageToHtml(Page page, List<PageBlock> blocks) {
        StringBuilder sb = new StringBuilder();
        sb.append("<!DOCTYPE html>\n<html>\n<head>\n<meta charset=\"UTF-8\">\n<title>")
          .append(escapeHtml(page.getTitle())).append("</title>\n")
          .append("<style>body{font-family:-apple-system,BlinkMacSystemFont,sans-serif;max-width:800px;margin:40px auto;padding:0 20px;line-height:1.6;color:#333;} blockquote{border-left:3px solid #ccc;margin-left:0;padding-left:16px;color:#666;} pre{background:#f4f4f4;padding:12px;border-radius:6px;overflow-x:auto;} hr{border:none;border-top:1px solid #eee;margin:24px 0;} .callout{background:#f8f9fa;border-left:4px solid #2383e2;padding:12px 16px;border-radius:4px;margin:12px 0;}</style>\n")
          .append("</head>\n<body>\n");

        sb.append("<h1>").append(page.getIcon()).append(" ").append(escapeHtml(page.getTitle())).append("</h1>\n");

        for (PageBlock block : blocks) {
            String c = escapeHtml(block.getContent() != null ? block.getContent() : "");
            switch (block.getBlockType()) {
                case HEADING_1 -> sb.append("<h1>").append(c).append("</h1>\n");
                case HEADING_2 -> sb.append("<h2>").append(c).append("</h2>\n");
                case HEADING_3 -> sb.append("<h3>").append(c).append("</h3>\n");
                case BULLET_LIST -> sb.append("<ul><li>").append(c).append("</li></ul>\n");
                case NUMBERED_LIST -> sb.append("<ol><li>").append(c).append("</li></ol>\n");
                case TODO -> sb.append("<p><input type=\"checkbox\" ").append(block.isChecked() ? "checked disabled" : "disabled").append("> ").append(c).append("</p>\n");
                case QUOTE -> sb.append("<blockquote>").append(c).append("</blockquote>\n");
                case CALLOUT -> sb.append("<div class=\"callout\">💡 ").append(c).append("</div>\n");
                case CODE -> sb.append("<pre><code>").append(c).append("</code></pre>\n");
                case DIVIDER -> sb.append("<hr>\n");
                default -> sb.append("<p>").append(c).append("</p>\n");
            }
        }
        sb.append("</body>\n</html>");
        return sb.toString();
    }

    public String exportDatabaseToCsv(List<DatabaseProperty> properties, List<DatabaseRow> rows) {
        StringBuilder sb = new StringBuilder();
        // Header
        for (int i = 0; i < properties.size(); i++) {
            sb.append("\"").append(properties.get(i).getName().replace("\"", "\"\"")).append("\"");
            if (i < properties.size() - 1) sb.append(",");
        }
        sb.append("\n");

        // Rows
        for (DatabaseRow row : rows) {
            Map<String, Object> values = row.getValues();
            for (int i = 0; i < properties.size(); i++) {
                String propName = properties.get(i).getName();
                Object val = values != null ? values.get(propName) : "";
                String strVal = val != null ? val.toString() : "";
                sb.append("\"").append(strVal.replace("\"", "\"\"")).append("\"");
                if (i < properties.size() - 1) sb.append(",");
            }
            sb.append("\n");
        }
        return sb.toString();
    }

    public List<PageBlock> parseMarkdownToBlocks(String pageId, String markdown) {
        List<PageBlock> blocks = new ArrayList<>();
        String[] lines = markdown.split("\r?\n");
        int pos = 0;

        for (String rawLine : lines) {
            String line = rawLine.trim();
            if (line.isEmpty()) continue;

            String blockId = "b_" + UUID.randomUUID().toString().substring(0, 8);
            if (line.startsWith("### ")) {
                blocks.add(new PageBlock(blockId, pageId, BlockType.HEADING_3, line.substring(4), pos++));
            } else if (line.startsWith("## ")) {
                blocks.add(new PageBlock(blockId, pageId, BlockType.HEADING_2, line.substring(3), pos++));
            } else if (line.startsWith("# ")) {
                blocks.add(new PageBlock(blockId, pageId, BlockType.HEADING_1, line.substring(2), pos++));
            } else if (line.startsWith("- [ ] ") || line.startsWith("* [ ] ")) {
                PageBlock b = new PageBlock(blockId, pageId, BlockType.TODO, line.substring(6), pos++);
                b.setChecked(false);
                blocks.add(b);
            } else if (line.startsWith("- [x] ") || line.startsWith("- [X] ") || line.startsWith("* [x] ")) {
                PageBlock b = new PageBlock(blockId, pageId, BlockType.TODO, line.substring(6), pos++);
                b.setChecked(true);
                blocks.add(b);
            } else if (line.startsWith("- ") || line.startsWith("* ")) {
                blocks.add(new PageBlock(blockId, pageId, BlockType.BULLET_LIST, line.substring(2), pos++));
            } else if (line.matches("^\\d+\\.\\s.*")) {
                String content = line.replaceFirst("^\\d+\\.\\s*", "");
                blocks.add(new PageBlock(blockId, pageId, BlockType.NUMBERED_LIST, content, pos++));
            } else if (line.startsWith("> ")) {
                blocks.add(new PageBlock(blockId, pageId, BlockType.QUOTE, line.substring(2), pos++));
            } else if (line.equals("---") || line.equals("***")) {
                blocks.add(new PageBlock(blockId, pageId, BlockType.DIVIDER, "", pos++));
            } else {
                blocks.add(new PageBlock(blockId, pageId, BlockType.PARAGRAPH, rawLine, pos++));
            }
        }
        return blocks;
    }

    private String escapeHtml(String text) {
        if (text == null) return "";
        return text.replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;")
                .replace("\n", "<br>");
    }
}
