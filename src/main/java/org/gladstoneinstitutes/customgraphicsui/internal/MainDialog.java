package org.gladstoneinstitutes.customgraphicsui.internal;

import java.awt.Frame;
import javax.swing.JDialog;
import javax.swing.JTabbedPane;

import org.cytoscape.model.CyNode;
import org.cytoscape.view.model.CyNetworkView;
import org.cytoscape.view.model.View;

import org.gladstoneinstitutes.customgraphicsui.internal.chart.ChartPanel;
import org.gladstoneinstitutes.customgraphicsui.internal.gradient.GradientPanel;

public class MainDialog extends JDialog {
  final ChartPanel chartPanel;
  final GradientPanel gradientPanel;
  public MainDialog(final Frame parent, final CustomGraphicsFactoryManager manager) {
    super(parent, "Enhanced Graphics", false);
    chartPanel = new ChartPanel(manager);
    gradientPanel = new GradientPanel(manager);

    final JTabbedPane pane = new JTabbedPane();
    pane.addTab("Chart", chartPanel);
    pane.addTab("Gradient", gradientPanel);
    super.add(pane);
  }

  public void setup(final CyNetworkView networkView, final View<CyNode> nodeView) {
    chartPanel.setup(networkView, nodeView);
  }
}