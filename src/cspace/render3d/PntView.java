package cspace.render3d;

import java.util.List;
import java.util.Map;

import javax.media.opengl.GL2;

import jgl.math.vector.Vec3f;
import cspace.scene.trimesh.Sample;
import cspace.scene.trimesh.SampledCSpace;
import cspace.scene.trimesh.SampledSub;
import cspace.scene.trimesh.SampledSub.Vertex;
import cspace.scene.visuals.PntVisuals;

public class PntView {

  SampledCSpace sampling;
  PntVisuals visuals;
  SampledCSpace sCspace;
  int list = -1;

  public PntView(PntVisuals visuals) {
    this.visuals = visuals;
  }

  void update(SampledCSpace scspace) {
    sCspace = scspace;
  }

  public void draw(GL2 gl) {
    if (!visuals.isVisible3d()) {
      return;
    }

    if (sCspace != null) {
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
      for (SampledSub sub : sCspace.subSamplings.values()) {
        drawLineStrip(gl, sub.vertMap, sub.tailSamples);
        drawLineStrip(gl, sub.vertMap, sub.headSamples);
      }
    }
    gl.glEndList();
    sCspace = null;
  }

  private void drawLineStrip(GL2 gl, Map<Sample, Vertex> vertMap,
      List<Sample> samples) {
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

  public void delete(GL2 gl) {
    if (list != -1) {
      gl.glDeleteLists(list, 2);
      list = -1;
    }
  }
}