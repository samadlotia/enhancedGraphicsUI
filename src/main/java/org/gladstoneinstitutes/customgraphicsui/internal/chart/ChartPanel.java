package org.gladstoneinstitutes.customgraphicsui.internal.chart;

import java.awt.Dimension;
import java.awt.GridBagLayout;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Color;
import java.awt.Paint;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.RenderingHints;
import java.awt.Insets;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.geom.Rectangle2D;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JComponent;
import javax.swing.JRadioButton;
import javax.swing.JLabel;
import javax.swing.ButtonGroup;
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

    final ButtonGroup typeGroup = new ButtonGroup();
    final JRadioButton barButton = new JRadioButton("Bar");
    typeGroup.add(barButton);
    barButton.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        chart = new BarChart();
        chart.setAttributes(attrsTable.getAttributes());
        preview.repaint();
      }
    });
    final JRadioButton pieButton = new JRadioButton("Pie");
    typeGroup.add(pieButton);
    pieButton.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        chart = new PieChart();
        chart.setAttributes(attrsTable.getAttributes());
        preview.repaint();
      }
    });
    final JRadioButton heatstripButton = new JRadioButton("Heatstrip");
    typeGroup.add(heatstripButton);
    heatstripButton.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        chart = new HeatstripChart();
        chart.setAttributes(attrsTable.getAttributes());
        preview.repaint();
      }
    });
    final JPanel typePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
    typePanel.add(new JLabel("Type: "));
    typePanel.add(barButton);
    typePanel.add(pieButton);
    typePanel.add(heatstripButton);

    final EasyGBC c = new EasyGBC();
    super.add(preview, c.spanV(2).expand(0.5, 1.0).insets(10, 10, 10, 0));
    super.add(typePanel, c.noSpan().right().expandH().insets(10, 10, 0, 0));
    super.add(new JScrollPane(attrsTable), c.down().right().expand(0.5, 1.0).insets(0, 10, 10, 10));
  }

  public void setup(final CyNetworkView networkView, final View<CyNode> nodeView) {
    attrsTable.forCyTable(networkView.getModel().getTable(CyNode.class, CyNetwork.DEFAULT_ATTRS));
    this.networkView = networkView;
    this.nodeView = nodeView;
  }

  class ChartPreview extends JComponent {
    // members so that these objects are not allocated every time paintComponent is called
    final Rectangle2D.Float componentBounds = new Rectangle2D.Float();
    final Rectangle2D.Float chartBounds     = new Rectangle2D.Float();
    final Insets            insets = new Insets(0, 0, 0, 0);
    final AffineTransform   at     = new AffineTransform();

    public ChartPreview() {
      super.setPreferredSize(new Dimension(300, 300));
    }

    protected void paintComponent(Graphics g) {
      if (chart == null || networkView == null || nodeView == null)
        return;

      // obtain the custom graphics
      final CyCustomGraphicsFactory<? extends CustomGraphicLayer> factory = cgMgr.getFactory(chart.getCgName());
      final String cgString = chart.buildCgString();
      if (cgString == null)
        return;
      final CyCustomGraphics<? extends CustomGraphicLayer> customGraphics = factory.getInstance(cgString);
      final float fit = customGraphics.getFitRatio();

      // setup g2d
      final Graphics2D g2d = (Graphics2D) g;
      g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

      // calculate componentBounds -- the rect wherein the chart is painted
      super.getInsets(insets);
      componentBounds.setRect(insets.left, insets.top, super.getWidth() - insets.left - insets.right, super.getHeight() - insets.top - insets.bottom);

      // calculate chartBounds -- the rect that's a union of each chart layer's shape boundaries
      chartBounds.setRect(0.0, 0.0, 0.0, 0.0);
      for (CustomGraphicLayer layer : customGraphics.getLayers(networkView, nodeView)) {
        final PaintedShape ps = (PaintedShape) layer;
        final Shape shape = ps.getShape();
        chartBounds.add(shape.getBounds2D());
      }

      // transform g2d such that chartBounds is scaled and translated to fit exactly in the middle of componentBounds
      final AffineTransform originalAt = g2d.getTransform();
      double factor;
      if (componentBounds.height / componentBounds.width > chartBounds.height / chartBounds.width) {
        factor = componentBounds.width / chartBounds.width;
      } else {
        factor = componentBounds.height / chartBounds.height;
      }
      factor *= fit;
      g2d.translate(componentBounds.x + componentBounds.width / 2.0, componentBounds.y + componentBounds.height / 2.0);
      g2d.scale(factor, factor);
      g2d.translate(-chartBounds.x - chartBounds.width / 2.0, -chartBounds.y - chartBounds.height / 2.0);

      // paint each layer
      for (CustomGraphicLayer layer : customGraphics.getLayers(networkView, nodeView)) {
        final PaintedShape ps = (PaintedShape) layer;
        final Shape shape = ps.getShape();

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

      // reset the transform so that other things being painted with this g2d won't get messed up like component border
      g2d.setTransform(originalAt);
    }
  }
}
