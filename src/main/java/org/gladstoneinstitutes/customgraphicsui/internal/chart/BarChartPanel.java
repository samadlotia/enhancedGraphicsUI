package org.gladstoneinstitutes.customgraphicsui.internal.chart;

import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.FlowLayout;
import org.gladstoneinstitutes.customgraphicsui.internal.util.EasyGBC;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

import javax.swing.event.TableModelListener;
import javax.swing.event.TableModelEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.ChangeEvent;

import org.cytoscape.model.CyNode;
import org.cytoscape.model.CyNetwork;
import org.cytoscape.view.model.CyNetworkView;
import org.cytoscape.view.model.View;

class BarChartPanel extends JPanel {
  public static final String CG_CHANGED = "cg changed";

  final BarNumericAttributesTable attrsTable = new BarNumericAttributesTable();
  final JCheckBox showLabelsCheckBox = new JCheckBox("Show labels");
  final JSpinner separationSpinner = new JSpinner(new SpinnerNumberModel(0, 0, 100, 1));

  public BarChartPanel() {
    super(new GridBagLayout());

    attrsTable.getModel().addTableModelListener(new TableModelListener() {
      public void tableChanged(TableModelEvent e) {
        BarChartPanel.this.firePropertyChange(CG_CHANGED, null, null);
      }
    });

    showLabelsCheckBox.setSelected(true);
    showLabelsCheckBox.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        BarChartPanel.this.firePropertyChange(CG_CHANGED, null, null);
      }
    });

    separationSpinner.addChangeListener(new ChangeListener() {
      public void stateChanged(ChangeEvent e) {
        BarChartPanel.this.firePropertyChange(CG_CHANGED, null, null);
      }
    });

    final JPanel separationPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
    separationPanel.add(new JLabel("Space between bars:"));
    separationPanel.add(separationSpinner);

    final JPanel optionsPanel = new JPanel(new GridLayout(0, 1, 0, 0));
    optionsPanel.add(showLabelsCheckBox);
    optionsPanel.add(separationPanel);

    final EasyGBC c = new EasyGBC();
    super.add(new JScrollPane(attrsTable), c.expandHV());
    super.add(optionsPanel, c.anchor("nw").down().noExpand());
  }

  public String buildCgString() {
    final StringBuffer buffer = new StringBuffer();
    attrsTable.appendCgString(buffer);
    buffer.append("showlabels=");
    buffer.append(showLabelsCheckBox.isSelected());
    buffer.append(" separation=");
    buffer.append(separationSpinner.getValue());
    return buffer.toString();
  }

  public void setup(final CyNetworkView networkView, final View<CyNode> nodeView) {
    attrsTable.forCyTable(networkView.getModel().getTable(CyNode.class, CyNetwork.DEFAULT_ATTRS));
  }
}