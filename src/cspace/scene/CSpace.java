package cspace.scene;

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
  public SumEE[]  sums;

  /** All intersections of pairs of SumEEs */
  public Intn[]   intns;

  /** All inner sub edges of SumEEs */
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
}
