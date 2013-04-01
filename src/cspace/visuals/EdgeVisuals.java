package cspace.visuals;

import jgl.math.vector.Vec3f;

public abstract class EdgeVisuals extends VisibleModel {

  Vec3f color = new Vec3f(0);
  float width2d = 0.03f;
  float smooth2d = 0.001f;
  boolean geomUpdated2d = true;
  boolean geomUpdated3d = true;

  public EdgeVisuals() {
  }

  public float getSmooth2d() {
    return smooth2d;
  }

  public void setSmooth2d(float smooth2d) {
    this.smooth2d = smooth2d;
    geomUpdated2d = true;
    updateVisuals();
  }

  public Vec3f getColor() {
    return color;
  }

  public void setColor(Vec3f color) {
    this.color = color;
    updateVisuals();
  }
  
  public float getScaledWidth2d() {
    if (parent == null)
      return width2d;
    return width2d * parent.genVisuals.getEdgeScale();
  }

  public float getWidth2d() {
    return width2d;
  }

  public void setWidth2d(float width) {
    this.width2d = width;
    geomUpdated2d = true;
    updateVisuals();
  }

  public boolean isGeomUpdated2d() {
    return geomUpdated2d;
  }
  
  public boolean isGeomUpdated3d() {
    return geomUpdated3d;
  }
  
  public void setGeomUpdated(boolean geomUpdated) {
    setGeomUpdated2d(geomUpdated);
    setGeomUpdated3d(geomUpdated);
  }

  public void setGeomUpdated2d(boolean geomUpdated) {
    this.geomUpdated2d = geomUpdated;
  }
  
  public void setGeomUpdated3d(boolean geomUpdated3d) {
    this.geomUpdated3d = geomUpdated3d;
  }
}