package cspace.render2d;

import javax.media.opengl.GL;
import javax.media.opengl.GL2;

import jgl.math.vector.Vec3f;
import cspace.scene.Path.Waypoint;
import cspace.scene.Scene;
import cspace.util.CachedRenderer;

public class PathRenderer extends CachedRenderer {

  private Scene  scene;
  private Camera camera;

  public PathRenderer(Scene scene, Camera camera) {
    this.scene = scene;
    this.camera = camera;
  }

  @Override
  protected void beginDraw(GL2 gl) {
    Vec3f c = scene.view.path.color;
    gl.glColor3f(c.x, c.y, c.z);
    gl.glLineWidth(scene.view.path.edgeWidth);
  }

  @Override
  protected void updateGeometry(GL2 gl) {
    // draw solid line segments for path
    gl.glBegin(GL.GL_LINE_STRIP);
    for (Waypoint wp : scene.path.waypoints) {
      gl.glVertex2d(wp.p.x, wp.p.y);
    }
    gl.glEnd();
  }
  
  @Override
  protected boolean isVisible() {
    return scene.view.path.visible2d;
  }
}
