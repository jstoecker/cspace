package cspace.visuals;

import jgl.math.vector.Vec3f;
import jgl.math.vector.Vec4f;

public class ArcShapeVisuals extends EdgeVisuals {

  Vec4f fillColor = new Vec4f(1, 1, 1, 1);
  boolean drawOrigin = true;

  public ArcShapeVisuals() {
  }

  public boolean isDrawOrigin() {
    return drawOrigin;
  }

  public void setDrawOrigin(boolean drawOrigin) {
    this.drawOrigin = drawOrigin;
    updateVisuals();
  }

  public Vec4f getFillColor() {
    return fillColor;
  }

  public void setFillColor(Vec3f c) {
    this.fillColor.x = c.x;
    this.fillColor.y = c.y;
    this.fillColor.z = c.z;
  }
  
  public float getFillAlpha() {
    return fillColor.w;
  }
  
  public void setFillAlpha(float a) {
    this.fillColor.w = a;
  }
}