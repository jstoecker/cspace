package cspace.visuals;

public abstract class VisibleModel extends VisualGroup {

  boolean visible2d = true;
  boolean visible3d = true;
  
  public VisibleModel() {
  }

  public boolean isVisible2d() {
    return visible2d;
  }

  public void setVisible2d(boolean visible2d) {
    this.visible2d = visible2d;
    updateVisuals();
  }

  public boolean isVisible3d() {
    return visible3d;
  }

  public void setVisible3d(boolean visible3d) {
    this.visible3d = visible3d;
    updateVisuals();
  }
}
