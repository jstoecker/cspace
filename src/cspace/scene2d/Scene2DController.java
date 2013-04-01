package cspace.scene2d;

import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.util.ArrayList;
import java.util.List;

import jgl.math.vector.Vec2d;
import jgl.math.vector.Vec2f;

/**
 * Mouse / keyboard input controller for 2D scene.
 */
public class Scene2DController implements MouseListener, MouseMotionListener,
    MouseWheelListener {

  public Scene2D view;
  Point mousePressPt;
  Vec2f cameraPos;
  public List<Listener> listeners = new ArrayList<Listener>();

  public Scene2DController(Scene2D view) {
    this.view = view;
  }

  private void updateView() {
    for (Listener l : listeners)
      l.viewUpdated(view);
  }

  @Override
  public void mouseWheelMoved(MouseWheelEvent e) {
    if (view.scene.newPath != null && (e.isShiftDown() || e.isAltDown())) {
      int wpIndex = e.isShiftDown() ? 0 : 1;
      double theta = view.scene.newPath.waypoints[wpIndex].theta;
      if (e.getWheelRotation() < 0) {
        theta += 0.1;
      } else {
        theta -= 0.1;
      }
      view.scene.newPath.waypoints[wpIndex].theta = theta;
      view.scene.newPath.waypoints[wpIndex].u = new Vec2d(Math.cos(theta), Math.sin(theta));
      updateView();
    } else {
      if (e.getWheelRotation() < 0)
        view.camera.scale += 0.1f * view.camera.scale;
      else
        view.camera.scale -= 0.1f * view.camera.scale;
      view.scene.visuals.genVisuals.setEdgeScale(1f / view.camera.scale);
    }
  }

  @Override
  public void mouseDragged(MouseEvent e) {
    if (mousePressPt != null) {
      int deltaX = e.getPoint().x - mousePressPt.x;
      int deltaY = e.getPoint().y - mousePressPt.y;
      float scale = 0.02f / view.camera.scale;
      if (view.scene.newPath != null && (e.isShiftDown() || e.isAltDown())) {
        int wpIndex = e.isShiftDown() ? 0 : 1;
        float wx = 16 * (float)e.getPoint().x / view.viewport.width - 8;
        float wy = (16 * (float)(view.viewport.height - 1 - e.getPoint().y) / view.viewport.height - 8) / view.viewport.aspect();
        view.scene.newPath.waypoints[wpIndex].p.x = view.camera.center.x + wx / view.camera.scale;
        view.scene.newPath.waypoints[wpIndex].p.y = view.camera.center.y + wy / view.camera.scale;
      } else if (e.isShiftDown()) { 
        moveRobot(e.getPoint());
      } else {
        panCamera(e.getPoint());
      }
      updateView();
    }
  }
  
  void moveRobot(Point mousePt) {
    Vec2f p = view.camera.center.plus(windowToWorld(mousePt));
    view.robotView.visuals.getP().x = p.x;
    view.robotView.visuals.getP().y = p.y;
  }
  
  void panCamera(Point mousePt) {
    Vec2f a = windowToWorld(mousePressPt);
    Vec2f b = windowToWorld(mousePt);
    view.camera.center = cameraPos.plus(a.minus(b));
  }
  
  Vec2f windowToWorld(Point p) {
    // normalize mouse coordinates to be [0,1] where 0 = left/bottom and 1 = right/top
    float nx = (float)p.x / view.viewport.width;
    float ny = (float)(view.viewport.height - 1 - p.y) / view.viewport.height;
    
    // transform to world coordinates
    float worldX = view.camera.left + view.camera.width * nx;
    float worldY = view.camera.bottom + view.camera.height * ny;
    return new Vec2f(worldX, worldY).over(view.camera.scale);
  }

  @Override
  public void mousePressed(MouseEvent e) {
    mousePressPt = e.getPoint();
    cameraPos = new Vec2f(view.camera.center);
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

  // ===========================================================================

  public interface Listener {
    void viewUpdated(Scene2D view);
  }

  // ===========================================================================
}
