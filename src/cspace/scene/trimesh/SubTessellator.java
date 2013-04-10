package cspace.scene.trimesh;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import jgl.math.vector.Vec2d;
import cspace.scene.SumEV;
import cspace.scene.SumVE;

/**
 * Takes a sampled sub and converts it to vertices and triangles.
 * 
 * @author justin
 */
public class SubTessellator {

  private SampledSub ssub;
  Set<Triangle> triangles;
  Set<Edge> edges;
  List<Vertex> verts;
  List<Vertex> outer;
  Map<Sample, Vertex> sampleVerts;

  static final int DEBUG_SUB = -1;

  public SubTessellator(SampledSub ssub) {
    this.ssub = ssub;
  }

  public void triangulate() {
    
    triangles = new HashSet<Triangle>();
    edges = new HashSet<Edge>();
    outer = new ArrayList<Vertex>(ssub.outerLoop.size());
    verts = new ArrayList<Vertex>();
    sampleVerts = new HashMap<Sample, Vertex>();
    List<Edge> potentialEdges = new ArrayList<Edge>();

    // convert samples to vertices
    for (List<Sample> l : ssub.innerSamples) {
      for (Sample s : l) {
        Vertex v = new Vertex(s, false, false, false, false);
        sampleVerts.put(s, v);
        verts.add(v);
      }
    }
    int si = 0;
    int sj = ssub.startSamples.size() - 1;
    int sk = sj + ssub.tailSamples.size() - 1;
    int sl = sk + ssub.endSamples.size() - 1;
    for (Sample s : ssub.outerLoop) {
      // the outerLoop is concatenation of start, tail, end, head (in order)
      // without duplicating shared vertices
      boolean onStart = (si <= sj);
      boolean onTail = (si >= sj && si <= sk);
      boolean onEnd = (si >= sk && si <= sl);
      boolean onHead = (si >= sl || si == 0);
      Vertex v = new Vertex(s, onHead, onTail, onStart, onEnd);

      // push top/bottom for triangulation purposes only to avoid precision
      // issues
      if (v.onStart)
        v.y -= 10e-10;
      if (v.onEnd)
        v.y += 10e-10;

      sampleVerts.put(s, v);
      outer.add(v);
      verts.add(v);
      si++;
    }

    if (ssub.sub.index == DEBUG_SUB) {
      System.out.println("num verts: " + ssub.outerLoop.size() + " + " + verts.size());
      System.out.println("Vertices (index, x, y):");
      for (Vertex v : verts) {
        System.out.printf("addVert(%.17f, %.17f);\n", v.x, v.y);
      }
      System.out.println();
    }

    // add all outer perimeter edges
    for (int i = 0; i < outer.size(); i++) {
      addEdge(new Edge(outer.get(i), outer.get((i + 1) % outer.size())));
    }

    // add potential edges for outer<->outer verts
    boolean straightTail = ssub.sub.tail instanceof SumVE
        || ssub.sub.tail instanceof SumEV;
    boolean straightHead = ssub.sub.head instanceof SumVE
        || ssub.sub.head instanceof SumEV;
    for (int i = 0; i < outer.size(); i++) {
      Vertex a = outer.get(i);
      Vertex b = outer.get((i + 1) % outer.size());
      Vertex c = outer.get((i + 2) % outer.size());
      Vec2d ba = a.minus(b).normalize();
      Vec2d bc = c.minus(b).normalize();

      for (int j = 0; j < outer.size() - 3; j++) {
        Vertex d = outer.get((i + 3 + j) % outer.size());

        // the start & end are straight in 2D; tail & head are straight if they
        // are SEV or SVEs
        if ((b.onStart && d.onStart) || (b.onEnd && d.onEnd)
            || (straightTail && b.onTail && d.onTail)
            || (straightHead && b.onHead && d.onHead)) {
          continue;
        }

        Edge e = new Edge(b, d);
        Vec2d bd = d.minus(b).normalize();

        boolean sticksOut = false;
        boolean concaveB = ba.cross(bc) > 0;
        if (concaveB) {
          sticksOut = ba.cross(bd) > 0 && bd.cross(bc) > 0;
        } else {
          sticksOut = ba.cross(bd) > 0 || bd.cross(bc) > 0;
        }
        
        double t = 0.001;
        boolean smallAngle = false;
        if (!sticksOut) {
          smallAngle = bc.cross(bd) < t || bd.cross(ba) < t;
        }
        
        // if it passes the test, make sure it doesn't cross any perim. edge
        boolean crossing = false;
        if (!sticksOut) {
          for (Edge e2 : edges) {
            if (e.intersects(e2)) {
              crossing = true;
              break;
            }
          }
        }

        if (!sticksOut && !crossing && !smallAngle) {
          if (ssub.sub.index == DEBUG_SUB)
            System.out.printf("POTENTIAL: %s (a=%d, b=%d, c=%d, d=%d)\n", e,
                a.index, b.index, c.index, d.index);
          potentialEdges.add(e);
        } else {
          if (ssub.sub.index == DEBUG_SUB)
            System.out.printf("BAD:       %s (a=%d, b=%d, c=%d, d=%d) : %s\n",
                e, a.index, b.index, c.index, d.index,
                (sticksOut ? "sticks out" : "crossing"));
        }
      }
    }
    
    // add potential edges for outer<->inner and inner<->inner
    for (int i = 0; i < verts.size(); i++) {
      Vertex a = verts.get(i);
      for (int j = i + 1; j < verts.size(); j++) {
        Vertex b = verts.get(j);
        // all potential outer<->outer edges have been added already
        if (!(a.onOuter && b.onOuter)) {
          Edge e = new Edge(a, b);
          if (!edges.contains(e)) {
            potentialEdges.add(e);
          }
        }
      }
    }

    // sort potential edges by length
    Collections.sort(potentialEdges, new EdgeLengthComparator());

    // add potential edges that don't cross added edges
    int maxEdges = 2 * outer.size() + 3 * (verts.size() - outer.size()) - 3;
    if (edges.size() < maxEdges) {
      for (Edge e : potentialEdges) {

        boolean crossing = false;
        for (Edge e2 : edges) {
          if (e.intersects(e2)) {
            crossing = true;
            break;
          }
        }

        if (!crossing) {
          addEdge(e);
          if (edges.size() == maxEdges) {
            break;
          }
        }
      }
    }

    // build triangles using added edges
    for (Vertex a : verts) {
      Collections.sort(a.edges, new EdgeAngleComparator(a));
      for (int i = 0; i < a.edges.size() - 1; i++) {
        Edge ab = a.edges.get(i);
        Edge ac = a.edges.get((i + 1) % a.edges.size());
        Vertex b = ab.getOther(a);
        Vertex c = ac.getOther(a);
        Edge bc = b.edgeTo(c);

        if (bc != null) {
          triangles.add(new Triangle(a, b, c));
        }
      }
    }

//    Set<Triangle> badTris = new HashSet<Triangle>();
//    for (Triangle t : triangles) {
//      if ((t.a.onTail && t.b.onTail && t.c.onTail)
//          || (t.a.onHead && t.b.onHead && t.c.onHead)) {
//        Vec2d ab = t.b.minus(t.a);
//        Vec2d ac = t.c.minus(t.a);
//        if (Math.abs(ac.cross(ab)) / 2 < 0.0000001) {
//          badTris.add(t);
//        }
//      }
//    }
//    triangles.removeAll(badTris);
  }

