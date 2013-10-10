package org.gladstoneinstitutes.customgraphicsui.internal.chart;

class PieChart extends Chart {
  public String getUserName() {
    return "Pie";
  }

  public String getCgName() {
    return "piechart";
  }

  public String buildCgString() {
    if (attrs == null)
      return null;
    final StringBuffer buffer = new StringBuffer();
    attrs.buildCgString(buffer);
    return buffer.toString();
  }
}
