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

class LineChartSubpanel extends ChartSubpanel {
  final NumericAttributesWithColorsTable attrsTable = new NumericAttributesWithColorsTable();
  final JSpinner lineWidthSpinner = new JSpinner(new SpinnerNumberModel(1, 1, 100, 1));

  public LineChartSubpanel() {
    attrsTable.getModel().addTableModelListener(new TableModelListener() {
      public void tableChanged(TableModelEvent e) {
        LineChartSubpanel.this.firePropertyChange(CG_CHANGED, null, null);
      }
    });

    lineWidthSpinner.addChangeListener(new ChangeListener() {
      public void stateChanged(ChangeEvent e) {
        LineChartSubpanel.this.firePropertyChange(CG_CHANGED, null, null);
      }
    });

    final JPanel lineWidthPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
    lineWidthPanel.add(new JLabel("Line width:"));
    lineWidthPanel.add(lineWidthSpinner);

    final JPanel optionsPanel = new JPanel(new GridLayout(0, 1, 0, 0));
    optionsPanel.add(lineWidthPanel);

    super.setLayout(new GridBagLayout());
    final EasyGBC c = new EasyGBC();
    super.add(new JScrollPane(attrsTable), c.expandHV());
    super.add(optionsPanel, c.anchor("nw").down().noExpand());
  }

  public String getUserName() {
    return "Line";
  }

  public String getCgName() {
    return "linechart";
  }

  public String buildCgString() {
    final StringBuffer buffer = new StringBuffer();
    attrsTable.appendCgString(buffer);
    buffer.append(" linewidth=");
    buffer.append(lineWidthSpinner.getValue());
    return buffer.toString();
  }

  public void setup(final CyNetworkView networkView, final View<CyNode> nodeView) {
    attrsTable.forCyTable(networkView.getModel().getTable(CyNode.class, CyNetwork.DEFAULT_ATTRS));
  }
}