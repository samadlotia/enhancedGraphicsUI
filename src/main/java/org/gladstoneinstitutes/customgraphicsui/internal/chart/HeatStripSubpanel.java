package org.gladstoneinstitutes.customgraphicsui.internal.chart;

import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.FlowLayout;
import java.awt.Color;

import javax.swing.ButtonGroup;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JCheckBox;
import javax.swing.JRadioButton;
import javax.swing.JLabel;
import javax.swing.JSpinner;
import javax.swing.JSlider;
import javax.swing.SpinnerNumberModel;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

import javax.swing.event.TableModelListener;
import javax.swing.event.TableModelEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.ChangeEvent;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeEvent;

import org.cytoscape.model.CyNode;
import org.cytoscape.model.CyNetwork;
import org.cytoscape.view.model.CyNetworkView;
import org.cytoscape.view.model.View;

import org.gladstoneinstitutes.customgraphicsui.internal.util.EasyGBC;
import org.gladstoneinstitutes.customgraphicsui.internal.util.Strings;
import org.gladstoneinstitutes.customgraphicsui.internal.util.ColorEditorPanel;

class HeatStripSubpanel extends ChartSubpanel {
  final NumericAttributesTable attrsTable = new NumericAttributesTable();
  final JCheckBox showLabelsCheckBox = new JCheckBox("Labels");
  Color negativeColor = Color.CYAN;
  Color zeroColor     = Color.BLACK;
  Color positiveColor = Color.YELLOW;
  final JSpinner separationSpinner = new JSpinner(new SpinnerNumberModel(0, 0, 100, 1));

  public HeatStripSubpanel() {
    super.setLayout(new GridBagLayout());

    attrsTable.getModel().addTableModelListener(new TableModelListener() {
      public void tableChanged(TableModelEvent e) {
        HeatStripSubpanel.this.firePropertyChange(CG_CHANGED, null, null);
      }
    });

    showLabelsCheckBox.setSelected(true);
    showLabelsCheckBox.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        HeatStripSubpanel.this.firePropertyChange(CG_CHANGED, null, null);
      }
    });

    separationSpinner.addChangeListener(new ChangeListener() {
      public void stateChanged(ChangeEvent e) {
        HeatStripSubpanel.this.firePropertyChange(CG_CHANGED, null, null);
      }
    });

    final JPanel labelPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
    labelPanel.add(showLabelsCheckBox);

    final JPanel separationPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
    separationPanel.add(new JLabel("Space between strips:"));
    separationPanel.add(separationSpinner);


    final JRadioButton positiveButton = new JRadioButton("Positive");
    final JRadioButton zeroButton = new JRadioButton("Zero");
    final JRadioButton negativeButton = new JRadioButton("Negative");

    final ColorEditorPanel colorEditorPanel = new ColorEditorPanel();
    colorEditorPanel.addPropertyChangeListener(ColorEditorPanel.COLOR_CHANGED, new PropertyChangeListener() {
      public void propertyChange(final PropertyChangeEvent e) {
        final Color newColor = colorEditorPanel.getColor();
        if (positiveButton.isSelected())
          positiveColor = newColor;
        else if (zeroButton.isSelected())
          zeroColor = newColor;
        else if (negativeButton.isSelected())
          negativeColor = newColor;
        HeatStripSubpanel.this.firePropertyChange(CG_CHANGED, null, null);
      }
    });

    positiveButton.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        colorEditorPanel.setColor(positiveColor);
      }
    });
    zeroButton.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        colorEditorPanel.setColor(zeroColor);
      }
    });
    negativeButton.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        colorEditorPanel.setColor(negativeColor);
      }
    });

    final ButtonGroup tricolorsGroup = new ButtonGroup();
    tricolorsGroup.add(positiveButton);
    tricolorsGroup.add(zeroButton);
    tricolorsGroup.add(negativeButton);

    final JPanel tricolorsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
    tricolorsPanel.add(new JLabel("Colors: "));
    tricolorsPanel.add(positiveButton);
    tricolorsPanel.add(zeroButton);
    tricolorsPanel.add(negativeButton);

    final JPanel optionsPanel = new JPanel(new GridLayout(0, 1, 0, 0));
    optionsPanel.add(labelPanel);
    optionsPanel.add(separationPanel);
    optionsPanel.add(tricolorsPanel);

    final EasyGBC c = new EasyGBC();
    super.add(new JScrollPane(attrsTable), c.reset().expandHV());
    super.add(optionsPanel, c.anchor("nw").down().noExpand());
    super.add(colorEditorPanel, c.down().expandHV());
  }

  public String getUserName() {
    return "Heat Strip";
  }

  public String getCgName() {
    return "heatstripchart";
  }

  public String buildCgString() {
    final StringBuffer buffer = new StringBuffer();
    attrsTable.appendCgString(buffer);
    buffer.append("showlabels=");
    buffer.append(showLabelsCheckBox.isSelected());
    buffer.append(" colorlist=\"");
    buffer.append("up:#");
    buffer.append(Strings.colorToHex(positiveColor));
    buffer.append(",zero:#");
    buffer.append(Strings.colorToHex(zeroColor));
    buffer.append(",down:#");
    buffer.append(Strings.colorToHex(negativeColor));
    buffer.append('\"');
    buffer.append(" separation=");
    buffer.append(separationSpinner.getValue());
    return buffer.toString();
  }

  public void setup(final CyNetworkView networkView, final View<CyNode> nodeView) {
    attrsTable.forCyTable(networkView.getModel().getTable(CyNode.class, CyNetwork.DEFAULT_ATTRS));
  }
}