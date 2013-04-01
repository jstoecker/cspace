package cspace.visuals;

import jgl.math.vector.Vec2f;
import jgl.math.vector.Vec3f;

public class CameraVisuals extends VisibleModel {

  Vec2f center2d = new Vec2f(0);
  float scale2d  = 1;
  Vec3f eye3d    = new Vec3f(0, -10, 0);
  float yaw3d    = 0;
  float pitch3d  = 0;
  
  public Vec2f getCenter2d() {
    return center2d;
  }
  
  public float getScale2d() {
    return scale2d;
  }
  
  public Vec3f getEye3d() {
    return eye3d;
  }
  
  public float getYaw3d() {
    return yaw3d;
  }
  
  public float getPitch3d() {
    return pitch3d;
  }
  
  public void setCenter2d(Vec2f center2d) {
    this.center2d = center2d;
  }
  
  public void setScale2d(float scale2d) {
    this.scale2d = scale2d;
  }
  
  public void setEye3d(Vec3f eye3d) {
    this.eye3d = eye3d;
  }
  
  public void setYaw3d(float yaw3d) {
    this.yaw3d = yaw3d;
  }
  
  public void setPitch3d(float pitch3d) {
    this.pitch3d = pitch3d;
  }
}
