package org.gladstoneinstitutes.customgraphicsui.internal;

import java.awt.Frame;
import javax.swing.JDialog;
import javax.swing.JTabbedPane;

import org.gladstoneinstitutes.customgraphicsui.internal.chart.ChartPanel;
import org.gladstoneinstitutes.customgraphicsui.internal.gradient.GradientPanel;

public class MainDialog extends JDialog {
  public MainDialog(final Frame parent, final CustomGraphicsFactoryManager manager) {
    super(parent, "Enhanced Graphics", false);

    final JTabbedPane pane = new JTabbedPane();
    pane.addTab("Chart", new ChartPanel());
    pane.addTab("Gradient", new GradientPanel(manager));
    super.add(pane);
  }
}