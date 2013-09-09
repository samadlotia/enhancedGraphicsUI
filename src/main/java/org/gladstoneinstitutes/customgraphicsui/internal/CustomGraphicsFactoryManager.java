package org.gladstoneinstitutes.customgraphicsui.internal;

import java.util.Map;
import java.util.HashMap;
import org.cytoscape.view.presentation.customgraphics.CyCustomGraphicsFactory;
import org.cytoscape.view.presentation.customgraphics.CustomGraphicLayer;

public class CustomGraphicsFactoryManager {
  final Map<String,CyCustomGraphicsFactory<? extends CustomGraphicLayer>> factories = new HashMap<String,CyCustomGraphicsFactory<? extends CustomGraphicLayer>>();

  public void addFactory(CyCustomGraphicsFactory<? extends CustomGraphicLayer> factory, Map props) {
    factories.put(factory.getPrefix(), factory);
  }

  public void removeFactory(CyCustomGraphicsFactory<? extends CustomGraphicLayer> factory, Map props) {
    factories.remove(factory.getPrefix());
  }

  public CyCustomGraphicsFactory<? extends CustomGraphicLayer> getFactory(final String prefix) {
    return factories.get(prefix);
  }
}