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
    float scaledWidth = scene.view.obstacle.edgeWidth / camera.getScale();
    for (int i = 0; i < scene.cspace.obstacle.e.length; i++)
      new ArcGeometry(scene.cspace.obstacle.e[i]).draw(gl, scaledWidth, scene.view.obstacle.edgeDetail);
  }

  @Override
  protected boolean isVisible() {
    return scene.view.obstacle.visible2d;
  }
}
