package cspace;

import javax.media.opengl.GL;
import javax.media.opengl.GL2;
import javax.media.opengl.GLAutoDrawable;
import javax.media.opengl.GLEventListener;

import jgl.core.Viewport;
import jgl.math.vector.Vec3f;
import cspace.model.Scene;
import cspace.scene2d.Scene2D;
import cspace.scene3d.Scene3D;

/** Configuration space view (both 2D and 3D) */
public class SceneRenderer implements GLEventListener {
  public Viewport    screen;
  public Viewport    vp2D;
  public Viewport    vp3D;
  public Scene2D renderer2d;
  public Scene3D renderer3d;
  Scene              scene;

  public SceneRenderer(Scene scene) {
    this.scene = scene;
    renderer2d = new Scene2D(scene);
    renderer3d = new Scene3D(scene);
  }

  @Override
  public void init(GLAutoDrawable drawable) {
    renderer2d.init(drawable);
    renderer3d.init(drawable);
  }

  @Override
  public void dispose(GLAutoDrawable drawable) {
    renderer2d.dispose(drawable);
    renderer3d.dispose(drawable);
  }

  private void draw2DOnly(GLAutoDrawable drawable, GL2 gl) {
    vp2D = new Viewport(screen.x, screen.y, screen.width, screen.height);
    vp3D = new Viewport(-1, -1, -1, -1);
    renderer2d.reshape(drawable, vp2D.x, vp2D.y, vp2D.width, vp2D.height);
    vp2D.apply(gl);
    renderer2d.display(drawable);
  }

  private void draw3DOnly(GLAutoDrawable drawable, GL2 gl) {
    vp2D = new Viewport(-1, -1, -1, -1);
    vp3D = new Viewport(screen.x, screen.y, screen.width, screen.height);
    renderer3d.reshape(drawable, vp3D.x, vp3D.y, vp3D.width, vp3D.height);
    vp3D.apply(gl);
    renderer3d.display(drawable);
  }

  private void drawSplit(GLAutoDrawable drawable, GL2 gl) {
    int w2d = screen.width / 2;
    int w3d = screen.width - w2d;
    vp2D = new Viewport(screen.x, screen.y, w2d, screen.height);
    vp3D = new Viewport(screen.x + w2d, screen.y, w3d, screen.height);
    renderer2d.reshape(drawable, vp2D.x, vp2D.y, vp2D.width, vp2D.height);
    renderer3d.reshape(drawable, vp3D.x, vp3D.y, vp3D.width, vp3D.height);

    gl.glEnable(GL.GL_SCISSOR_TEST);
    drawView(drawable, renderer2d, vp2D);
    drawView(drawable, renderer3d, vp3D);
    gl.glDisable(GL.GL_SCISSOR_TEST);

    // draw center line
    gl.glMatrixMode(GL2.GL_PROJECTION);
    gl.glLoadIdentity();
    gl.glMatrixMode(GL2.GL_MODELVIEW);
    gl.glLoadIdentity();
    screen.apply(gl);
    gl.glLineWidth(3);
    Vec3f c = new Vec3f(1).subtract(scene.visuals.genVisuals.getBgColor());
    gl.glColor3f(c.x, c.y, c.z);
    gl.glBegin(GL2.GL_LINES);
    gl.glVertex2f(0, -1);
    gl.glVertex2f(0, 1);
    gl.glEnd();
    gl.glLineWidth(1);
  }

  @Override
  public void display(GLAutoDrawable gld) {
    GL2 gl = gld.getGL().getGL2();

    Vec3f bg = scene.visuals.genVisuals.getBgColor();
    gl.glClearColor(bg.x, bg.y, bg.z, 0);

    switch (scene.visuals.genVisuals.getView()) {
    case VIEW_2D:
      draw2DOnly(gld, gl);
      break;
    case VIEW_3D:
      draw3DOnly(gld, gl);
      break;
    case VIEW_SPLIT:
      drawSplit(gld, gl);
      break;
    }
  }

  private void drawView(GLAutoDrawable gld, GLEventListener view, Viewport vp) {
    GL2 gl = gld.getGL().getGL2();

    gl.glScissor(vp.x, vp.y, vp.width, vp.height);
    vp.apply(gl);

    gl.glMatrixMode(GL2.GL_PROJECTION);
    gl.glPushMatrix();
    gl.glMatrixMode(GL2.GL_MODELVIEW);
    gl.glPushMatrix();

    view.display(gld);

    gl.glMatrixMode(GL2.GL_PROJECTION);
    gl.glPopMatrix();
    gl.glMatrixMode(GL2.GL_MODELVIEW);
    gl.glPopMatrix();
  }

  @Override
  public void reshape(GLAutoDrawable gld, int x, int y, int w, int h) {
    screen = new Viewport(x, y, w, h);
  }

  // ===========================================================================

  public enum ViewMode {
    VIEW_2D("2D Only"),
    VIEW_3D("3D Only"),
    VIEW_SPLIT("Split 2D/3D");

    private final String toString;

    private ViewMode(String toString) {
      this.toString = toString;
    }

    @Override
    public String toString() {
      return toString;
    }
  }

  // ===========================================================================
}
