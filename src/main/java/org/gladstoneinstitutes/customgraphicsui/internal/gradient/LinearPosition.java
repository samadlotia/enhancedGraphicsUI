package org.gladstoneinstitutes.customgraphicsui.internal.gradient;

import java.awt.geom.Line2D;

public class LinearPosition extends Line2D.Float {
  public LinearPosition() {
    super(0.0f, 0.5f, 1.0f, 0.5f);
  }

  public LinearPosition(final String startStr, final String endStr) {
    final float[] pos0 = parsePosition(startStr);
    final float[] pos1 = parsePosition(endStr);
    super.x1 = pos0[0];
    super.y1 = pos0[1];
    super.x2 = pos1[0];
    super.y2 = pos1[1];
  }

  public LinearPosition(LinearPosition that) {
    this.x1 = that.x1;
    this.y1 = that.y1;
    this.x2 = that.x2;
    this.y2 = that.y2;
  }

  private static float[] parsePosition(final String str) {
    final String[] pieces = str.trim().split(",");
    if (pieces.length != 2)
      throw new IllegalArgumentException("Must have only 2 positions: " + str);
    final float[] pos = new float[2];
    for (int i = 0; i < 2; i++) {
      try {
        pos[i] = java.lang.Float.parseFloat(pieces[i].trim());
        if (!(0.0f <= pos[i] && pos[i] <= 1.0f))
          throw new NumberFormatException();
      } catch (NumberFormatException e) {
        throw new IllegalArgumentException(String.format("position '%s' must be a float between 0 and 1: %s", pieces[i], str));
      }
    }
    return pos;
  }

  public String toString() {
    return String.format("start=\"%f,%f\" end=\"%f,%f\"", x1, y1, x2, y2);
  }
}