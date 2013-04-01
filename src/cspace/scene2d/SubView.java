package cspace.scene2d;

import javax.media.opengl.GL2;

import jgl.math.vector.Vec3f;
import cspace.model.CSArc.Arc;
import cspace.model.Sub;
import cspace.visuals.RobotVisuals;
import cspace.visuals.SubVisuals;
import cspace.visuals.SubVisuals.SubColoring;

public class SubView {

  Sub[] subs;
  SubVisuals visuals;
  RobotVisuals robotVisuals;
  int list = -1;

  public SubView(Sub[] subs, SubVisuals visuals, RobotVisuals robotVisuals) {
    this.subs = subs;
    this.visuals = visuals;
    this.robotVisuals = robotVisuals;
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
    gl.glCallList(list);
    
    // debugging
    if (visuals.getSelected() > -1 && visuals.getSelected() < subs.length) {

      gl.glColor3f(1, 1, 1);
      Arc arc = subs[visuals.getSelected()].arc(robotVisuals.getU());
      new ArcTriStrip(arc).draw(gl, visuals.getScaledWidth2d(), visuals.getSmooth2d());
    }
  }

  private void update(GL2 gl) {
    delete(gl);

    list = gl.glGenLists(1);
    gl.glNewList(list, GL2.GL_COMPILE);
    {
      for (Sub sub : subs) {
        
        if (visuals.getSelected() == 308 && sub.index == 308) {
          System.out.println(sub.isActive(robotVisuals.getTheta()));
        }
        
        if (sub != null && sub.isActive(robotVisuals.getTheta())) {
          
          Arc arc = sub.arc(robotVisuals.getU());
          
          if (visuals.getColoring() == SubColoring.UNIQUE_COLOR) {
            Vec3f color = visuals.getColor(sub);
            gl.glColor3f(color.x, color.y, color.z);
          } else {
            Vec3f color = visuals.getColor();
            gl.glColor3f(color.x, color.y, color.z);
          }
          new ArcTriStrip(arc).draw(gl, visuals.getScaledWidth2d(), visuals.getSmooth2d());
        }
      }
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
