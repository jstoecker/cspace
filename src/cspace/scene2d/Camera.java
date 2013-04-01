package cspace.scene2d;

import javax.media.opengl.GL2;

import jgl.core.Viewport;
import jgl.math.vector.Mat4f;
import jgl.math.vector.Transform;
import jgl.math.vector.Vec2f;

public class Camera {
  
  // size of the projection at scale 1
  private static final float DEFAULT_WIDTH = 16;

  public Mat4f projection;
  public Vec2f    center = new Vec2f(0);
  public float    scale  = 1;
  
  // projection dimensions
  public float left;
  public float right;
  public float width;
  public float bottom;
  public float top;
  public float height;
  
  private void updateProjection(Viewport vp) {
    // projection scales height to maintain proper world ratio
    left = -DEFAULT_WIDTH / 2;
    right = -left;
    bottom = left / vp.aspect();
    top = -bottom;
    width = right - left;
    height = top - bottom;
    
    projection = Transform.orthographic2D(left, right, bottom, top);
  }

  public void apply(GL2 gl, Viewport vp) {
    
    updateProjection(vp);
    
    gl.glMatrixMode(GL2.GL_PROJECTION);
    gl.glLoadIdentity();
    gl.glLoadMatrixf(projection.a, 0);
    gl.glMatrixMode(GL2.GL_MODELVIEW);
    gl.glLoadIdentity();
    gl.glScaled(scale, scale, 0);
    gl.glTranslated(-center.x, -center.y, 0);
  }
}
