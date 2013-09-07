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
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.awt.geom.Path2D;
import java.awt.geom.Point2D;
import java.awt.geom.AffineTransform;

class LinearPositionEditorUI extends ComponentUI {
  protected static final Color  BKGND_COLOR       = UIManager.getColor("Panel.background");
  protected static final float  BOX_FRACTION_SIZE = 0.5f;
  protected static final Color  BOX_BORDER_COLOR  = new Color(0x8A8A8A);
  protected static final Stroke BOX_BORDER_STROKE = new BasicStroke(3.5f);
  protected static final Color  ANCHOR_COLOR      = new Color(0x424242);
  protected static final float  ANCHOR_RADIUS_PX  = 15.0f;
  protected static final Color  ARROW_COLOR       = new Color(0x424242);
  protected static final float  ARROW_THICKNESS   = 7.5f;
  protected static final Stroke ARROW_STROKE      = new BasicStroke(ARROW_THICKNESS);
  protected static final float  SNAP_TOLERANCE    = 0.05f;

  final LinearPositionEditor editor;
  public LinearPositionEditorUI(final LinearPositionEditor editor) {
    this.editor = editor;
  }

  // These are class members to avoid unnecessary heap allocations everytime paint() is invoked.
  protected final Insets insets = new Insets(0, 0, 0, 0);
  protected final Rectangle2D.Float box = new Rectangle2D.Float();
  protected final Ellipse2D.Float anchor = new Ellipse2D.Float(0, 0, ANCHOR_RADIUS_PX, ANCHOR_RADIUS_PX);
  protected final Line2D.Float arrow = new Line2D.Float();
  protected final AffineTransform transform = new AffineTransform();

  public void paint(Graphics g, final JComponent component) {
    final Graphics2D g2d = (Graphics2D) g;
    g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

    calculateBox();
    //g2d.setColor(BKGND_COLOR);
    //g2d.fillRect(x, y, w, h);

    g2d.setPaint(BOX_BORDER_COLOR);
    g2d.setStroke(BOX_BORDER_STROKE);
    g2d.draw(box);

    g2d.setPaint(ANCHOR_COLOR);
    for (float anchorX = 0.0f; anchorX <= 1.0f; anchorX += 0.5f) {
      for (float anchorY = 0.0f; anchorY <= 1.0f; anchorY += 0.5f) {
        anchor.x = box.width  * anchorX + box.x - ANCHOR_RADIUS_PX / 2.0f;
        anchor.y = box.height * anchorY + box.y - ANCHOR_RADIUS_PX / 2.0f;
        g2d.fill(anchor);
      }
    }

    final Path2D.Float arrowVee = newArrowVee(ARROW_THICKNESS);
    final Rectangle2D bounds = arrowVee.getBounds2D();

    final LinearPosition position = editor.getLinearPosition();
    arrow.setLine(position.x1 * box.width  + box.x,
                  position.y1 * box.height + box.y,
                  position.x2 * box.width  + box.x,
                  position.y2 * box.height + box.y);
    g2d.setPaint(ARROW_COLOR);
    g2d.setStroke(ARROW_STROKE);
    g2d.draw(arrow);

    final double arrowAngle = Math.atan2(position.y2 - position.y1, position.x2 - position.x1) - Math.PI / 2.0;
    transform.setToIdentity();
    transform.translate(arrow.x2, arrow.y2);
    transform.rotate(arrowAngle);
    g2d.transform(transform);

    transform.setToIdentity();
    transform.translate(-bounds.getWidth() / 2.0, -bounds.getHeight());
    arrowVee.transform(transform);
    g2d.fill(arrowVee);
  }

  private static Path2D.Float newArrowVee(final float t) {
    final Path2D.Float p = new Path2D.Float();
    p.moveTo(0.0f, 0.0f);
    p.lineTo(2.0f * t, 1.0f * t);
    p.lineTo(4.0f * t, 0.0f);
    p.lineTo(2.0f * t, 6.0f * t);
    p.closePath();
    return p;
  }

  public void mouseToRel(final Point2D.Float point, final int mouseX, int mouseY) {
    calculateBox();
    float relX = (mouseX - box.x) / box.width;
    float relY = (mouseY - box.y) / box.height;
    for (float anchorX = 0.0f; anchorX <= 1.0f; anchorX += 0.5f) {
      if ((anchorX - SNAP_TOLERANCE) <= relX && relX <= (anchorX + SNAP_TOLERANCE)) {
        relX = anchorX;
        break;
      }
    }
    for (float anchorY = 0.0f; anchorY <= 1.0f; anchorY += 0.5f) {
      if ((anchorY - SNAP_TOLERANCE) <= relY && relY <= (anchorY + SNAP_TOLERANCE)) {
        relY = anchorY;
        break;
      }
    }
    point.setLocation(relX, relY);
  }

  protected void calculateBox() {
    editor.getInsets(insets);
    final int x = insets.left;
    final int y = insets.top;
    final int w = editor.getWidth()  - insets.left - insets.right;
    final int h = editor.getHeight() - insets.top  - insets.bottom;

    box.width = box.height = (w < h ? w : h) * BOX_FRACTION_SIZE;
    box.x = (w - box.width)  / 2.0f + x;
    box.y = (h - box.height) / 2.0f + y;
  }
}
