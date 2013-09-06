package org.gladstoneinstitutes.customgraphicsui.internal.gradient;

import javax.swing.JComponent;
import javax.swing.plaf.ComponentUI;
import javax.swing.UIManager;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Color;
import java.awt.Insets;
import java.awt.Stroke;
import java.awt.BasicStroke;
import java.awt.RenderingHints;
import java.awt.geom.Rectangle2D;
import java.awt.geom.Point2D;
import java.awt.geom.Ellipse2D;
import java.util.List;
import java.util.Arrays;

class LinearPositionEditorUI extends ComponentUI {
  protected static final Color BKGND_COLOR = UIManager.getColor("Panel.background");
  protected static final float BOX_FRACTION_SIZE = 0.6f;
  protected static final Color BOX_BORDER_COLOR = new Color(0x8A8A8A);
  protected static final Stroke BOX_BORDER_STROKE = new BasicStroke(3.5f);
  protected static final List<Point2D.Float> ANCHOR_POSITIONS = Arrays.asList(
    new Point2D.Float(0.0f, 0.0f),
    new Point2D.Float(0.5f, 0.0f),
    new Point2D.Float(1.0f, 0.0f),
    new Point2D.Float(0.0f, 0.5f),
    new Point2D.Float(0.5f, 0.5f),
    new Point2D.Float(1.0f, 0.5f),
    new Point2D.Float(0.0f, 1.0f),
    new Point2D.Float(0.5f, 1.0f),
    new Point2D.Float(1.0f, 1.0f));
  protected static final int ANCHOR_POSITIONS_N = ANCHOR_POSITIONS.size();
  protected static final Color ANCHOR_COLOR = new Color(0x424242);
  protected static final float ANCHOR_RADIUS_PX = 10.0f;

  final LinearPositionEditor editor;
  public LinearPositionEditorUI(final LinearPositionEditor editor) {
    this.editor = editor;
  }

  protected final Insets insets = new Insets(0, 0, 0, 0);
  protected final Rectangle2D.Float box = new Rectangle2D.Float();
  protected final Ellipse2D.Float anchor = new Ellipse2D.Float(0, 0, ANCHOR_RADIUS_PX, ANCHOR_RADIUS_PX);

  public void paint(Graphics g, final JComponent component) {
    final Graphics2D g2d = (Graphics2D) g;
    g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

    component.getInsets(insets);
    final int x = insets.left;
    final int y = insets.top;
    final int w = component.getWidth()  - insets.left - insets.right;
    final int h = component.getHeight() - insets.top  - insets.bottom;

    g2d.setColor(BKGND_COLOR);
    g2d.fillRect(x, y, w, h);

    box.width = box.height = (w < h ? w : h) * BOX_FRACTION_SIZE;
    box.x = (w - box.width)  / 2.0f + x;
    box.y = (h - box.height) / 2.0f + y;

    g2d.setPaint(BOX_BORDER_COLOR);
    g2d.setStroke(BOX_BORDER_STROKE);
    g2d.draw(box);

    g2d.setPaint(ANCHOR_COLOR);
    for (int i = 0; i < ANCHOR_POSITIONS_N; i++) { // avoid creating iterators to save heap allocations
      final Point2D.Float anchorPosition = ANCHOR_POSITIONS.get(i);
      anchor.x = box.width  * anchorPosition.x + box.x - ANCHOR_RADIUS_PX / 2.0f;
      anchor.y = box.height * anchorPosition.y + box.y - ANCHOR_RADIUS_PX / 2.0f;
      g2d.fill(anchor);
    }
  }
}
