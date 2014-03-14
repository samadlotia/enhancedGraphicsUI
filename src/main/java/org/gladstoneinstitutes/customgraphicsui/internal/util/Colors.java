package org.gladstoneinstitutes.customgraphicsui.internal.util;

import java.awt.Color;

public class Colors {
  private static float generateHue(final int i) {
    if (i == 0)
      return 0.0f;
    final int base = 2 * Integer.highestOneBit(i);
    return ((float) 2 * i + 1) / base - 1;
  }

  public static Color getColor(final int i) {
    if (i < 0)
      throw new IllegalArgumentException("i must be non-negative");
    return new Color(Color.HSBtoRGB(generateHue(i), 0.7f, 0.8f));
  }
}