  private void addEdge(Edge e) {
    if (edges.add(e)) {
      e.a.edges.add(e);
      e.b.edges.add(e);

      if (ssub.sub.index == DEBUG_SUB) {
        System.out.printf("addEdge(%d, %d);\n", e.a.index, e.b.index);
      }
    }
  }

  public static class Triangle {

    // ordered CCW
    final Vertex a, b, c;

    public Triangle(Vertex a, Vertex b, Vertex c) {
      this.b = b;
      if ((b.minus(a)).cross(c.minus(a)) < 0) {
        this.a = c;
        this.c = a;
      } else {
        this.a = a;
        this.c = c;
      }
    }

    @Override
    public boolean equals(Object obj) {
      if (!(obj instanceof Triangle)) {
        return false;
      }
      Triangle that = (Triangle) obj;
      return (this.a == that.a && this.b == that.b && this.c == that.c)
          || (this.a == that.b && this.b == that.c && this.c == that.a)
          || (this.a == that.c && this.b == that.a && this.c == that.b);
    }

    @Override
    public int hashCode() {
      return 51 * (a.hashCode() + b.hashCode() + c.hashCode());
    }
  }

  class Vertex extends Vec2d {

    final Sample sample;
    List<Edge> edges = new ArrayList<Edge>();
    final int index;
    final boolean onHead, onTail, onStart, onEnd, onOuter;

