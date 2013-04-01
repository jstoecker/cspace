package cspace.model;

import jgl.math.vector.Vec2d;

/**
 * A shape constructed of vertices and arcs.
 */
public class ArcShape {

  /** All vertices */
  public Vertex[] v;

  /** All edges */
  public Edge[]   e;

  ArcShape() {
  }

  /**
   * A corner of the shape.
   */
  public class Vertex {

    /** Position */
    public final Vec2d p;

    /** Normal */
    public final Vec2d n;
    
    /** Index of the vertex in the object */
    public final int index;

    Vertex(Vec2d p, Vec2d n, int index) {
      this.p = p;
      this.n = n;
      this.index = index;
    }
  }

  /**
   * An arc that joints two vertices on the boundary of the shape.
   */
  public class Edge {

    /** Center */
    public final Vec2d  c;

    /** Radius */
    public final double r;

    /** Second vertex */
    public final Vertex head;

    /** First vertex */
    public final Vertex tail;

    /** Second angle */
    public final double headAngle;

    /** First angle */
    public final double tailAngle;
    
    /** Index of the edge in the object */
    public final int index;

    Edge(Vec2d c, double r, Vertex head, Vertex tail, int index) {
      this.c = c;
      this.r = r;
      this.head = head;
      this.tail = tail;
      this.index = index;

      tailAngle = Math.atan2(tail.n.y, tail.n.x);

      double headAngle = Math.atan2(head.n.y, head.n.x);
      if (tail.n.cross(head.n) > 0) {
        if (headAngle - tailAngle < 0) headAngle += Math.PI * 2.0;
      } else {
        if (headAngle - tailAngle > 0) headAngle -= Math.PI * 2.0;
      }
      this.headAngle = headAngle;
    }
  }
}
