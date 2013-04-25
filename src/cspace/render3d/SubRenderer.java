package cspace.render3d;

import javax.media.opengl.GL;
import javax.media.opengl.GL2;

import jgl.cameras.Camera;
import jgl.core.Program;
import jgl.core.Shader;
import jgl.core.Viewport;
import cspace.scene.Scene;
import cspace.scene.SceneView.Subs.ClipStyle3D;

public class SubRenderer {

  private Scene   scene;
  private Program prog;
  private Program progWireframed;
  private SubMesh mesh;

  SubRenderer(Scene scene) {
    this.scene = scene;
    this.mesh = new SubMesh(scene);
  }
  
  void init(GL2 gl) {
    prog = new Program();
    prog.attach(gl, Shader.load(gl, "/shaders/sub.vs", Shader.Type.VERTEX));
    prog.attach(gl, Shader.load(gl, "/shaders/sub.fs", Shader.Type.FRAGMENT));
    prog.link(gl);

    // check if geometry shader extensions are available for single-pass wireframe
    String ext = gl.glGetString(GL.GL_EXTENSIONS);
    if (ext.contains("GL_ARB_geometry_shader4")) {
      progWireframed = new Program();
      progWireframed.attach(gl, Shader.load(gl, "/shaders/sub.vs", Shader.Type.VERTEX));
      progWireframed.attach(gl, Shader.load(gl, "/shaders/sub_wireframe.gs", Shader.Type.GEOMETRY));
      progWireframed.attach(gl, Shader.load(gl, "/shaders/sub_wireframe.fs", Shader.Type.FRAGMENT));
      progWireframed.param(gl, GL2.GL_GEOMETRY_INPUT_TYPE_EXT, GL2.GL_TRIANGLES);
      progWireframed.param(gl, GL2.GL_GEOMETRY_OUTPUT_TYPE_EXT, GL2.GL_TRIANGLE_STRIP);
      progWireframed.param(gl, GL2.GL_GEOMETRY_VERTICES_OUT_EXT, 3);
      progWireframed.link(gl);
    }
  }

  void delete(GL gl) {
    mesh.delete(gl);
    prog.delete(gl.getGL2GL3(), true);
    if (progWireframed != null)
      progWireframed.delete(gl.getGL2GL3(), true);
  }

  private static int clipValue(ClipStyle3D style) {
    switch (style) {
    case CLIP_ABOVE_THETA:
      return 1;
    case HIGHLIGHT_THETA:
      return 2;
    case CLIP_BELOW_THETA:
      return 3;
    default:
      return 0;
    }
  }
  
  private void setBlending(GL2 gl) {
    if (scene.view.subs.drawAlpha < 1.0f) {
      gl.glEnable(GL.GL_BLEND);
      gl.glBlendFunc(GL.GL_SRC_ALPHA, GL.GL_ONE);
      gl.glDisable(GL.GL_DEPTH_TEST);
    }
  }
  
  private void endBlending(GL2 gl) {
    if (scene.view.subs.drawAlpha < 1.0f) {
      gl.glDisable(GL.GL_BLEND);
      gl.glEnable(GL.GL_DEPTH_TEST);
    }
  }
  
  private void setUniforms(GL2 gl, Program shader) {
    shader.uniform("color").set(gl, scene.view.subs.color);
    shader.uniform("color_style").set(gl, scene.view.subs.colorStyle3d.ordinal());
    shader.uniform("clip_style").set(gl, clipValue(scene.view.subs.clipStyle3d));
    shader.uniform("robot_theta").set(gl, (float) scene.view.robot.rotation.anglePi());
    shader.uniform("shading").set(gl, scene.view.subs.shaded);
    shader.uniform("alpha").set(gl, scene.view.subs.drawAlpha);
  }

  private void draw(GL2 gl) {
    setBlending(gl);
    prog.bind(gl);
    setUniforms(gl, prog);
    mesh.setState(gl);
    mesh.draw(gl);
    mesh.unsetState(gl);
    prog.unbind(gl);
    endBlending(gl);
  }
  
  private void drawWireframedTwoPass(GL2 gl) {
    // standard pass
    setBlending(gl);
    prog.bind(gl);
    setUniforms(gl, prog);
    mesh.setState(gl);
    gl.glEnable(GL.GL_POLYGON_OFFSET_FILL);
    gl.glPolygonOffset(1, 1);
    mesh.draw(gl);
    gl.glDisable(GL.GL_POLYGON_OFFSET_FILL);
    endBlending(gl);

    // wireframe pass
    prog.uniform("color").set(gl, 0.0f, 0.0f, 0.0f);
    prog.uniform("color_style").set(gl, 0);
    prog.uniform("clip_style").set(gl, 0);
    prog.uniform("shading").set(gl, false);
    prog.uniform("alpha").set(gl, 1.0f);
    gl.glPolygonMode(GL2.GL_FRONT_AND_BACK, GL2.GL_LINE);
    mesh.draw(gl);
    gl.glPolygonMode(GL2.GL_FRONT_AND_BACK, GL2.GL_FILL);
    mesh.unsetState(gl);
    prog.unbind(gl);
  }

  private void drawWireframedOnePass(GL2 gl, Viewport vp) {
    setBlending(gl);
    progWireframed.bind(gl);
    setUniforms(gl, progWireframed);
    progWireframed.uniform("wire_color").set(gl, 0f, 0f, 0f, 1f);
    progWireframed.uniform("viewport").set(gl, (float)vp.width, (float)vp.height);
    mesh.setState(gl);
    mesh.draw(gl);
    mesh.unsetState(gl);
    prog.unbind(gl);
    endBlending(gl);
  }

  void draw(GL2 gl, Camera camera, Viewport viewport) {
    if (!scene.view.subs.visible3d)
      return;

    if (scene.view.subs.wireframed) {
      if (progWireframed == null)
        drawWireframedTwoPass(gl);
      else
        drawWireframedOnePass(gl, viewport);
    } else {
      draw(gl);
    }
  }

  public void update(Scene scene) {
    mesh.update(scene);
  }
}
