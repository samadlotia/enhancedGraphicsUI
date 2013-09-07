package org.gladstoneinstitutes.customgraphicsui.internal.gradient;

import javax.swing.JComponent;
import java.awt.Dimension;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Point2D;

class LinearPositionEditor extends JComponent {
  final LinearPositionEditorUI ui = new LinearPositionEditorUI(this);
  LinearPosition position = new LinearPosition();

  public LinearPositionEditor() {
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
    }

    public void mouseMoved(MouseEvent e) {}
  }
}
