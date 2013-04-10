package cspace.render2d;

import javax.media.opengl.GL;
import javax.media.opengl.GL2;

import jgl.math.vector.Vec3f;
import cspace.scene.Path;
import cspace.scene.Path.Waypoint;
import cspace.scene.visuals.PathVisuals;

public class PathView {

  PathVisuals visuals;
  Path path;
  int list = -1;

  public PathView(Path path, PathVisuals visuals) {
    this.path = path;
    this.visuals = visuals;
  }

  public void draw(GL2 gl) {
    if (!visuals.isVisible2d()) {
      return;
    }

    if (visuals.isGeomUpdated2d() || list == -1) {
      update(gl);
    }

    Vec3f c = visuals.getColor();
    gl.glColor3f(c.x, c.y, c.z);
    gl.glLineWidth(visuals.getWidth2d());
    gl.glCallList(list);
  }
  
  private void update(GL2 gl) {
    delete(gl);

    list = gl.glGenLists(1);
    gl.glNewList(list, GL2.GL_COMPILE);
    {
      // draw solid line segments for path
      gl.glBegin(GL.GL_LINE_STRIP);
      for (Waypoint wp : path.waypoints) {
        gl.glVertex2d(wp.p.x, wp.p.y);
      }
      gl.glEnd();
      
      // draw points at waypoints
      gl.glPointSize(3);
      gl.glBegin(GL2.GL_POINTS);
      for (Waypoint wp : path.waypoints) {
        gl.glVertex2d(wp.p.x, wp.p.y);
      }
      gl.glEnd();
      gl.glPointSize(1);
    }
    gl.glEndList();

    visuals.setGeomUpdated2d(false);
  }
  
  public void delete(GL2 gl) {
    if (list != -1) {
      gl.glDeleteLists(list, 1);
      list = -1;
    }
  }
}
