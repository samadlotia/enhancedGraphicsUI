package org.gladstoneinstitutes.customgraphicsui.internal.gradient;

import javax.swing.JComponent;

class LinearPositionEditor extends JComponent {
  LinearPositionEditorUI ui = new LinearPositionEditorUI(this);

  public LinearPositionEditor() {
    super.setUI(ui);
  }
}
