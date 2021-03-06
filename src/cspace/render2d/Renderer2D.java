package cspace.render2d;

import javax.media.opengl.GL;
import javax.media.opengl.GL2;

import jgl.core.Viewport;
import cspace.scene.Scene;

public class Renderer2D {

  private Camera             camera = new Camera();
  private Viewport           viewport;
  private RobotRenderer      robotRenderer;
  private ObstacleRenderer   obstacleRenderer;
  private SubRenderer        subRenderer;
  private PathRenderer       pathRenderer;
  private SumRenderer        sumRenderer;
  private ContactRenderer    contactRenderer;
  private InspectRenderer    inspectRenderer;
  private PathFinderRenderer pathFindRenderer;

  public Renderer2D(Scene scene) {
    camera.setScale(scene.view.camera2d.initialScale);
    camera.setCenter(scene.view.camera2d.initialCenter);
    robotRenderer = new RobotRenderer(scene, camera);
    obstacleRenderer = new ObstacleRenderer(scene, camera);
    subRenderer = new SubRenderer(scene, camera);
    pathRenderer = new PathRenderer(scene, camera);
    sumRenderer = new SumRenderer(scene, camera);
    contactRenderer = new ContactRenderer(scene);
    inspectRenderer = new InspectRenderer(scene, camera);
    pathFindRenderer = new PathFinderRenderer(scene, robotRenderer);
  }

  public Viewport getViewport() {
    return viewport;
  }

  public Camera getCamera() {
    return camera;
  }

  public RobotRenderer getRobotRenderer() {
    return robotRenderer;
  }

  public ObstacleRenderer getObstacleRenderer() {
    return obstacleRenderer;
  }

  public SubRenderer getSubRenderer() {
    return subRenderer;
  }

  public PathRenderer getPathRenderer() {
    return pathRenderer;
  }

  public SumRenderer getSumRenderer() {
    return sumRenderer;
  }

  public ContactRenderer getContactRenderer() {
    return contactRenderer;
  }

  public InspectRenderer getInspectRenderer() {
    return inspectRenderer;
  }

  public PathFinderRenderer getPathFindRenderer() {
    return pathFindRenderer;
  }

  public void markDirty() {
    obstacleRenderer.markDirty();
    sumRenderer.markDirty();
    subRenderer.markDirty();
    pathRenderer.markDirty();
    robotRenderer.markDirty();
    contactRenderer.markDirty();
  }

  public void display(GL2 gl) {
    gl.glClear(GL.GL_COLOR_BUFFER_BIT);

    camera.apply(gl, viewport);

    gl.glDisable(GL.GL_DEPTH_TEST);

    obstacleRenderer.draw(gl);
    sumRenderer.draw(gl);
    subRenderer.draw(gl);
    contactRenderer.draw(gl);
    pathRenderer.draw(gl);
    if (pathFindRenderer.isEnabled())
      pathFindRenderer.draw(gl, viewport);
    else
      robotRenderer.draw(gl);
    inspectRenderer.draw(gl, viewport);
  }

  public void reshape(int x, int y, int w, int h) {
    viewport = new Viewport(x, y, w, h);
  }

  public void dispose(GL2 gl) {
    robotRenderer.delete(gl);
    subRenderer.delete(gl);
    obstacleRenderer.delete(gl);
    pathRenderer.delete(gl);
    sumRenderer.delete(gl);
  }

  public void init(GL2 gl) {
  }
}
