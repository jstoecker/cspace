package cspace.ui;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;

import javax.media.opengl.GLAutoDrawable;
import javax.media.opengl.GLEventListener;
import javax.media.opengl.awt.GLCanvas;

import com.jogamp.opengl.util.FPSAnimator;

import cspace.SceneRenderer;
import cspace.scene.Scene;

/**
 * Delegates mouse & keyboard input to 2D and 3D scene controllers.
 * 
 * @author justin
 */
public class SceneController implements GLEventListener, MouseListener, MouseMotionListener,
    MouseWheelListener, KeyListener {

  private final FPSAnimator   animator;
  private final Scene         scene;
  private final SceneRenderer renderer;
  private final Controller2D  controller2d;
  private final Controller3D  controller3d;

  public SceneController(Scene scene, GLCanvas canvas) {
    this.scene = scene;
    this.renderer = new SceneRenderer(scene);
    controller2d = new Controller2D(scene, renderer);
    controller3d = new Controller3D(scene, renderer);

    canvas.addGLEventListener(this);
    canvas.addMouseListener(this);
    canvas.addMouseMotionListener(this);
    canvas.addMouseWheelListener(this);
    canvas.addKeyListener(this);

    animator = new FPSAnimator(canvas, 60);
    animator.start();
  }

  public void shutdown() {
    animator.stop();
    scene.view.save();
  }

  public Controller2D get2D() {
    return controller2d;
  }

  public Controller3D get3D() {
    return controller3d;
  }

  @Override
  public void keyPressed(KeyEvent e) {
    controller3d.keyPressed(e);
  }

  @Override
  public void keyReleased(KeyEvent e) {
    controller3d.keyReleased(e);
  }

  @Override
  public void keyTyped(KeyEvent e) {
    controller3d.keyTyped(e);
  }

  @Override
  public void mouseWheelMoved(MouseWheelEvent e) {
    if (renderer.vp2D.contains(e.getPoint())) {
      controller2d.mouseWheelMoved(e);
    } else {
      controller3d.mouseWheelMoved(e);
    }
  }

  @Override
  public void mouseDragged(MouseEvent e) {
    controller2d.mouseDragged(e);
    controller3d.mouseDragged(e);
  }

  @Override
  public void mousePressed(MouseEvent e) {
    if (renderer.vp2D.contains(e.getPoint())) {
      controller2d.mousePressed(e);
    } else {
      controller3d.mousePressed(e);
    }
  }

  @Override
  public void mouseReleased(MouseEvent e) {
    controller2d.mouseReleased(e);
    controller3d.mouseReleased(e);
  }

  @Override
  public void mouseMoved(MouseEvent e) {
  }

  @Override
  public void mouseClicked(MouseEvent e) {
  }

  @Override
  public void mouseEntered(MouseEvent e) {
  }

  @Override
  public void mouseExited(MouseEvent e) {
  }

  @Override
  public void display(GLAutoDrawable drawable) {
    controller3d.update();
    renderer.display(drawable.getGL().getGL2());
  }

  @Override
  public void dispose(GLAutoDrawable drawable) {
    renderer.dispose(drawable.getGL().getGL2());
  }

  @Override
  public void init(GLAutoDrawable drawable) {
    renderer.init(drawable.getGL().getGL2());
  }

  @Override
  public void reshape(GLAutoDrawable drawable, int x, int y, int width, int height) {
    renderer.reshape(x, y, width, height);
  }
}
