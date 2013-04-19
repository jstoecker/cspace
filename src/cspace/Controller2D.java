package cspace;

import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;

import javax.swing.SwingUtilities;

import jgl.core.Viewport;
import jgl.math.vector.Vec2d;
import jgl.math.vector.Vec2f;
import cspace.render2d.Camera;
import cspace.scene.Scene;
import cspace.scene.Sub;

/**
 * Mouse / keyboard input controller for 2D visualization.
 */
public class Controller2D implements MouseListener, MouseMotionListener, MouseWheelListener {

  private final SceneController controller;
  private final Scene           scene;
  private final SceneRenderer   renderer;
  private Point                 mousePressPt;
  private Vec2f                 cameraPos;
  private boolean               robotHovered = false;

  public Controller2D(SceneController controller) {
    this.scene = controller.getScene();
    this.renderer = controller.getRenderer();
    this.controller = controller;
  }

  @Override
  public void mouseWheelMoved(MouseWheelEvent e) {
    if (e.isShiftDown()) {
      // rotate robot
      float delta = e.getWheelRotation() < 0 ? 3 : -3;
      scene.view.robot.rotation.rotateDegrees(delta);
    } else {
      // zoom camera
      Camera camera = renderer.get2D().getCamera();
      float deltaScale = (e.getWheelRotation() < 0 ? 0.1f : -0.1f) * camera.getScale();
      camera.setScale(camera.getScale() + deltaScale);
    }
    renderer.get2D().markDirty();
  }

  @Override
  public void mouseDragged(MouseEvent e) {
    if (SwingUtilities.isRightMouseButton(e)) {
      rotateRobotPrecise(e);
    } else if (mousePressPt != null) {
      if (robotHovered) {
        // move robot
        Vec2f p = renderer.get2D().getCamera().getCenter().plus(windowToWorld(e.getPoint()));
        scene.view.robot.position.x = p.x;
        scene.view.robot.position.y = p.y;
      } else {
        // translate camera
        Vec2f a = windowToWorld(mousePressPt);
        Vec2f b = windowToWorld(e.getPoint());
        renderer.get2D().getCamera().setCenter(cameraPos.plus(a.minus(b)));
      }
    }
  }

  private void rotateRobotPrecise(MouseEvent e) {
    // rotate robot using angle between anchor and robot center
    Vec2f p = renderer.get2D().getCamera().getCenter().plus(windowToWorld(e.getPoint()));
    Vec2f angle = p.minus(scene.view.robot.position.toFloat());
    renderer.get2D().getRobotRenderer().setAnglePt(p);
    scene.view.robot.rotation = new Vec2d(angle.x, angle.y).normalize();
    renderer.get2D().markDirty();
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

  Point worldToWindow(Vec2d p) {
    Viewport vp = renderer.get2D().getViewport();
    Camera camera = renderer.get2D().getCamera();
    p.x -= camera.getCenter().x();
    p.y -= camera.getCenter().y();
    int x = (int) ((p.x * camera.getScale() - camera.getLeft()) * vp.width / camera.getWidth());
    int y = -(int) (((p.y * camera.getScale() - camera.getBottom()) * vp.height / camera
        .getHeight()) - vp.height + 1);
    return new Point(x, y);
  }

  @Override
  public void mousePressed(MouseEvent e) {
    if (SwingUtilities.isRightMouseButton(e)) {
      rotateRobotPrecise(e);
    } else {
      mousePressPt = e.getPoint();
      cameraPos = new Vec2f(renderer.get2D().getCamera().getCenter());
    }
  }

  @Override
  public void mouseReleased(MouseEvent e) {
    mousePressPt = null;
    renderer.get2D().getRobotRenderer().setAnglePt(null);
  }

  @Override
  public void mouseMoved(MouseEvent e) {
    Point rWindow = worldToWindow(scene.view.robot.position.copy());
    float dist = new Vec2f(e.getX(), e.getY()).minus(new Vec2f(rWindow.x, rWindow.y)).length();
    robotHovered = dist < 20;
    renderer.get2D().getRobotRenderer().setHighlight(robotHovered);

    if (controller.isInspectMode()) {
      debugPick(e.getPoint());
    }
  }

  private void debugPick(Point p) {
    // highlight the sub arc that is closest to the mouse cursor
    Vec2f pWorldf = renderer.get2D().getCamera().getCenter().plus(windowToWorld(p));
    Vec2d pWorld = new Vec2d(pWorldf.x, pWorldf.y);
    Sub closest = null;
    double closestDist = 0;
    double minHLDist = 0.5;
    double theta = scene.view.robot.rotation.anglePi();
    for (Sub sub : scene.cspace.subs) {
      if (sub.isActive(theta)) {
        Vec2d subC = sub.center(scene.view.robot.rotation);
        Vec2d cToP = pWorld.minus(subC);
        Vec2d tn = sub.tail.normal(scene.view.robot.rotation, subC, sub.r);
        Vec2d hn = sub.head.normal(scene.view.robot.rotation, subC, sub.r);
        if (tn.cross(cToP) > 0 && cToP.cross(hn) > 0) {
          double dist = Math.abs(pWorld.minus(sub.center(scene.view.robot.rotation)).length()
              - Math.abs(sub.r));
          if ((closest == null || dist < closestDist) && dist < minHLDist) {
            closest = sub;
            closestDist = dist;
          }
        }
      }
    }
    
    Point winCoords = new Point(p.x, renderer.getViewport2d().height - p.y - 1);
    renderer.get2D().getInspectRenderer().setCursor(winCoords);
    renderer.get2D().getInspectRenderer().setHighlightedSub(closest);
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
