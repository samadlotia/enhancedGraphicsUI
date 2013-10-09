package org.gladstoneinstitutes.customgraphicsui.internal.chart;

abstract class Chart {
	public abstract String getUserName();
  public abstract String getCgName();
  public abstract String buildCgString();

  protected Attributes attrs = null;

  public void setAttributes(final Attributes attrs) {
    this.attrs = attrs;
  }
}
