package cspace.scene;

import jgl.math.vector.ConstVec2d;
import jgl.math.vector.Vec2d;

/**
 * Geometry that exists as an arc cross section for a given theta.
 */
public abstract class CSArc extends CSpacePart {

  /** Radius */
  public final double r;

  /** Head point geometry */
  public final Contact  head;

  /** Tail point geometry */
  public final Contact  tail;

  public CSArc(Event start, Event end, int index, double r, Contact head, Contact tail) {
    super(start, end, index);
    this.r = r;
    this.head = head;
    this.tail = tail;
  }

  public Arc arc(ConstVec2d u) {
    ConstVec2d c = center(u);
    Vec2d tn = tail.normal(u, c, r);
    Vec2d hn = head.normal(u, c, r);
    return new Arc(c, r, tn, hn);
  }

  public abstract Vec2d center(ConstVec2d u);

  /**
   * An arc cross section.
   */
  public class Arc {
    /** Center */
    public final ConstVec2d c;

    /** Radius */
    public final double r;
    
    /** Head normal */
    public final ConstVec2d tailN;
    
    /** Tail normal */
    public final ConstVec2d headN;

    /** Tail angle in radians */
    public final double tailAngle;

    /** Head angle in radians */
    public final double headAngle;

    /** The difference of the tail & head angles */
    public final double angle;

    Arc(ConstVec2d c, double r, ConstVec2d tn, ConstVec2d hn) {
      double tAngle = Math.atan2(tn.y(), tn.x());
      double hAngle = Math.atan2(hn.y(), hn.x());
      if (tn.cross(hn) > 0) {
        if (hAngle - tAngle < 0)
          hAngle += Math.PI * 2;
      } else {
        if (hAngle - tAngle > 0)
          hAngle -= Math.PI * 2;
      }
      
      this.c = c;
      this.r = r;
      this.tailN = tn;
      this.headN = hn;
      tailAngle = tAngle;
      headAngle = hAngle;
      this.angle = headAngle - tailAngle;
    }
  }
}
