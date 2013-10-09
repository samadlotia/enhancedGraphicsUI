package org.gladstoneinstitutes.customgraphicsui.internal.gradient;

import javax.swing.JComponent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.Color;
import java.awt.Dimension;

public class GradientEditor extends JComponent {
  public static final String SELECTED_STOP_CHANGED = "selected stop changed";
  public static final String SELECTED_STOP_POSITION_CHANGED = "selected stop position changed";
  final GradientEditorUI ui = new GradientEditorUI(this);
  public GradientEditor() {
    super.setUI(ui);
    super.setFocusable(true);

    super.addMouseListener(new MouseAdapter() {
      public void mousePressed(final MouseEvent e) {
        requestFocusInWindow();

        final int x = e.getX();
        final int y = e.getY();

        if (ui.inKnobRegion(y)) {
          selectedStop = ui.xToStop(x);
        } else {
          selectedStop = null;
        }
        repaint();
        firePropertyChange(SELECTED_STOP_CHANGED, null, null);
      }

      public void mouseClicked(final MouseEvent e) {
        if (selectedStop != null)
          return;
        if (e.getClickCount() != 2)
          return;
        if (!ui.inKnobRegion(e.getY()))
          return;
        final float position = ui.xToPosition(e.getX());
        selectedStop = gradient.add(position, Color.WHITE);
        repaint();
        firePropertyChange(SELECTED_STOP_CHANGED, null, null);
      }
    });

    super.addMouseMotionListener(new MouseAdapter() {
      public void mouseDragged(final MouseEvent e) {
        if (selectedStop == null)
          return;
        final int x = e.getX();
        final float position = ui.xToPosition(x);
        selectedStop.setPosition(position);
        repaint();
        firePropertyChange(SELECTED_STOP_POSITION_CHANGED, null, null);
      }
    });

    super.addKeyListener(new KeyAdapter() {
      public void keyPressed(final KeyEvent e) {
        if (selectedStop == null)
          return;
        if (gradient.size() < 2)
          return;
        if (e.getKeyCode() != KeyEvent.VK_BACK_SPACE && e.getKeyCode() != KeyEvent.VK_DELETE)
          return;
        gradient.remove(selectedStop);
        selectedStop = null;
        repaint();
        firePropertyChange(SELECTED_STOP_CHANGED, null, null);
      }
    });

    super.setPreferredSize(new Dimension(200, 70));
  }

  Gradient gradient = new Gradient();
  Gradient.Stop selectedStop = null;

  public void setGradient(final Gradient gradient) {
    if (gradient == null)
      throw new IllegalArgumentException("gradient is null");
    this.gradient = new Gradient(gradient);
  }

  public Gradient getGradient() {
    return gradient;
  }

  public Gradient.Stop getSelectedStop() {
    return selectedStop;
  }

  public Dimension getMinimumSize() {
    return ui.getMinimumSize();
  }
}