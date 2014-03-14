package org.gladstoneinstitutes.customgraphicsui.internal.chart;

import java.util.List;
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
    for (final ChartSubpanel subpanel : Arrays.asList(
        new BarChartSubpanel(),
        new HeatStripSubpanel(),
        new LineChartSubpanel(),
        new PieChartSubpanel()
      )) {
      subpanels.put(subpanel.getUserName(), subpanel);
      subpanelsPanel.add(subpanel, subpanel.getUserName());
      subpanel.addPropertyChangeListener(cgUpdater);
      final JRadioButton button = new JRadioButton(subpanel.getUserName());
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
    typeGroup.getElements().nextElement().setSelected(true); // select the first panel's button

    final EasyGBC c = new EasyGBC();
    final JPanel leftPanel = new JPanel(new GridBagLayout());
    leftPanel.add(typePanel, c.expandH().insets(10, 10, 0, 0));
    leftPanel.add(subpanelsPanel, c.noSpan().down().expandHV().insets(0, 10, 10, 10));

    final JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, leftPanel, preview);
    super.add(splitPane, c.reset().expandHV());
  }

  public void setup(final CyNetworkView networkView, final View<CyNode> nodeView) {
    preview.setup(networkView, nodeView);
    for (final ChartSubpanel subpanel : subpanels.values()) {
      subpanel.setup(networkView, nodeView);
    }
  }
}

class ChartPreview extends JComponent {
  final CustomGraphicsFactoryManager cgMgr;

  static final String NO_PREVIEW_TEXT = "No Preview";
  static final Color NO_PREVIEW_COLOR = new Color(0xB3B3B3);

  // allocate these only when needed in the newNoPreview() method
  Shape noPreviewShape = null;
  Rectangle2D.Float noPreviewBounds = null;

  // these get filled in by the setup() method
  CyNetworkView networkView = null;
  View<CyNode> nodeView = null;

  // these get filld in by the assignCg() method
  CyCustomGraphicsFactory<? extends CustomGraphicLayer> factory = null;
  String cgString = null;

  public ChartPreview(final CustomGraphicsFactoryManager cgMgr) {
    super.setPreferredSize(new Dimension(100, 100));
    this.cgMgr = cgMgr;
  }

  public void setup(final CyNetworkView networkView, final View<CyNode> nodeView) {
    this.networkView = networkView;
    this.nodeView = nodeView;
  }

  public void assignCg(final String cgName, final String cgString) {
    this.factory = cgMgr.getFactory(cgName);
    this.cgString = cgString;
    //System.out.println("cgString: " + cgString);
    super.repaint();
  }

  // members so that these objects are not allocated every time paintComponent is called
  final Rectangle2D.Float componentBounds = new Rectangle2D.Float();
  final Rectangle2D.Float chartBounds     = new Rectangle2D.Float();
  final Insets            insets = new Insets(0, 0, 0, 0);

  protected void paintComponent(Graphics g) {
      // setup g2d
    final Graphics2D g2d = (Graphics2D) g;
    final AffineTransform originalAt = g2d.getTransform();
    final Stroke originalStroke = g2d.getStroke();
    g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

    updateComponentBounds();

    // obtain the custom graphics
    CyCustomGraphics<? extends CustomGraphicLayer> customGraphics = null;
    List<? extends CustomGraphicLayer> layers = null;
    if (networkView != null && nodeView != null && factory != null && cgString != null) {
      try {
        customGraphics = factory.getInstance(cgString);
        layers = customGraphics.getLayers(networkView, nodeView);
      } catch (Exception e) {
        // we need to catch exceptions from the cg engine, otherwise
        // the exception blows up the Swing event thread and messes up the UI
        /*
        System.err.println("Custom graphics internal error:");
        e.printStackTrace();
        */
      }
    }

    if (layers == null) { 
      // layers is null if the cg string is not acceptable or there's a bug in the cg string parser
      paintNoPreview(g2d);
    } else {
      // we have some custom graphics! now let's paint it
      final float fit = customGraphics.getFitRatio();
      paintCgLayers(g2d, layers, fit);
    }

      // reset g2d so that other things being painted with this g2d won't get messed up like component border
    g2d.setTransform(originalAt);
    g2d.setStroke(originalStroke);
  }

  private void paintCgLayers(final Graphics2D g2d, final Iterable<? extends CustomGraphicLayer> layers, final double fit) {
    calculateChartBounds(chartBounds, layers);
    centerBounds(g2d, chartBounds, fit);

    for (final CustomGraphicLayer layer : layers) {
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
  }

  /**
   * Calculate the component bounds, the rect wherein the chart is painted
   */
  private void updateComponentBounds() {
    super.getInsets(insets);
    componentBounds.setRect(insets.left, insets.top, super.getWidth() - insets.left - insets.right, super.getHeight() - insets.top - insets.bottom);
  }

  /**
   * Calculate chart bounds, the rect that's a union of each chart layer's shape boundaries
   */
  private void calculateChartBounds(final Rectangle2D.Float chartBounds, final Iterable<? extends CustomGraphicLayer> layers) {
    chartBounds.setRect(0.0, 0.0, 0.0, 0.0);
    for (final CustomGraphicLayer layer : layers) {
      final PaintedShape ps = (PaintedShape) layer;
      final Shape shape = ps.getShape();
      chartBounds.add(shape.getBounds2D());
    }
  }

  /**
   * Transform g2d such that contentBounds is scaled and translated to fit exactly in the middle of componentBounds
   */
  private void centerBounds(final Graphics2D g2d, final Rectangle2D.Float contentBounds, final double fit) {
    double factor = 1.0;
    if (contentBounds.height > contentBounds.width) {
      factor = componentBounds.height / contentBounds.height;
      if (factor * contentBounds.width > componentBounds.width) {
        factor *= componentBounds.width / (factor * contentBounds.width);
      }
    } else {
      factor = componentBounds.width / contentBounds.width;
      if (factor * contentBounds.height > componentBounds.height) {
        factor *= componentBounds.height / (factor * contentBounds.height);
      }
    }
    factor *= fit;
    g2d.translate(componentBounds.x + componentBounds.width / 2.0, componentBounds.y + componentBounds.height / 2.0);
    g2d.scale(factor, factor);
    g2d.translate(-contentBounds.x - contentBounds.width / 2.0, -contentBounds.y - contentBounds.height / 2.0);
  }

  private void newNoPreviewShape() {
    final Font f = getFont();
    final Shape textShape = f.createGlyphVector(getFontMetrics(f).getFontRenderContext(), NO_PREVIEW_TEXT).getOutline();
    noPreviewShape = new Path2D.Float(textShape, AffineTransform.getRotateInstance(-Math.PI / 4.0));
    noPreviewBounds = new Rectangle2D.Float();
    noPreviewBounds.add(noPreviewShape.getBounds2D());
  }

  private void paintNoPreview(final Graphics2D g2d) {
    if (noPreviewShape == null)
      newNoPreviewShape();
    updateComponentBounds();
    centerBounds(g2d, noPreviewBounds, 0.7);
    g2d.setPaint(NO_PREVIEW_COLOR);
    g2d.fill(noPreviewShape);
  }
}
