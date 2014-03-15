package org.gladstoneinstitutes.customgraphicsui.internal.chart;

import javax.swing.table.AbstractTableModel;
import javax.swing.event.TableModelEvent;

import java.awt.Color;

import java.util.ArrayList;
import java.util.List;
import java.util.Collections;

import org.cytoscape.model.CyTable;

import org.gladstoneinstitutes.customgraphicsui.internal.util.Colors;

class NumericAttrsModel extends AbstractTableModel {
  final List<NumericAttr> rows;
  final boolean showColorCol;

  public NumericAttrsModel(final List<NumericAttr> rows, final boolean showColorCol) {
    this.rows = rows;
    this.showColorCol = showColorCol;
  }

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
