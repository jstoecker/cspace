package cspace.scene3d;

import javax.media.opengl.GL;
import javax.media.opengl.GL2;

import jgl.core.Program;
import jgl.core.Shader;
import jgl.core.Uniform;
import jgl.loaders.ShaderLoader;
import cspace.model.Scene;
import cspace.sampling.SampledSub;
import cspace.sampling.SampledSub.Triangle;
import cspace.visuals.RobotVisuals;
import cspace.visuals.SubVisuals;
import cspace.visuals.SumEEVisuals;

public class SubView {

  private static final int COLOR_EDGE   = 0;
  private static final int COLOR_UNIQUE = 1;
  private static final int COLOR_NORMAL = 2;
  private static final int CLIP_NONE    = 0;
  private static final int CLIP_ABOVE   = 1;
  private static final int CLIP_AROUND  = 2;
  private static final int CLIP_BELOW   = 3;
  Program                  subShader;
  SubMesh                  mesh;
  SubVisuals               visuals;
  RobotVisuals             robotVisuals;
  Uniform                  uShading;
  Uniform                  uColor;
  Uniform                  uColoring;
  Uniform                  uClipping;
  Uniform                  uAlpha;
  Uniform                  uTheta;
  // TODO REMOVE DEBUG ONLY
  public static int        selectedTri;
  public static SampledSub selected;

  SubView(SubVisuals visuals, SumEEVisuals sumVisuals, RobotVisuals robotVisuals) {
    this.visuals = visuals;
    this.robotVisuals = robotVisuals;
    this.mesh = new SubMesh(visuals, sumVisuals);
  }

  void init(GL2 gl) {
    
    Shader vs = ShaderLoader.load(gl, "/shaders/cspace.vert", Shader.Type.VERTEX);
    Shader fs = ShaderLoader.load(gl, "/shaders/cspace.frag", Shader.Type.FRAGMENT);
    subShader = new Program();
    subShader.attach(gl, vs);
    subShader.attach(gl, fs);
    subShader.link(gl);

    uShading = subShader.uniform("shading");
    uColor = subShader.uniform("edgeColor");
    uColoring = subShader.uniform("coloring");
    uClipping = subShader.uniform("clipping");
    uAlpha = subShader.uniform("alpha");
    uTheta = subShader.uniform("robotTheta");
  }

  void delete(GL gl) {
    mesh.delete(gl);
  }

  void drawSub(GL2 gl, SampledSub s) {
    gl.glBegin(GL2.GL_TRIANGLES);
    for (Triangle tri : s.triangles) {
      drawTri(gl, tri);
    }
    gl.glEnd();
  }

  void drawTri(GL2 gl, Triangle t) {
    gl.glVertex3d(t.a.position.x, t.a.position.y, t.a.position.z);
    gl.glVertex3d(t.b.position.x, t.b.position.y, t.b.position.z);
    gl.glVertex3d(t.c.position.x, t.c.position.y, t.c.position.z);
  }

  void draw(GL2 gl) {

    if (selected != null) {
      gl.glColor3f(1, 1, 1);
      drawSub(gl, selected);
      gl.glColor3f(1, 0, 0);
      for (SampledSub n : selected.neighbors) {
        drawSub(gl, n);
      }

      if (selectedTri >= 0 && selectedTri < selected.triangles.size()) {
        Triangle t = selected.triangles.get(selectedTri);
        gl.glColor3f(0.5f, 0.5f, 1);
        gl.glDisable(GL.GL_DEPTH_TEST);
        gl.glBegin(GL2.GL_TRIANGLES);
        drawTri(gl, t);
        gl.glEnd();
        gl.glColor3f(0, 1, 0);
        for (Triangle n : t.neighbors) {
          if (n != null) {
            gl.glBegin(GL2.GL_TRIANGLES);
            drawTri(gl, n);
            gl.glEnd();
          }
        }
        gl.glEnd();
        gl.glEnable(GL.GL_DEPTH_TEST);
        gl.glLineWidth(1);
      }
    }

    if (!visuals.isVisible3d()) {
      return;
    }

    subShader.bind(gl);
    uShading.set(gl, visuals.isShading());
    uAlpha.set(gl, 1);
    uTheta.set(gl, (float) robotVisuals.getTheta());
    switch (visuals.getColoring()) {
    case SOLID_COLOR:
      uColor.set(gl, visuals.getColor());
      uColoring.set(gl, COLOR_EDGE);
      break;
    case UNIQUE_COLOR:
      uColoring.set(gl, COLOR_UNIQUE);
      break;
    case NORMAL_COLOR:
      uColoring.set(gl, COLOR_NORMAL);
      break;
    case SUMEE_COLOR:
      uColoring.set(gl, COLOR_UNIQUE);
      break;
    }

    mesh.setState(gl);
    switch (visuals.getStyle()) {
    case SOLID:
      drawSolid(gl);
      break;
    case TRANSLUCENT:
      drawTranslucent(gl);
      break;
    case BORDER_THETA:
      drawBorderTheta(gl);
      break;
    case CLIP_ABOVE:
      drawClipAbove(gl);
      break;
    case CLIP_AROUND:
      drawClipAround(gl);
      break;
    case CLIP_BELOW:
      drawClipBelow(gl);
      break;
    }

    if (visuals.isWireframe()) {
      drawWireframe(gl);
    }

    subShader.unbind(gl);
    mesh.unsetState(gl);
  }

  private void drawSolid(GL2 gl) {
    uClipping.set(gl, CLIP_NONE);
    mesh.draw(gl);
  }

  private void drawTranslucent(GL2 gl) {
    gl.glDisable(GL.GL_DEPTH_TEST);
    uClipping.set(gl, CLIP_NONE);
    gl.glEnable(GL.GL_BLEND);
    gl.glBlendFunc(GL.GL_SRC_ALPHA, GL.GL_ONE);
    uAlpha.set(gl, 0.3f);
    mesh.draw(gl);
    gl.glDisable(GL.GL_BLEND);
    gl.glEnable(GL.GL_DEPTH_TEST);
  }

  private void drawBorderTheta(GL2 gl) {
  }

  private void drawClipAbove(GL2 gl) {
    uClipping.set(gl, CLIP_ABOVE);
    mesh.draw(gl);
  }

  private void drawClipAround(GL2 gl) {
    uClipping.set(gl, CLIP_AROUND);
    mesh.draw(gl);
    drawTranslucent(gl);
  }

  private void drawClipBelow(GL2 gl) {
    uClipping.set(gl, CLIP_BELOW);
    mesh.draw(gl);
  }

  private void drawWireframe(GL2 gl) {
    uColoring.set(gl, COLOR_EDGE);
    uColor.set(gl, 0f, 0f, 0f);
    uAlpha.set(gl, 1f);
    uShading.set(gl, false);

    gl.glPolygonMode(GL2.GL_FRONT_AND_BACK, GL2.GL_LINE);
    mesh.draw(gl);
    gl.glPolygonMode(GL2.GL_FRONT_AND_BACK, GL2.GL_FILL);
  }

  void update(Scene scene) {
    mesh.update(scene);
  }
}
