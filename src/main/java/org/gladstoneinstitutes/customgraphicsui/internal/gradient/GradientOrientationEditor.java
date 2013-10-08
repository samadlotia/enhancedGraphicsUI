package org.gladstoneinstitutes.customgraphicsui.internal.gradient;

import javax.swing.JComponent;
import java.awt.Dimension;
import java.awt.Cursor;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Point2D;

import org.gladstoneinstitutes.customgraphicsui.internal.CustomGraphicsFactoryManager;

class GradientOrientationEditor extends JComponent {
  public static final String ORIENTATION_CHANGED = "orientation changed";

  static final float ANCHOR_TOLERANCE = 0.05f;
  static final Cursor DEFAULT_CURSOR = Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR);
  static final Cursor HAND_CURSOR = Cursor.getPredefinedCursor(Cursor.HAND_CURSOR);

  final GradientEditor gradientEditor;
  final CustomGraphicsFactoryManager manager;
  final GradientOrientationEditorUI ui;

  GradientOrientation orientation = new GradientOrientation();

  public GradientOrientationEditor(final GradientEditor gradientEditor, final CustomGraphicsFactoryManager manager) {
    this.gradientEditor = gradientEditor;
    this.manager = manager;
    this.ui = new GradientOrientationEditorUI(this, gradientEditor, manager);
    super.setUI(ui);
    super.setPreferredSize(new Dimension(400, 400));
    final MouseUpdater updater = new MouseUpdater();
    super.addMouseListener(updater);
    super.addMouseMotionListener(updater);
  }

  public void setGradientOrientation(GradientOrientation orientation) {
    this.orientation = new GradientOrientation(orientation);
  }

  public GradientOrientation getGradientOrientation() {
    return orientation;
  }

  class MouseUpdater extends MouseAdapter implements MouseMotionListener {
    final Point2D.Float point = new Point2D.Float();
    int state = 0;

    public void mousePressed(MouseEvent e) {}

    public void mouseDragged(MouseEvent e) {
      if (state == 0)
        return;
      ui.mouseToRel(point, e.getX(), e.getY(), true);
      if (state == 1) {
        orientation.x1 = point.x;
        orientation.y1 = point.y;
      } else if (state == 2) {
        orientation.x2 = point.x;
        orientation.y2 = point.y;
      }
      repaint();
      firePropertyChange(ORIENTATION_CHANGED, null, null);
    }

    public void mouseMoved(MouseEvent e) {
      ui.mouseToRel(point, e.getX(), e.getY(), false);
      if (orientation.x1 - ANCHOR_TOLERANCE <= point.x && point.x <= orientation.x1 + ANCHOR_TOLERANCE &&
          orientation.y1 - ANCHOR_TOLERANCE <= point.y && point.y <= orientation.y1 + ANCHOR_TOLERANCE) {
        state = 1;
        setCursor(HAND_CURSOR);
      } else if (orientation.x2 - ANCHOR_TOLERANCE <= point.x && point.x <= orientation.x2 + ANCHOR_TOLERANCE &&
                 orientation.y2 - ANCHOR_TOLERANCE <= point.y && point.y <= orientation.y2 + ANCHOR_TOLERANCE) {
        state = 2;
        setCursor(HAND_CURSOR);
      } else {
        setCursor(DEFAULT_CURSOR);
        state = 0;
      }
    }
  }
}
