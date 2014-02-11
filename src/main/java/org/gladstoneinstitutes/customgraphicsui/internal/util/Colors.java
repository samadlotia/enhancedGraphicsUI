package org.gladstoneinstitutes.customgraphicsui.internal.util;

import java.awt.Color;

public class Colors {
  protected static final float hues[] = {
    0.0000f,

    0.5000f,

    0.2500f,
    0.7500f,

    0.1250f,
    0.3750f,
    0.6250f,
    0.8750f,

    0.0625f,
    0.1875f,
    0.3125f,
    0.4375f,
    0.5625f,
    0.6875f,
    0.8125f,
    0.9375f 
  };

  protected static final Color colors[] = new Color[hues.length];
  static {
    for (int i = 0; i < hues.length; i++) {
      colors[i] = new Color(Color.HSBtoRGB(hues[i], 0.5f, 0.7f));
    }
  }

  public static Color getColor(int i) {
    if (i < 0)
      throw new IllegalArgumentException("i must be non-negative");
    return colors[i % colors.length];
  }
}
