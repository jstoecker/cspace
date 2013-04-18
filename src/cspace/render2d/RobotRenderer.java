package cspace.render2d;

import javax.media.opengl.GL2;

import jgl.math.vector.Vec2f;
import jgl.math.vector.Vec3f;
import cspace.scene.Scene;
import cspace.util.CachedRenderer;

public class RobotRenderer extends CachedRenderer {

  private Scene   scene;
  private Camera  camera;
  private boolean highlight = false;
  private Vec2f   anglePt   = null;

  RobotRenderer(Scene scene, Camera camera) {
    this.scene = scene;
    this.camera = camera;
  }

  public void setHighlight(boolean highlight) {
    this.highlight = highlight;
  }
  
  public void setAnglePt(Vec2f anglePt) {
    this.anglePt = anglePt;
  }

  @Override
  protected void beginDraw(GL2 gl) {
    if (highlight || anglePt != null) {
      gl.glColor3fv(new Vec3f(1).minus(scene.view.renderer.background).toArray(), 0);
    } else {
      Vec3f color = scene.view.robot.color;
      gl.glColor3f(color.x, color.y, color.z);
    }
    
    if (anglePt != null) {
      gl.glEnable( GL2.GL_LINE_STIPPLE );
      gl.glLineStipple(1, (short)(0x1111));
      gl.glBegin(GL2.GL_LINES);
      gl.glVertex2d(scene.view.robot.position.x, scene.view.robot.position.y);
      gl.glVertex2d(anglePt.x, anglePt.y);
      gl.glEnd();
      gl.glDisable( GL2.GL_LINE_STIPPLE );
    }
    
    gl.glPushMatrix();
    gl.glTranslated(scene.view.robot.position.x, scene.view.robot.position.y, 0);
    gl.glRotated(180 + scene.view.robot.rotation.angle180(), 0, 0, 1);
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

    if (scene.view.robot.originVisible) {
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
    return scene.view.robot.visible2d;
  }
}
