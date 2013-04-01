package cspace.util;

import jgl.math.vector.ConstVec2d;
import jgl.math.vector.Vec2d;

public class Math {

  public static Vec2d complexProduct(ConstVec2d a, ConstVec2d b) {
    return new Vec2d(a.x() * b.x() - a.y() * b.y(), a.x() * b.y() + a.y() * b.x());
  }
}
