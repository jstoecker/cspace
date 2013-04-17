package cspace.render3d;

import java.util.List;
import java.util.Map;

import javax.media.opengl.GL2;

import jgl.math.vector.Vec3f;
import cspace.scene.Contact;
import cspace.scene.Intn;
import cspace.scene.Sample;
import cspace.scene.Scene;
import cspace.scene.SceneView;
import cspace.scene.Sub;
import cspace.scene.Sub.Vertex;
import cspace.scene.SumEV;
import cspace.scene.SumVE;
import cspace.util.CachedRenderer;

public class ContactRenderer extends CachedRenderer {

  private Scene scene;

  public ContactRenderer(Scene scene) {
    this.scene = scene;
  }

  @Override
  protected void beginDraw(GL2 gl) {
    Vec3f c = scene.view.contacts.intnColor;
    gl.glColor3f(c.x, c.y, c.z);
  }

  @Override
  protected void updateGeometry(GL2 gl) {
    for (Sub sub : scene.cspace.subs) {
      Vec3f color = color(sub.tail);
      if (color != null) {
        gl.glColor3f(color.x, color.y, color.z);
        drawLineStrip(gl, sub.vertMap, sub.tailSamples);
      }

      color = color(sub.head);
      if (color != null) {
        gl.glColor3f(color.x, color.y, color.z);
        drawLineStrip(gl, sub.vertMap, sub.headSamples);
      }
    }
  }

  Vec3f color(Contact pnt) {
    if (pnt instanceof Intn && scene.view.contacts.intnVisible3d)
      return scene.view.contacts.intnColor;
    if (pnt instanceof SumVE && scene.view.contacts.sveVisible3d)
      return scene.view.contacts.sveColor;
    if (pnt instanceof SumEV && scene.view.contacts.sevVisible3d)
      return scene.view.contacts.sevColor;
    return null;
  }

  private void drawLineStrip(GL2 gl, Map<Sample, Vertex> vertMap, List<Sample> samples) {
    if (samples == null) {
      return;
    }

    gl.glBegin(GL2.GL_LINE_STRIP);
    for (Sample s : samples) {
      Vertex v = vertMap.get(s);
      if (v != null) {
        gl.glVertex3d(v.position.x, v.position.y, v.position.z);
      }
    }
    gl.glEnd();
  }

  @Override
  protected boolean isVisible() {
    SceneView.Contacts view = scene.view.contacts;
    return view.intnVisible3d || view.sveVisible3d || view.sevVisible3d;
  }
}