package cspace.scene;

import java.util.ArrayList;
import java.util.List;

import jgl.math.vector.ConstVec2d;
import jgl.math.vector.Vec2d;

/**
 * Geometry that exists as a point cross-section for a specific theta. This includes intersections
 * of two sum edges (SumEE), sum of an obstacle vertex & robot edge (SumVE), and sum of an obstacle
 * edge & robot vertex (SumEV).
 */
public abstract class Contact extends CSpacePart {

  public List<Sample> samples = new ArrayList<Sample>();

  public Contact(Event start, Event end, int index) {
    super(start, end, index);
  }

  /** Calculate position at theta = u */
  public abstract Vec2d position(ConstVec2d u);

  /** Calculate normal at theta = u and an arc with center c and radius r */
  public abstract Vec2d normal(ConstVec2d u, ConstVec2d c, double r);

  /** Add samples at the start and end events of a sub whose tail/head is this contact. */
  void sampleAtSubEvents(Sub sub) {
    // add samples at the start and end of the contact if they haven't been added yet
    if (samples.isEmpty()) {
      samples.add(new Sample(this, start));
      samples.add(new Sample(this, end));
    }
    sampleSubEvt(sub, true);
    sampleSubEvt(sub, false);
  }

  private void sampleSubEvt(Sub sub, boolean start) {
    Vec2d p;
    Event evt;

    // subs that end/start at a point should replace
    // the sample on both Pnts with a single sample
    // at the event position
    boolean replace = false;
    if (start) {
      evt = sub.start;
      if (sub.startHead == null && sub.startTail == null) {
        p = evt.p;
        replace = true;
      } else {
        p = position(evt.u);
      }
    } else {
      evt = sub.end;
      if (sub.endHead == null && sub.endTail == null) {
        p = evt.p;
        replace = true;
      } else {
        p = position(evt.u);
      }
    }

    // reuse a sample if it has the exact same p, u as the pending sample
    boolean added = false;
    for (int i = 0; !added && i < evt.samples.size(); i++) {
      Sample other = evt.samples.get(i);
      if (other.equals(p, evt.u)) {
        addSorted(other, replace);
        added = true;
      }
    }

    // if this sample hasn't been seen, create a new sample
    if (!added) {
      Sample s = new Sample(p, evt);
      if (addSorted(s, replace)) {
        evt.samples.add(s);
      }
    }
  }

  private boolean addSorted(Sample sample, boolean replace) {
    int x = sample.event.index;
    for (int i = 0; i < samples.size() - 1; i++) {
      int a = samples.get(i).event.index;
      int b = samples.get(i + 1).event.index;

      if (replace) {
        // a sample already exists for the event, but replace it
        if (x == a) {
          samples.set(i, sample);
        } else if (x == b) {
          samples.set(i + 1, sample);
        }
      } else {
        // a sample already exists for the event, don't replace
        if (x == a || x == b) {
          return false;
        }
      }

      if (a < b) {
        if (a < x && x < b) {
          samples.add(i + 1, sample);
          return true;
        }
      } else {
        if (a < x || x < b) {
          samples.add(i + 1, sample);
          return true;
        }
      }
    }

    // if sample wasn't inserted, append it
    samples.add(sample);
    return true;
  }

  void sampleInner(double thresholdSquared) {
    int i = 0;
    int depth = 0;
    int j = 1;
    int originalSize = samples.size();
    int prevSize = originalSize;
    for (int pair = 0; pair < originalSize - 1; pair++) {
      samplePnt(i, j, thresholdSquared, depth);
      int added = samples.size() - prevSize;
      prevSize = samples.size();
      i = j + added;
      j = i + 1;
    }
  }

  void samplePnt(int left, int right, double thresholdSquared, int depth) {
    if (depth > 10)
      return;
    Sample a = samples.get(left);
    Sample b = samples.get(right);
    
    // if the angle between samples is really tiny, don't bother sampling
    // between them
    if (a.u.minus(b.u).length() < 0.00001) {
      return;
    }

    Vec2d avgU = a.u.plus(b.u).normalize();
    Vec2d midP = position(avgU);
    Vec2d avgP = a.p.plus(b.p).over(2);

    if (midP.minus(avgP).lengthSquared() > thresholdSquared) {
      Sample newS = new Sample(midP, avgU);
      samples.add(right, newS);
      int prevSize = samples.size();
      samplePnt(left, right, thresholdSquared, depth + 1);
      int added = samples.size() - prevSize;
      right += added;
      samplePnt(right, right + 1, thresholdSquared, depth + 1);
    }
  }

  public Sample findSample(Event evt) {
    for (Sample s : samples) {
      if (s.event != null && s.event.index == evt.index) {
        return s;
      }
    }
    return null;
  }
}
