package cspace.render2d;

import javax.media.opengl.GL2;

import jgl.math.vector.Vec3f;
import cspace.scene.CSArc.Arc;
import cspace.scene.Scene;
import cspace.scene.Sub;
import cspace.util.CachedRenderer;

public class SubRenderer extends CachedRenderer {

  private Scene  scene;
  private Camera camera;

  public SubRenderer(Scene scene, Camera camera) {
    this.scene = scene;
    this.camera = camera;
  }

  @Override
  protected void beginDraw(GL2 gl) {
    Vec3f c = scene.view.subs.color;
    gl.glColor3f(c.x, c.y, c.z);
  }

  @Override
  protected void updateGeometry(GL2 gl) {
    double theta = scene.view.robot.rotation.anglePi();
    
    float width = scene.view.subs.edgeWidth;
    if (scene.view.renderer.fixedWidthEdges)
      width /= camera.getScale();
    
    for (Sub sub : scene.cspace.subs) {
      if (sub != null && sub.isActive(theta)) {
        Arc arc = sub.arc(scene.view.robot.rotation);
        new ArcGeometry(arc).draw(gl, width, scene.view.subs.edgeDetail);
      }
    }
  }
  
  @Override
  protected boolean isVisible() {
    return scene.view.subs.visible2d;
  }
}
