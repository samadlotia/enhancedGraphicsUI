package org.gladstoneinstitutes.customgraphicsui.internal.chart;

class HeatstripChart extends Chart {
  public String getUserName() {
    return "Heatstrip";
  }

  public String getCgName() {
    return "heatstripchart";
  }

  public String buildCgString() {
    if (attrs == null)
      return null;
    final StringBuffer buffer = new StringBuffer();
    attrs.buildCgString(buffer);
    return buffer.toString();
  }
}
