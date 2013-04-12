package cspace.scene.triangulate;

import jgl.math.vector.Vec2d;
import cspace.scene.CSPnt;
import cspace.scene.Event;

/**
 * 3D sampling of CSpace. Each sub is triangulated using the samples along its
 * edges.
 */
public class Sample {

  public static int NUM_SAMPLES = 0;
  /**
   * Position
   */
  public final Vec2d p;
  /**
   * Theta
   */
  public final Vec2d u;
  /**
   * Event the sample occurs (or null if between events)
   */
  public final Event event;
  public final int index;

  private Sample(Vec2d p, Vec2d u, Event event) {
    this.p = p;
    this.u = u;
    this.event = event;
    this.index = NUM_SAMPLES++;
  }

  public Sample(Vec2d p, Vec2d u) {
    this(p, u, null);
  }

  public Sample(Vec2d p, Event event) {
    this(p, event.u, event);
  }

  public Sample(CSPnt pnt, Event event) {
    this(event.p, event.u, event);
  }

  @Override
  public String toString() {
    return (event == null) ? "-" : event.index + "";
  }

  public boolean equals(Vec2d p, Vec2d u) {
    return p.x == this.p.x
            && p.y == this.p.y
            && u.x == this.u.x
            && u.y == this.u.y;
  }
 
  @Override
  public boolean equals(Object obj) {
    if (obj instanceof Sample) {
      return index == ((Sample) obj).index;
    }
    return false;
  }

  @Override
  public int hashCode() {
    int hash = 7;
    hash = 37 * hash + this.index;
    return hash;
  }
}
