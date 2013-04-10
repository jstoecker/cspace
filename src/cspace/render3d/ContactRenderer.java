package cspace.render3d;

import java.util.List;
import java.util.Map;

import javax.media.opengl.GL2;

import jgl.math.vector.Vec3f;
import cspace.scene.Scene;
import cspace.scene.trimesh.Sample;
import cspace.scene.trimesh.SampledSub;
import cspace.scene.trimesh.SampledSub.Vertex;
import cspace.util.CachedRenderer;

public class ContactRenderer extends CachedRenderer {

  private Scene scene;

  public ContactRenderer(Scene scene) {
    this.scene = scene;
  }

  @Override
  protected void beginDraw(GL2 gl) {
    Vec3f c = scene.view.contacts.color;
    gl.glColor3f(c.x, c.y, c.z);
  }

  @Override
  protected void updateGeometry(GL2 gl) {
    for (SampledSub sub : scene.sampledCS.subSamplings.values()) {
      drawLineStrip(gl, sub.vertMap, sub.tailSamples);
      drawLineStrip(gl, sub.vertMap, sub.headSamples);
    }
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
    return scene.view.contacts.visible3d;
  }
}