package cspace.ui;

import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;

import jgl.core.Viewport;
import jgl.math.vector.Vec2f;
import cspace.SceneRenderer;
import cspace.render2d.Camera;
import cspace.scene.Scene;

/**
 * Mouse / keyboard input controller for 2D visualization.
 */
public class Controller2D implements MouseListener, MouseMotionListener, MouseWheelListener {

  private final Scene         scene;
  private final SceneRenderer renderer;
  private Point               mousePressPt;
  private Vec2f               cameraPos;

  public Controller2D(Scene scene, SceneRenderer renderer) {
    this.scene = scene;
    this.renderer = renderer;
  }

  @Override
  public void mouseWheelMoved(MouseWheelEvent e) {
    Camera camera = renderer.get2D().getCamera();
    float deltaScale = (e.getWheelRotation() < 0 ? 0.1f : -0.1f) * camera.getScale();
    camera.setScale(camera.getScale() + deltaScale);
    renderer.get2D().markDirty();
  }

  @Override
  public void mouseDragged(MouseEvent e) {
    if (mousePressPt != null) {
      if (e.isShiftDown()) {
        moveRobot(e.getPoint());
      } else {
        panCamera(e.getPoint());
      }
    }
  }

  private void moveRobot(Point mousePt) {
    Vec2f p = renderer.get2D().getCamera().getCenter().plus(windowToWorld(mousePt));
    scene.view.robot.position.x = p.x;
    scene.view.robot.position.y = p.y;
  }

  private void panCamera(Point mousePt) {
    Vec2f a = windowToWorld(mousePressPt);
    Vec2f b = windowToWorld(mousePt);
    renderer.get2D().getCamera().setCenter(cameraPos.plus(a.minus(b)));
  }

  Vec2f windowToWorld(Point p) {
    // normalize mouse coordinates to be [0,1] where 0 = left/bottom and 1 = right/top
    Viewport vp = renderer.get2D().getViewport();
    float nx = (float) p.x / vp.width;
    float ny = (float) (vp.height - 1 - p.y) / vp.height;

    // transform to world coordinates
    Camera camera = renderer.get2D().getCamera();
    float worldX = camera.getLeft() + camera.getWidth() * nx;
    float worldY = camera.getBottom() + camera.getHeight() * ny;
    return new Vec2f(worldX, worldY).over(camera.getScale());
  }

  @Override
  public void mousePressed(MouseEvent e) {
    mousePressPt = e.getPoint();
    cameraPos = new Vec2f(renderer.get2D().getCamera().getCenter());
  }

  @Override
  public void mouseReleased(MouseEvent e) {
    mousePressPt = null;
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
}
