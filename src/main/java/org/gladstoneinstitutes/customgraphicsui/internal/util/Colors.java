package org.gladstoneinstitutes.customgraphicsui.internal.util;

import java.awt.Color;

public class Colors {
  protected static final Color colors[] = {
    new Color(0xCA554C), // red
    new Color(0x4973B7), // blue
    new Color(0xB78D48), // orange
    new Color(0x8451A0), // purple
    new Color(0xB8B048), // yellow
    new Color(0x49B67E)  // green
  };

  public static Color getColor(int i) {
    return colors[i % colors.length];
  }
}