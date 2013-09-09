package org.gladstoneinstitutes.customgraphicsui.internal.gradient;

import javax.swing.JComponent;
import java.awt.Dimension;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Point2D;

import org.gladstoneinstitutes.customgraphicsui.internal.CustomGraphicsFactoryManager;

class LinearPositionEditor extends JComponent {
  public static final String POSITION_CHANGED = "position changed";
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
    Point2D.Float start = new Point2D.Float();
    Point2D.Float end = new Point2D.Float();

    public void mousePressed(MouseEvent e) {
      ui.mouseToRel(start, e.getX(), e.getY());
    }

    public void mouseDragged(MouseEvent e) {
      ui.mouseToRel(end, e.getX(), e.getY());
      position.setLine(start.x, start.y, end.x, end.y);
      repaint();
      firePropertyChange(POSITION_CHANGED, null, null);
    }

    public void mouseMoved(MouseEvent e) {}
  }
}
