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

public class CyActivator extends AbstractCyActivator {
  public static CySwingApplication swingApp = null;

  private static Properties ezProps(String... vals) {
    final Properties props = new Properties();
    for (int i = 0; i < vals.length; i += 2)
      props.put(vals[i], vals[i + 1]);
    return props;
  }

  public void start(BundleContext bc) {
    swingApp = super.getService(bc, CySwingApplication.class);
    
    super.registerService(bc, new AddCustomGraphicNodeViewTaskFactory(), NodeViewTaskFactory.class, ezProps(
      ServiceProperties.TITLE, "Add Custom Graphic",
      ServiceProperties.PREFERRED_MENU, ServiceProperties.APPS_MENU
      ));
  }
}
