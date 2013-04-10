package cspace.render3d;

import javax.media.opengl.GL2;

import jgl.math.vector.Vec3f;
import cspace.scene.visuals.RobotVisuals;

public class RobotView {

  private RobotVisuals visuals;
  
  public RobotView(RobotVisuals visuals) {
    this.visuals = visuals;
  }
  
  void draw(GL2 gl) {
    if (!visuals.isVisible3d()) {
      return;
    }
    
    Vec3f c = visuals.getColor();
    gl.glColor3f(c.x, c.y, c.z);

    double x = visuals.getP().x;
    double y = visuals.getP().y;
    double z = visuals.getTheta();

    gl.glBegin(GL2.GL_LINES);
    gl.glVertex3d(x, y, z - 10);
    gl.glVertex3d(x, y, z + 10);
    gl.glVertex3d(x - 10, y, z);
    gl.glVertex3d(x + 10, y, z);
    gl.glVertex3d(x, y - 10, z);
    gl.glVertex3d(x, y + 10, z);
    gl.glEnd();
  }
}
