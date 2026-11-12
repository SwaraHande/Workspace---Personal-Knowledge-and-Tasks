package com.workspace.app.ui.dialogs;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.workspace.app.app.AppContext;
import com.workspace.app.model.BlockType;
import com.workspace.app.model.Page;
import com.workspace.app.model.PageBlock;
import com.workspace.app.model.Template;
import com.workspace.app.ui.components.HoverButton;
import com.workspace.app.ui.theme.Theme;
import com.workspace.app.ui.theme.ThemeManager;
import com.workspace.app.util.IconFactory;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class TemplatesDialog extends JDialog {
    private final AppContext context;
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final DefaultListModel<Template> listModel = new DefaultListModel<>();
    private final JList<Template> templateList = new JList<>(listModel);
    private JTextArea previewArea;
    private Template selectedTemplate;

    public TemplatesDialog(Frame parent, AppContext context) {
        super(parent, "Templates Gallery", true);
        this.context = context;

        setSize(700, 480);
        setLocationRelativeTo(parent);
        setLayout(new BorderLayout());

        buildUI();
    }

    private void buildUI() {
        Theme theme = ThemeManager.getTheme();

        // Split: Left list of templates, Right preview
        JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        split.setDividerLocation(260);
        split.setBorder(null);

        // Left List
        JPanel leftPanel = new JPanel(new BorderLayout());
        leftPanel.setBackground(theme.bgCard);
        leftPanel.setBorder(new EmptyBorder(12, 12, 12, 12));

        JLabel title = new JLabel("Templates");
        title.setFont(Theme.FONT_BODY.deriveFont(Font.BOLD, 15f));
        title.setForeground(theme.textPrimary);
        leftPanel.add(title, BorderLayout.NORTH);

        List<Template> templates = context.getTemplateRepository().findAll();
        for (Template t : templates) {
            listModel.addElement(t);
        }

        templateList.setOpaque(false);
        templateList.setCellRenderer(new TemplateCellRenderer());
        templateList.addListSelectionListener(e -> {
            selectedTemplate = templateList.getSelectedValue();
            updatePreview();
        });

        JScrollPane leftScroll = new JScrollPane(templateList);
        leftScroll.setBorder(null);
        leftScroll.setOpaque(false);
        leftScroll.getViewport().setOpaque(false);
        leftPanel.add(leftScroll, BorderLayout.CENTER);
        split.setLeftComponent(leftPanel);

        // Right Preview
        JPanel rightPanel = new JPanel(new BorderLayout());
        rightPanel.setBackground(theme.bgMain);
        rightPanel.setBorder(new EmptyBorder(16, 20, 16, 20));

        previewArea = new JTextArea();
        previewArea.setEditable(false);
        previewArea.setOpaque(false);
        previewArea.setFont(Theme.FONT_BODY);
        previewArea.setForeground(theme.textPrimary);
        previewArea.setLineWrap(true);
        previewArea.setWrapStyleWord(true);

        JScrollPane previewScroll = new JScrollPane(previewArea);
        previewScroll.setBorder(null);
        previewScroll.setOpaque(false);
        previewScroll.getViewport().setOpaque(false);
        rightPanel.add(previewScroll, BorderLayout.CENTER);

        // Bottom Action
        JPanel bottomBar = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 10));
        bottomBar.setBackground(theme.bgCard);
        bottomBar.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, theme.border));

        HoverButton cancelBtn = new HoverButton("Cancel");
        cancelBtn.addActionListener(e -> dispose());
        bottomBar.add(cancelBtn);

        HoverButton useBtn = new HoverButton("Use this template");
        useBtn.setFont(Theme.FONT_BODY.deriveFont(Font.BOLD));
        useBtn.setCustomBg(theme.accent);
        useBtn.setForeground(Color.WHITE);
        useBtn.addActionListener(e -> applySelectedTemplate());
        bottomBar.add(useBtn);

        split.setRightComponent(rightPanel);
        add(split, BorderLayout.CENTER);
        add(bottomBar, BorderLayout.SOUTH);

        if (!listModel.isEmpty()) {
            templateList.setSelectedIndex(0);
        }
    }

    private void updatePreview() {
        if (selectedTemplate == null) return;
        StringBuilder sb = new StringBuilder();
        sb.append(selectedTemplate.getIcon()).append("  ").append(selectedTemplate.getName()).append("\n");
        sb.append(selectedTemplate.getDescription()).append("\n\n");
        sb.append("--------------------------------------------------\n\n");

        try {
            List<Map<String, Object>> blockData = objectMapper.readValue(
                    selectedTemplate.getContentJson(), new TypeReference<>() {}
            );
            for (Map<String, Object> map : blockData) {
                String type = (String) map.getOrDefault("type", "PARAGRAPH");
                String content = (String) map.getOrDefault("content", "");
                sb.append("[").append(type).append("] ").append(content).append("\n");
            }
        } catch (Exception e) {
            sb.append("Template content ready to insert.");
        }

        previewArea.setText(sb.toString());
        previewArea.setCaretPosition(0);
    }

    private void applySelectedTemplate() {
        if (selectedTemplate == null) return;

        Page newPage = context.getPageService().createPage(
                context.getCurrentWorkspace().getId(),
                null,
                selectedTemplate.getName(),
                selectedTemplate.getIcon()
        );

        try {
            List<Map<String, Object>> blockData = objectMapper.readValue(
                    selectedTemplate.getContentJson(), new TypeReference<>() {}
            );
            List<PageBlock> blocks = new ArrayList<>();
            for (int i = 0; i < blockData.size(); i++) {
                Map<String, Object> map = blockData.get(i);
                String typeStr = (String) map.getOrDefault("type", "PARAGRAPH");
                String content = (String) map.getOrDefault("content", "");
                BlockType bt;
                try {
                    bt = BlockType.valueOf(typeStr);
                } catch (Exception ex) {
                    bt = BlockType.PARAGRAPH;
                }
                PageBlock b = new PageBlock("b_" + UUID.randomUUID().toString().substring(0, 8), newPage.getId(), bt, content, i);
                if (map.containsKey("checked")) {
                    b.setChecked(Boolean.TRUE.equals(map.get("checked")));
                }
                blocks.add(b);
            }
            context.getBlockService().saveAll(newPage.getId(), blocks);
        } catch (Exception e) {
            // fallback
        }

        context.notifyTreeRefresh();
        context.setActivePage(newPage);
        dispose();
    }

    private static class TemplateCellRenderer extends DefaultListCellRenderer {
        @Override
        public Component getListCellRendererComponent(JList<?> list, Object value, int index,
                                                      boolean isSelected, boolean cellHasFocus) {
            JPanel p = new JPanel(new BorderLayout(8, 0));
            Theme theme = ThemeManager.getTheme();
            p.setOpaque(true);
            p.setBackground(isSelected ? theme.bgActive : theme.bgCard);
            p.setBorder(new EmptyBorder(8, 10, 8, 10));

            if (value instanceof Template t) {
                JLabel icon = new JLabel(IconFactory.createTextIcon(t.getIcon(), 16, theme.textPrimary));
                p.add(icon, BorderLayout.WEST);

                JLabel name = new JLabel(t.getName());
                name.setFont(Theme.FONT_BODY);
                name.setForeground(theme.textPrimary);
                p.add(name, BorderLayout.CENTER);
            }
            return p;
        }
    }
}
