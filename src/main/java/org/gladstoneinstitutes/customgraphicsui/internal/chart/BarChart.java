package org.gladstoneinstitutes.customgraphicsui.internal.chart;

class BarChart extends Chart {
  public String getUserName() {
    return "Bar";
  }

  public String getCgName() {
    return "barchart";
  }

  public String buildCgString() {
    if (attrs == null)
      return null;
    final StringBuffer buffer = new StringBuffer();
    attrs.buildCgString(buffer);
    return buffer.toString();
  }
}
