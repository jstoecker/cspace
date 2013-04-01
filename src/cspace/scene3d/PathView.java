package cspace.scene3d;

import java.util.ArrayList;
import java.util.List;

import javax.media.opengl.GL;
import javax.media.opengl.GL2;

import jgl.math.vector.Vec3f;
import cspace.model.Path;
import cspace.model.Path.Waypoint;
import cspace.visuals.PathVisuals;
import cspace.visuals.RobotVisuals;

/**
 * Visualization of a saved robot path.
 */
public class PathView {

  public static final List<Vec3f> pc = new ArrayList<Vec3f>();
  public static final List<Vec3f> p = new ArrayList<Vec3f>();
  public static final List<Vec3f[]> lines = new ArrayList<Vec3f[]>();
  final Path path;
  final PathVisuals visuals;
  final RobotVisuals robotVisuals;
  double minZ = Double.POSITIVE_INFINITY;
  double maxZ = Double.NEGATIVE_INFINITY;
  int list = -1;

  public PathView(Path path, PathVisuals visuals, RobotVisuals robotVisuals) {
    this.path = path;
    this.visuals = visuals;
    this.robotVisuals = robotVisuals;

    for (Waypoint wp : path.waypoints) {
      if (wp.theta < minZ) {
        minZ = wp.theta;
      }
      if (wp.theta > maxZ) {
        maxZ = wp.theta;
      }
    }
  }

  void draw(GL2 gl) {
    if (!visuals.isVisible3d()) {
      return;
    }

    if (list == -1 || visuals.isWaypointsChanged()) {
      update(gl);
      visuals.setWaypointsChanged(false);
    }

    Vec3f c = visuals.getColor();
    gl.glColor3f(c.x, c.y, c.z);
    gl.glCallList(list);

//    c = robotVisuals.getColor();
//    gl.glColor3f(c.x, c.y, c.z);
//
//    double z = robotVisuals.getTheta();
//    if (z < minZ) {
//      z += Math.PI * 2;
//    }
//    if (z > maxZ) {
//      z -= Math.PI * 2;
//    }
//
//    // cange to sphere or something (or crosshairs)
//    gl.glBegin(GL2.GL_LINES);
//    gl.glVertex3d(robotVisuals.getP().x, robotVisuals.getP().y, z - 10);
//    gl.glVertex3d(robotVisuals.getP().x, robotVisuals.getP().y, z + 10);
//    gl.glVertex3d(robotVisuals.getP().x - 10, robotVisuals.getP().y, z);
//    gl.glVertex3d(robotVisuals.getP().x + 10, robotVisuals.getP().y, z);
//    gl.glVertex3d(robotVisuals.getP().x, robotVisuals.getP().y - 10, z);
//    gl.glVertex3d(robotVisuals.getP().x, robotVisuals.getP().y + 10, z);
//    gl.glEnd();

    gl.glPointSize(3);
    gl.glColor3f(1, 0, 0);
    gl.glBegin(GL2.GL_POINTS);

    for (int i = 0; i < p.size(); i++) {
      gl.glColor3fv(pc.get(i).toArray(), 0);
      gl.glVertex3fv(p.get(i).toArray(), 0);
    }
    gl.glEnd();

    gl.glBegin(GL2.GL_LINES);
    for (Vec3f[] v : lines) {
      gl.glColor3f(1, 0, 0);
      gl.glVertex3fv(v[0].toArray(), 0);
      gl.glColor3f(1, 0, 1);
      gl.glVertex3fv(v[1].toArray(), 0);
    }

    gl.glEnd();
  }

  private void update(GL2 gl) {
    list = gl.glGenLists(1);
    gl.glNewList(list, GL2.GL_COMPILE);
    gl.glBegin(GL.GL_LINE_STRIP);
    for (Waypoint wp : path.waypoints) {
      gl.glVertex3d(wp.p.x, wp.p.y, wp.theta);
    }
    gl.glEnd();
    gl.glEndList();
  }

  public void delete(GL2 gl) {
    if (list != -1) {
      gl.glDeleteLists(list, 1);
      list = -1;
    }
  }
}
