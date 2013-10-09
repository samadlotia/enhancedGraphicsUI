package org.gladstoneinstitutes.customgraphicsui.internal.chart;

import java.awt.GridBagLayout;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import org.cytoscape.model.CyTable;

import org.gladstoneinstitutes.customgraphicsui.internal.util.EasyGBC;

public class ChartPanel extends JPanel {
  final AttributesTable attrsTable;
  public ChartPanel() {
    super(new GridBagLayout());

    attrsTable = new AttributesTable();

    final EasyGBC c = new EasyGBC();
    super.add(new JScrollPane(attrsTable), c.expandHV());
  }

  public void forCyTable(final CyTable table) {
    attrsTable.forCyTable(table);
  }
}