package org.gladstoneinstitutes.customgraphicsui.internal;

import org.osgi.framework.BundleContext;
import org.cytoscape.service.util.AbstractCyActivator;
import java.util.Properties;
import org.cytoscape.work.ServiceProperties;
import org.cytoscape.task.NodeViewTaskFactory;
import org.cytoscape.view.model.View;
import org.cytoscape.view.model.CyNetworkView;
import org.cytoscape.model.CyNode;
import org.cytoscape.work.TaskIterator;
import org.cytoscape.application.swing.CySwingApplication;
import org.cytoscape.view.presentation.customgraphics.CyCustomGraphicsFactory;

public class CyActivator extends AbstractCyActivator {
  private static Properties ezProps(String... vals) {
    final Properties props = new Properties();
    for (int i = 0; i < vals.length; i += 2)
      props.put(vals[i], vals[i + 1]);
    return props;
  }

  public void start(BundleContext bc) {
    final CySwingApplication swingApp = super.getService(bc, CySwingApplication.class);

    final CustomGraphicsFactoryManager manager = new CustomGraphicsFactoryManager();
    super.registerServiceListener(bc, manager, "addFactory", "removeFactory", CyCustomGraphicsFactory.class);
    
    super.registerService(bc, new ChartNodeViewTaskFactory(swingApp, manager), NodeViewTaskFactory.class, ezProps(
      ServiceProperties.TITLE, "Chart...",
      ServiceProperties.PREFERRED_MENU, ServiceProperties.APPS_MENU
      ));
    super.registerService(bc, new GradientNodeViewTaskFactory(swingApp, manager), NodeViewTaskFactory.class, ezProps(
      ServiceProperties.TITLE, "Gradient...",
      ServiceProperties.PREFERRED_MENU, ServiceProperties.APPS_MENU
      ));
    System.out.println((new java.util.Date()).toString() + "  enhancedGraphicsUI started");
  }
}
