package cspace.render2d;

import javax.media.opengl.GL2;

import jgl.math.vector.Vec3f;
import cspace.scene.Scene;
import cspace.util.CachedRenderer;

public class RobotRenderer extends CachedRenderer {

  private Scene  scene;
  private Camera camera;

  RobotRenderer(Scene scene, Camera camera) {
    this.scene = scene;
    this.camera = camera;
  }

  @Override
  protected void beginDraw(GL2 gl) {
    gl.glPushMatrix();
    gl.glTranslated(scene.view.robot.position.x, scene.view.robot.position.y, 0);
    gl.glRotated(180 + scene.view.robot.rotation.angle180(), 0, 0, 1);
    Vec3f color = scene.view.robot.color;
    gl.glColor3f(color.x, color.y, color.z);
  }

  @Override
  protected void endDraw(GL2 gl) {
    gl.glPopMatrix();
  }

  @Override
  protected void updateGeometry(GL2 gl) {
    float width = scene.view.robot.edgeWidth;
    if (scene.view.renderer.fixedWidthEdges)
      width /= camera.getScale();
    for (int i = 0; i < scene.cspace.robot.e.length; i++)
      new ArcGeometry(scene.cspace.robot.e[i]).draw(gl, width, scene.view.obstacle.edgeDetail);
  }
  
  @Override
  protected boolean isVisible() {
    return scene.view.robot.visible2d;
  }
}
