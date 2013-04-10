package cspace.scene.visuals;

import jgl.math.vector.Vec2d;
import jgl.math.vector.Vec3f;

public class RobotVisuals extends ArcShapeVisuals {
  Vec2d   p          = new Vec2d(0);
  Vec2d   u          = new Vec2d(1, 0);
  double  theta      = 0;
  boolean onPath     = true;

  public RobotVisuals() {
    color = new Vec3f(1, .4f, .4f);
  }

  public boolean isOnPath() {
    return onPath;
  }

  public void setOnPath(boolean onPath) {
    this.onPath = onPath;
    updateVisuals();
  }

  public Vec2d getP() {
    return p;
  }

  public void setP(Vec2d p) {
    this.p = p;
    updateVisuals();
  }

  public Vec2d getU() {
    return u;
  }

  public void setU(Vec2d u) {
    this.u = u;
    this.theta = Math.atan2(u.y, u.x);
    if (parent != null) {
      this.parent.subVisuals.geomUpdated2d = true;
      this.parent.subVisuals.geomUpdated3d = true;
      this.parent.sumEEVisuals.geomUpdated2d = true;
      this.parent.sumEEVisuals.geomUpdated3d = true;
    }
    updateVisuals();
  }

  public double getTheta() {
    return theta;
  }

  public void setTheta(double theta) {
    this.theta = theta;
    this.u = new Vec2d(Math.cos(theta), Math.sin(theta));
    if (parent != null) {
      this.parent.subVisuals.geomUpdated2d = true;
      this.parent.subVisuals.geomUpdated3d = true;
      this.parent.sumEEVisuals.geomUpdated2d = true;
      this.parent.sumEEVisuals.geomUpdated3d = true;
    }
    updateVisuals();
  }
}