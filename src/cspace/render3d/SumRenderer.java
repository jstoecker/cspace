package cspace.render3d;

import static cspace.util.Math.complexProduct;

import java.util.ArrayList;
import java.util.List;

import javax.media.opengl.GL;
import javax.media.opengl.GL2;

import jgl.core.Program;
import jgl.core.Shader;
import jgl.core.Uniform;
import jgl.loaders.ShaderLoader;
import jgl.math.vector.Vec2d;
import jgl.math.vector.Vec3d;
import jgl.math.vector.Vec3f;
import cspace.scene.CSArc.Arc;
import cspace.scene.EdgePair;
import cspace.scene.Scene;
import cspace.scene.SumEE;
import cspace.util.CachedRenderer;

/**
 * Draws 3D sums. Not really useful except for debugging.
 * 
 * @author justin
 */
public class SumRenderer extends CachedRenderer {

  private Scene   scene;
  private Program shader;
  private Uniform uColor;
  private Uniform uColorMode;
  int             list = -1;

  public SumRenderer(Scene scene) {
    this.scene = scene;
  }

  void init(GL2 gl) {
    shader = new Program();
    shader.attach(gl, ShaderLoader.load(gl, "/shaders/sum.vert", Shader.Type.VERTEX));
    shader.attach(gl, ShaderLoader.load(gl, "/shaders/sum.frag", Shader.Type.FRAGMENT));
    shader.link(gl);

    uColor = shader.uniform("edgeColor");
    uColorMode = shader.uniform("colorMode");
  }

  @Override
  protected boolean isVisible() {
    return (scene.view.sums.visible3d && !scene.view.sums.drawn3d.isEmpty());
  }

  @Override
  protected void beginDraw(GL2 gl) {
    shader.bind(gl);
    uColorMode.set(gl, 1);
    uColor.set(gl, scene.view.sums.color);
    gl.glPolygonMode(GL.GL_FRONT_AND_BACK, GL2.GL_LINE);
  }

  @Override
  protected void endDraw(GL2 gl) {
    gl.glPolygonMode(GL.GL_FRONT_AND_BACK, GL2.GL_FILL);
    gl.glDisable(GL.GL_BLEND);
    shader.unbind(gl);
  }

  @Override
  protected void updateGeometry(GL2 gl) {

    for (EdgePair pair : scene.view.sums.drawn3d) {
      int robEdge = pair.robEdge;
      int obsEdge = pair.obsEdge;

      List<SumEE> sees = scene.cspace.getSumEEs(robEdge, obsEdge);

      if (sees != null) {
        for (SumEE see : sees) {

          Vec3f c = scene.view.sums.getColor(pair);
          gl.glColor3f(c.x, c.y, c.z);

          List<Vec3d> verts = new ArrayList<Vec3d>();
          List<Vec3d> normals = new ArrayList<Vec3d>();
          List<int[]> triangles = new ArrayList<int[]>();
          int numArcs = (int) (see.angle * 45 / Math.PI);
          double theta = see.startAngle;
          double thetaStep = see.angle / numArcs;

          // calculate triangles
          int sizeA = addArc(verts, normals, see, theta);
          for (int j = 1; j <= numArcs; j++) {
            theta += thetaStep;
            int sizeB = addArc(verts, normals, see, theta);
            triangulateArcs(triangles, verts, sizeA, sizeB);
            sizeA = sizeB;
          }

          // draw triangles
          for (int[] t : triangles) {
            gl.glBegin(GL2.GL_TRIANGLES);

            gl.glNormal3dv(normals.get(t[0]).toArray(), 0);
            gl.glVertex3dv(verts.get(t[0]).toArray(), 0);

            gl.glNormal3dv(normals.get(t[1]).toArray(), 0);
            gl.glVertex3dv(verts.get(t[1]).toArray(), 0);

            gl.glNormal3dv(normals.get(t[2]).toArray(), 0);
            gl.glVertex3dv(verts.get(t[2]).toArray(), 0);
            gl.glEnd();
          }
        }
      }
    }
  }

  // adds vertices for a single 2D arc at theta; returns # verts added
  private int addArc(List<Vec3d> verts, List<Vec3d> normals, SumEE see, double theta) {

    Vec2d u = new Vec2d(Math.cos(theta), Math.sin(theta));
    Arc arc = see.arc(u);
    Vec2d v = new Vec2d(arc.tailN);

    int numSegments = (int) Math.max(4,
        Math.min(Math.abs(arc.angle / Math.acos(1 - .01 / Math.abs(arc.r))), 1000));
    double alphaStep = arc.angle / numSegments;
    double c = Math.cos(alphaStep);
    double s = Math.sin(alphaStep);

    for (int i = 0; i <= numSegments; i++) {
      verts.add(new Vec3d(arc.c.plus(v.times(arc.r)), theta));

      Vec3d pa = new Vec3d(-v.y, v.x, 0);

      Vec3d pt = new Vec3d(complexProduct(new Vec2d(-u.y, u.x), see.robEdge.c), 1);
      normals.add(pt.cross(pa).normalize());

      // rotate
      double vx = v.x;
      v.x = c * vx - s * v.y;
      v.y = s * vx + c * v.y;
    }

    return numSegments + 1;
  }

  /** Adds triangles between verts in A and B */
  private void triangulateArcs(List<int[]> triangles, List<Vec3d> verts, int sizeA, int sizeB) {
    int startA = verts.size() - sizeA - sizeB;
    int startB = verts.size() - sizeB;

    // ensure arc A is the one with fewer verts
    if (sizeA > sizeB) {
      int temp = sizeA;
      sizeA = sizeB;
      sizeB = temp;
      temp = startA;
      startA = startB;
      startB = temp;
    }

    List<Vec3d> A = verts.subList(startA, startA + sizeA);
    List<Vec3d> B = verts.subList(startB, startB + sizeB);

    int i = 0;
    int j = 0;
    while (j < B.size()) {
      if (i < A.size() - 1 && dist(A, B, i, j) > dist(A, B, i + 1, j)) {
        triangles.add(new int[] { i + startA, j + startB, i + 1 + startA });
        i++;
      }
      if (j < B.size() - 1) {
        triangles.add(new int[] { i + startA, j + startB, j + 1 + startB });
      }
      j++;
    }

    while (i < A.size() - 1) {
      triangles.add(new int[] { i + startA, j - 1 + startB, i + 1 + startA });
      i++;
    }
  }

  private static double dist(List<Vec3d> A, List<Vec3d> B, int i, int j) {
    return A.get(i).minus(B.get(j)).lengthSquared();
  }
}
