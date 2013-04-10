package cspace;

import javax.media.opengl.GL;
import javax.media.opengl.GL2;

import jgl.core.Viewport;
import jgl.math.vector.Vec3f;
import cspace.render2d.Renderer2D;
import cspace.render3d.Renderer3D;
import cspace.scene.Scene;

/**
 * Configuration space view (both 2D and 3D)
 */
public class SceneRenderer {

  public Viewport   screen;
  public Viewport   vp2D;
  public Viewport   vp3D;
  public Renderer2D renderer2d;
  public Renderer3D renderer3d;
  Scene             scene;
  
  // TODO: why viewports duplicated?

  public SceneRenderer(Scene scene) {
    this.scene = scene;
    renderer2d = new Renderer2D(scene);
    renderer3d = new Renderer3D(scene);
  }

  public Renderer2D get2D() {
    return renderer2d;
  }

  public Renderer3D get3D() {
    return renderer3d;
  }

  public void init(GL2 gl) {
    renderer2d.init(gl);
    renderer3d.init(gl);
  }

  public void dispose(GL2 gl) {
    renderer2d.dispose(gl);
    renderer3d.dispose(gl);
  }

  private void draw2DOnly(GL2 gl) {
    vp2D = new Viewport(screen.x, screen.y, screen.width, screen.height);
    vp3D = new Viewport(-1, -1, -1, -1);
    renderer2d.reshape(vp2D.x, vp2D.y, vp2D.width, vp2D.height);
    vp2D.apply(gl);
    renderer2d.display(gl);
  }

  private void draw3DOnly(GL2 gl) {
    vp2D = new Viewport(-1, -1, -1, -1);
    vp3D = new Viewport(screen.x, screen.y, screen.width, screen.height);
    renderer3d.reshape(vp3D.x, vp3D.y, vp3D.width, vp3D.height);
    vp3D.apply(gl);
    renderer3d.display(gl);
  }

  private void drawSplit(GL2 gl) {
    int w2d = screen.width / 2;
    int w3d = screen.width - w2d;
    vp2D = new Viewport(screen.x, screen.y, w2d, screen.height);
    vp3D = new Viewport(screen.x + w2d, screen.y, w3d, screen.height);
    renderer2d.reshape(vp2D.x, vp2D.y, vp2D.width, vp2D.height);
    renderer3d.reshape(vp3D.x, vp3D.y, vp3D.width, vp3D.height);

    gl.glEnable(GL.GL_SCISSOR_TEST);

    beginViewport(gl, vp2D);
    renderer2d.display(gl);
    endViewport(gl);
    
    beginViewport(gl, vp3D);
    renderer3d.display(gl);
    endViewport(gl);
    
    gl.glDisable(GL.GL_SCISSOR_TEST);

    // draw center line
    gl.glMatrixMode(GL2.GL_PROJECTION);
    gl.glLoadIdentity();
    gl.glMatrixMode(GL2.GL_MODELVIEW);
    gl.glLoadIdentity();
    screen.apply(gl);
    gl.glLineWidth(3);
    Vec3f c = new Vec3f(1).subtract(scene.view.renderer.background);
    gl.glColor3f(c.x, c.y, c.z);
    gl.glBegin(GL2.GL_LINES);
    gl.glVertex2f(0, -1);
    gl.glVertex2f(0, 1);
    gl.glEnd();
    gl.glLineWidth(1);
  }

  int frames = 0;

  public void display(GL2 gl) {
    Vec3f bg = scene.view.renderer.background;
    gl.glClearColor(bg.x, bg.y, bg.z, 0);

    switch (scene.view.renderer.viewMode) {
    case VIEW_2D:
      draw2DOnly(gl);
      break;
    case VIEW_3D:
      draw3DOnly(gl);
      break;
    case VIEW_SPLIT:
      drawSplit(gl);
      break;
    }
  }
  
  private void beginViewport(GL2 gl, Viewport vp) {
    gl.glScissor(vp.x, vp.y, vp.width, vp.height);
    vp.apply(gl);
    gl.glMatrixMode(GL2.GL_PROJECTION);
    gl.glPushMatrix();
    gl.glMatrixMode(GL2.GL_MODELVIEW);
    gl.glPushMatrix();
  }
  
  private void endViewport(GL2 gl) {
    gl.glMatrixMode(GL2.GL_PROJECTION);
    gl.glPopMatrix();
    gl.glMatrixMode(GL2.GL_MODELVIEW);
    gl.glPopMatrix();
  }

  public void reshape(int x, int y, int w, int h) {
    screen = new Viewport(x, y, w, h);
  }

}
