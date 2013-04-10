package cspace.render2d;

import javax.media.opengl.GL2;

import jgl.math.vector.Vec2d;
import cspace.scene.ArcShape.Edge;
import cspace.scene.CSArc.Arc;

/**
 * Draws an arc as a triangle strip.
 */
public class ArcTriStrip {
  
  Vec2d tailNormal;
  Vec2d headNormal;
  Vec2d center;
  double radius;
  double headAngle;
  double tailAngle;
  boolean drawLinear;
  boolean flip = false;
  
  public ArcTriStrip(Edge e) {
    tailNormal = e.tail.n;
    headNormal = e.head.n;
    radius = e.r;
    center = e.c;
    tailAngle = e.tailAngle;
    headAngle = e.headAngle;
    drawLinear = 2.0 / (e.head.n.minus(e.tail.n)).length() > 10e6;
  }
  
  public ArcTriStrip(Arc arc) {
    tailNormal = new Vec2d(arc.tailN);
    headNormal = new Vec2d(arc.headN);
    center = new Vec2d(arc.c);
    radius = arc.r;
    headAngle = arc.headAngle;
    tailAngle = arc.tailAngle;
    drawLinear = 2.0 / headNormal.minus(tailNormal).length() > 10e6;
    flip = true;
  }
  
  public void draw(GL2 gl, double width, double smoothing) {
    if (drawLinear)
      linear(gl, width);
    else
      sweep(gl, width, smoothing);
  }
  
  private void linear(GL2 gl, double width) {
    Vec2d a = center.plus(tailNormal.times(radius));
    Vec2d b = center.plus(headNormal.times(radius));
    Vec2d ab = b.minus(a);
    double abLength = ab.length();
    Vec2d n = new Vec2d(ab.y, -ab.x).divide(abLength);
    
    if (flip)
      n.multiply(-1);
    
    double d = abLength / 2.0;
    double s = Math.sqrt(radius * radius - d * d);

    gl.glBegin(GL2.GL_TRIANGLE_STRIP);
    gl.glVertex2d(a.x, a.y);
    gl.glVertex2d(a.x + n.x * width, a.y + n.y * width);
    
    double t = 0.1;
    for (int i = 0; i < 9; i++) {
      double x = (2.0 * t - 1.0) * d;
      double y = (d * d - x * x) / (s + Math.sqrt(radius * radius + x * x));
      
      Vec2d p1 = a.plus(ab.times(t)).plus(n.times(y));
      Vec2d p2 = a.plus(ab.times(t)).plus(n.times(y + width));
      gl.glVertex2d(p1.x, p1.y);
      gl.glVertex2d(p2.x, p2.y);
      t += 0.1;
    }

    gl.glVertex2d(b.x, b.y);
    gl.glVertex2d(b.x + n.x * width, b.y + n.y * width);
    gl.glEnd();
  }

  private void sweep(GL2 gl, double width, double smoothing) {
    
    double arcAngle = headAngle - tailAngle;
    double innerRadius = radius - width;
    double outerRadius = radius;
    double maxRadius = Math.max(innerRadius, outerRadius);
//    int numSegments = GLArc.numSegments(arcAngle, maxRadius, smoothing);
    int numSegments = 32;
    double theta = arcAngle / numSegments;

    Vec2d v = new Vec2d(tailNormal);
    double c = Math.cos(theta);
    double s = Math.sin(theta);

    gl.glBegin(GL2.GL_TRIANGLE_STRIP);
    for (int j = 0; j <= numSegments; j++) {
      Vec2d sample = v.times(innerRadius).plus(center);
      Vec2d sample2 = v.times(outerRadius).plus(center);
      gl.glVertex2d(sample.x, sample.y);
      gl.glVertex2d(sample2.x, sample2.y);
      double newX = v.x * c - v.y * s;
      double newY = v.x * s + v.y * c;
      v.x = newX;
      v.y = newY;
    }
    gl.glEnd();
  }
}
