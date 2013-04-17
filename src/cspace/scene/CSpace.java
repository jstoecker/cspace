package cspace.scene;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

import jgl.math.geometry.Ray;


/**
 * Configuration space.
 */
public class CSpace implements Iterable<Contact> {

  // input model
  public ArcShape obstacle = new ArcShape();
  public ArcShape robot    = new ArcShape();
  public Event[]  events;
  public SumVE[]  sves;
  public SumEV[]  sevs;
  public Intn[]   intns;
  public SumEE[]  sums;
  public Sub[]    subs;
  
  /** Maps a pair of edge indices to a SumEE */
  Map<EdgePair, List<SumEE>> sumMap;
  
  CSpace() {
  }

  void init() {
    for (Event event : events) {
      for (Sub startSub : event.startSubs) {
        for (Sub endSub : event.endSubs) {
          if (startSub.robEdge == endSub.robEdge && startSub.obsEdge == endSub.obsEdge) {
            if (startSub.tail == endSub.tail) {
              startSub.startTail = endSub;
              endSub.endTail = startSub;
            }
            if (startSub.head == endSub.head) {
              startSub.startHead = endSub;
              endSub.endHead = startSub;
            }
          }
        }
      }
    }
  }
  
  /** Returns a list of all SumEEs that have the edge indices given */
  public List<SumEE> getSumEEs(int robEdge, int obsEdge) {
    return sumMap.get(new EdgePair(robEdge, obsEdge));
  }
  
  public Sub.Intersection intersect(Ray r) {
    Sub.Intersection nearest = null;
    double nearestDist = Double.POSITIVE_INFINITY;
    for (Sub sub : subs) {
      Sub.Intersection x = sub.intersect(r);
      if (x != null) {
        double d = x.p.minus(r.p).lengthSquared();
        if (d < nearestDist) {
          nearestDist = d;
          nearest = x;
        }
      }
    }
    return (nearest == null) ? null : nearest;
  }
  
  public void sample(double threshold, double samplingLength) {
    // remove old samples
    Sample.NUM_SAMPLES = 0;
    for (Contact contact : this)
      if (contact != null)
        contact.samples.clear();
    // TODO: sub.samples.clear();
    // TODO : CLEAR ALL THE SAMPLE STATE INFO
    
    // begin contact samplings by adding samples at sub start & end events
    for (Sub sub : subs) {
      sub.tail.sampleAtSubEvents(sub);
      sub.head.sampleAtSubEvents(sub);
    }
    
    // end contact samplings by adding samples between current samples
    double thresholdSquared = threshold * threshold;
    for (Contact contact : this)
      if (contact != null)
        contact.fineSample(thresholdSquared);
    
    // begin sub samplings by adding samples along start & end
    for (Sub sub : subs) {
      sub.initSublists();
      sub.sampleStart(samplingLength);
      sub.sampleEnd(samplingLength);
    }
    
    // finish sub samplings by adding samples for postponed lists then inside
    for (Sub sub : subs) {
      sub.samplePostponed();
      sub.sampleInside(samplingLength);
      sub.triangulate();
      sub.initNeighbors(subs);
    }
    
    // connect adjacent triangles across subs
    for (Sub sub : subs)
      sub.initTriangleAdjacency();
  }

  @Override
  public Iterator<Contact> iterator() {
    return new ContactIterator();
  }
  
  private class ContactIterator implements Iterator<Contact> {
    private int position = 0;

    public boolean hasNext() {
      return position < (sves.length + sevs.length + intns.length);
    }

    public Contact next() {
      if (position < sves.length)
        return sves[position++];
      if (position < sves.length + sevs.length)
        return sevs[position++ - sves.length];
      if (position < sves.length + sevs.length + intns.length)
        return intns[position++ - sves.length - sevs.length];
      throw new NoSuchElementException();
    }

    public void remove() {
      throw new UnsupportedOperationException();
    }
  }
}
