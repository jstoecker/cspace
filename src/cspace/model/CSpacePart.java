package cspace.model;

/**
 * A smaller part of the CSpace that exists in an interval of theta = [start,end].
 */
public abstract class CSpacePart {

  /** Start of the geometry */
  public final Event  start;

  /** End of the geometry */
  public final Event  end;

  /** Start of the geometry in radians */
  public final double startAngle;

  /** End of the geometry in radians */
  public final double endAngle;
  
  /** The magnitude of the difference of the end and start angles */
  public final double angle;

  /** Index of the geometry in its respective list */
  public final int    index;

  public CSpacePart(Event start, Event end, int index) {
    this.start = start;
    this.end = end;
    this.index = index;

    startAngle = Math.atan2(start.u.y, start.u.x);
    double endAngle = Math.atan2(end.u.y, end.u.x);
    while (endAngle - startAngle > 0)
      endAngle -= 2 * Math.PI;
    while (endAngle - startAngle < 0)
      endAngle += 2 * Math.PI;
    this.endAngle = endAngle;
    this.angle = endAngle - startAngle;
  }

  public boolean isActive(double theta) {
    while (theta >= startAngle)
      theta -= Math.PI * 2;
    while (theta < startAngle)
      theta += Math.PI * 2;
    return (theta >= startAngle) && (theta <= endAngle);
  }
}
