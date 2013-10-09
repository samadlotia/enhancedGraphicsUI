package org.gladstoneinstitutes.customgraphicsui.internal;

import org.cytoscape.task.NodeViewTaskFactory;
import org.cytoscape.model.CyNetwork;
import org.cytoscape.model.CyNode;
import org.cytoscape.view.model.View;
import org.cytoscape.view.model.CyNetworkView;
import org.cytoscape.model.CyNode;
import org.cytoscape.work.TaskIterator;
import org.cytoscape.work.Task;
import org.cytoscape.work.TaskMonitor;
import org.cytoscape.application.swing.CySwingApplication;


class AddCustomGraphicNodeViewTaskFactory implements NodeViewTaskFactory {
  final CySwingApplication swingApp;
  final CustomGraphicsFactoryManager manager;

  public AddCustomGraphicNodeViewTaskFactory(final CySwingApplication swingApp, final CustomGraphicsFactoryManager manager) {
    this.swingApp = swingApp;
    this.manager = manager;
  }

  public TaskIterator createTaskIterator(View<CyNode> nodeView, CyNetworkView networkView) {
    return new TaskIterator(new AddCustomGraphicTask(swingApp, manager, networkView.getModel()));
  }

  public boolean isReady(View<CyNode> nodeView, CyNetworkView networkView) {
    return true;
  }
}

class AddCustomGraphicTask implements Task {
  final CySwingApplication swingApp;
  final CustomGraphicsFactoryManager manager;
  final CyNetwork network;

  public AddCustomGraphicTask(final CySwingApplication swingApp, final CustomGraphicsFactoryManager manager, final CyNetwork network) { 
    this.swingApp = swingApp;
    this.manager = manager;
    this.network = network;
  }

  public void run(TaskMonitor monitor) {
    final MainDialog d = new MainDialog(swingApp.getJFrame(), manager);
    d.setChartPanelForCyTable(network.getTable(CyNode.class, CyNetwork.DEFAULT_ATTRS));
    d.pack();
    d.setVisible(true);
  }

  public void cancel() {}
}