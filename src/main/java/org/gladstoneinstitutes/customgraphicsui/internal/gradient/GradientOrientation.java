package org.gladstoneinstitutes.customgraphicsui.internal.gradient;

import java.util.Map;
import java.awt.geom.Line2D;

/**
 * Data structure that specifies the orientation of a gradient.
 */
public class GradientOrientation extends Line2D.Float {
  public static enum Type {
    RADIAL("Radial", "radgrad"),
    LINEAR("Linear", "lingrad");

    final String name;
    final String cgName;

    Type(final String name, final String cgName) {
      this.name = name;
      this.cgName = cgName;
    }

    public String toString() {
      return name;
    }

    public String getCgName() {
      return cgName;
    }
  }

  protected Type type;

  public GradientOrientation() {
    super(0.0f, 0.5f, 1.0f, 0.5f);
    type = Type.LINEAR;
  }
  
  public GradientOrientation(final Map<String,String> args) {
    if (args.containsKey("start") && args.containsKey("end")) {
      final String startStr = args.get("start");
      final String endStr = args.get("end");
      final float[] pos0 = parsePosition(startStr);
      final float[] pos1 = parsePosition(endStr);
      super.x1 = pos0[0];
      super.y1 = pos0[1];
      super.x2 = pos1[0];
      super.y2 = pos1[1];
      this.type = Type.LINEAR;
    } else if (args.containsKey("center") && args.containsKey("radius")) {
      final String centerStr = args.get("center");
      final float[] pos0 = parsePosition(centerStr);
      super.x1 = pos0[0];
      super.y1 = pos0[1];
      final String radiusStr = args.get("radius");
      try {
        final float radius = java.lang.Float.parseFloat(radiusStr);
        final double angle = Math.atan2(y1, x1);
        super.x2 = (float) (radius * Math.cos(angle));
        super.y2 = (float) (radius * Math.sin(angle));
      } catch (NumberFormatException e) {
        throw new IllegalArgumentException("radius must be a valid float");
      }
      this.type = Type.RADIAL;
    } else {
      throw new IllegalArgumentException("Args do not define a legal gradient orientation");
    }
  }

  public GradientOrientation(GradientOrientation that) {
    this.x1 = that.x1;
    this.y1 = that.y1;
    this.x2 = that.x2;
    this.y2 = that.y2;
    this.type = that.type;
  }

  private static float[] parsePosition(final String str) {
    final String[] pieces = str.trim().split(",");
    if (pieces.length != 2)
      throw new IllegalArgumentException("Must have only 2 positions: " + str);
    final float[] pos = new float[2];
    for (int i = 0; i < 2; i++) {
      try {
        pos[i] = java.lang.Float.parseFloat(pieces[i].trim());
      } catch (NumberFormatException e) {
        throw new IllegalArgumentException(String.format("position '%s' must be a float: %s", pieces[i], str));
      }
    }
    return pos;
  }

  public Type getType() {
    return type;
  }

  public void setType(final Type type) {
    this.type = type;
  }

  public String toString() {
    switch (type) {
      case LINEAR:
        return String.format("start=\"%f,%f\" end=\"%f,%f\"", x1, y1, x2, y2);
      case RADIAL:
        final float r = (float) Math.sqrt(Math.pow(x2 - x1, 2.0) + Math.pow(y2 - y1, 2.0));
        return String.format("center=\"%f,%f\" radius=\"%f\"", x1, y1, r);
      default:
        throw new IllegalArgumentException("Unknown type");
    }
  }
}