package cspace.scene3d;

import java.awt.Point;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.util.ArrayList;
import java.util.List;

import jgl.cameras.FirstPersonController;
import jgl.math.vector.Vec3f;

public class Scene3DController implements MouseListener, MouseMotionListener, MouseWheelListener,
    KeyListener {

  private FirstPersonController cameraController;
  public Scene3D                view;
  public List<Listener>         listeners        = new ArrayList<Listener>();
  private float                 pixelsToRadians  = 0.005f;
  private int                   rotButtonMask    = MouseEvent.BUTTON1_DOWN_MASK;
  private Point                 mouseAnchor;
  private float                 anchorYaw;
  private float                 anchorPitch;
  private boolean               moveFwd          = false;
  private boolean               moveBack         = false;
  private boolean               moveLeft         = false;
  private boolean               moveRight        = false;
  private ControlThread         controlThread;
  private float                 translationScale = 0.1f;

  public Scene3DController(Scene3D view) {
    this.view = view;

    cameraController = new FirstPersonController(new Vec3f(-9, -6.8f, 6.2f), 0, 0, false);
    cameraController.setCamera(view.camera);
    cameraController.setRotation(-3.9f, -.535f);
  }

  public void start() {
    controlThread = new ControlThread();
    controlThread.start();
  }

  public void stop() {
    if (controlThread != null) {
      controlThread.running = false;
      controlThread = null;
    }
  }

  public void addListener(Listener listener) {
    listeners.add(listener);
  }

  public void removeListener(Listener listener) {
    listeners.remove(listener);
  }

  @Override
  public void mouseWheelMoved(MouseWheelEvent e) {
  }

  @Override
  public void mouseDragged(MouseEvent e) {
    if (mouseAnchor != null && (e.getModifiersEx() & rotButtonMask) == rotButtonMask) {
      Point curPt = e.getPoint();
      float dx = (mouseAnchor.x - curPt.x) * pixelsToRadians;
      float dy = (mouseAnchor.y - curPt.y) * pixelsToRadians;
      cameraController.setRotation(anchorYaw + dx, anchorPitch + dy);
      updateView();
    }
  }

  @Override
  public void mousePressed(MouseEvent e) {
    mouseAnchor = e.getPoint();
    anchorPitch = cameraController.getPitch();
    anchorYaw = cameraController.getYaw();
  }

  @Override
  public void mouseReleased(MouseEvent e) {
    mouseAnchor = null;
  }

  @Override
  public void keyPressed(KeyEvent e) {
    switch (e.getKeyCode()) {
    case KeyEvent.VK_W:
      moveFwd = true;
      break;
    case KeyEvent.VK_A:
      moveLeft = true;
      break;
    case KeyEvent.VK_S:
      moveBack = true;
      break;
    case KeyEvent.VK_D:
      moveRight = true;
      break;
    }
  }

  @Override
  public void keyReleased(KeyEvent e) {
    switch (e.getKeyCode()) {
    case KeyEvent.VK_W:
      moveFwd = false;
      break;
    case KeyEvent.VK_A:
      moveLeft = false;
      break;
    case KeyEvent.VK_S:
      moveBack = false;
      break;
    case KeyEvent.VK_D:
      moveRight = false;
      break;
    }
  }

  private void updateView() {
    for (Listener l : listeners)
      l.viewUpdated(view);
  }

  private void updateCamera() {
    Vec3f translation = new Vec3f(0);

    if (moveFwd)
      translation.add(view.camera.getForward().times(translationScale));

    if (moveBack)
      translation.add(view.camera.getBackward().times(translationScale));

    if (moveLeft)
      translation.add(view.camera.getLeft().times(translationScale));

    if (moveRight)
      translation.add(view.camera.getRight().times(translationScale));

    if (translation.lengthSquared() > 0) {
      cameraController.move(translation);
      updateView();
    }
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
  public void keyTyped(KeyEvent e) {
  }

  // ===========================================================================

  public interface Listener {
    void viewUpdated(Scene3D view);
  }

  // ===========================================================================

  private class ControlThread extends Thread {
    volatile boolean running = true;

    public void run() {
      while (running) {
        try {
          updateCamera();
          Thread.sleep(16);
        } catch (InterruptedException ex) {
        }
      }
    }
  }

  // ===========================================================================
}
