package cspace.render3d;

import javax.media.opengl.GL;
import javax.media.opengl.GL2;

import jgl.core.Program;
import jgl.core.Shader;
import jgl.core.Uniform;
import jgl.loaders.ShaderLoader;
import cspace.scene.Scene;

public class SubRenderer {

  private static final int COLOR_EDGE   = 0;
  private static final int COLOR_UNIQUE = 1;
  private static final int COLOR_NORMAL = 2;
  private static final int CLIP_NONE    = 0;
  private static final int CLIP_ABOVE   = 1;
  private static final int CLIP_AROUND  = 2;
  private static final int CLIP_BELOW   = 3;

  private Scene            scene;
  private Program          subShader;
  private SubMesh          mesh;
  private Uniform          uShading;
  private Uniform          uColor;
  private Uniform          uColoring;
  private Uniform          uClipping;
  private Uniform          uAlpha;
  private Uniform          uTheta;
  private Uniform          uReverse;

  SubRenderer(Scene scene) {
    this.scene = scene;
    this.mesh = new SubMesh(scene);
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
    uReverse = subShader.uniform("reverse");
  }

  void delete(GL gl) {
    mesh.delete(gl);
  }

  void draw(GL2 gl) {
    if (!scene.view.subs.visible3d)
      return;

    subShader.bind(gl);
    uShading.set(gl, scene.view.subs.shaded);
    uAlpha.set(gl, 1);
    uTheta.set(gl, (float) scene.view.robot.rotation.anglePi());
    switch (scene.view.subs.colorStyle3d) {
    case UNIFORM:
      uColor.set(gl, scene.view.subs.color);
      uColoring.set(gl, COLOR_EDGE);
      break;
    case PER_SUB:
      uColoring.set(gl, COLOR_UNIQUE);
      break;
    case NORMALS:
      uColoring.set(gl, COLOR_NORMAL);
      break;
    case PER_SUM:
      uColoring.set(gl, COLOR_UNIQUE);
      break;
    }

    mesh.setState(gl);
    switch (scene.view.subs.renderStyle3d) {
    case OPAQUE:
      drawSolid(gl);
      break;
    case TRANSLUCENT:
      drawTranslucent(gl);
      break;
    case CLIP_ABOVE_THETA:
      drawClipAbove(gl);
      break;
    case CLIP_AROUND_THETA:
      drawClipAround(gl);
      break;
    case CLIP_BELOW_THETA:
      drawClipBelow(gl);
      break;
    }

    if (scene.view.subs.wireframed)
      drawWireframe(gl);

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
    if (scene.view.renderer.background.length() < 0.86) {
      gl.glBlendEquation(GL.GL_FUNC_ADD);
      gl.glBlendFunc(GL.GL_SRC_ALPHA, GL.GL_ONE);
      uReverse.set(gl, false);
    } else {
      gl.glBlendEquation(GL.GL_FUNC_REVERSE_SUBTRACT);
      gl.glBlendFunc(GL.GL_SRC_COLOR, GL.GL_ONE);
      uReverse.set(gl, true);
    }
    uAlpha.set(gl, 0.25f);
    mesh.draw(gl);
    gl.glDisable(GL.GL_BLEND);
    gl.glEnable(GL.GL_DEPTH_TEST);
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
