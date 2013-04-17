package cspace.render3d;

import java.awt.Font;
import java.awt.Point;
import java.awt.geom.Rectangle2D;

import javax.media.opengl.GL;
import javax.media.opengl.GL2;

import jgl.core.Viewport;

import com.jogamp.opengl.util.awt.TextRenderer;

import cspace.scene.Scene;
import cspace.scene.Sub;

/**
 * Draws shapes/text for inspect purposes.
 * 
 * @author justin
 */
public class InspectRenderer {

  private Scene        scene;
  private boolean      enabled    = false;
  private Sub          hlSub      = null;
  private Sub.Triangle hlTriangle = null;
  private TextRenderer textRenderer;
  private Point        cursor     = null;

  public InspectRenderer(Scene scene) {
    this.scene = scene;
    Font font = new Font(Font.SANS_SERIF, Font.BOLD, 14);
    textRenderer = new TextRenderer(font, true);
  }

  public void setEnabled(boolean enabled) {
    this.enabled = enabled;
  }

  public void setHighlightedSub(Sub sub) {
    this.hlSub = sub;
  }

  public void setHighlightedTriangle(Sub.Triangle tri) {
    this.hlTriangle = tri;
  }

  public void setCursor(Point cursor) {
    this.cursor = cursor;
  }

  public void draw(GL2 gl, Viewport viewport) {
    if (!enabled)
      return;

    if (hlSub != null) {
      drawHighlightedSub(gl, viewport);
    }

    if (cursor != null)
      drawTooltip(gl, viewport);
  }

  private void drawHighlightedSub(GL2 gl, Viewport viewport) {
    gl.glEnable(GL2.GL_BLEND);
    gl.glBlendFunc(GL.GL_DST_COLOR, GL.GL_ONE);
    gl.glColor4f(1, 1, 1, 0.5f);
    gl.glBegin(GL2.GL_TRIANGLES);
    for (Sub.Triangle triangle : hlSub.triangles) {
      Sub.Vertex a = triangle.a;
      Sub.Vertex b = triangle.b;
      Sub.Vertex c = triangle.c;
      gl.glVertex3d(a.position.x, a.position.y, a.position.z);
      gl.glVertex3d(b.position.x, b.position.y, b.position.z);
      gl.glVertex3d(c.position.x, c.position.y, c.position.z);
    }
    gl.glEnd();
    gl.glDisable(GL2.GL_BLEND);
  }

  private void drawTooltip(GL2 gl, Viewport viewport) {
    gl.glMatrixMode(GL2.GL_PROJECTION);
    gl.glPushMatrix();
    gl.glLoadIdentity();
    System.out.println(viewport.x + " / " + viewport.width);
    gl.glOrtho(0, viewport.width, 0, viewport.height, -1, 1);
    gl.glMatrixMode(GL2.GL_MODELVIEW);
    gl.glLoadIdentity();

    String text = String.format("sub %d (robot %d / obstacle %d)", hlSub.index, hlSub.robEdge.index, hlSub.obsEdge.index);
    Rectangle2D textBounds = textRenderer.getBounds(text);
    double width = textBounds.getWidth();
    double height = textBounds.getHeight();
    double pad = 5;
    int cx = cursor.x - viewport.x;
    int cy = cursor.y - viewport.y;
    
    // draw background
    gl.glDisable(GL2.GL_DEPTH_TEST);
    gl.glEnable(GL2.GL_BLEND);
    gl.glBlendFunc(GL.GL_SRC_ALPHA, GL.GL_ONE_MINUS_SRC_ALPHA);
    gl.glColor4f(0, 0, 0, 0.7f);
    gl.glBegin(GL2.GL_QUADS);
    gl.glVertex2d(cx - pad, cy - pad);
    gl.glVertex2d(cx + pad + width, cy - pad);
    gl.glVertex2d(cx + pad + width, cy + pad + height);
    gl.glVertex2d(cx - pad, cy + pad + height);
    gl.glEnd();
    gl.glDisable(GL.GL_BLEND);

    gl.glColor4f(1, 1, 1, 1);
    gl.glLineWidth(2);
    gl.glBegin(GL2.GL_LINE_LOOP);
    gl.glVertex2d(cx - pad, cy - pad);
    gl.glVertex2d(cx + pad + width, cy - pad);
    gl.glVertex2d(cx + pad + width, cy + pad + height);
    gl.glVertex2d(cx - pad, cy + pad + height);
    gl.glEnd();
    gl.glLineWidth(1);

    // draw text
    textRenderer.beginRendering(viewport.width, viewport.height);
    textRenderer.setColor(1, 1, 1, 1);
    textRenderer.draw(text, cx, cy);
    textRenderer.endRendering();

    gl.glMatrixMode(GL2.GL_PROJECTION);
    gl.glPopMatrix();
    gl.glMatrixMode(GL2.GL_MODELVIEW);
    gl.glPopMatrix();
  }
}
