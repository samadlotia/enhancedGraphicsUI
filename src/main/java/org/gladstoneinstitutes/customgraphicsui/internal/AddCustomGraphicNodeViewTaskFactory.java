package org.gladstoneinstitutes.customgraphicsui.internal;

import org.cytoscape.task.NodeViewTaskFactory;
import org.cytoscape.view.model.View;
import org.cytoscape.view.model.CyNetworkView;
import org.cytoscape.model.CyNode;
import org.cytoscape.work.TaskIterator;
import org.cytoscape.work.Task;
import org.cytoscape.work.TaskMonitor;

import org.gladstoneinstitutes.customgraphicsui.internal.gradient.GradientDialog;

class AddCustomGraphicNodeViewTaskFactory implements NodeViewTaskFactory {
  public TaskIterator createTaskIterator(View<CyNode> nodeView, CyNetworkView networkView) {
    return new TaskIterator(new AddCustomGraphicTask());
  }

  public boolean isReady(View<CyNode> nodeView, CyNetworkView networkView) {
    return true;
  }
}

class AddCustomGraphicTask implements Task {
  public void run(TaskMonitor monitor) {
    final GradientDialog d = new GradientDialog(CyActivator.swingApp.getJFrame());
    d.pack();
    d.setVisible(true);
  }

  public void cancel() {}
}