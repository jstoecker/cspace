package cspace.scene;

import jgl.math.vector.ConstVec2d;
import jgl.math.vector.Vec2d;
import cspace.scene.ArcShape.Edge;

/**
 * Intersection of two SumEEs.
 */
public class Intn extends CSPnt {

  /** Obstacle edges for both SumEEs */
  public final Edge[] eO;

  /** Robot edges for both SumEEs */
  public final Edge[] eR;

  public Intn(Edge[] eObstacle, Edge[] eRobot, Event start, Event end, int index) {
    super(start, end, index);
    eO = eObstacle;
    eR = eRobot;
  }

  @Override
  public Vec2d position(ConstVec2d q) {
    
    Vec2d[] c = { eO[0].c.plus(cspace.util.Math.complexProduct(q, eR[0].c)), eO[1].c.plus(cspace.util.Math.complexProduct(q, eR[1].c)) };
    double[] s = { eO[0].r + eR[0].r, eO[1].r + eR[1].r };

    double[] r = { Math.abs(s[0]), Math.abs(s[1]) };

    int i0 = (r[0] > r[1]) ? 1 : 0;
    int i1 = (i0 == 1) ? 0 : 1;
    Vec2d cc = c[i1].minus(c[i0]);
    double d = cc.length();
    double x;

    if (r[i0] < d) {
      double e = d - r[i1];
      double e1 = (r[i0] * r[i0] - e * e) / d / 2;
      x = e + e1;
    } else {
      double f = r[i1] - r[i0];
      if (f > d) {
        f = d;
      }
      double f1 = (d * d - f * f) / d / 2;
      double f2 = f / d;
      double f3 = r[i0] * f2;
      x = f1 - f3;
    }

    double y2 = r[i0] * r[i0] - x * x;
    double y = (y2 < 0) ? 0 : Math.sqrt(y2);

    Vec2d u = cc.over(d);
    Vec2d v = u.rotatedDegrees90();
    Vec2d[] p = new Vec2d[2];
    p[i0] = c[i0].plus(u.times(x)).plus(v.times(y));
    p[i1] = c[i0].plus(u.times(x)).minus(v.times(y));
    
    Vec2d pOut = ((s[0]) > 0 == (s[1] > 0)) ? p[0] : p[1];
    return pOut;
  }
  
  static int bad = 0;

  @Override
  public Vec2d normal(ConstVec2d u, ConstVec2d c, double r) {
    return position(u).minus(c).over(r);
  }
}
