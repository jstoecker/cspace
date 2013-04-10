package cspace.render2d;

import javax.media.opengl.GL2;

import jgl.math.vector.Vec3f;
import jgl.math.vector.Vec4f;
import cspace.scene.ArcShape;
import cspace.scene.visuals.ArcShapeVisuals;

/**
 * 2D drawing of an ArcShape.
 */
public abstract class ArcShapeView {

  final ArcShape shape;
  ArcShapeVisuals arcShapeVisuals;
  int list = -1;

  public ArcShapeView(ArcShape shape, ArcShapeVisuals visuals) {
    this.shape = shape;
    this.arcShapeVisuals = visuals;
  }

  public void draw(GL2 gl) {
    if (!arcShapeVisuals.isVisible2d()) {
      return;
    }

    if (list == -1 || arcShapeVisuals.isGeomUpdated2d()) {
      update(gl);
    }

    drawBorder(gl);

    if (arcShapeVisuals.isDrawOrigin()) {
      gl.glBegin(GL2.GL_LINES);
      gl.glVertex2f(-0.3f, 0);
      gl.glVertex2f(0.3f, 0);
      gl.glVertex2f(0, -0.3f);
      gl.glVertex2f(0, 0.3f);
      gl.glEnd();
    }
  }

  protected void drawInterior(GL2 gl) {
    Vec4f color = arcShapeVisuals.getFillColor();
    gl.glColor4f(color.x, color.y, color.z, color.w);
    gl.glCallList(list + 1);
  }

  protected void drawBorder(GL2 gl) {
    Vec3f color = arcShapeVisuals.getColor();
    gl.glColor3f(color.x, color.y, color.z);
    gl.glCallList(list);
  }

  private void update(GL2 gl) {
    delete(gl);

    list = gl.glGenLists(2);
    gl.glNewList(list, GL2.GL_COMPILE);
    {
      for (int i = 0; i < shape.e.length; i++)
        new ArcTriStrip(shape.e[i]).draw(gl, arcShapeVisuals.getScaledWidth2d(), arcShapeVisuals.getSmooth2d());
    }
    gl.glEndList();

    arcShapeVisuals.setGeomUpdated2d(false);
  }

  public void delete(GL2 gl) {
    if (list != -1) {
      gl.glDeleteLists(list, 2);
      list = -1;
    }
  }
}