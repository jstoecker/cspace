package cspace.scene3d;

import javax.media.opengl.GL;
import javax.media.opengl.GL2;
import javax.media.opengl.GLAutoDrawable;
import javax.media.opengl.GLEventListener;

import jgl.cameras.Camera;
import jgl.core.Viewport;
import jgl.math.vector.Transform;
import cspace.model.Path.Waypoint;
import cspace.model.Scene;

public class Scene3D implements GLEventListener {

  Camera    camera = new Camera();
  Viewport  viewport;
  Scene     scene;
  SubView   subView;
  PntView   pntView;
  PathView  pathView;
  RobotView robotView;
  SumEEView sumEEView;

  public Scene3D(Scene scene) {
    this.scene = scene;

    camera.setView(Transform.lookAt(10, -7, 8, 0, 0, 0, 0, 0, 1));
    camera.setProjection(Transform.perspective(50, 1, 0.1f, 100));
    subView = new SubView(scene.visuals.subVisuals, scene.visuals.sumEEVisuals,
        scene.visuals.robotVisuals);
    pntView = new PntView(scene.visuals.pntVisuals);
    pathView = new PathView(scene.path, scene.visuals.pathVisuals, scene.visuals.robotVisuals);
    robotView = new RobotView(scene.visuals.robotVisuals);
    sumEEView = new SumEEView(scene.cspace, scene.visuals.sumEEVisuals, scene.visuals.subVisuals,
        scene.visuals.robotVisuals);
    updateGeometry();
  }

  @Override
  public void display(GLAutoDrawable drawable) {
    GL2 gl = drawable.getGL().getGL2();

    gl.glClear(GL.GL_COLOR_BUFFER_BIT | GL.GL_DEPTH_BUFFER_BIT);

    gl.glEnable(GL.GL_DEPTH_TEST);

    camera.apply(gl);
    pathView.draw(gl);
    robotView.draw(gl);
    pntView.draw(gl);

    if (scene.newPath != null) {
      gl.glPointSize(8);
      gl.glBegin(GL2.GL_POINTS);
      Waypoint s = scene.newPath.waypoints[0];
      Waypoint e = scene.newPath.waypoints[1];
      gl.glColor3f(0, 1, 0);
      gl.glVertex3d(s.p.x, s.p.y, s.theta);
      gl.glColor3f(1, 1, 0);
      gl.glVertex3d(e.p.x, e.p.y, e.theta);
      gl.glEnd();
    }

    gl.glEnable(GL.GL_POLYGON_OFFSET_FILL);
    gl.glPolygonOffset(1, 1);
    subView.draw(gl);
    gl.glDisable(GL.GL_POLYGON_OFFSET_FILL);

    sumEEView.draw(gl);

    gl.glDisable(GL.GL_DEPTH_TEST);
  }

  @Override
  public void dispose(GLAutoDrawable drawable) {
    subView.delete(drawable.getGL());
    sumEEView.delete(drawable.getGL().getGL2());
  }

  @Override
  public void init(GLAutoDrawable drawable) {
    subView.init(drawable.getGL().getGL2());
    sumEEView.init(drawable.getGL().getGL2());
  }

  @Override
  public void reshape(GLAutoDrawable drawable, int x, int y, int w, int h) {
    camera.setProjection(Transform.perspective(50, (float) w / h, 0.1f, 100));
  }

  public void updateGeometry() {
    subView.update(scene);
    pntView.update(scene.sampledCS);
  }
}