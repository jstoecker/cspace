package cspace.render2d;

import javax.media.opengl.GL2;

import jgl.math.vector.Vec3f;
import cspace.scene.SumEE;
import cspace.scene.CSArc.Arc;
import cspace.scene.visuals.RobotVisuals;
import cspace.scene.visuals.SumEEVisuals;

public class SumEEView {

  SumEE[] sees;
  SumEEVisuals visuals;
  RobotVisuals robotVisuals;
  int list = -1;

  public SumEEView(SumEE[] sees, SumEEVisuals visuals, RobotVisuals robotVisuals) {
    this.sees = sees;
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
  }

  private void update(GL2 gl) {
    delete(gl);

    list = gl.glGenLists(1);
    gl.glNewList(list, GL2.GL_COMPILE);
    {
      for (SumEE see : sees) {
        if (see != null && see.isActive(robotVisuals.getTheta())) {
          
          Arc arc = see.arc(robotVisuals.getU());
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
