package org.gladstoneinstitutes.customgraphicsui.internal.util;

public class Strings {
	public static String join(String[] pieces, String joiner) {
    final StringBuffer buffer = new StringBuffer();
    join(pieces, joiner, buffer);
    return buffer.toString();
  }

  public static void join(final String[] pieces, final String joiner, final StringBuffer buffer) {
    if (pieces.length == 0)
      return;
    for (int i = 0; i < pieces.length - 1; i++) {
      buffer.append(pieces[i]);
      buffer.append(joiner);
    }
    buffer.append(pieces[pieces.length - 1]);
  }
}