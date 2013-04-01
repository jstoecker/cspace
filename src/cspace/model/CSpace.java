package cspace.model;

import java.util.List;
import java.util.Map;


/**
 * Configuration space.
 */
public class CSpace {

  /** Obstacle shape */
  public ArcShape obstacle = new ArcShape();

  /** Robot shape */
  public ArcShape robot    = new ArcShape();

  /** All events */
  public Event[]  events;

  /** All sums of obstacle vertices + robot edges */
  public SumVE[]  sves;

  /** All sums of obstacle edges + robot vertices */
  public SumEV[]  sevs;

  /** All sums of obstacle edges + robot edges */
  public SumEE[]  sees;

  /** All intersections of pairs of SumEEs */
  public Intn[]   intns;

  /** All inner sub edges of SumEEs */
  public Sub[]    subs;
  
  /** Maps a pair of edge indices to a SumEE */
  Map<EdgeSum, List<SumEE>> sumMap;
  
  /** Used only for the hashing to find sumees by index pairs */
  public class EdgeSum {
    final int robEdge;
    final int obsEdge;
    public EdgeSum(int robEdge, int obsEdge) {
      this.robEdge = robEdge;
      this.obsEdge = obsEdge;
    }
    
    @Override
    public boolean equals(Object obj) {
      if (obj == null || obj.getClass() != getClass())
        return false;
      EdgeSum that = (EdgeSum)obj;
      return (robEdge == that.robEdge && obsEdge == that.obsEdge);
    }
    
    @Override
    public int hashCode() {
      return robEdge * obstacle.e.length + obsEdge;
    }
  }
  
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
    return sumMap.get(new EdgeSum(robEdge, obsEdge));
  }
}
