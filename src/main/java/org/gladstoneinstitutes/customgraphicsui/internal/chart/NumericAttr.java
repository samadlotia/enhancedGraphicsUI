package org.gladstoneinstitutes.customgraphicsui.internal.chart;

import java.util.List;

import java.awt.Color;

import org.cytoscape.model.CyTable;
import org.cytoscape.model.CyColumn;

import org.gladstoneinstitutes.customgraphicsui.internal.util.Colors;

class NumericAttr {
  final CyColumn col;
  boolean enabled = false;
  String label;
  Color color = null;

  public NumericAttr(final CyColumn col) {
    this.col = col;
    label = col.getName();
  }

  public CyColumn getColumn() {
    return col;
  }

  public boolean isEnabled() {
    return enabled;
  }

  public void setEnabled(final boolean enabled) {
    this.enabled = enabled;
  }

  public String getLabel() {
    return label;
  }

  public void setLabel(final String label) {
    this.label = label;
  }

  public Color getColor() {
    return color;
  }

  public String getColorHex() {
    return String.format("#%02x%02x%02x", color.getRed(), color.getGreen(), color.getBlue());
  }

  public void setColor(final Color color) {
    this.color = color;
  }

  public static void fillInList(final CyTable table, final List<NumericAttr> list) {
    list.clear();
    int i = 0;
    for (final CyColumn col : table.getColumns()) {
      final Class<?> type = col.getType();
      if (type.getSuperclass() != Number.class)
        continue;
      list.add(new NumericAttr(col));
    }
  }
}