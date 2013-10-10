package org.gladstoneinstitutes.customgraphicsui.internal.chart;

import java.awt.GridBagLayout;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Color;
import java.awt.Paint;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.RenderingHints;
import java.awt.Insets;
import java.awt.geom.Rectangle2D;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JComponent;
import javax.swing.BorderFactory;

import javax.swing.event.TableModelListener;
import javax.swing.event.TableModelEvent;

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
  final CustomGraphicsFactoryManager cgMgr;
  final AttributesTable attrsTable;
  final ChartPreview preview = new ChartPreview();

  Chart chart = new BarChart();
  CyNetworkView networkView = null;
  View<CyNode> nodeView = null;

  public ChartPanel(final CustomGraphicsFactoryManager cgMgr) {
    super(new GridBagLayout());

    this.cgMgr = cgMgr;
    attrsTable = new AttributesTable();
    attrsTable.getModel().addTableModelListener(new TableModelListener() {
      public void tableChanged(final TableModelEvent e) {
        chart.setAttributes(attrsTable.getAttributes());
        preview.repaint();
      }
    });

    preview.setBorder(BorderFactory.createLineBorder(java.awt.Color.GRAY, 1));

    final EasyGBC c = new EasyGBC();
    super.add(preview, c.expand(0.5, 1.0));
    super.add(new JScrollPane(attrsTable), c.right().insets(0, 10, 0, 0));
  }

  public void setup(final CyNetworkView networkView, final View<CyNode> nodeView) {
    attrsTable.forCyTable(networkView.getModel().getTable(CyNode.class, CyNetwork.DEFAULT_ATTRS));
    this.networkView = networkView;
    this.nodeView = nodeView;
  }

  class ChartPreview extends JComponent {
    final Rectangle2D.Float box    = new Rectangle2D.Float();
    final Insets            insets = new Insets(0, 0, 0, 0);
    final AffineTransform   at     = new AffineTransform();


    protected void paintComponent(Graphics g) {
      if (chart == null || networkView == null || nodeView == null)
        return;

      super.getInsets(insets);
      box.x = insets.left;
      box.y = insets.top;
      box.width = super.getWidth() - insets.left - insets.right;
      box.height = super.getHeight() - insets.top - insets.bottom;
      System.out.println(box);

      final Graphics2D g2d = (Graphics2D) g;
      g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

      final CyCustomGraphicsFactory<? extends CustomGraphicLayer> factory = cgMgr.getFactory(chart.getCgName());
      final String cgString = chart.buildCgString();
      if (cgString == null)
        return;
      final CyCustomGraphics<? extends CustomGraphicLayer> customGraphics = factory.getInstance(cgString);
      final float fit = customGraphics.getFitRatio();

      for (CustomGraphicLayer layer : customGraphics.getLayers(networkView, nodeView)) {
        final Rectangle2D originalBounds = layer.getBounds2D();
        System.out.println(originalBounds);
        if (originalBounds != null) {
          g2d.scale(fit * box.width / originalBounds.getWidth(), fit * box.height / originalBounds.getHeight());
          /*
          at.setToScale(fit * box.width / originalBounds.getWidth(), fit * box.height / originalBounds.getHeight());
          layer = layer.transform(at);
          */
        }
        final PaintedShape ps = (PaintedShape) layer;
        final Shape shape = ps.getShape();
        g2d.translate(box.x, box.y);
        if (ps.getStroke() != null) {
          Paint strokePaint = ps.getStrokePaint();
          if (strokePaint == null) strokePaint = Color.BLACK;
          g2d.setPaint(strokePaint);
          g2d.setStroke(ps.getStroke());
          g2d.draw(shape);
        }
        g2d.setPaint(ps.getPaint());
        g2d.fill(shape);
      }
    }
  }
}
