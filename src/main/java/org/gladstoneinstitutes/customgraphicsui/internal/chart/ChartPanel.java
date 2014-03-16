package org.gladstoneinstitutes.customgraphicsui.internal.chart;

import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
import java.util.Arrays;

import java.awt.Dimension;
import java.awt.GridBagLayout;
import java.awt.CardLayout;
import java.awt.Font;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Color;
import java.awt.Paint;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.geom.AffineTransform;
import java.awt.RenderingHints;
import java.awt.Insets;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.geom.Path2D;
import java.awt.geom.Rectangle2D;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JComponent;
import javax.swing.JRadioButton;
import javax.swing.JToggleButton;
import javax.swing.JLabel;
import javax.swing.JSplitPane;
import javax.swing.ButtonGroup;
import javax.swing.BorderFactory;

import javax.swing.event.TableModelListener;
import javax.swing.event.TableModelEvent;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeEvent;

import org.cytoscape.model.CyNode;
import org.cytoscape.model.CyNetwork;
import org.cytoscape.view.model.CyNetworkView;
import org.cytoscape.view.model.View;

import org.cytoscape.view.presentation.customgraphics.CyCustomGraphicsFactory;
import org.cytoscape.view.presentation.customgraphics.CyCustomGraphics;
import org.cytoscape.view.presentation.customgraphics.CustomGraphicLayer;
import org.cytoscape.view.presentation.customgraphics.PaintedShape;

import org.gladstoneinstitutes.customgraphicsui.internal.CustomGraphicsFactoryManager;
import org.gladstoneinstitutes.customgraphicsui.internal.util.EasyGBC;

public class ChartPanel extends JPanel {
  final ChartPreview preview;
  final List<NumericAttr> numericAttrs = new ArrayList<NumericAttr>();
  final Map<String,ChartSubpanel> subpanels = new HashMap<String,ChartSubpanel>();
  ChartSubpanel currentSubpanel = null;

  public ChartPanel(final CustomGraphicsFactoryManager cgMgr) {
    super(new GridBagLayout());
    preview = new ChartPreview(cgMgr);

    final JPanel typePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
    final ButtonGroup typeGroup = new ButtonGroup();
    final PropertyChangeListener cgUpdater = new PropertyChangeListener() {
      public void propertyChange(final PropertyChangeEvent e) {
        if (currentSubpanel == null) return;
        preview.assignCg(currentSubpanel.getCgName(), currentSubpanel.buildCgString());
      }
    };

    final CardLayout cardLayout = new CardLayout();
    final JPanel subpanelsPanel = new JPanel(cardLayout);
    subpanelsPanel.add(new JPanel(), "empty");
    cardLayout.show(subpanelsPanel, "empty");
    for (final ChartSubpanel subpanel : Arrays.asList(
        new BarChartSubpanel(numericAttrs),
        new HeatStripSubpanel(numericAttrs),
        new LineChartSubpanel(numericAttrs),
        new PieChartSubpanel(numericAttrs)
      )) {
      subpanels.put(subpanel.getUserName(), subpanel);
      subpanelsPanel.add(subpanel, subpanel.getUserName());
      subpanel.addPropertyChangeListener(cgUpdater);
      final JToggleButton button = new JToggleButton(subpanel.getUserName());
      typeGroup.add(button);
      typePanel.add(button);
      button.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          currentSubpanel = subpanel;
          cardLayout.show(subpanelsPanel, subpanel.getUserName());
          cgUpdater.propertyChange(null);
        }
      });
    }

    final EasyGBC c = new EasyGBC();
    final JPanel leftPanel = new JPanel(new GridBagLayout());
    leftPanel.add(typePanel, c.expandH().insets(0, 0, 0, 0));
    leftPanel.add(subpanelsPanel, c.noSpan().down().expandHV().insets(0, 10, 10, 10));

    final JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, leftPanel, preview);
    super.add(splitPane, c.reset().expandHV());
  }

  public void setup(final CyNetworkView networkView, final View<CyNode> nodeView) {
    preview.setup(networkView, nodeView);
    NumericAttr.fillInList(networkView.getModel().getTable(CyNode.class, CyNetwork.DEFAULT_ATTRS), numericAttrs);
    for (final ChartSubpanel subpanel : subpanels.values()) {
      subpanel.refreshTable();
    }
  }
}
