package org.gladstoneinstitutes.customgraphicsui.internal.gradient;

import javax.swing.JComponent;
import java.awt.Dimension;

class LinearPositionEditor extends JComponent {
  LinearPositionEditorUI ui = new LinearPositionEditorUI(this);

  public LinearPositionEditor() {
    super.setUI(ui);
    super.setPreferredSize(new Dimension(400, 400));
  }
}
