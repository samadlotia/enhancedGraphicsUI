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

class NumericAttrsTable extends JTable {
  final boolean showColorCol;
  final AttributeTableModel model = new AttributeTableModel();

  public NumericAttrsTable(final boolean showColorCol) {
    this.showColorCol = showColorCol;
    super.setModel(model);
    
    final TableColumn activeCol = super.getColumnModel().getColumn(0);
    activeCol.setCellRenderer(new BooleanCellHandler());
    activeCol.setCellEditor(new BooleanCellHandler());
    activeCol.setMaxWidth(35);
    activeCol.setMinWidth(35);

    if (showColorCol) {
      final TableColumn colorCol = super.getColumnModel().getColumn(3);
      colorCol.setCellRenderer(new ColorCellRenderer());
      colorCol.setCellEditor(new ColorCellEditor());
      colorCol.setMaxWidth(50);
      colorCol.setMinWidth(50);
    }

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

  class AttributeTableModel extends AbstractTableModel {
    final List<NumericAttr> rows = new ArrayList<NumericAttr>();

    public void appendCgString(final StringBuffer buffer) {
      // count the number of enabled rows
      int count = 0;
      for (int i = 0; i < rows.size(); i++) {
        final NumericAttr row = rows.get(i);
        if (row.isEnabled())
          count++;
      }
      if (count < 2) // don't fill in the cg string if we don't have enough enabled rows
        return;

      buffer.append("attributelist=\"");
      for (int i = 0; i < rows.size(); i++) {
        final NumericAttr row = rows.get(i);
        if (!row.isEnabled()) continue;
        buffer.append(row.getColumn().getName());
        buffer.append(',');
      }
      buffer.deleteCharAt(buffer.length() - 1);
      buffer.append("\" ");

      buffer.append("labellist=\"");
      for (int i = 0; i < rows.size(); i++) {
        final NumericAttr row = rows.get(i);
        if (!row.isEnabled()) continue;
        buffer.append(row.getLabel());
        buffer.append(',');
      }
      buffer.deleteCharAt(buffer.length() - 1);
      buffer.append("\" ");

      if (showColorCol) {
        buffer.append("colorlist=\"");
        for (int i = 0; i < rows.size(); i++) {
          final NumericAttr row = rows.get(i);
          if (!row.isEnabled()) continue;
          buffer.append(row.getColorHex());
          buffer.append(',');
        }
        buffer.deleteCharAt(buffer.length() - 1);
        buffer.append("\" ");
      }
    }

    public void forCyTable(final CyTable table) {
      NumericAttr.fillInList(table, rows);
      super.fireTableRowsInserted(0, rows.size() - 1);
    }

    public int getColumnCount() {
      if (showColorCol)
        return 4;
      else
        return 3;
    }

    public int getRowCount() {
      return rows.size();
    }

    public Class<?> getColumnClass(int col) {
      switch (col) {
        case 0: return Boolean.class;
        case 3: return Color.class;
        default: return String.class;
      }
    }

    public String getColumnName(int col) {
      switch (col) {
        case 0: return "";
        case 1: return "Attribute";
        case 2: return "Label in Chart";
        case 3: return "Color";
        default: return null;
      }
    }

    public boolean isCellEditable(int row, int col) {
      switch (col) {
        case 0: 
        case 2: 
        case 3: return true;
        default: return false;
      }
    }

    public Object getValueAt(int row, int col) {
      final NumericAttr attrRow = rows.get(row);
      switch (col) {
        case 0: return attrRow.isEnabled();
        case 1: return attrRow.getColumn().getName();
        case 2: return attrRow.getLabel();
        case 3: return attrRow.getColor();
        default: return null;
      }
    }

    int colorIndex = 0;
    public void setValueAt(Object val, int row, int col) {
      final NumericAttr attrRow = rows.get(row);
      switch (col) {
        case 0:
          attrRow.setEnabled((Boolean) val);
          if (attrRow.isEnabled() && attrRow.getColor() == null) {
            attrRow.setColor(Colors.getColor(colorIndex++));
            super.fireTableChanged(new TableModelEvent(this, row, 3));
          }
          break;
        case 2:
          attrRow.setLabel((String) val);
          break;
        case 3:
          attrRow.setColor((Color) val);
          if (!attrRow.isEnabled() && val != null) {
            attrRow.setEnabled(true);
            super.fireTableChanged(new TableModelEvent(this, row, 0));
          }
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
      checkBox.setBackground(NumericAttrsTable.super.getBackground());
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

  class ColorCellRenderer implements TableCellRenderer {
    final JLabel label = new JLabel();

    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
      if (value == null) {
        label.setOpaque(false);
      } else {
        label.setBackground((Color) value);
        label.setOpaque(true);
      }
      return label;
    }
  }

  public class ColorCellEditor extends AbstractCellEditor implements TableCellEditor, ActionListener {
    Color currentColor;
    JButton button;
    JColorChooser colorChooser;
    JDialog dialog;
    protected static final String EDIT = "edit";

    public ColorCellEditor() {
      button = new JButton();
      button.setActionCommand(EDIT);
      button.addActionListener(this);
      button.setBorderPainted(false);

        //Set up the dialog that the button brings up.
      colorChooser = new JColorChooser();
      dialog = JColorChooser.createDialog(button, "Pick a Color", true, colorChooser, this, null);
    }

    public void actionPerformed(ActionEvent e) {
      if (EDIT.equals(e.getActionCommand())) {
            //The user has clicked the cell, so
            //bring up the dialog.
        button.setBackground(currentColor);
        colorChooser.setColor(currentColor);
        dialog.setVisible(true);

            fireEditingStopped(); //Make the renderer reappear.

        } else { //User pressed dialog's "OK" button.
        currentColor = colorChooser.getColor();
      }
    }

    //Implement the one CellEditor method that AbstractCellEditor doesn't.
    public Object getCellEditorValue() {
      return currentColor;
    }

    //Implement the one method defined by TableCellEditor.
    public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
      currentColor = (Color)value;
      return button;
    }
  }

  class InternalTransferHandler extends TransferHandler {
    public int getSourceActions(JComponent c) {
      return TransferHandler.MOVE;
    }

    public Transferable createTransferable(JComponent c) {
      return new StringSelection(Integer.toString(NumericAttrsTable.this.getSelectedRow()));
    }

    private Integer getRowIndexFromStringData(final TransferHandler.TransferSupport support) {
      final Transferable transferable = support.getTransferable();
      for (final DataFlavor flavor : support.getDataFlavors()) {
        if (!flavor.isFlavorTextType())
          continue;
        try {
          final Integer rowIndex = Integer.parseInt((String) transferable.getTransferData(flavor));
          if (0 <= rowIndex && rowIndex < NumericAttrsTable.this.getRowCount())
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
      NumericAttrsTable.this.setRowSelectionInterval(targetRow, targetRow);
      return true;
    }
  }
}
