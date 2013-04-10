package cspace.scene;

import java.util.ArrayList;
import java.util.List;

import jgl.math.vector.Vec2d;

/**
 * Time in theta for when changes occur to some geometry.
 */
public class Event {

  /** Theta */
  public final Vec2d     u;

  /** Position */
  public final Vec2d     p;

  /** Index from the list of events */
  public final int       index;

  /** All subs that start at this event */
  public final List<Sub> startSubs = new ArrayList<Sub>();

  /** All subs that end at this event */
  public final List<Sub> endSubs   = new ArrayList<Sub>();

  public Event(Vec2d u, Vec2d p, int index) {
    this.u = u;
    this.p = p;
    this.index = index;
  }
  
  @Override
  public boolean equals(Object obj) {
    if (obj == null)
      return false;
    if (!(obj instanceof Event))
      return false;
    Event that = (Event)obj;
    return index == that.index;
  }
}
