package cspace.render2d;

import javax.media.opengl.GL2;

import jgl.math.vector.Vec2d;
import jgl.math.vector.Vec3f;
import cspace.scene.Contact;
import cspace.scene.Scene;
import cspace.scene.SceneView;
import cspace.util.CachedRenderer;

public class ContactRenderer extends CachedRenderer {

  private Scene scene;

  public ContactRenderer(Scene scene) {
    this.scene = scene;
  }

  @Override
  protected boolean isVisible() {
    SceneView.Contacts view = scene.view.contacts;
    return view.intnVisible2d || view.sveVisible2d || view.sevVisible2d;
  }

  @Override
  protected void updateGeometry(GL2 gl) {
    double theta = scene.view.robot.rotation.anglePi();
    gl.glPointSize(7);
    gl.glBegin(GL2.GL_POINTS);
    if (scene.view.contacts.intnVisible2d)
      draw(gl, scene.cspace.intns, scene.view.contacts.intnColor, theta);
    if (scene.view.contacts.sveVisible2d)
      draw(gl, scene.cspace.sves, scene.view.contacts.sveColor, theta);
    if (scene.view.contacts.sevVisible2d)
      draw(gl, scene.cspace.sevs, scene.view.contacts.sevColor, theta);
    gl.glEnd();
  }
  
  private void draw(GL2 gl, Contact[] contacts, Vec3f color, double theta) {
    gl.glColor3f(color.x, color.y, color.z);
    for (Contact contact : contacts) {
      if (contact != null && contact.isActive(theta)) {
        Vec2d p = contact.position(scene.view.robot.rotation);
        gl.glVertex2d(p.x, p.y);
      }
    }
  }
}
