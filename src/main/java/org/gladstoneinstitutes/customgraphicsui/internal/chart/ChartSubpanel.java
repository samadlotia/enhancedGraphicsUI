package org.gladstoneinstitutes.customgraphicsui.internal.chart;

import javax.swing.JPanel;
import org.cytoscape.model.CyNode;
import org.cytoscape.model.CyNetwork;
import org.cytoscape.view.model.CyNetworkView;
import org.cytoscape.view.model.View;

abstract class ChartSubpanel extends JPanel {
  public static final String CG_CHANGED = "cg changed";

	public abstract String getCgName();
	public abstract String buildCgString();
	public abstract String getUserName();
  public abstract void setup(final CyNetworkView networkView, final View<CyNode> nodeView);
}