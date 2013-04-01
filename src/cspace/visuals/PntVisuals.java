package cspace.visuals;

import jgl.math.vector.Vec3f;

public class PntVisuals extends VisibleModel {
  Vec3f color = new Vec3f(.2f, 1, .4f);

  public PntVisuals() {
  }

  public Vec3f getColor() {
    return color;
  }

  public void setColor(Vec3f color) {
    this.color = color;
    updateVisuals();
  }
}