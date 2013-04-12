package cspace.ui;

import java.awt.Point;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;

import jgl.cameras.Camera;
import jgl.cameras.FirstPersonController;
import jgl.math.vector.Vec3f;
import cspace.SceneRenderer;
import cspace.scene.Scene;

/**
 * Mouse / keyboard input controller for 3D visualization.
 */
public class Controller3D implements MouseListener, MouseMotionListener, MouseWheelListener,
    KeyListener {

  private final Scene           scene;
  private final SceneRenderer   renderer;

  private FirstPersonController cameraController;
  private float                 pixelsToRadians  = 0.005f;
  private int                   rotButtonMask    = MouseEvent.BUTTON1_DOWN_MASK;
  private Point                 mouseAnchor;
  private float                 anchorYaw;
  private float                 anchorPitch;
  private boolean               moveFwd          = false;
  private boolean               moveBack         = false;
  private boolean               moveLeft         = false;
  private boolean               moveRight        = false;
  private float                 translationScale = 0.1f;

  public Controller3D(Scene scene, SceneRenderer renderer) {
    this.scene = scene;
    this.renderer = renderer;

    cameraController = new FirstPersonController(new Vec3f(-9, -6.8f, 6.2f), 0, 0, false);
    cameraController.setCamera(renderer.get3D().getCamera());
    cameraController.setRotation(-3.9f, -.535f);
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
  
  public void update() {
    Vec3f translation = new Vec3f(0);

    Camera camera = renderer.get3D().getCamera();

    if (moveFwd)
      translation.add(camera.getForward().times(translationScale));

    if (moveBack)
      translation.add(camera.getBackward().times(translationScale));

    if (moveLeft)
      translation.add(camera.getLeft().times(translationScale));

    if (moveRight)
      translation.add(camera.getRight().times(translationScale));

    if (translation.lengthSquared() > 0) {
      cameraController.move(translation);
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
}
