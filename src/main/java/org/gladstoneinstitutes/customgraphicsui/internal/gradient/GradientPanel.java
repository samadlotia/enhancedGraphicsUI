package org.gladstoneinstitutes.customgraphicsui.internal.gradient;

import java.util.Arrays;
import java.util.List;

import java.awt.Color;
import java.awt.Paint;
import java.awt.Rectangle;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.awt.FlowLayout;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;

import javax.swing.event.ChangeListener;
import javax.swing.event.ChangeEvent;

import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.JTextField;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import javax.swing.BorderFactory;
import javax.swing.JSeparator;
import javax.swing.JComboBox;
import javax.swing.JRadioButton;
import javax.swing.ButtonGroup;
import javax.swing.border.BevelBorder;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeEvent;

import org.gladstoneinstitutes.customgraphicsui.internal.util.EasyGBC;
import org.gladstoneinstitutes.customgraphicsui.internal.CustomGraphicsFactoryManager;

public class GradientPanel extends JPanel {
  final CustomGraphicsFactoryManager manager;
  final GradientEditor editor;
  final GradientOrientationEditor gradientPositionEditor;
  final ColorPanel colorPanel;
  final AnchorPositionPanel anchorPositionPanel;

  public GradientPanel(final CustomGraphicsFactoryManager manager) {
    this.manager = manager;
    this.editor = new GradientEditor();
    this.gradientPositionEditor = new GradientOrientationEditor(editor, manager);
    this.colorPanel = new ColorPanel();
    this.anchorPositionPanel = new AnchorPositionPanel();

    editor.addPropertyChangeListener(editor.SELECTED_STOP_CHANGED, new PropertyChangeListener() {
      public void propertyChange(final PropertyChangeEvent e) {
        final Gradient.Stop selectedStop = editor.getSelectedStop();
        colorPanel.setColor(selectedStop == null ? null : selectedStop.getColor());
        anchorPositionPanel.setPosition(selectedStop == null ? null : selectedStop.getPosition());
      }
    });

    colorPanel.addPropertyChangeListener(ColorPanel.COLOR_CHANGED, new PropertyChangeListener() {
      public void propertyChange(final PropertyChangeEvent e) {
        final Color newColor = colorPanel.getColor();
        final Gradient.Stop selectedStop = editor.getSelectedStop();
        if (selectedStop != null) {
          selectedStop.setColor(newColor);
          editor.repaint();
          gradientPositionEditor.repaint();
        }
      }
    });

    editor.addPropertyChangeListener(editor.SELECTED_STOP_POSITION_CHANGED, new PropertyChangeListener() {
      public void propertyChange(final PropertyChangeEvent e) {
        final Gradient.Stop selectedStop = editor.getSelectedStop();
        anchorPositionPanel.setPosition(selectedStop == null ? null : selectedStop.getPosition());
        gradientPositionEditor.repaint();
      }
    });

    anchorPositionPanel.addPropertyChangeListener(AnchorPositionPanel.ANCHOR_POSITION_CHANGED, new PropertyChangeListener() {
      public void propertyChange(final PropertyChangeEvent e) {
        final float newPosition = (float) anchorPositionPanel.getPosition();
        final Gradient.Stop selectedStop = editor.getSelectedStop();
        if (selectedStop != null) {
          selectedStop.setPosition(newPosition);
          editor.repaint();
          gradientPositionEditor.repaint();
        }
      }
    });

    final JPanel anchorPanel = new JPanel(new GridBagLayout());
    final EasyGBC c = new EasyGBC();
    anchorPanel.add(colorPanel, c);
    anchorPanel.add(anchorPositionPanel, c.anchor("w").down().insets(10, 0, 0, 0));

    gradientPositionEditor.setBorder(BorderFactory.createLineBorder(new Color(0x858585), 1));

    final JPanel typePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
    typePanel.add(new JLabel("Gradient Type: "));
    final ButtonGroup typeGroup = new ButtonGroup();
    for (final GradientOrientation.Type type : GradientOrientation.Type.values()) {
      final JRadioButton typeButton = new JRadioButton(type.toString());
      typePanel.add(typeButton);
      typeGroup.add(typeButton);
      typeButton.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          gradientPositionEditor.getGradientOrientation().setType(type);
          gradientPositionEditor.repaint();
        }
      });
      if (type == gradientPositionEditor.getGradientOrientation().getType())
        typeButton.setSelected(true);
    }

    super.setLayout(new GridBagLayout());
    super.add(gradientPositionEditor, c.expand(0.5, 1.0).spanV(3).insets(10, 10, 10, 10));
    super.add(typePanel, c.right().noSpan().expandH().insets(0, 0, 0, 0));
    super.add(editor, c.down().right().expand(0.5, 1.0).insets(10, 0, 0, 10));
    super.add(anchorPanel, c.noExpand().down().right().insets(10, 0, 0, 10));
  }	
}

class ColorPanel extends JPanel {
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

  public ColorPanel() {
    super(new GridBagLayout());

    addChannelUpdates(fieldR, sliderR);
    addChannelUpdates(fieldG, sliderG);
    addChannelUpdates(fieldB, sliderB);
    addChannelUpdates(fieldA, sliderA);

    final EasyGBC c = new EasyGBC();
    super.add(well, c.down().spanV(4).anchor("nw").insets(0, 0, 0, 10));
    super.add(new JLabel("R:"), c.noSpan().right().noInsets());
    super.add(sliderR, c.right());
    super.add(fieldR, c.right());
    super.add(new JLabel("G:"), c.down().right());
    super.add(sliderG, c.right());
    super.add(fieldG, c.right());
    super.add(new JLabel("B:"), c.down().right());
    super.add(sliderB, c.right());
    super.add(fieldB, c.right());
    super.add(new JLabel("A:"), c.down().right());
    super.add(sliderA, c.right());
    super.add(fieldA, c.right());

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
    g2d.setPaint(GradientEditorUI.checkeredPaint());
    g2d.fillRect(x, y, w, h);
    if (color != null) {
      g2d.setColor(color);
      g2d.fillRect(x, y, w, h);
    }
  }
}

class AnchorPositionPanel extends JPanel {
  public static final String ANCHOR_POSITION_CHANGED = "position changed";

  final JSpinner relSpinner = createSpinner(new SpinnerNumberModel(0.0, 0.0, 1.0, 0.1));

  public AnchorPositionPanel() {
    relSpinner.addChangeListener(new ChangeListener() {
      public void stateChanged(final ChangeEvent e) {
        final double relValue = ((Number) relSpinner.getValue()).doubleValue();
        firePropertyChange(ANCHOR_POSITION_CHANGED, null, null);
      }
    });

    super.add(new JLabel("Anchor Position:"));
    super.add(relSpinner);
  }

  private JSpinner createSpinner(final SpinnerNumberModel model) {
    final JSpinner spinner = new JSpinner(model);
    spinner.setEnabled(false);
    final JSpinner.DefaultEditor editor = (JSpinner.DefaultEditor) spinner.getEditor();
    editor.getTextField().setColumns(6);
    return spinner;
  }

  public void setPosition(final Float position) {
    if (position == null) {
      relSpinner.setEnabled(false);
      relSpinner.setValue(new Double(0.0));
    } else {
      relSpinner.setEnabled(true);
      relSpinner.setValue(position);
    }
  }

  public float getPosition() {
    return (((Number) relSpinner.getValue()).floatValue());
  }
}