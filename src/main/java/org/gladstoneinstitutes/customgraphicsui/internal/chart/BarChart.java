package org.gladstoneinstitutes.customgraphicsui.internal.chart;

class BarChart extends Chart {
  public String getUserName() {
    return "Bar";
  }

  public String getCgName() {
    return "barchart";
  }

  public String buildCgString() {
    final StringBuffer buffer = new StringBuffer();
    if (attrs != null) {
      attrs.buildCgString(buffer);
    }
    return buffer.toString();
  }
}
