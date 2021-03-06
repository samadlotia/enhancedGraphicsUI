package org.gladstoneinstitutes.customgraphicsui.internal.chart;

import java.util.List;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.FlowLayout;
import org.gladstoneinstitutes.customgraphicsui.internal.util.EasyGBC;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JCheckBox;
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

import org.cytoscape.model.CyNode;
import org.cytoscape.model.CyNetwork;
import org.cytoscape.view.model.CyNetworkView;
import org.cytoscape.view.model.View;

class PieChartSubpanel extends ChartSubpanel {
  final NumericAttrsTable attrsTable;
  final JCheckBox showLabelsCheckBox = new JCheckBox("Labels: ");
  final JSpinner labelSizeSpinner = new JSpinner(new SpinnerNumberModel(8, 1, 100, 1));
  final JSlider arcStartSlider = new JSlider(0, 360, 0);
  final JSpinner arcStartSpinner = new JSpinner(new SpinnerNumberModel(0, 0, 360, 45));

  public PieChartSubpanel(final List<NumericAttr> rows) {
    super.setLayout(new GridBagLayout());

    attrsTable = new NumericAttrsTable(rows, true);

    attrsTable.getModel().addTableModelListener(new TableModelListener() {
      public void tableChanged(TableModelEvent e) {
        PieChartSubpanel.this.firePropertyChange(CG_CHANGED, null, null);
      }
    });

    showLabelsCheckBox.setSelected(true);
    showLabelsCheckBox.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        PieChartSubpanel.this.firePropertyChange(CG_CHANGED, null, null);
        labelSizeSpinner.setEnabled(showLabelsCheckBox.isSelected());
      }
    });

    labelSizeSpinner.addChangeListener(new ChangeListener() {
      public void stateChanged(ChangeEvent e) {
        PieChartSubpanel.this.firePropertyChange(CG_CHANGED, null, null);
      }
    });

    arcStartSlider.setPaintTicks(true);
    arcStartSlider.setMajorTickSpacing(45);
    arcStartSlider.addChangeListener(new ChangeListener() {
      public void stateChanged(ChangeEvent e) {
        arcStartSpinner.setValue(arcStartSlider.getValue());
      }
    });

    arcStartSpinner.addChangeListener(new ChangeListener() {
      public void stateChanged(ChangeEvent e) {
        PieChartSubpanel.this.firePropertyChange(CG_CHANGED, null, null);
        arcStartSlider.setValue(((Number)arcStartSpinner.getValue()).intValue());
      }
    });

    final JPanel labelPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
    labelPanel.add(showLabelsCheckBox);
    labelPanel.add(new JLabel("Size: "));
    labelPanel.add(labelSizeSpinner);

    final JPanel arcStartPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
    arcStartPanel.add(new JLabel("Rotate: "));
    arcStartPanel.add(arcStartSlider);
    arcStartPanel.add(arcStartSpinner);

    final JPanel optionsPanel = new JPanel(new GridLayout(0, 1, 0, 0));
    optionsPanel.add(labelPanel);
    optionsPanel.add(arcStartPanel);

    final EasyGBC c = new EasyGBC();
    super.add(new JScrollPane(attrsTable), c.expandHV());
    super.add(optionsPanel, c.anchor("nw").down().noExpand());
  }

  public String getUserName() {
    return "Pie";
  }

  public String getCgName() {
    return "piechart";
  }

  public String buildCgString() {
    final StringBuffer buffer = new StringBuffer();
    attrsTable.appendCgString(buffer);
    buffer.append("showlabels=");
    buffer.append(showLabelsCheckBox.isSelected());
    buffer.append(" labelsize=");
    buffer.append(labelSizeSpinner.getValue());
    buffer.append(" arcstart=");
    buffer.append(arcStartSpinner.getValue());
    return buffer.toString();
  }

  public void refreshTable() {
    ((NumericAttrsModel) attrsTable.getModel()).fireTableDataChanged();
  }
}