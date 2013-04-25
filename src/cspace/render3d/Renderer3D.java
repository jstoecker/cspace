package cspace.render3d;

import javax.media.opengl.GL;
import javax.media.opengl.GL2;

import jgl.cameras.Camera;
import jgl.core.Viewport;
import jgl.geometry.extra.AxesGeometry;
import jgl.math.vector.ConstVec3f;
import jgl.math.vector.Mat4f;
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
  private InspectRenderer   debugRenderer;
  private AxesGeometry    axes   = new AxesGeometry(1);

  public Renderer3D(Scene scene) {
    this.scene = scene;

    camera.setView(Transform.lookAt(10, -7, 8, 0, 0, 0, 0, 0, 1));
    camera.setProjection(Transform.perspective(scene.view.camera3d.fieldOfView, 1, 0.1f, 100));
    subRenderer = new SubRenderer(scene);
    contactRenderer = new ContactRenderer(scene);
    pathRenderer = new PathRenderer(scene);
    robotRenderer = new RobotRenderer(scene);
    sumRenderer = new SumRenderer(scene);
    debugRenderer = new InspectRenderer(scene);

    updateGeometry();
  }
  
  public PathRenderer getPathRenderer() {
    return pathRenderer;
  }

  public Camera getCamera() {
    return camera;
  }
  
  public SubRenderer getSubRenderer() {
    return subRenderer;
  }

  public ContactRenderer getContactRenderer() {
    return contactRenderer;
  }

  public SumRenderer getSumRenderer() {
    return sumRenderer;
  }
  
  public InspectRenderer getDebugRenderer() {
    return debugRenderer;
  }

  public void display(GL2 gl, Viewport viewport) {

    gl.glClear(GL.GL_COLOR_BUFFER_BIT | GL.GL_DEPTH_BUFFER_BIT);

    gl.glEnable(GL.GL_DEPTH_TEST);

    camera.apply(gl);

    if (scene.view.renderer.drawAxes)
      drawAxes(gl);

    int numPeriods = scene.view.renderer.periods3d;
    if (numPeriods > 1) {
      double z = camera.getEye().z();
      int centerPeriod = (int) ((z < 0 ? (z - Math.PI) : (z + Math.PI)) / (2 * Math.PI));
      for (int i = -numPeriods / 2; i <= numPeriods / 2; i++) {
        gl.glPushMatrix();
        gl.glTranslated(0, 0, Math.PI * 2 * (centerPeriod + i));
        drawScene(gl, viewport);
        gl.glPopMatrix();
      }
    } else {
      drawScene(gl, viewport);
    }
    
    if (scene.view.renderer.drawPiPlanes)
      drawPiPlanes(gl);

    gl.glDisable(GL.GL_DEPTH_TEST);
    
    debugRenderer.draw(gl, viewport);
  }

  private void drawAxes(GL2 gl) {
    gl.glEnable(GL2.GL_LIGHTING);
    gl.glEnable(GL2.GL_LIGHT0);
    gl.glEnable(GL2.GL_COLOR_MATERIAL);
    axes.drawArrays(gl);
    gl.glDisable(GL2.GL_LIGHTING);
    gl.glDisable(GL2.GL_COLOR_MATERIAL);
  }

  private void drawAxesOverlay(GL2 gl) {
    ConstVec3f eye = camera.getForward().times(-4);
    ConstVec3f up = camera.getUp();
    Mat4f view = Transform.lookAt(eye.x(), eye.y(), eye.z(), 0, 0, 0, up.x(), up.y(), up.z());
    gl.glLoadMatrixf(view.a, 0);
    gl.glClear(GL.GL_DEPTH_BUFFER_BIT);
    gl.glEnable(GL2.GL_LIGHTING);
    gl.glEnable(GL2.GL_LIGHT0);
    gl.glEnable(GL2.GL_COLOR_MATERIAL);
    axes.drawArrays(gl);
    gl.glDisable(GL2.GL_LIGHTING);
    gl.glDisable(GL2.GL_COLOR_MATERIAL);
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

  private void drawScene(GL2 gl, Viewport viewport) {
    pathRenderer.draw(gl);
    robotRenderer.draw(gl);
    contactRenderer.draw(gl);
    subRenderer.draw(gl, camera, viewport);
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
    camera.setProjection(Transform.perspective(scene.view.camera3d.fieldOfView, (float) w / h, 0.1f, 100));
  }

  public void updateGeometry() {
    subRenderer.update(scene);
    // contactRenderer.update(scene.sampledCS);
  }
}
