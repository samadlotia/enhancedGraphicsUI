package org.gladstoneinstitutes.customgraphicsui.internal.gradient;

import javax.swing.plaf.ComponentUI;
import javax.swing.JComponent;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GradientPaint;
import java.awt.RenderingHints;
import java.awt.Dimension;
import java.awt.Stroke;
import java.awt.BasicStroke;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.awt.geom.Path2D;
import java.awt.Insets;

import org.gladstoneinstitutes.customgraphicsui.internal.util.ColorEditorPanel;

public class GradientEditorUI extends ComponentUI {
  final GradientEditor editor;
  final KnobUI knobUI = new KnobUI();

  final Rectangle2D.Float gradientRegion = new Rectangle2D.Float();
  // keep this as a class member so that it won't have to be reallocated
  // every time paint() is called
  final Rectangle2D.Float singleGradientRect = new Rectangle2D.Float();

  public GradientEditorUI(final GradientEditor editor) {
    this.editor = editor;
  }

  public void paint(final Graphics g, final JComponent component) {
    final Gradient gradient = editor.getGradient();
    final int nStops = gradient.size();
    if (nStops == 0)
      return;

    final Graphics2D g2d = (Graphics2D) g;
    g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

    updateGradientRegion();

    // paint background pattern
    g2d.setPaint(ColorEditorPanel.checkeredPaint());
    g2d.fill(gradientRegion);

    // paint up to the first stop
    final Gradient.Stop firstStop = gradient.stopAtIndex(0);
    final float x0 = positionToX(firstStop.getPosition());
    g2d.setPaint(firstStop.getColor());
    singleGradientRect.setRect(gradientRegion.x, gradientRegion.y,
                               x0, gradientRegion.height);
    g2d.fill(singleGradientRect);

    // paint each gradient
    for (int i = 0; i < nStops - 1; i++) {
      final Gradient.Stop stop1 = gradient.stopAtIndex(i);
      final Gradient.Stop stop2 = gradient.stopAtIndex(i + 1);

      final float x1 = positionToX(stop1.getPosition());
      final float x2 = positionToX(stop2.getPosition());
      
      final GradientPaint p = new GradientPaint(x1, 0.0f, stop1.getColor(),
                                                x2, 0.0f, stop2.getColor());
      g2d.setPaint(p);
      singleGradientRect.setRect(x1, gradientRegion.y, x2 - x1, gradientRegion.height);
      g2d.fill(singleGradientRect);
    }

    // paint from the last stop to the end of the component
    final Gradient.Stop lastStop = gradient.stopAtIndex(nStops - 1);
    g2d.setPaint(lastStop.getColor());
    final float xN = positionToX(lastStop.getPosition());
    singleGradientRect.setRect(xN, gradientRegion.y,
                               gradientRegion.width - xN + gradientRegion.x, gradientRegion.height);
    g2d.fill(singleGradientRect);

    // paint border around gradient
    g2d.setPaint(KnobUI.KNOB_BORDER_COLOR);
    g2d.draw(gradientRegion);

    // paint each knob
    final Gradient.Stop selectedStop = editor.getSelectedStop();
    for (final Gradient.Stop stop : gradient.getStops()) {
      if (stop == selectedStop) continue;
      paintStop(g2d, stop, false);
    }
    if (selectedStop != null)
      paintStop(g2d, selectedStop, true); // always paint the selected stop on top
  }

  protected void paintStop(final Graphics2D g2d, final Gradient.Stop stop, final boolean selected) {
    final float x = positionToX(stop.getPosition());
    final float y = gradientRegion.y + gradientRegion.height;
    knobUI.paintKnob(g2d, x, y, stop.getColor(), selected);
  }

  protected void updateGradientRegion() {
    final Insets insets = editor.getInsets();
    final float kw = KnobUI.knobWidth();
    final float kh = KnobUI.knobHeight();
    gradientRegion.setRect((float) Math.floor(insets.left + kw / 2.0f),
                           (float) Math.floor(insets.top),
                           (float) Math.ceil(editor.getWidth() - insets.right - insets.left - kw),
                           (float) Math.floor(editor.getHeight() - insets.bottom - insets.top - kh));
  }

  protected float positionToX(final float position) {
    return (float) Math.ceil(position * gradientRegion.width + gradientRegion.x);
  }

  public Dimension getMinimumSize() {
    return new Dimension((int) (KnobUI.knobWidth() * 5.0f), (int) (KnobUI.knobHeight() * 12.0f * editor.getGradient().size()));
  }

  public boolean inKnobRegion(final int y) {
    final int minY = (int) (gradientRegion.y + gradientRegion.height);
    final int maxY = (int) (minY + KnobUI.knobHeight());
    return (minY <= y && y <= maxY);
  }

  public float xToPosition(final int x) {
    final float position = (x - gradientRegion.x) / gradientRegion.width;
    if (position < 0.0f)
      return 0.0f;
    else if (position > 1.0f)
      return 1.0f;
    else
      return position;
  }

  public Gradient.Stop xToStop(final int x) {
    final float kw = KnobUI.knobWidth();
    for (final Gradient.Stop stop : editor.getGradient().getStops()) {
      final float stopX = positionToX(stop.getPosition());
      final float xMin = stopX - kw / 2.0f;
      final float xMax = xMin + kw;
      if (xMin <= x && x <= xMax)
        return stop;
    }
    return null;
  }

