package org.gladstoneinstitutes.customgraphicsui.internal.gradient;

import java.awt.Color;

import java.util.List;
import java.util.Collections;
import java.util.ArrayList;

/**
 * Data structure representing a color gradient.
 *
 * A color gradient is an ordered list of stops, where a stop
 * denotes color and position, where position is always between 0 and 1.
 * Stops are ordered by position.
 *
 * This data structure requires there to be at least two stops.
 */
public class Gradient {
  public class Stop implements Comparable<Stop> {
    protected float position;
    protected Color color;

    public Stop(final Stop stop) {
      this(stop.getPosition(), stop.getColor());
    }

    public Stop(final float position, final Color color) {
      if (0.0f > position || position > 1.0f)
        throw new IllegalArgumentException("position must be in [0.0, 1.0]");
      if (color == null)
        throw new IllegalArgumentException("color cannot be null");
      this.position = position;
      this.color = color;
    }

    /**
     * Parses a string representing a stop.
     * The acceptable format is comma-separated list of 5 numbers:
     *   R,G,B,A,P
     * where R, G, B, and A are integers between 0 and 255 denoting the stop's color channels,
     * and P is a float between 0 and 1 denoting the stop's position.
     */
    public Stop(final String str) {
      final String[] pieces = str.trim().split(",");
      if (pieces.length != 5)
        throw new IllegalArgumentException("Must contain 5 numbers separated by commas: " + str);
      final int[] channels = new int[4];
      for (int i = 0; i < 4; i++) {
        try {
          final int n = Integer.parseInt(pieces[i].trim());
          if (!(0 <= n && n <= 255))
            throw new NumberFormatException();
          channels[i] = n;
        } catch (NumberFormatException e) {
          throw new IllegalArgumentException(String.format("'%s' must be an integer between 0 and 255: %s", pieces[i], str));
        }
      }
      this.color = new Color(channels[0], channels[1], channels[2], channels[3]);
      try {
        this.position = Float.parseFloat(pieces[4].trim());
        if (!(0.0f <= position && position <= 1.0f))
          throw new NumberFormatException();
      } catch (NumberFormatException e) {
        throw new IllegalArgumentException(String.format("'%s' must be a float between 0 and 1: %s", pieces[4], str));
      }
    }

    public float getPosition() {
      return position;
    }

    public Color getColor() {
      return color;
    }

    public void setPosition(final float position) {
      if (0.0f > position || position > 1.0f)
        throw new IllegalArgumentException("position must be in [0.0, 1.0]");
      this.position = position;
      Collections.sort(stops);
    }

    public void setColor(final Color color) {
      if (color == null)
        throw new IllegalArgumentException("color cannot be null");
      this.color = color;
    }

    public int compareTo(final Stop that) {
      return Double.compare(this.getPosition(), that.getPosition());
    }

    public String toString() {
      return String.format("%d,%d,%d,%d,%f", color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha(), position);
    }
  }	

  List<Stop> stops = new ArrayList<Stop>();

  public Gradient() {
    this(Color.BLUE, Color.BLACK, Color.YELLOW);
  }

  /**
   * Duplicate the given gradient.
   */
  public Gradient(Gradient that) {
    for (Stop stop : that.getStops())
      this.add(stop);
    Collections.sort(stops);
  }

  /**
   * Creates a gradient of equally positioned colors.
   * There must be at least two colors.
   */
  public Gradient(Color ... colors) {
    final int n = colors.length;
    if (n < 2)
      throw new IllegalArgumentException("must be at least two colors");
    for (int i = 0; i < n; i++)
      this.add(i / ((float) (n - 1)), colors[i]);
  }

  /**
   * Parses a string that represents a gradient.
   * This takes this format:
   *   stop1|stop2|stop3 ...
   * Where stopI follows the format for stops defined by Stop(String).
   * There must be at least two stops.
   */
  public Gradient(final String str) {
    final String[] pieces = str.trim().split("\\|");
    if (pieces.length < 2)
      throw new IllegalArgumentException("Must be at least two stops: " + str);
    for (final String piece : pieces) {
      stops.add(new Stop(piece.trim()));
    }
  }

  public boolean add(final Stop stop) {
    final boolean result = stops.add(new Stop(stop));
    Collections.sort(stops);
    return result;
  }

  public Stop add(final float position, final Color color) {
    final Stop stop = new Stop(position, color);
    stops.add(stop);
    Collections.sort(stops);
    return stop;
  }

  public boolean remove(final Stop stop) {
    if (stops.size() <= 2)
      throw new IllegalStateException("Gradient must have at least two stops");
    final boolean result = stops.remove(stop);
    Collections.sort(stops);
    return result;
  }

  public Iterable<Stop> getStops() {
    return stops;
  }

  public int size() {
    return stops.size();
  }

  public String toString() {
    final StringBuffer buffer = new StringBuffer();
    for (int i = 0; i < stops.size(); i++) {
      buffer.append(stops.get(i).toString());
      if (i != stops.size() - 1)
        buffer.append('|');
    }
    return buffer.toString();
  }

  public int indexOfStop(final Stop stop) {
    return stops.indexOf(stop);
  }

  public Stop stopAtIndex(final int i) {
    return stops.get(i);
  }
}