package cspace.model;

import cspace.util.Math;
import jgl.math.vector.ConstVec2d;
import jgl.math.vector.Vec2d;
import cspace.model.ArcShape.Edge;
import cspace.model.ArcShape.Vertex;

/**
 * Sum of an obstacle vertex and robot edge.
 */
public class SumVE extends CSPnt {
  
  /** Obstacle vertex */
  public final Vertex vert;

  /** Robot edge */
  public final Edge   edge;

  public SumVE(Vertex vert, Edge edge, Event start, Event end, int index) {
    super(start, end, index);
    this.vert = vert;
    this.edge = edge;
  }

  @Override
  public Vec2d position(ConstVec2d u) {
    return vert.p.plus(Math.complexProduct(u, edge.c)).plus(vert.n.times(edge.r));
  }

  @Override
  public Vec2d normal(ConstVec2d u, ConstVec2d c, double r) {
    return vert.n;
  }
}