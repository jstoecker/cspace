package cspace.render2d;

import javax.media.opengl.GL2;

import jgl.math.vector.Vec3f;
import cspace.scene.Scene;
import cspace.util.CachedRenderer;

public class ObstacleRenderer extends CachedRenderer {

  private Scene  scene;
  private Camera camera;

  public ObstacleRenderer(Scene scene, Camera camera) {
    this.scene = scene;
    this.camera = camera;
  }

  @Override
  protected void beginDraw(GL2 gl) {
    Vec3f color = scene.view.obstacle.color;
    gl.glColor3f(color.x, color.y, color.z);
  }

  @Override
  protected void updateGeometry(GL2 gl) {
    float width = scene.view.obstacle.edgeWidth;
    if (scene.view.renderer.fixedWidthEdges)
      width /= camera.getScale();
    for (int i = 0; i < scene.cspace.obstacle.e.length; i++)
      new ArcGeometry(scene.cspace.obstacle.e[i]).draw(gl, width, scene.view.obstacle.edgeDetail);
    
    if (scene.view.obstacle.originVisible) {
      gl.glBegin(GL2.GL_LINES);
      gl.glVertex2f(-0.1f, 0);
      gl.glVertex2f(0.1f, 0);
      gl.glVertex2f(0, -0.1f);
      gl.glVertex2f(0, 0.1f);
      gl.glEnd();
    }
  }

  @Override
  protected boolean isVisible() {
    return scene.view.obstacle.visible2d;
  }
}
