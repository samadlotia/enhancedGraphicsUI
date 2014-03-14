package org.gladstoneinstitutes.customgraphicsui.internal.util;

import java.util.Arrays;

import java.awt.GridBagLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.TexturePaint;
import java.awt.image.BufferedImage;
import java.awt.geom.Rectangle2D;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.ChangeEvent;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.JTextField;
import javax.swing.JComponent;
import javax.swing.BorderFactory;

public class ColorEditorPanel extends JPanel {
  public static final String COLOR_CHANGED = "color changed";
  final ColorWell well = new ColorWell();
  final JSlider sliderR = createColorSlider();
  final JSlider sliderG = createColorSlider();
  final JSlider sliderB = createColorSlider();
  final JSlider sliderA = createColorSlider();
  final JTextField fieldR = createColorField();
  final JTextField fieldG = createColorField();
  final JTextField fieldB = createColorField();
  final JTextField fieldA = createColorField();

  public ColorEditorPanel() {
    super(new GridBagLayout());

    addChannelUpdates(fieldR, sliderR);
    addChannelUpdates(fieldG, sliderG);
    addChannelUpdates(fieldB, sliderB);
    addChannelUpdates(fieldA, sliderA);

    final EasyGBC c = new EasyGBC();
    super.add(well, c.down().spanV(4).anchor("nw").insets(0, 0, 0, 10));
    super.add(new JLabel("R:"), c.noSpan().right().insets(7, 0, 0, 0));
    super.add(sliderR, c.expandH().noInsets().right());
    super.add(fieldR, c.noExpand().right());
    super.add(new JLabel("G:"), c.down().right().insets(7, 0, 0, 0));
    super.add(sliderG, c.expandH().noInsets().right());
    super.add(fieldG, c.noExpand().right());
    super.add(new JLabel("B:"), c.down().right().insets(7, 0, 0, 0));
    super.add(sliderB, c.expandH().noInsets().right());
    super.add(fieldB, c.noExpand().right());
    super.add(new JLabel("A:"), c.down().right().insets(7, 0, 0, 0));
    super.add(sliderA, c.expandH().noInsets().right());
    super.add(fieldA, c.noExpand().right());

  }

  private JTextField createColorField() {
    final JTextField field = new JTextField(3);
    field.setEnabled(false);
    return field;
  }

  private JSlider createColorSlider() {
    final JSlider slider = new JSlider(0, 255, 0);
    slider.setPaintTicks(true);
    slider.setMajorTickSpacing(64);
    slider.setMinorTickSpacing(16);
    slider.setEnabled(false);
    return slider;
  }

  private void addChannelUpdates(final JTextField field, final JSlider slider) {
    field.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        field.setText(validateChannelNumber(field.getText()));
        slider.setValue(Integer.parseInt(field.getText()));
        updateColor();
      }
    });

    field.addFocusListener(new FocusAdapter() {
      public void focusLost(FocusEvent e) {
        field.setText(validateChannelNumber(field.getText()));
        slider.setValue(Integer.parseInt(field.getText()));
        updateColor();
      }
    });

    slider.addChangeListener(new ChangeListener() {
      public void stateChanged(ChangeEvent e) {
        field.setText(Integer.toString(slider.getValue()));
        updateColor();
      }
    });
  }

  public Color getColor() {
    return new Color(
      sliderR.getValue(),
      sliderG.getValue(),
      sliderB.getValue(),
      sliderA.getValue()
      );
  }

  private void updateColor() {
    final Color color = getColor();
    well.setColor(color);
    super.firePropertyChange(COLOR_CHANGED, null, null);
  }

  private static String validateChannelNumber(final String value) {
    try {
      final int numValue = Integer.parseInt(value);
      if (numValue < 0)
        return "0";
      else if (numValue > 255)
        return "255";
      else
        return value;
    } catch (NumberFormatException e) {
      return "0";
    }
  }

  public void setColor(final Color color) {
    well.setColor(color);
    if (color == null) {
      for (final JTextField field : Arrays.asList(fieldR, fieldG, fieldB, fieldA)) {
        field.setEnabled(false);
        field.setText("");
      }
      for (final JSlider slider : Arrays.asList(sliderR, sliderG, sliderB, sliderA)) {
        slider.setEnabled(false);
        slider.setValue(0);
      }
    } else {
      for (final JTextField field : Arrays.asList(fieldR, fieldG, fieldB, fieldA)) {
        field.setEnabled(true);
      }
      for (final JSlider slider : Arrays.asList(sliderR, sliderG, sliderB, sliderA)) {
        slider.setEnabled(true);
      }

      final int r = color.getRed();
      final int g = color.getGreen();
      final int b = color.getBlue();
      final int a = color.getAlpha();

      fieldR.setText(Integer.toString(r));
      fieldG.setText(Integer.toString(g));
      fieldB.setText(Integer.toString(b));
      fieldA.setText(Integer.toString(a));

      sliderR.setValue(r);
      sliderG.setValue(g);
      sliderB.setValue(b);
      sliderA.setValue(a);
    }
  }

  private static TexturePaint BKGND_PAINT = null;
  /**
   * Return checkered pattern used to depict transparency like in Photoshop.
   */
  public static TexturePaint checkeredPaint() {
    if (BKGND_PAINT == null) {
      final int d = 20;
      final BufferedImage img = new BufferedImage(d, d, BufferedImage.TYPE_INT_RGB);
      final Graphics2D g2d = img.createGraphics();
      g2d.setColor(new Color(0xf2f2f2));
      g2d.fillRect(0, 0, d, d);
      g2d.setColor(new Color(0xcccccc));
      g2d.fillRect(0, 0, d / 2, d / 2);
      g2d.fillRect(d / 2, d / 2, d, d);
      BKGND_PAINT = new TexturePaint(img, new Rectangle2D.Float(0, 0, d, d));
    }
    return BKGND_PAINT;
  }
}

class ColorWell extends JComponent {
  Color color = null;

  public ColorWell() {
    super.setBorder(BorderFactory.createEtchedBorder());
    super.setPreferredSize(new Dimension(48, 48));
  }

  public void setColor(final Color color) {
    this.color = color;
    super.repaint();
  }

  public void paintComponent(Graphics g) {
    final Graphics2D g2d = (Graphics2D) g;
    final Insets insets = super.getInsets();
    final int x = insets.left;
    final int y = insets.top;
    final int w = super.getWidth() - insets.left - insets.right;
    final int h = super.getHeight() - insets.top - insets.bottom;
    g2d.setPaint(ColorEditorPanel.checkeredPaint());
    g2d.fillRect(x, y, w, h);
    if (color != null) {
      g2d.setColor(color);
      g2d.fillRect(x, y, w, h);
    }
  }
}