  protected static class KnobUI {
    protected static final float KNOB_WIDTH = 16.0f;

    protected static final float KNOB_BORDER = 1.0f;
    protected static final Stroke KNOB_BORDER_STROKE = new BasicStroke(KNOB_BORDER);
    protected static final float KNOB_INNER_PADDING = 1.0f;
    protected static final float KNOB_SELECTED_BORDER_FACTOR = 1.20f;

    protected static final Color KNOB_INNER_COLOR = new Color(0xEDEDED);
    protected static final Color KNOB_BORDER_COLOR = new Color(0xA0A0A0);
    protected static final Color KNOB_SELECTED_COLOR = new Color(0x606060);

    protected static final Path2D.Float KNOB_PATH = knobPath();
    protected static final Path2D.Float KNOB_SELECTED_PATH = knobSelectedPath();
    protected static final Rectangle2D  KNOB_BOUNDS = KNOB_SELECTED_PATH.getBounds2D();
    protected static final Path2D.Float COLOR_FILL_PATH = colorFillPath();

    protected static Path2D.Float knobPath() {
      final float k = KNOB_WIDTH;
      final Path2D.Float path = new Path2D.Float();
      path.moveTo(k * 0.5f, 0.0f);            // tip
      path.lineTo(k       , k * 1.0f / 3.0f); // top-right corner
      path.lineTo(k       , k * 4.0f / 3.0f); // bottom-right
      path.lineTo(0.0f    , k * 4.0f / 3.0f); // bottom-left
      path.lineTo(0.0f    , k * 1.0f / 3.0f); // top-left corner
      path.closePath();

      final Rectangle2D pathBounds = path.getBounds2D();
      final double centerx = pathBounds.getWidth() / 2.0;
      final AffineTransform t = new AffineTransform();
      t.setToTranslation(-centerx, 0.0);
      path.transform(t);

      return path;
    }

    protected static Path2D.Float knobSelectedPath() {
      final Path2D.Float path = KNOB_PATH;
      final Path2D.Float newPath = new Path2D.Float(path);

      final AffineTransform t = new AffineTransform();
      t.setToScale(KNOB_SELECTED_BORDER_FACTOR, KNOB_SELECTED_BORDER_FACTOR);
      newPath.transform(t);

      final Rectangle2D pathBounds = path.getBounds2D();
      final Rectangle2D newPathBounds = newPath.getBounds2D();
      final double centerx = (newPathBounds.getWidth() - pathBounds.getWidth()) / 2.0 - 1;
      final double centery = (newPathBounds.getHeight() - pathBounds.getHeight()) / 2.0;
      t.setToTranslation(+centerx, -centery);
      newPath.transform(t);

      return newPath;
    }

    protected static Path2D.Float colorFillPath() {
      final float k = KNOB_WIDTH;
      final float kp = KNOB_INNER_PADDING;
      final Path2D.Float path = new Path2D.Float();
      path.moveTo(k - kp , k * 1.0f / 3.0f + kp + 1); // top-right corner
      path.lineTo(k - kp , k * 4.0f / 3.0f - kp - 1); // bottom-right
      path.lineTo(kp + 1 , k * 4.0f / 3.0f - kp - 1); // bottom-left
      path.lineTo(kp + 1 , k * 1.0f / 3.0f + kp + 1); // top-left corner
      path.closePath();


      final Rectangle2D pathBounds = KNOB_PATH.getBounds2D();
      final double centerx = pathBounds.getWidth() / 2.0;
      final AffineTransform t = new AffineTransform();
      t.setToTranslation(-centerx, 0.0);
      path.transform(t);

      return path;     
    }

    public static float knobWidth() {
      return (float) KNOB_BOUNDS.getWidth();
    }

    public static float knobHeight() {
      return (float) KNOB_BOUNDS.getHeight();
    }

    protected OffsetPath knobPath = new OffsetPath(KNOB_PATH);
    protected OffsetPath knobSelectedPath = new OffsetPath(KNOB_SELECTED_PATH);
    protected OffsetPath colorFillPath = new OffsetPath(COLOR_FILL_PATH);

    public void paintKnob(final Graphics2D g, final float x, final float y,
                          final Color knobColor, final boolean selected) {
      if (selected) {
        g.setColor(KNOB_SELECTED_COLOR);
        g.fill(knobSelectedPath.offset(x, y));
      }

      g.setColor(KNOB_INNER_COLOR);
      g.fill(knobPath.offset(x, y));

      g.setStroke(KNOB_BORDER_STROKE);
      g.setPaint(KNOB_BORDER_COLOR);
      g.draw(knobPath.path());

      g.setColor(new Color(knobColor.getRGB())); // ignore alpha channel
      g.fill(colorFillPath.offset(x, y));
    }
  }
}

class OffsetPath {
  final Path2D path;
  final AffineTransform t = new AffineTransform();
  float lastx = 0.0f;
  float lasty = 0.0f;
  public OffsetPath(final Path2D path) {
    this.path = new Path2D.Float(path);
  }

  public Path2D path() {
    return path;
  }

  public Path2D offset(final float x, final float y) {
    t.setToTranslation(x - lastx, y - lasty);
    path.transform(t);
    lastx = x;
    lasty = y;
    return path;
  }
}
