package cspace.render3d;

import javax.media.opengl.GL2;

import jgl.math.vector.Vec3f;
import cspace.scene.Scene;

public class RobotRenderer {

  private Scene scene;

  public RobotRenderer(Scene scene) {
    this.scene = scene;
  }

  public void draw(GL2 gl) {
    if (!scene.view.robot.visible3d || scene.view.robot.cameraRobot) {
      return;
    }

    Vec3f c = scene.view.robot.color;
    gl.glColor3f(c.x, c.y, c.z);

    double x = scene.view.robot.position.x;
    double y = scene.view.robot.position.y;
    double z = scene.view.robot.rotation.anglePi();

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
