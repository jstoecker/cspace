package cspace.scene;

import jgl.math.vector.ConstVec2d;
import jgl.math.vector.Vec2d;
import cspace.scene.ArcShape.Edge;


/**
 * Sum of an obstacle and robot edge.
 */
public class SumEE extends CSArc {
  
  /** Obstacle edge */
  public final Edge obsEdge;
  
  /** Robot edge */
  public final Edge robEdge;
  
  public SumEE(Edge obsEdge, Edge robEdge, CSPnt tail, CSPnt head, Event start, Event end,
      int index) {
    super(start, end, index, obsEdge.r + robEdge.r, head, tail);
    this.obsEdge = obsEdge;
    this.robEdge = robEdge;
  }

  @Override
  public Vec2d center(ConstVec2d u) {
    return obsEdge.c.plus(cspace.util.Math.complexProduct(u, robEdge.c));
  }
}
