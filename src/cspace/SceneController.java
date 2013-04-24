package cspace;

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

import cspace.gui.MainWindow;
import cspace.scene.Path;
import cspace.scene.PathFinder;
import cspace.scene.Scene;

/**
 * Delegates mouse & keyboard input to 2D and 3D scene controllers.
 * 
 * @author justin
 */
public class SceneController implements GLEventListener, MouseListener, MouseMotionListener,
    MouseWheelListener, KeyListener {

  private final MainWindow    mainWindow;
  private final GLCanvas      canvas;
  private final Scene         scene;
  private final SceneRenderer renderer;
  private final Controller2D  controller2d;
  private final Controller3D  controller3d;
  private boolean             inspectMode = false;
  private PathFinder          pathFinder  = null;

  public SceneController(Scene scene, MainWindow mainWindow) {
    this.scene = scene;
    this.renderer = new SceneRenderer(scene);
    this.canvas = mainWindow.getCanvas();
    this.mainWindow = mainWindow;
    
    controller2d = new Controller2D(this);
    controller3d = new Controller3D(this);

    canvas.addGLEventListener(this);
    canvas.addMouseListener(this);
    canvas.addMouseMotionListener(this);
    canvas.addMouseWheelListener(this);
    canvas.addKeyListener(this);
  }

  public void shutdown() {
    canvas.removeGLEventListener(this);
    canvas.removeMouseListener(this);
    canvas.removeMouseMotionListener(this);
    canvas.removeMouseWheelListener(this);
    canvas.removeKeyListener(this);
    scene.view.save();
  }

  public void setInspectMode(boolean inspectMode) {
    this.inspectMode = inspectMode;
    renderer.get2D().getInspectRenderer().setEnabled(inspectMode);
    renderer.get3D().getDebugRenderer().setEnabled(inspectMode);
  }

  public PathFinder getPathFinder() {
    return pathFinder;
  }

  public boolean isInspectMode() {
    return inspectMode;
  }

  public Controller2D get2D() {
    return controller2d;
  }

  public Controller3D get3D() {
    return controller3d;
  }

  public Scene getScene() {
    return scene;
  }

  public SceneRenderer getRenderer() {
    return renderer;
  }

  public void setPathFinder(PathFinder pathFinder) {
    this.pathFinder = pathFinder;
    renderer.get2D().getPathFindRenderer().setPathFinder(pathFinder);
  }

  @Override
  public void keyPressed(KeyEvent e) {
    controller3d.keyPressed(e);

    if (pathFinder != null) {
      if (e.getKeyChar() == KeyEvent.VK_ESCAPE) {
        setPathFinder(null);
      } else if (pathFinder.getRobotStartPos() == null) {
        if (e.getKeyChar() == KeyEvent.VK_ENTER) {
          pathFinder.setRobotStart(scene.view.robot.position, scene.view.robot.rotation);
        }
      } else {
        if (e.getKeyChar() == KeyEvent.VK_ENTER) {
          pathFinder.setRobotEnd(scene.view.robot.position, scene.view.robot.rotation);
          Path newPath = pathFinder.makePath(scene.cspace);
          if (newPath != null) {
            scene.path = newPath;
            mainWindow.getToolBar().updatePath();
            renderer.get2D().markDirty();
          }
          setPathFinder(null);
        }
      }
    }

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
    if (renderer.getViewport2d().contains(e.getPoint())) {
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
    if (renderer.getViewport2d().contains(e.getPoint())) {
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

  @Override
  public void mouseMoved(MouseEvent e) {
    if (renderer.getViewport3d() != null && renderer.getViewport3d().contains(e.getPoint()))
      controller3d.mouseMoved(e);
    else if (renderer.getViewport2d() != null)
      controller2d.mouseMoved(e);
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
