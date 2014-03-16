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

import org.gladstoneinstitutes.customgraphicsui.internal.gradient.GradientDialog;

class GradientNodeViewTaskFactory implements NodeViewTaskFactory {
  final CySwingApplication swingApp;
  final CustomGraphicsFactoryManager manager;

  public GradientNodeViewTaskFactory(final CySwingApplication swingApp, final CustomGraphicsFactoryManager manager) {
    this.swingApp = swingApp;
    this.manager = manager;
  }

  public TaskIterator createTaskIterator(View<CyNode> nodeView, CyNetworkView networkView) {
    return new TaskIterator(new GradientNodeViewTask(swingApp, manager, networkView, nodeView));
  }

  public boolean isReady(View<CyNode> nodeView, CyNetworkView networkView) {
    return true;
  }
}

class GradientNodeViewTask implements Task {
  final CySwingApplication swingApp;
  final CustomGraphicsFactoryManager manager;
  final CyNetworkView networkView;
  final View<CyNode> nodeView;

  public GradientNodeViewTask(final CySwingApplication swingApp, final CustomGraphicsFactoryManager manager, final CyNetworkView networkView, final View<CyNode> nodeView) { 
    this.swingApp = swingApp;
    this.manager = manager;
    this.networkView = networkView;
    this.nodeView = nodeView;
  }

  public void run(TaskMonitor monitor) {
    final GradientDialog d = new GradientDialog(swingApp.getJFrame(), manager);
    //d.setup(networkView, nodeView);
    d.pack();
    d.setVisible(true);
  }

  public void cancel() {}
}