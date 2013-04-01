package cspace.sampling;

import cspace.model.CSPnt;
import cspace.model.Event;
import cspace.model.Sub;
import java.util.ArrayList;
import java.util.List;
import jgl.math.vector.Vec2d;

public class SampledPnt {

  public final CSPnt pnt;
  public List<Sample> samples = new ArrayList<Sample>();

  public SampledPnt(CSPnt pnt) {
    this.pnt = pnt;

    samples.add(new Sample(pnt, pnt.start));
    samples.add(new Sample(pnt, pnt.end));
  }

  void sampleAtSubEvents(Sub sub, CSPnt pnt, List<List<Sample>> eventSamples) {
    sampleSubEvt(sub, true, eventSamples);
    sampleSubEvt(sub, false, eventSamples);
  }

  private void sampleSubEvt(Sub sub, boolean start, List<List<Sample>> eventSamples) {
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
        p = pnt.position(evt.u);
      }
    } else {
      evt = sub.end;
      if (sub.endHead == null && sub.endTail == null) {
        p = evt.p;
        replace = true;
      } else {
        p = pnt.position(evt.u);
      }
    }

    // reuse a sample if it has the exact same p, u as the pending sample
    List<Sample> evtSamples = eventSamples.get(evt.index);
    boolean added = false;
    for (int i = 0; !added && i < evtSamples.size(); i++) {
      Sample other = evtSamples.get(i);
      if (other.equals(p, evt.u)) {
        addSorted(other, replace);
        added = true;
      }
    }

    // if this sample hasn't been seen, create a new sample
    if (!added) {
      Sample s = new Sample(p, evt);
      if (addSorted(s, replace)) {
        evtSamples.add(s);
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
    Vec2d midP = pnt.position(avgU);
    Vec2d avgP = a.p.plus(b.p).over(2);

    if (midP.minus(avgP).lengthSquared() > thresholdSquared) {
      Sample newS = new Sample(midP, avgU);
      
//      if (newS.p.y < -5 && a.p.y > -5 && b.p.y > -5) {
//        System.out.println("new: " + newS.p);
//        System.out.println("left: " + a.p);
//        System.out.println("right: " + b.p);
//        System.out.println("avgp: " + avgP);
//        System.out.println("----------------------------");
//        System.out.println("lefu" + a.u);
//        System.out.println("rigu" + b.u);
//        System.out.println("avgu: " + avgU);
//        System.out.println("midp: " + midP);
//        System.out.println(pnt.getClass().toString());
//        System.out.println();
//      }
      
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
