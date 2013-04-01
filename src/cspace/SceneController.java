package cspace;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.util.ArrayList;
import java.util.List;

import cspace.scene2d.Scene2D;
import cspace.scene2d.Scene2DController;
import cspace.scene3d.Scene3D;
import cspace.scene3d.Scene3DController;

/**
 * Delegates mouse & keyboard input to 2D and 3D scene controllers.
 */
public class SceneController implements MouseListener, MouseMotionListener,
    MouseWheelListener, KeyListener, Scene2DController.Listener,
    Scene3DController.Listener {

  public Scene2DController control2d;
  public Scene3DController control3d;
  public SceneRenderer         view;
  public List<Listener>    listeners = new ArrayList<Listener>();

  public SceneController(SceneRenderer view) {
    this.view = view;
    control2d = new Scene2DController(view.renderer2d);
    control2d.listeners.add(this);
    control3d = new Scene3DController(view.renderer3d);
    control3d.listeners.add(this);
    control3d.start();
  }

  private void updateView() {
    for (Listener l : listeners)
      l.viewUpdated(view);
  }

  @Override
  public void keyPressed(KeyEvent e) {
    control3d.keyPressed(e);
  }

  @Override
  public void keyReleased(KeyEvent e) {
    control3d.keyReleased(e);
  }

  @Override
  public void keyTyped(KeyEvent e) {
    control3d.keyTyped(e);
  }

  @Override
  public void mouseWheelMoved(MouseWheelEvent e) {
    if (view.vp2D.contains(e.getPoint())) {
      control2d.mouseWheelMoved(e);
    } else {
      control3d.mouseWheelMoved(e);
    }
  }

  @Override
  public void mouseDragged(MouseEvent e) {
    control2d.mouseDragged(e);
    control3d.mouseDragged(e);
  }

  @Override
  public void mousePressed(MouseEvent e) {
    if (view.vp2D.contains(e.getPoint())) {
      control2d.mousePressed(e);
    } else {
      control3d.mousePressed(e);
    }
  }

  @Override
  public void mouseReleased(MouseEvent e) {
    control2d.mouseReleased(e);
    control3d.mouseReleased(e);
  }

  @Override
  public void viewUpdated(Scene2D view) {
    updateView();
  }

  @Override
  public void viewUpdated(Scene3D view) {
    updateView();
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

  // ============================================================================

  public interface Listener {
    void viewUpdated(SceneRenderer view);
  }

  // ============================================================================
}
