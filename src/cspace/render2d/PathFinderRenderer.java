package cspace.render2d;

import java.awt.Font;

import javax.media.opengl.GL2;

import jgl.core.Viewport;
import jgl.math.vector.Vec2d;
import jgl.math.vector.Vec2f;
import jgl.math.vector.Vec3f;

import com.jogamp.opengl.util.awt.TextRenderer;

import cspace.scene.PathFinder;
import cspace.scene.Scene;

public class PathFinderRenderer {

  private PathFinder    finder;
  private RobotRenderer robotRenderer;
  private Scene         scene;
  private TextRenderer  textRenderer;

  public PathFinderRenderer(Scene scene, RobotRenderer robotRenderer) {
    this.scene = scene;
    this.robotRenderer = robotRenderer;
    Font font = new Font(Font.SANS_SERIF, Font.BOLD, 14);
    textRenderer = new TextRenderer(font, true);
  }

  public void setPathFinder(PathFinder finder) {
    this.finder = finder;
  }
  
  public boolean isEnabled() {
    return finder != null;
  }

  public void draw(GL2 gl, Viewport viewport) {
    if (finder == null)
      return;

    String message = null;
    if (finder.getRobotStartPos() == null) {
      message = "Move robot to start configuration. Press ENTER to save or ESC to cancel.";
    } else {
      message = "Move robot to end configuration. Press ENTER to find path or ESC to cancel.";
    }
    
    if (finder.getRobotStartRot() == null) {
      Vec3f savedColor = scene.view.robot.color.copy();
      scene.view.robot.color.set(0.5f, 0.8f, 0.5f);
      robotRenderer.setHighlight(false);
      robotRenderer.draw(gl);
      scene.view.robot.color.set(savedColor);
    } else {
      Vec2d savedPos = scene.view.robot.position.copy();
      Vec2d savedRot = scene.view.robot.rotation.copy();
      Vec3f savedColor = scene.view.robot.color.copy();
      Vec2f savedAnglePt = robotRenderer.getAnglePt();
      boolean savedHighlight = robotRenderer.isHighlight();

      scene.view.robot.color.set(0.5f, 0.8f, 0.5f);
      scene.view.robot.position.set(finder.getRobotStartPos());
      scene.view.robot.rotation.set(finder.getRobotStartRot());

      robotRenderer.setAnglePt(null);
      robotRenderer.setHighlight(false);
      robotRenderer.draw(gl);
      robotRenderer.setAnglePt(savedAnglePt);

      scene.view.robot.color.set(0.9f, 0.5f, 0.5f);
      scene.view.robot.position.set(savedPos);
      scene.view.robot.rotation.set(savedRot);
      robotRenderer.draw(gl);
      
      gl.glColor3f(0.5f, 0.5f, 0.5f);
      gl.glBegin(GL2.GL_LINES);
      gl.glVertex2d(finder.getRobotStartPos().x, finder.getRobotStartPos().y);
      gl.glVertex2d(savedPos.x, savedPos.y);
      gl.glEnd();
      
      scene.view.robot.color.set(savedColor);
      robotRenderer.setHighlight(savedHighlight);
    }
    
    gl.glMatrixMode(GL2.GL_PROJECTION);
    gl.glPushMatrix();
    gl.glLoadIdentity();
    gl.glOrthof(0, viewport.width, 0, viewport.height, -1, 1);
    gl.glMatrixMode(GL2.GL_MODELVIEW);
    gl.glPushMatrix();
    gl.glLoadIdentity();
    
    Vec3f c = new Vec3f(1).minus(scene.view.renderer.background);
    textRenderer.beginRendering(viewport.width, viewport.height);
    textRenderer.setColor(c.x, c.y, c.z, 1);
    textRenderer.draw(message, 20, 20);
    textRenderer.endRendering();
    
    gl.glMatrixMode(GL2.GL_PROJECTION);
    gl.glPopMatrix();
    gl.glMatrixMode(GL2.GL_MODELVIEW);
    gl.glPopMatrix();
  }
}
