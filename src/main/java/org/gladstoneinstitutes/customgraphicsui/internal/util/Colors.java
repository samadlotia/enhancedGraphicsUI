package org.gladstoneinstitutes.customgraphicsui.internal.util;

import java.awt.Color;

public class Colors {
  protected static final Color colors[] = {
    Color.RED,
    Color.ORANGE,
    Color.YELLOW,
    Color.GREEN,
    Color.BLUE,
    Color.MAGENTA
  };

  public static Color getColor(int i) {
    return colors[i % colors.length];
  }
}