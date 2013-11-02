package org.gladstoneinstitutes.customgraphicsui.internal.chart;

import javax.swing.JTable;
import javax.swing.JScrollPane;
import javax.swing.JPanel;
import javax.swing.JDialog;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.TransferHandler;
import javax.swing.AbstractCellEditor;
import javax.swing.ListSelectionModel;
import javax.swing.DropMode;
import javax.swing.JComponent;
import javax.swing.JColorChooser;

import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.table.TableModel;

import javax.swing.event.TableModelEvent;

import java.awt.Component;
import java.awt.Color;
import java.awt.GridBagLayout;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.DataFlavor;
  
import java.util.EventObject;
import java.util.List;
import java.util.ArrayList;
import java.util.Collections;

import org.cytoscape.model.CyTable;
import org.cytoscape.model.CyColumn;

import org.gladstoneinstitutes.customgraphicsui.internal.util.EasyGBC;
import org.gladstoneinstitutes.customgraphicsui.internal.util.Colors;
import org.gladstoneinstitutes.customgraphicsui.internal.util.Strings;

class NumericAttributesTable extends JTable {
  final AttributeTableModel model = new AttributeTableModel();

  public NumericAttributesTable() {
    super.setModel(model);
    
    final TableColumn activeCol = super.getColumnModel().getColumn(0);
    activeCol.setCellRenderer(new BooleanCellHandler());
    activeCol.setCellEditor(new BooleanCellHandler());
    activeCol.setMaxWidth(35);
    activeCol.setMinWidth(35);

    super.setDragEnabled(true);
    super.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    super.setDropMode(DropMode.INSERT_ROWS);
    super.setTransferHandler(new InternalTransferHandler());
  }

  public void forCyTable(final CyTable table) {
    model.forCyTable(table);
  }

  public void appendCgString(final StringBuffer buffer) {
    model.appendCgString(buffer);
  }

  static class AttributeRow {
    public boolean enabled = false;
    public CyColumn attribute;
    public String label;
  }

  static class AttributeTableModel extends AbstractTableModel {
    final List<AttributeRow> rows = new ArrayList<AttributeRow>();

    public void appendCgString(final StringBuffer buffer) {
      int count = 0;
      for (int i = 0; i < rows.size(); i++) {
        final AttributeRow row = rows.get(i);
        if (row.enabled)
          count++;
      }
      if (count < 2)
        return;

      buffer.append("attributelist=\"");
      for (int i = 0; i < rows.size(); i++) {
        final AttributeRow row = rows.get(i);
        if (!row.enabled) continue;
        buffer.append(row.attribute.getName());
        buffer.append(',');
      }
      buffer.deleteCharAt(buffer.length() - 1);
      buffer.append("\" ");

      buffer.append("labellist=\"");
      for (int i = 0; i < rows.size(); i++) {
        final AttributeRow row = rows.get(i);
        if (!row.enabled) continue;
        buffer.append(row.label);
        buffer.append(',');
      }
      buffer.deleteCharAt(buffer.length() - 1);
      buffer.append("\" ");
    }

    public void forCyTable(final CyTable table) {
      rows.clear();
      for (final CyColumn col : table.getColumns()) {
        final Class<?> type = col.getType();
        if (type.getSuperclass() != Number.class)
          continue;
        final AttributeRow row = new AttributeRow();
        row.enabled = false;
        row.attribute = col;
        row.label = col.getName();
        rows.add(row);
      }
      super.fireTableRowsInserted(0, rows.size() - 1);
    }

    public int getColumnCount() {
      return 4;
    }

    public int getRowCount() {
      return rows.size();
    }

    public Class<?> getColumnClass(int col) {
      switch (col) {
        case 0: return Boolean.class;
        default: return String.class;
      }
    }

    public String getColumnName(int col) {
      switch (col) {
        case 0: return "";
        case 1: return "Attribute";
        case 2: return "Label in Chart";
        default: return null;
      }
    }

    public boolean isCellEditable(int row, int col) {
      switch (col) {
        case 0: 
        case 2: return true;
        default: return false;
      }
    }

    public Object getValueAt(int row, int col) {
      final AttributeRow attrRow = rows.get(row);
      switch (col) {
        case 0: return attrRow.enabled;
        case 1: return attrRow.attribute.getName();
        case 2: return attrRow.label;
        default: return null;
      }
    }

    int colorIndex = 0;
    public void setValueAt(Object val, int row, int col) {
      final AttributeRow attrRow = rows.get(row);
      switch (col) {
        case 0:
          attrRow.enabled = (Boolean) val;
            break;
        case 2:
          attrRow.label = (String) val;
          break;
      }
      super.fireTableChanged(new TableModelEvent(this, row, col));
    }

    public void move(int source, int target) {
      final int shift = target - source;
      if (shift < 0) {
        Collections.rotate(rows.subList(target, source + 1), shift);
      } else {
        Collections.rotate(rows.subList(source, target + 1), shift);
      }
      super.fireTableDataChanged();
    }
  }

  class BooleanCellHandler extends AbstractCellEditor implements TableCellEditor, TableCellRenderer {
    final JCheckBox checkBox = new JCheckBox();

    public BooleanCellHandler() {
      checkBox.setBackground(NumericAttributesTable.super.getBackground());
      checkBox.setFocusPainted(false);
      checkBox.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          fireEditingStopped();
        }
      });
    }
   
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
      checkBox.setSelected((Boolean) value);
      return checkBox;
    }

    public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
      checkBox.setSelected((Boolean) value);
      return checkBox;
    }

    public Object getCellEditorValue() {
      return Boolean.valueOf(checkBox.isSelected());
    }

    public boolean shouldSelectCell(EventObject anEvent) {
      return false;
    }
  }

  class InternalTransferHandler extends TransferHandler {
    public int getSourceActions(JComponent c) {
      return TransferHandler.MOVE;
    }

    public Transferable createTransferable(JComponent c) {
      return new StringSelection(Integer.toString(NumericAttributesTable.this.getSelectedRow()));
    }

    private Integer getRowIndexFromStringData(final TransferHandler.TransferSupport support) {
      final Transferable transferable = support.getTransferable();
      for (final DataFlavor flavor : support.getDataFlavors()) {
        if (!flavor.isFlavorTextType())
          continue;
        try {
          final Integer rowIndex = Integer.parseInt((String) transferable.getTransferData(flavor));
          if (0 <= rowIndex && rowIndex < NumericAttributesTable.this.getRowCount())
            return rowIndex;
        } catch (Exception e) {}
      }
      return null;
    }

    public boolean canImport(TransferHandler.TransferSupport support) {
      if ((support.getDropAction() & TransferHandler.MOVE) == 0)
        return false;
      return getRowIndexFromStringData(support) != null;
    }

    public boolean importData(TransferHandler.TransferSupport support) {
      final Integer sourceRow = getRowIndexFromStringData(support);
      if (sourceRow == null)
        return false;
      int targetRow = ((JTable.DropLocation) support.getDropLocation()).getRow();
      if (targetRow < 0)
        targetRow = 0;
      if (sourceRow < targetRow)
        targetRow -= 1;

      model.move(sourceRow, targetRow);
      NumericAttributesTable.this.setRowSelectionInterval(targetRow, targetRow);
      return true;
    }
  }
}