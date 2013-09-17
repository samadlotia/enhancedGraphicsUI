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
import java.awt.Paint;
import java.awt.geom.Rectangle2D;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.awt.geom.Path2D;
import java.awt.geom.Point2D;
import java.awt.geom.AffineTransform;

import org.cytoscape.view.presentation.customgraphics.CyCustomGraphicsFactory;
import org.cytoscape.view.presentation.customgraphics.CyCustomGraphics;
import org.cytoscape.view.presentation.customgraphics.CustomGraphicLayer;

class PositionEditorUI extends ComponentUI {
  static final Paint        BKGND_PAINT           = GradientEditorUI.checkeredPaint();
  static final float        BOX_FRACTION_SIZE     = 0.5f;
  static final Color        BOX_BORDER_COLOR      = new Color(0x8A8A8A);
  static final float        BOX_BORDER_THICKNESS  = 2.5f;
  static final Stroke       BOX_BORDER_STROKE     = new BasicStroke(BOX_BORDER_THICKNESS);
  static final Color        ARROW_COLOR           = new Color(/*0x424242*/ 0x8A8A8A);
  static final float        ARROW_THICKNESS       = 4.0f;
  static final Stroke       ARROW_STROKE          = new BasicStroke(ARROW_THICKNESS);
  static final float        ANCHOR_SIZE           = 8.0f;
  static final float        SNAP_TOLERANCE        = 0.05f;
  static final Path2D.Float ARROW_VEE             = newArrowVee(ARROW_THICKNESS);
  static final Rectangle2D  ARROW_VEE_BOUNDS      = ARROW_VEE.getBounds2D();
  static final float        ARROW_VEE_H           = (float) ARROW_VEE_BOUNDS.getHeight();

  final PositionEditor editor;
  final GradientEditor gradientEditor;
  CyCustomGraphicsFactory<? extends CustomGraphicLayer> factory = null;

  public PositionEditorUI(
    final PositionEditor editor,
    final GradientEditor gradientEditor) {
    this.editor = editor;
    this.gradientEditor = gradientEditor;
  }

  public void setFactory(final CyCustomGraphicsFactory<? extends CustomGraphicLayer> factory) {
    this.factory = factory;
  }

  // These are class members to avoid unnecessary heap allocations everytime paint() is invoked.
  protected final Insets            insets    = new Insets(0, 0, 0, 0);
  protected final Rectangle2D.Float box       = new Rectangle2D.Float();
  protected final Rectangle2D.Float anchor    = new Rectangle2D.Float();
  protected final Line2D.Float      arrowStem = new Line2D.Float();
  protected final AffineTransform   transform = new AffineTransform();

  public void paint(Graphics g, final JComponent component) {
    calculateBox();

    final Graphics2D g2d = (Graphics2D) g;
    g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

    // paint the background -- only shows if the gradient is transparent
    g2d.setPaint(BKGND_PAINT);
    g2d.fill(box);

    // paint the gradient
    if (factory != null) {
      final Gradient gradient = gradientEditor.getGradient();
      try {
        final String cgDescription = String.format("%s stoplist=\"%s\"", editor.getLinearPosition().formatCg(editor.getType()), gradient.toString());
        final CyCustomGraphics<? extends CustomGraphicLayer> customGraphics = factory.getInstance(cgDescription);
        final CustomGraphicLayer layer = customGraphics.getLayers(null, null).get(0);
        g2d.setPaint(layer.getPaint(box));
        g2d.fill(box);
      } catch (Exception e) {}
    }

    // paint the box that's the border around the gradient
    g2d.setPaint(BOX_BORDER_COLOR);
    g2d.setStroke(BOX_BORDER_STROKE);
    g2d.draw(box);

    // calculate the arrow's beginning and end coordinates
    final LinearPosition position = editor.getLinearPosition();
    final float  arrowX1         = position.x1 * box.width  + box.x;
    final float  arrowY1         = position.y1 * box.height + box.y;
    final float  arrowX2         = position.x2 * box.width  + box.x;
    final float  arrowY2         = position.y2 * box.height + box.y;

    // get the arrow's polar coordinates so we can adjust the line length
    final double arrowStemLength = Math.sqrt(Math.pow(arrowX2 - arrowX1, 2.0)
                                           + Math.pow(arrowY2 - arrowY1, 2.0)) - ARROW_VEE_H; // subtract the arrow vee length
    final double arrowAngle      = Math.atan2(position.y2 - position.y1, position.x2 - position.x1);

    // paint the arrow stem
    arrowStem.setLine(arrowX1, arrowY1,
      arrowX1 + Math.cos(arrowAngle) * (arrowStemLength + ARROW_THICKNESS),
      arrowY1 + Math.sin(arrowAngle) * (arrowStemLength + ARROW_THICKNESS));
    g2d.setPaint(ARROW_COLOR);
    g2d.setStroke(ARROW_STROKE);
    g2d.draw(arrowStem);

    anchor.setRect(arrowX1 - ANCHOR_SIZE / 2.0f, arrowY1 - ANCHOR_SIZE / 2.0f, ANCHOR_SIZE, ANCHOR_SIZE);
    g2d.fill(anchor);

    anchor.setRect(arrowX2 - ANCHOR_SIZE / 2.0f, arrowY2 - ANCHOR_SIZE / 2.0f, ANCHOR_SIZE, ANCHOR_SIZE);
    g2d.fill(anchor);

    // paint the arrow vee
    transform.setToTranslation(
      arrowX1 + Math.cos(arrowAngle) * arrowStemLength,
      arrowY1 + Math.sin(arrowAngle) * arrowStemLength);
    transform.rotate(arrowAngle - Math.PI / 2.0);
    g2d.transform(transform);
    g2d.setPaint(ARROW_COLOR);
    g2d.fill(ARROW_VEE);
  }

  private static Path2D.Float newArrowVee(final float t) {
    final Path2D.Float p = new Path2D.Float();
    p.moveTo( 0.0f    , 1.0f * t);
    p.lineTo( 2.0f * t, 0.0f);
    p.lineTo( 0.0f    , 6.0f * t);
    p.lineTo(-2.0f * t, 0.0f);
    p.closePath();
    return p;
  }

  public void mouseToRel(final Point2D.Float point, final int mouseX, int mouseY, boolean snapTolerance) {
    calculateBox();
    float relX = (mouseX - box.x) / box.width;
    float relY = (mouseY - box.y) / box.height;
    if (!snapTolerance) {
      point.setLocation(relX, relY);
      return;
    }
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
