package org.gladstoneinstitutes.customgraphicsui.internal.gradient;

import javax.swing.JComponent;
import java.awt.Dimension;
import java.awt.Cursor;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Point2D;

import org.gladstoneinstitutes.customgraphicsui.internal.CustomGraphicsFactoryManager;

class LinearPositionEditor extends JComponent {
  public static final String POSITION_CHANGED = "position changed";

  static final float ANCHOR_TOLERANCE = 0.05f;
  static final Cursor DEFAULT_CURSOR = Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR);
  static final Cursor HAND_CURSOR = Cursor.getPredefinedCursor(Cursor.HAND_CURSOR);

  final GradientEditor gradientEditor;
  final CustomGraphicsFactoryManager manager;
  final LinearPositionEditorUI ui;

  LinearPosition position = new LinearPosition();

  public LinearPositionEditor(final GradientEditor gradientEditor, final CustomGraphicsFactoryManager manager) {
    this.gradientEditor = gradientEditor;
    this.manager = manager;
    this.ui = new LinearPositionEditorUI(this, gradientEditor, manager.getFactory("lingrad"));
    super.setUI(ui);
    super.setPreferredSize(new Dimension(400, 400));
    final PositionUpdater positionUpdater = new PositionUpdater();
    super.addMouseListener(positionUpdater);
    super.addMouseMotionListener(positionUpdater);
  }

  public void setLinearPosition(LinearPosition position) {
    this.position = new LinearPosition(position);
  }

  public LinearPosition getLinearPosition() {
    return position;
  }

  class PositionUpdater extends MouseAdapter implements MouseMotionListener {
    final Point2D.Float point = new Point2D.Float();
    int state = 0;

    public void mousePressed(MouseEvent e) {}

    public void mouseDragged(MouseEvent e) {
      if (state == 0)
        return;
      ui.mouseToRel(point, e.getX(), e.getY(), true);
      if (state == 1) {
        position.x1 = point.x;
        position.y1 = point.y;
      } else if (state == 2) {
        position.x2 = point.x;
        position.y2 = point.y;
      }
      repaint();
      firePropertyChange(POSITION_CHANGED, null, null);
    }

    public void mouseMoved(MouseEvent e) {
      ui.mouseToRel(point, e.getX(), e.getY(), false);
      if (position.x1 - ANCHOR_TOLERANCE <= point.x && point.x <= position.x1 + ANCHOR_TOLERANCE &&
          position.y1 - ANCHOR_TOLERANCE <= point.y && point.y <= position.y1 + ANCHOR_TOLERANCE) {
        state = 1;
        setCursor(HAND_CURSOR);
      } else if (position.x2 - ANCHOR_TOLERANCE <= point.x && point.x <= position.x2 + ANCHOR_TOLERANCE &&
                 position.y2 - ANCHOR_TOLERANCE <= point.y && point.y <= position.y2 + ANCHOR_TOLERANCE) {
        state = 2;
        setCursor(HAND_CURSOR);
      } else {
        setCursor(DEFAULT_CURSOR);
        state = 0;
      }
    }
  }
}
