package cspace.render2d;

import javax.media.opengl.GL2;

import jgl.core.Viewport;
import jgl.math.vector.ConstVec2f;
import jgl.math.vector.Mat4f;
import jgl.math.vector.Transform;
import jgl.math.vector.Vec2f;

public class Camera {

  // size of the projection at scale 1
  private static final float DEFAULT_WIDTH = 16;

  private Mat4f               projection;
  private Vec2f               center        = new Vec2f(0);
  private float               scale         = 1;

  // projection dimensions
  private float               left;
  private float               right;
  private float               width;
  private float               bottom;
  private float               top;
  private float               height;
  
  public ConstVec2f getCenter() {
    return center;
  }
  
  public float getScale() {
    return scale;
  }

  public float getLeft() {
    return left;
  }
  
  public float getRight() {
    return right;
  }
  
  public float getWidth() {
    return width;
  }
  
  public float getHeight() {
    return height;
  }
  
  public float getBottom() {
    return bottom;
  }
  
  public float getTop() {
    return top;
  }
  
  public void setCenter(ConstVec2f center) {
    this.center = center.copy();
  }
  
  public void setScale(float scale) {
    this.scale = scale;
  }

  public void apply(GL2 gl, Viewport vp) {

    // projection scales height to maintain proper world ratio
    left = -DEFAULT_WIDTH / 2;
    right = -left;
    bottom = left / vp.aspect();
    top = -bottom;
    width = right - left;
    height = top - bottom;
    projection = Transform.orthographic2D(left, right, bottom, top);

    gl.glMatrixMode(GL2.GL_PROJECTION);
    gl.glLoadIdentity();
    gl.glLoadMatrixf(projection.a, 0);
    gl.glMatrixMode(GL2.GL_MODELVIEW);
    gl.glLoadIdentity();
    gl.glScaled(scale, scale, 0);
    gl.glTranslated(-center.x, -center.y, 0);
  }
}
