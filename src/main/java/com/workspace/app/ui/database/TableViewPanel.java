package com.workspace.app.ui.database;

import com.workspace.app.app.AppContext;
import com.workspace.app.model.DatabaseDefinition;
import com.workspace.app.model.DatabaseProperty;
import com.workspace.app.model.DatabaseRow;
import com.workspace.app.model.PropertyType;
import com.workspace.app.ui.components.HoverButton;
import com.workspace.app.ui.theme.Theme;
import com.workspace.app.ui.theme.ThemeManager;
import com.workspace.app.util.IconFactory;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TableViewPanel extends JPanel {
    private final AppContext context;
    private final DatabaseDefinition database;
    private final Runnable onDataChanged;

    private JTable table;
    private DefaultTableModel tableModel;
    private List<DatabaseProperty> properties;
    private List<DatabaseRow> rows;

    public TableViewPanel(AppContext context, DatabaseDefinition database, Runnable onDataChanged) {
        this.context = context;
        this.database = database;
        this.onDataChanged = onDataChanged;

        setLayout(new BorderLayout());
        setOpaque(false);

        buildUI();
    }

    public void reload() {
        this.properties = context.getDatabaseEntityService().getProperties(database.getId());
        this.rows = context.getDatabaseEntityService().getRows(database.getId());

        String[] columnNames = properties.stream().map(DatabaseProperty::getName).toArray(String[]::new);
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public Class<?> getColumnClass(int columnIndex) {
                if (columnIndex < properties.size() && properties.get(columnIndex).getPropertyType() == PropertyType.CHECKBOX) {
                    return Boolean.class;
                }
                return Object.class;
            }

            @Override
            public boolean isCellEditable(int row, int column) {
                return true;
            }
        };

        for (DatabaseRow row : rows) {
            Object[] rowData = new Object[properties.size()];
            Map<String, Object> map = row.getValues();
            for (int i = 0; i < properties.size(); i++) {
                String prop = properties.get(i).getName();
                Object val = map != null ? map.get(prop) : null;
                if (properties.get(i).getPropertyType() == PropertyType.CHECKBOX) {
                    rowData[i] = Boolean.TRUE.equals(val);
                } else {
                    rowData[i] = val != null ? val.toString() : "";
                }
            }
            tableModel.addRow(rowData);
        }

        table.setModel(tableModel);
        table.setRowSorter(new TableRowSorter<>(tableModel));

        // Sync changes on edit
        tableModel.addTableModelListener(e -> {
            int rowIdx = e.getFirstRow();
            int colIdx = e.getColumn();
            if (rowIdx >= 0 && rowIdx < rows.size() && colIdx >= 0 && colIdx < properties.size()) {
                DatabaseRow row = rows.get(rowIdx);
                DatabaseProperty prop = properties.get(colIdx);
                Object newVal = tableModel.getValueAt(rowIdx, colIdx);
                if (row.getValues() == null) row.setValues(new HashMap<>());
                row.getValues().put(prop.getName(), newVal);
                context.getDatabaseEntityService().updateRow(row);
                if (onDataChanged != null) onDataChanged.run();
            }
        });
    }

    private void buildUI() {
        table = new JTable();
        table.setRowHeight(32);
        table.setShowGrid(true);
        table.setGridColor(ThemeManager.getTheme().border);
        table.setFont(Theme.FONT_BODY);
        table.getTableHeader().setFont(Theme.FONT_BODY.deriveFont(Font.BOLD, 12f));
        table.getTableHeader().setReorderingAllowed(false);
        table.setDefaultRenderer(Object.class, new NotionTableCellRenderer());

        table.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
        reload();

        JScrollPane scroll = new JScrollPane(table);
        scroll.setBorder(BorderFactory.createLineBorder(ThemeManager.getTheme().border));
        scroll.setPreferredSize(new Dimension(200, 200));
        add(scroll, BorderLayout.CENTER);

        // Bottom Add Row Button
        JPanel bottom = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 4));
        bottom.setOpaque(false);
        HoverButton addRowBtn = new HoverButton("New row", IconFactory.createPlusIcon(11, ThemeManager.getTheme().textPrimary));
        addRowBtn.setFont(Theme.FONT_SMALL.deriveFont(Font.BOLD));
        addRowBtn.addActionListener(e -> {
            Map<String, Object> initialValues = new HashMap<>();
            if (!properties.isEmpty()) {
                initialValues.put(properties.get(0).getName(), "Untitled");
            }
            context.getDatabaseEntityService().addRow(database.getId(), initialValues, rows.size());
            reload();
            if (onDataChanged != null) onDataChanged.run();
        });
        bottom.add(addRowBtn);
        add(bottom, BorderLayout.SOUTH);
    }

    private static class NotionTableCellRenderer extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
                                                       boolean hasFocus, int row, int column) {
            Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            Theme theme = ThemeManager.getTheme();

            c.setBackground(isSelected ? theme.bgActive : theme.bgMain);
            c.setForeground(isSelected ? theme.textPrimary : theme.textPrimary);
            setBorder(new EmptyBorder(0, 8, 0, 8));

            // Custom styling for Select tags
            String valStr = value != null ? value.toString() : "";
            if (valStr.equalsIgnoreCase("Done")) {
                setForeground(new Color(0x34, 0xC7, 0x59));
            } else if (valStr.equalsIgnoreCase("In Progress")) {
                setForeground(new Color(0x23, 0x83, 0xE2));
            } else if (valStr.equalsIgnoreCase("High")) {
                setForeground(new Color(0xFF, 0x3B, 0x30));
            } else if (valStr.equalsIgnoreCase("Medium")) {
                setForeground(new Color(0xFF, 0x95, 0x00));
            }

            return c;
        }
    }
}
