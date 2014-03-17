package org.gladstoneinstitutes.customgraphicsui.internal.gradient;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

import java.awt.Color;
import java.awt.Frame;
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

import javax.swing.JDialog;
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
import org.gladstoneinstitutes.customgraphicsui.internal.util.ColorEditorPanel;
import org.gladstoneinstitutes.customgraphicsui.internal.util.Strings;
import org.gladstoneinstitutes.customgraphicsui.internal.CustomGraphicsFactoryManager;

public class GradientDialog extends JDialog {
  final CustomGraphicsFactoryManager manager;
  final GradientEditor editor;
  final GradientOrientationEditor gradientPositionEditor;
  final ColorEditorPanel colorEditorPanel;
  final AnchorPositionPanel anchorPositionPanel;
  final Map<GradientOrientation.Type,JRadioButton> typeButtons = new HashMap<GradientOrientation.Type,JRadioButton>();

  public GradientDialog(final Frame parent, final CustomGraphicsFactoryManager manager) {
    super(parent, "Node Gradient", false);
    this.manager = manager;
    this.editor = new GradientEditor();
    this.gradientPositionEditor = new GradientOrientationEditor(editor, manager);
    this.colorEditorPanel = new ColorEditorPanel();
    this.anchorPositionPanel = new AnchorPositionPanel();

    editor.addPropertyChangeListener(editor.SELECTED_STOP_CHANGED, new PropertyChangeListener() {
      public void propertyChange(final PropertyChangeEvent e) {
        final Gradient.Stop selectedStop = editor.getSelectedStop();
        colorEditorPanel.setColor(selectedStop == null ? null : selectedStop.getColor());
        anchorPositionPanel.setPosition(selectedStop == null ? null : selectedStop.getPosition());
      }
    });

    colorEditorPanel.addPropertyChangeListener(ColorEditorPanel.COLOR_CHANGED, new PropertyChangeListener() {
      public void propertyChange(final PropertyChangeEvent e) {
        final Color newColor = colorEditorPanel.getColor();
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
    anchorPanel.add(colorEditorPanel, c);
    anchorPanel.add(anchorPositionPanel, c.anchor("w").down().insets(10, 0, 0, 0));

    gradientPositionEditor.setBorder(BorderFactory.createLineBorder(new Color(0x858585), 1));

    final JPanel typePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
    typePanel.add(new JLabel("Type: "));
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
      typeButtons.put(type, typeButton);
    }

    super.setLayout(new GridBagLayout());
    super.add(gradientPositionEditor, c.expandHV().spanV(3).insets(10, 10, 10, 10));
    super.add(typePanel, c.right().noSpan().noExpand().insets(0, 0, 0, 0));
    super.add(editor, c.down().right().expand(0.0, 0.0).insets(10, 0, 0, 10));
    super.add(anchorPanel, c.anchor("nw").down().right().noExpand().insets(10, 0, 0, 10));
  }	

  public void setupWithCgString(final String cg) {
    if (cg == null)
      return;
    final Map<String,String> args = Strings.extractArgMap(cg);
    if (args == null)
      return;
    try {
      final GradientOrientation orientation = new GradientOrientation(args);
      gradientPositionEditor.setGradientOrientation(orientation);
      typeButtons.get(orientation.getType()).setSelected(true);

      final String stoplist = args.get("stoplist");
      if (stoplist == null || stoplist.length() == 0)
        return;
      final Gradient gradient = new Gradient(stoplist);
      editor.setGradient(gradient);
    } catch (IllegalArgumentException e) {
      //e.printStackTrace();
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
