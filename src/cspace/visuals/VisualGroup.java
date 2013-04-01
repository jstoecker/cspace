package cspace.visuals;

public abstract class VisualGroup {
  Visuals parent = null;

  public VisualGroup() {
  }

  protected void updateVisuals() {
    if (parent != null)
      parent.updateVisuals(this);
  }
}