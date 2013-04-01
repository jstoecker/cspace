package cspace.scene3d;

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
import cspace.model.CSArc.Arc;
import cspace.model.CSpace;
import cspace.model.SumEE;
import cspace.visuals.RobotVisuals;
import cspace.visuals.SubVisuals;
import cspace.visuals.SumEEVisuals;

public class SumEEView {

  Program shader;
  CSpace cspace;
  SumEEVisuals visuals;
  SubVisuals subVisuals;
  RobotVisuals robotVisuals;
  Uniform uColor;
  Uniform uColorMode;
  int list = -1;

  public SumEEView(CSpace cspace, SumEEVisuals visuals, SubVisuals subVisuals,
      RobotVisuals robotVisuals) {
    this.cspace = cspace;
    this.visuals = visuals;
    this.subVisuals = subVisuals;
    this.robotVisuals = robotVisuals;
  }

  void init(GL2 gl) {
    
    shader = new Program();
    shader.attach(gl, ShaderLoader.load(gl, "/shaders/sum.vert", Shader.Type.VERTEX));
    shader.attach(gl, ShaderLoader.load(gl, "/shaders/sum.frag", Shader.Type.FRAGMENT));
    shader.link(gl);
    
    uColor = shader.uniform("edgeColor");
    uColorMode = shader.uniform("colorMode");
  }

  void delete(GL2 gl) {
    if (list != -1) {
      gl.glDeleteLists(list, 1);
      list = -1;
    }
  }

  public void draw(GL2 gl) {
    if (!visuals.isVisible3d()) {
      return;
    }

    if (visuals.isGeomUpdated3d() || list == -1) {
      update(gl);
      visuals.setGeomUpdated3d(false);
    }

    shader.bind(gl);
    uColorMode.set(gl, 1);
    uColor.set(gl, visuals.getColor());
//    gl.glEnable(GL.GL_BLEND);
    gl.glBlendFunc(GL.GL_SRC_ALPHA, GL.GL_ONE_MINUS_SRC_ALPHA);
    gl.glPolygonMode(GL.GL_FRONT_AND_BACK, GL2.GL_LINE);
    gl.glCallList(list);
    gl.glPolygonMode(GL.GL_FRONT_AND_BACK, GL2.GL_FILL);
    gl.glDisable(GL.GL_BLEND);
    shader.unbind(gl);
  }

  private void update(GL2 gl) {
    delete(gl);

    list = gl.glGenLists(1);
    gl.glNewList(list, GL2.GL_COMPILE);
    {
      for (int i = 0; i < visuals.getSelected().size(); i += 2) {
        int robEdge = visuals.getSelected().get(i);
        int obsEdge = visuals.getSelected().get(i + 1);

        List<SumEE> sees = cspace.getSumEEs(robEdge, obsEdge);
        
        if (sees != null) {
          for (SumEE see : sees) {

            Vec3f c = visuals.getColor(see.robEdge.index, see.obsEdge.index);
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
    gl.glEndList();

    visuals.setGeomUpdated(false);
  }

  // adds vertices for a single 2D arc at theta; returns # verts added
  private int addArc(List<Vec3d> verts, List<Vec3d> normals, SumEE see,
      double theta) {

    Vec2d u = new Vec2d(Math.cos(theta), Math.sin(theta));
    Arc arc = see.arc(u);
    Vec2d v = new Vec2d(arc.tailN);
    
    int numSegments =  (int) Math.max(4, Math.min(Math.abs(arc.angle / Math.acos(1 - .01 / Math.abs(arc.r))), 1000));
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
  private void triangulateArcs(List<int[]> triangles, List<Vec3d> verts,
      int sizeA, int sizeB) {
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
