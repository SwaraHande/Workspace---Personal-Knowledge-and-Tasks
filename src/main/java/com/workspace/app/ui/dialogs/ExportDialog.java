package com.workspace.app.ui.dialogs;

import com.workspace.app.app.AppContext;
import com.workspace.app.model.DatabaseDefinition;
import com.workspace.app.model.Page;
import com.workspace.app.model.PageBlock;
import com.workspace.app.ui.components.HoverButton;
import com.workspace.app.ui.theme.Theme;
import com.workspace.app.ui.theme.ThemeManager;
import com.workspace.app.util.IconFactory;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.io.File;
import java.io.FileWriter;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Optional;

public class ExportDialog extends JDialog {
    private final AppContext context;
    private final Page page;
    private final List<PageBlock> blocks;

    public ExportDialog(Frame parent, AppContext context, Page page, List<PageBlock> blocks) {
        super(parent, "Export Page", true);
        this.context = context;
        this.page = page;
        this.blocks = blocks;

        setSize(420, 320);
        setLocationRelativeTo(parent);
        setLayout(new BorderLayout());

        buildUI();
    }

    private void buildUI() {
        Theme theme = ThemeManager.getTheme();

        JPanel root = new JPanel();
        root.setLayout(new BoxLayout(root, BoxLayout.Y_AXIS));
        root.setBackground(theme.bgMain);
        root.setBorder(new EmptyBorder(16, 20, 16, 20));

        JLabel title = new JLabel("Export '" + page.getTitle() + "'");
        title.setFont(Theme.FONT_BODY.deriveFont(Font.BOLD, 16f));
        title.setForeground(theme.textPrimary);
        root.add(title);
        root.add(Box.createVerticalStrut(16));

        // Format selection buttons
        HoverButton mdBtn = new HoverButton("Export as Markdown (.md)", IconFactory.createDocumentIcon(15, theme.textPrimary));
        mdBtn.setFont(Theme.FONT_BODY);
        mdBtn.addActionListener(e -> exportToFile("md", context.getImportExportService().exportPageToMarkdown(page, blocks)));

        HoverButton htmlBtn = new HoverButton("Export as HTML (.html)", IconFactory.createExportIcon(15, theme.textPrimary));
        htmlBtn.setFont(Theme.FONT_BODY);
        htmlBtn.addActionListener(e -> exportToFile("html", context.getImportExportService().exportPageToHtml(page, blocks)));

        HoverButton txtBtn = new HoverButton("Export as Plain Text (.txt)", IconFactory.createDocumentIcon(15, theme.textPrimary));
        txtBtn.setFont(Theme.FONT_BODY);
        txtBtn.addActionListener(e -> exportToFile("txt", context.getImportExportService().exportPageToText(page, blocks)));

        root.add(mdBtn);
        root.add(Box.createVerticalStrut(8));
        root.add(htmlBtn);
        root.add(Box.createVerticalStrut(8));
        root.add(txtBtn);
        root.add(Box.createVerticalStrut(8));

        // Check if database exists on this page for CSV export
        Optional<DatabaseDefinition> dbOpt = context.getDatabaseEntityService().getDatabaseForPage(page.getId());
        if (dbOpt.isPresent()) {
            HoverButton csvBtn = new HoverButton("Export Database as CSV (.csv)", IconFactory.createTableIcon(15, theme.textPrimary));
            csvBtn.setFont(Theme.FONT_BODY);
            csvBtn.addActionListener(e -> {
                DatabaseDefinition db = dbOpt.get();
                String csv = context.getImportExportService().exportDatabaseToCsv(
                        context.getDatabaseEntityService().getProperties(db.getId()),
                        context.getDatabaseEntityService().getRows(db.getId())
                );
                exportToFile("csv", csv);
            });
            root.add(csvBtn);
        }

        add(root, BorderLayout.CENTER);

        // Cancel button
        JPanel bottom = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        bottom.setBackground(theme.bgCard);
        HoverButton closeBtn = new HoverButton("Cancel");
        closeBtn.addActionListener(e -> dispose());
        bottom.add(closeBtn);
        add(bottom, BorderLayout.SOUTH);
    }

    private void exportToFile(String extension, String content) {
        JFileChooser chooser = new JFileChooser();
        chooser.setSelectedFile(new File(page.getTitle().replaceAll("[^a-zA-Z0-9.-]", "_") + "." + extension));
        int res = chooser.showSaveDialog(this);
        if (res == JFileChooser.APPROVE_OPTION) {
            File dest = chooser.getSelectedFile();
            try (FileWriter fw = new FileWriter(dest, StandardCharsets.UTF_8)) {
                fw.write(content);
                JOptionPane.showMessageDialog(this, "File exported successfully to:\n" + dest.getAbsolutePath(),
                        "Export Successful", JOptionPane.INFORMATION_MESSAGE);
                dispose();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Failed to write file: " + ex.getMessage(),
                        "Export Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}
