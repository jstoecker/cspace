package cspace.model;

import jgl.math.vector.ConstVec2d;
import jgl.math.vector.Vec2d;

/**
 * Geometry that exists as a point cross-section for a specific theta.
 */
public abstract class CSPnt extends CSpacePart {

  public CSPnt(Event start, Event end, int index) {
    super(start, end, index);
  }

  /** Calculate position at theta = u */
  public abstract Vec2d position(ConstVec2d u);

  /** Calculate normal at theta = u and an arc with center c and radius r */
  public abstract Vec2d normal(ConstVec2d u, ConstVec2d c, double r);
}
