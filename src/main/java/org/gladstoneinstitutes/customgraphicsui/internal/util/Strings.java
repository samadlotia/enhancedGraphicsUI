package org.gladstoneinstitutes.customgraphicsui.internal.util;

import java.awt.Color;

import java.util.HashMap;
import java.util.Map;
import java.util.ArrayList;
import java.util.List;

import java.io.StringReader;
import java.io.StreamTokenizer;

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

  public static String colorToHex(final Color c) {
    return String.format("%02x%02x%02x%02x",
      c.getRed(),
      c.getGreen(),
      c.getBlue(),
      c.getAlpha());
  }

  public static String extractCgType(final String input) {
    final int i = input.indexOf(':');
    if (i < 0)
      return null;
    return input.substring(0, i).trim();
  }

  public static Map<String,String> extractArgMap(final String input) {
    final int i = input.indexOf(':');
    if (i < 0)
      return null;
    return toArgMap(input.substring(i + 1));
  }

  private static Map<String,String> toArgMap(String input) {
    Map<String,String> settings = new HashMap<String,String>();

    StringReader reader = new StringReader(input);
    StreamTokenizer st = new StreamTokenizer(reader);

    st.ordinaryChar('/');
    st.ordinaryChar('_');
    st.ordinaryChar('-');
    st.ordinaryChar('.');
    st.ordinaryChars('0', '9');

    st.wordChars('/', '/');
    st.wordChars('_', '_');
    st.wordChars('-', '-');
    st.wordChars('.', '.');
    st.wordChars('0', '9');

    List<String> tokenList = new ArrayList();
    int tokenIndex = 0;
    int i;
    try {
      while ((i = st.nextToken()) != StreamTokenizer.TT_EOF) {
        switch(i) {
          case '=':
            i = st.nextToken();
            if (i == StreamTokenizer.TT_WORD || i == '"') {
              tokenIndex--;
              String key = tokenList.get(tokenIndex);
              settings.put(key, st.sval);
              tokenList.remove(tokenIndex);
            }
            break;
          case '"':
          case StreamTokenizer.TT_WORD:
            tokenList.add(st.sval);
            tokenIndex++;
            break;
          default:
            break;
        }
      }
    } catch (Exception e) { return null; }

    return settings;
  }
}