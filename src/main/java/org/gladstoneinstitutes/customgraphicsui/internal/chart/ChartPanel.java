package org.gladstoneinstitutes.customgraphicsui.internal.chart;

import java.awt.GridBagLayout;
import java.awt.Graphics;
import java.awt.Graphics2D;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JComponent;

import javax.swing.event.TableModelListener;
import javax.swing.event.TableModelEvent;

import org.cytoscape.model.CyTable;

import org.gladstoneinstitutes.customgraphicsui.internal.util.EasyGBC;

public class ChartPanel extends JPanel {
  final AttributesTable attrsTable;
  public ChartPanel() {
    super(new GridBagLayout());

    attrsTable = new AttributesTable();
    attrsTable.getModel().addTableModelListener(new TableModelListener() {
      public void tableChanged(final TableModelEvent e) {
        System.out.println(attrsTable.getAttributes().buildCgString(new StringBuffer()).toString());
      }
    });

    final EasyGBC c = new EasyGBC();
    super.add(new JScrollPane(attrsTable), c.expandHV());
  }

  public void forCyTable(final CyTable table) {
    attrsTable.forCyTable(table);
  }
}

class ChartPreview extends JComponent {
  protected void paintComponent(Graphics g) {

  }
}