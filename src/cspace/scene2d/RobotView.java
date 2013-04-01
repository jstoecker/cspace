package cspace.scene2d;

import javax.media.opengl.GL2;

import cspace.model.ArcShape;
import cspace.visuals.RobotVisuals;

public class RobotView extends ArcShapeView {

  RobotVisuals visuals;

  RobotView(ArcShape robot, RobotVisuals visuals) {
    super(robot, visuals);
    this.visuals = visuals;
  }

  @Override
  public void draw(GL2 gl) {
    gl.glPushMatrix();
    {
      gl.glTranslated(visuals.getP().x, visuals.getP().y, 0);
      gl.glRotated(180 + Math.toDegrees(visuals.getTheta()), 0, 0, 1);
      super.draw(gl);
    }
    gl.glPopMatrix();
  }
}
