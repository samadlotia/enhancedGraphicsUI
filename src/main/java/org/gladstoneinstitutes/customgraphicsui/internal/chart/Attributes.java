package org.gladstoneinstitutes.customgraphicsui.internal.chart;

import org.gladstoneinstitutes.customgraphicsui.internal.util.Strings;

class Attributes {
  final String[] numericColNames;
  final String[] labels;
  final String numericListColName;

  public Attributes(final String[] numericColNames, final String[] labels) {
    this.numericColNames = numericColNames;
    this.labels = labels;
    this.numericListColName = null;
  }

  public Attributes(final String numericListColName, final String[] labels) {
    this.numericColNames = null;
    this.labels = labels;
    this.numericListColName = numericListColName;
  }

  public StringBuffer buildCgString(StringBuffer buffer) {
    if (numericColNames != null) {
      buffer.append("attributelist=\"");
      Strings.join(numericColNames, ",", buffer);
      buffer.append("\" ");
    }

    buffer.append("labels=\"");
    Strings.join(labels, ",", buffer);
    buffer.append("\" ");

    return buffer;
  }
}