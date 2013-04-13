package cspace.render3d;

import javax.media.opengl.GL;
import javax.media.opengl.GL2;

import jgl.cameras.Camera;
import jgl.geometry.extra.AxesGeometry;
import jgl.math.vector.Transform;
import cspace.scene.Scene;

public class Renderer3D {

  private Camera          camera = new Camera();
  private Scene           scene;
  private SubRenderer     subRenderer;
  private ContactRenderer contactRenderer;
  private PathRenderer    pathRenderer;
  private RobotRenderer   robotRenderer;
  private SumRenderer     sumRenderer;
  private AxesGeometry    axes   = new AxesGeometry(0.5f);

  public Renderer3D(Scene scene) {
    this.scene = scene;

    camera.setView(Transform.lookAt(10, -7, 8, 0, 0, 0, 0, 0, 1));
    camera.setProjection(Transform.perspective(50, 1, 0.1f, 100));
    subRenderer = new SubRenderer(scene);
    contactRenderer = new ContactRenderer(scene);
    pathRenderer = new PathRenderer(scene);
    robotRenderer = new RobotRenderer(scene);
    sumRenderer = new SumRenderer(scene);

    updateGeometry();
  }

  public Camera getCamera() {
    return camera;
  }

  public ContactRenderer getContactRenderer() {
    return contactRenderer;
  }

  public void display(GL2 gl) {

    gl.glClear(GL.GL_COLOR_BUFFER_BIT | GL.GL_DEPTH_BUFFER_BIT);

    gl.glEnable(GL.GL_DEPTH_TEST);

    camera.apply(gl);

    int numPeriods = scene.view.renderer.periods3d;
    if (numPeriods > 1) {
      int centerPeriod = (int) (camera.getEye().z() / (2 * Math.PI));
      for (int i = -numPeriods / 2; i <= numPeriods / 2; i++) {
        gl.glPushMatrix();
        gl.glTranslated(0, 0, Math.PI * 2 * (centerPeriod + i));
        drawScene(gl);
        gl.glPopMatrix();
      }
    } else {
      drawScene(gl);
    }

    if (scene.view.renderer.drawPiPlanes)
      drawPiPlanes(gl);

    gl.glDisable(GL.GL_DEPTH_TEST);
  }

  private void drawPiPlanes(GL2 gl) {
    if (camera.getEye().z() < -Math.PI) {
      drawPlane(gl, 15, 7, Math.PI);
      drawPlane(gl, 15, 7, -Math.PI);
    } else {
      drawPlane(gl, 15, 7, -Math.PI);
      drawPlane(gl, 15, 7, Math.PI);
    }
  }

  private void drawPlane(GL2 gl, float w, float h, double z) {
    float hw = w / 2;
    float hh = h / 2;
    gl.glColor3f(1, 1, 1);
    gl.glBegin(GL2.GL_LINE_LOOP);
    gl.glVertex3d(-hw, -hh, z);
    gl.glVertex3d(hw, -hh, z);
    gl.glVertex3d(hw, hh, z);
    gl.glVertex3d(-hw, hh, z);
    gl.glEnd();
    gl.glEnable(GL.GL_BLEND);
    gl.glBlendFunc(GL.GL_SRC_ALPHA, GL2.GL_ONE_MINUS_SRC_ALPHA);
    gl.glColor4f(1, 1, 1, 0.5f);
    gl.glBegin(GL2.GL_QUADS);
    gl.glVertex3d(-hw, -hh, z);
    gl.glVertex3d(hw, -hh, z);
    gl.glVertex3d(hw, hh, z);
    gl.glVertex3d(-hw, hh, z);
    gl.glEnd();
    gl.glDisable(GL.GL_BLEND);
  }

  private void drawScene(GL2 gl) {
    pathRenderer.draw(gl);
    robotRenderer.draw(gl);
    contactRenderer.draw(gl);

    gl.glEnable(GL.GL_POLYGON_OFFSET_FILL);
    gl.glPolygonOffset(1, 1);
    subRenderer.draw(gl, camera);
    gl.glDisable(GL.GL_POLYGON_OFFSET_FILL);

    sumRenderer.draw(gl);
  }

  public void dispose(GL2 gl) {
    subRenderer.delete(gl);
    sumRenderer.delete(gl);
  }

  public void init(GL2 gl) {
    subRenderer.init(gl);
    sumRenderer.init(gl);
  }

  public void reshape(int x, int y, int w, int h) {
    camera.setProjection(Transform.perspective(50, (float) w / h, 0.1f, 100));
  }

  public void updateGeometry() {
    subRenderer.update(scene);
    // contactRenderer.update(scene.sampledCS);
  }
}
