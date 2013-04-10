package cspace.render3d;

import javax.media.opengl.GL;
import javax.media.opengl.GL2;

import jgl.math.vector.Vec3f;
import cspace.scene.Path.Waypoint;
import cspace.scene.Scene;
import cspace.util.CachedRenderer;

public class PathRenderer extends CachedRenderer {

  private Scene scene;

  public PathRenderer(Scene scene) {
    this.scene = scene;
  }

  @Override
  protected void beginDraw(GL2 gl) {
    Vec3f c = scene.view.path.color;
    gl.glColor3f(c.x, c.y, c.z);
  }
  
  @Override
  protected void updateGeometry(GL2 gl) {
    gl.glBegin(GL.GL_LINE_STRIP);
    for (Waypoint wp : scene.path.waypoints) {
      gl.glVertex3d(wp.p.x, wp.p.y, wp.theta);
    }
    gl.glEnd();
  }
  
  @Override
  protected boolean isVisible() {
    return scene.view.path.visible3d;
  }
}
