package cspace.render2d;

import javax.media.opengl.GL2;

import jgl.math.vector.Vec3f;
import cspace.scene.CSArc.Arc;
import cspace.scene.Scene;
import cspace.scene.SumEE;
import cspace.util.CachedRenderer;

public class SumRenderer extends CachedRenderer {

  private Scene  scene;
  private Camera camera;

  public SumRenderer(Scene scene, Camera camera) {
    this.scene = scene;
    this.camera = camera;
  }

  @Override
  protected void beginDraw(GL2 gl) {
    Vec3f c = scene.view.sums.color;
    gl.glColor3f(c.x, c.y, c.z);
  }

  @Override
  protected void updateGeometry(GL2 gl) {
    double theta = scene.view.robot.rotation.anglePi();
    double scaledWidth = scene.view.sums.edgeWidth / camera.getScale();
    for (SumEE sum : scene.cspace.sums) {
      if (sum != null && sum.isActive(theta)) {
        Arc arc = sum.arc(scene.view.robot.rotation);
        new ArcGeometry(arc).draw(gl, scaledWidth, scene.view.sums.edgeDetail);
      }
    }
  }
  
  @Override
  protected boolean isVisible() {
    return scene.view.sums.visible2d;
  }
}
