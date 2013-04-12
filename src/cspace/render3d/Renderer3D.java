package cspace.render3d;

import javax.media.opengl.GL;
import javax.media.opengl.GL2;
import javax.media.opengl.GLAutoDrawable;
import javax.media.opengl.GLEventListener;

import jgl.cameras.Camera;
import jgl.core.Viewport;
import jgl.math.vector.Transform;
import cspace.scene.Scene;
import cspace.scene.Path.Waypoint;

public class Renderer3D {

  private Camera          camera = new Camera();
  private Scene           scene;
  private SubRenderer     subRenderer;
  private ContactRenderer contactRenderer;
  private PathRenderer    pathRenderer;
  private RobotRenderer   robotRenderer;
  private SumRenderer     sumRenderer;

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
    pathRenderer.draw(gl);
    robotRenderer.draw(gl);
    contactRenderer.draw(gl);

    gl.glEnable(GL.GL_POLYGON_OFFSET_FILL);
    gl.glPolygonOffset(1, 1);
    subRenderer.draw(gl);
    gl.glDisable(GL.GL_POLYGON_OFFSET_FILL);

    sumRenderer.draw(gl);

    gl.glDisable(GL.GL_DEPTH_TEST);
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
//    contactRenderer.update(scene.sampledCS);
  }
}
