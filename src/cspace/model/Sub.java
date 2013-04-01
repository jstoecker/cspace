package cspace.model;

import cspace.model.ArcShape.Edge;


/**
 * Sub-edge of a SumEE.
 */
public class Sub extends SumEE {
  
  /** The sub that shares this sub's tail pnt and start event */
  public Sub startTail;
  
  /** The sub that shares this sub's head pnt and start event */
  public Sub startHead;
  
  /** The sub that shares this sub's tail pnt and end event */
  public Sub endTail;
  
  /** The sub that shares this sub's head pnt and end event */
  public Sub endHead;
  
  public Sub(Edge obsEdge, Edge robEdge, CSPnt tail, CSPnt head, Event start, Event end,
      int index) {
    super(obsEdge, robEdge, tail, head, start, end, index);
    start.startSubs.add(this);
    end.endSubs.add(this);
  }
}