    public Vertex(Sample s, boolean onHead, boolean onTail, boolean onStart,
        boolean onEnd) {
      super(ssub.getVertex(s).alphaTheta);
      this.sample = s;
      index = verts.size();
      this.onOuter = onHead || onTail || onStart || onEnd;
      this.onHead = onHead;
      this.onTail = onTail;
      this.onStart = onStart;
      this.onEnd = onEnd;
    }

    Edge edgeTo(Vertex v) {
      for (Edge e : edges) {
        if (e.getOther(this) == v) {
          return e;
        }
      }
      return null;
    }

    @Override
    public boolean equals(Object obj) {
      if (!(obj instanceof Vertex)) {
        return false;
      }
      return this.index == ((Vertex) obj).index;
    }

    @Override
    public int hashCode() {
      return 469 * index;
    }

    @Override
    public String toString() {
      return String.format(
          "{%f, %f} onStart=%b, onTail=%b, onEnd=%b, onHead=%b", x, y, onStart,
          onTail, onEnd, onHead);
    }
  }

  private class Edge {

    final Vertex a, b;

    public Edge(Vertex a, Vertex b) {
      this.a = a;
      this.b = b;
    }

    double angle(Vertex v) {
      Vec2d u = getOther(v).minus(v);
      return Math.atan2(u.y, u.x);
    }

    Vertex getOther(Vertex a) {
      return (a == this.a) ? this.b : this.a;
    }

    public double lengthSquared() {
      return b.minus(a).lengthSquared();
    }

    @Override
    public String toString() {
      return String.format("(%d, %d)", a.index, b.index);
    }

    public boolean intersects(Edge that) {
      double[] x = { this.a.x(), this.b.x(), that.a.x(), that.b.x() };
      double[] y = { this.a.y(), this.b.y(), that.a.y(), that.b.y() };
      double d = (y[3] - y[2]) * (x[1] - x[0]) - (x[3] - x[2]) * (y[1] - y[0]);
      double uan = (x[3] - x[2]) * (y[0] - y[2]) - (y[3] - y[2])
          * (x[0] - x[2]);
      double ubn = (x[1] - x[0]) * (y[0] - y[2]) - (y[1] - y[0])
          * (x[0] - x[2]);

      // if d, uan, and ubn are all 0 the edges are collinear
      if (d == 0 && uan == 0 && ubn == 0) {
        Vertex A = this.a;
        Vertex B = this.b;
        Vertex C = that.a;
        Vertex D = that.b;
        Vec2d AB = B.minus(A);
        Vec2d CD = D.minus(C);
        if (AB.dot(CD) < 0) {
          CD.multiply(-1);
          Vertex temp = C;
          C = D;
          D = temp;
        }

        if (B.x == C.x && B.y == C.y) {
          return false;
        } else if (A.x == D.x && A.y == D.y) {
          return false;
        } else if (C.minus(B).dot(AB) > 0) {
          return false;
        } else if (A.minus(D).dot(AB) > 0) {
          return false;
        }

        return true;
      }

      // if two edges share a vertex and don't overlap they can't intersect
      if ((this.a.index == that.a.index && this.b.index != that.b.index)
          || (this.a.index == that.b.index && this.b.index != that.a.index)
          || (this.b.index == that.a.index && this.a.index != that.b.index)
          || (this.b.index == that.b.index && this.a.index != that.a.index)) {
        return false;
      }

      double ua = uan / d;
      if (ua <= 0 || ua >= 1) {
        return false;
      }

      double ub = ubn / d;
      if (ub <= 0 || ub >= 1) {
        return false;
      }

      return true;
    }

    @Override
    public boolean equals(Object that) {
      if (that instanceof Edge) {
        Edge thatEdge = (Edge) that;
        return (a == thatEdge.a && b == thatEdge.b)
            || (a == thatEdge.b && b == thatEdge.a);
      }
      return false;
    }

    @Override
    public int hashCode() {
      return 679 * (a.hashCode() + b.hashCode());
    }
  }

  private class EdgeAngleComparator implements Comparator<Edge> {
    Vertex v;

    public EdgeAngleComparator(Vertex v) {
      this.v = v;
    }

    public int compare(Edge e1, Edge e2) {
      double a1 = e1.angle(v);
      double a2 = e2.angle(v);
      return (a1 < a2 ? -1 : (a1 > a2 ? 1 : 0));
    }
  }

  private class EdgeLengthComparator implements Comparator<Edge> {
    public int compare(Edge e1, Edge e2) {
      double l1 = e1.lengthSquared();
      double l2 = e2.lengthSquared();
      return (l1 < l2 ? -1 : (l1 > l2 ? 1 : 0));
    }
  }
}
