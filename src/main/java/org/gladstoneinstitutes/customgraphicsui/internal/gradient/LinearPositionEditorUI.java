package org.gladstoneinstitutes.customgraphicsui.internal.gradient;

import javax.swing.JComponent;
import javax.swing.plaf.ComponentUI;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Color;

class LinearPositionEditorUI extends ComponentUI {
  final LinearPositionEditor editor;
  public LinearPositionEditorUI(final LinearPositionEditor editor) {
    this.editor = editor;
  }

  public void paint(Graphics g, JComponent component) {
    g.setColor(Color.white);
    g.fillRect(0, 0, component.getWidth(), component.getHeight());
  }
}
