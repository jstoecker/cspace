package cspace.scene;

import cspace.scene.ArcShape.Edge;
import cspace.scene.ArcShape.Vertex;
import cspace.util.Math;
import jgl.math.vector.ConstVec2d;
import jgl.math.vector.Vec2d;

/**
 * Sum of an obstacle edge and robot vertex
 */
public class SumEV extends CSPnt {

  /** Obstacle edge */
  public final Edge   edge;

  /** Robot vertex */
  public final Vertex vert;

  public SumEV(Vertex vert, Edge edge, Event start, Event end, int index) {
    super(start, end, index);
    this.vert = vert;
    this.edge = edge;
  }

  @Override
  public Vec2d position(ConstVec2d u) {
    return edge.c.plus(Math.complexProduct(u, vert.n).times(edge.r)).plus(Math.complexProduct(u, vert.p));
  }
  
  @Override
  public Vec2d normal(ConstVec2d u, ConstVec2d c, double r) {
    return Math.complexProduct(u, vert.n);
  }
}