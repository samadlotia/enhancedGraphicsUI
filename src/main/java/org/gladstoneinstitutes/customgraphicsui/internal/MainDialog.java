package org.gladstoneinstitutes.customgraphicsui.internal;

import java.awt.Frame;
import javax.swing.JDialog;
import javax.swing.JTabbedPane;

import org.cytoscape.model.CyTable;

import org.gladstoneinstitutes.customgraphicsui.internal.chart.ChartPanel;
import org.gladstoneinstitutes.customgraphicsui.internal.gradient.GradientPanel;

public class MainDialog extends JDialog {
  final ChartPanel chartPanel;
  final GradientPanel gradientPanel;
  public MainDialog(final Frame parent, final CustomGraphicsFactoryManager manager) {
    super(parent, "Enhanced Graphics", false);
    chartPanel = new ChartPanel();
    gradientPanel = new GradientPanel(manager);

    final JTabbedPane pane = new JTabbedPane();
    pane.addTab("Chart", chartPanel);
    pane.addTab("Gradient", gradientPanel);
    super.add(pane);
  }

  public void setChartPanelForCyTable(final CyTable table) {
    chartPanel.forCyTable(table);
  }
}