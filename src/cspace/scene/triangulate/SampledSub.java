package cspace.scene.triangulate;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jgl.math.geometry.Ray;
import jgl.math.vector.Vec2d;
import jgl.math.vector.Vec3d;
import jgl.math.vector.Vec3f;
import cspace.scene.CSPnt;
import cspace.scene.Event;
import cspace.scene.Sub;
import cspace.scene.CSArc.Arc;

import static cspace.util.Math.complexProduct;

/**
 * Stores samples for a single sub.
 */
public class SampledSub {

  public final Sub sub;
  public List<Sample> startSamples = new ArrayList<Sample>();
  public List<Sample> endSamples = new ArrayList<Sample>();
  public List<Sample> tailSamples;
  public List<Sample> headSamples;
  public List<Sample> outerLoop;
  public List<List<Sample>> innerSamples;
  public List<Vertex> verts = new ArrayList<Vertex>();
  public List<Triangle> triangles = new ArrayList<Triangle>();
  public List<SampledSub> neighbors = new ArrayList<SampledSub>();
  public Map<Sample, Vertex> vertMap = new HashMap<Sample, Vertex>();

  public SampledSub(Sub sub, Map<CSPnt, SampledPnt> pntSamplings) {
    this.sub = sub;

    // the Pnt samples are finished at this point, so get the sublists for the
    // head & tail
    tailSamples = subList(pntSamplings.get(sub.tail).samples);
    headSamples = subList(pntSamplings.get(sub.head).samples);
  }

  private List<Sample> subList(List<Sample> pntSamples) {
    int start = 0;
    while (!sub.start.equals(pntSamples.get(start).event)) {
      start++;
    }
    int end = 0;
    while (!sub.end.equals(pntSamples.get(end).event)) {
      end++;
    }
    
    List<Sample> samples = new ArrayList<Sample>();
    
    // making a copy in case this sub needs to change the order of the sublist
    // without affecting its neighbors' lists
    if (end < start) {
      samples.addAll(pntSamples.subList(start, pntSamples.size()));
      samples.addAll(pntSamples.subList(0, end + 1));
    } else {
      samples.addAll(pntSamples.subList(start, end + 1));
    }

    return samples;
  }

  /**
   * Generate samples along the start
   */
  void sampleStart(Map<CSPnt, SampledPnt> pntSamplings,
      Map<Sub, SampledSub> subSamplings, double alphaDetail) {

    if (sub.startTail == null) {
      if (sub.startHead == null) {
        startSamples.add(pntSamplings.get(sub.tail).findSample(sub.start));
      } else {
        SampledSub shSampling = subSamplings.get(sub.startHead);
        if (shSampling == null || shSampling.endSamples.isEmpty()) {
          Sample tail = pntSamplings.get(sub.tail).findSample(sub.start);
          Sample head = pntSamplings.get(sub.head).findSample(sub.start);
          sampleBetween(tail, head, true, alphaDetail);
        } else {
          startSamples = shSampling.endSamples;
        }
      }
    } else {
      if (sub.startHead == null) {
        SampledSub stSampling = subSamplings.get(sub.startTail);
        if (stSampling == null || stSampling.endSamples.isEmpty()) {
          Sample tail = pntSamplings.get(sub.tail).findSample(sub.start);
          Sample head = pntSamplings.get(sub.head).findSample(sub.start);
          sampleBetween(tail, head, true, alphaDetail);
        } else {
          startSamples = stSampling.endSamples;
        }
      } // else postpone
    }
  }

  /**
   * Generate samples along the end
   */
  void sampleEnd(Map<CSPnt, SampledPnt> pntSamplings,
      Map<Sub, SampledSub> subSamplings, double samplingLength) {
    
    if (sub.endTail == null) {
      if (sub.endHead == null) {
        endSamples.add(pntSamplings.get(sub.tail).findSample(sub.end));
      } else {
        SampledSub ehSampling = subSamplings.get(sub.endHead);
        if (ehSampling == null) {
          Sample tail = pntSamplings.get(sub.tail).findSample(sub.end);
          Sample head = pntSamplings.get(sub.head).findSample(sub.end);
          sampleBetween(tail, head, false, samplingLength);
        } else {
          endSamples = ehSampling.startSamples;
        }
      }
    } else {
      if (sub.endHead == null) {
        SampledSub etSampling = subSamplings.get(sub.endTail);
        if (etSampling == null) {
          Sample tail = pntSamplings.get(sub.tail).findSample(sub.end);
          Sample head = pntSamplings.get(sub.head).findSample(sub.end);
          sampleBetween(tail, head, false, samplingLength);
        } else {
          endSamples = etSampling.startSamples;
        }
      } // else postpone
    }
  }

  /**
   * Finish sampling start & end for postponed case
   */
  void samplePostponed(Map<Sub, SampledSub> subSamplings) {
    if (sub.startTail != null && sub.startHead != null) {
      startSamples.addAll(subSamplings.get(sub.startTail).endSamples);
      if (!startSamples.isEmpty()) {
        startSamples.remove(startSamples.size() - 1);
      }
      startSamples.addAll(subSamplings.get(sub.startHead).endSamples);
    }

    if (sub.endTail != null && sub.endHead != null) {
      endSamples.addAll(subSamplings.get(sub.endTail).startSamples);
      if (!endSamples.isEmpty()) {
        endSamples.remove(endSamples.size() - 1);
      }
      endSamples.addAll(subSamplings.get(sub.endHead).startSamples);
    }
  }

  /**
   * Generates samples for the start or end of the sub
   */
  private void sampleBetween(Sample tail, Sample head, boolean start,
      double alphaDetail) {
    List<Sample> sampleList = start ? startSamples : endSamples;

    sampleList.add(tail);

    Event event = tail.event;
    
    Arc arc = sub.arc(event.u);
    int numSamples = (int) Math.max(1, Math.abs(arc.angle / alphaDetail));
    Vec2d step = arc.headN.minus(arc.tailN).over(numSamples + 1);
    for (int i = 0; i < numSamples; i++) {
      Vec2d alpha = arc.tailN.plus(step.times(i + 1)).normalize().times(sub.r);
      sampleList.add(new Sample(arc.c.plus(alpha), event));
    }
    

    sampleList.add(head);
  }
  
  /**
   * Generates samples for the interior of the sub
   */
  void sampleInside(double samplingLength) {
    int numSamplesTheta = (int) Math.max(1,
        Math.abs(sub.angle / samplingLength));
    innerSamples = new ArrayList<List<Sample>>(numSamplesTheta);
    
    // NOTE: this approach will have problems if (end-start) % pi == 0 exactly
    // use non-vector angle?
    
    Vec2d stepTheta = ((sub.end.u).minus(sub.start.u))
        .over(numSamplesTheta + 1);

    for (int sampleTheta = 0; sampleTheta < numSamplesTheta; sampleTheta++) {
      Vec2d thetaBase = sub.start.u.plus(stepTheta.times(sampleTheta + 1))
          .normalize();
      Arc arc = sub.arc(thetaBase);
      int numSamplesAlpha = (int) Math.max(2,
          Math.abs(arc.angle / samplingLength));
      Vec2d stepAlpha = ((arc.headN).minus(arc.tailN))
          .over(numSamplesAlpha + 1);

      List<Sample> row = new ArrayList<Sample>();
      for (int sampleX = 0; sampleX < numSamplesAlpha; sampleX++) {
        Vec2d theta = sub.start.u.plus(
            stepTheta.times(sampleTheta + 1 + (Math.random() - 0.5) * 2 * 0.1))
            .normalize();
        arc = sub.arc(theta);
        Vec2d alpha = arc.tailN.plus(
            stepAlpha.times(sampleX + 1 + (Math.random() - 0.5) * 2 * 0.1))
            .normalize();
        Sample s = new Sample(alpha.times(sub.r).plus(arc.c), theta);
        row.add(s);
      }
      innerSamples.add(row);
    }
  }

  void triangulate() {
    alignSamples2D();

    SubTessellator tess = new SubTessellator(this);
    tess.triangulate();
    for (SubTessellator.Triangle t : tess.triangles) {
      triangles.add(new Triangle(t.a.sample, t.b.sample, t.c.sample));
    }
  }

  private void alignSamples2D() {
    // align start row
    double prevAlpha = getVertex(startSamples.get(0)).alphaTheta.x;
    for (int i = 1; i < startSamples.size(); i++) {
      prevAlpha = alignVertex2D(startSamples.get(i), prevAlpha);
    }

    // align tail samples
    for (int i = 0; i < tailSamples.size() - 1; i++) {
      prevAlpha = alignVertex2D(tailSamples.get(i), prevAlpha);
    }

    // align head samples
    prevAlpha = getVertex(startSamples.get(0)).alphaTheta.x;
    for (int i = 0; i < headSamples.size(); i++) {
      prevAlpha = alignVertex2D(headSamples.get(i), prevAlpha);
    }

    // align end samples
    for (int i = 0; i < endSamples.size(); i++) {
      prevAlpha = alignVertex2D(endSamples.get(i), prevAlpha);
    }

    // align inner samples
    prevAlpha = getVertex(startSamples.get(0)).alphaTheta.x;
    for (int i = 0; i < innerSamples.size(); i++) {
      for (int j = 0; j < innerSamples.get(i).size(); j++) {
        prevAlpha = alignVertex2D(innerSamples.get(i).get(j), prevAlpha);
      }
      prevAlpha = getVertex(innerSamples.get(i).get(0)).alphaTheta.x;
    }

    boolean cw = false;
    if (startSamples.size() > 1) {
      Vertex v1 = getVertex(startSamples.get(0));
      Vertex v2 = getVertex(startSamples.get(1));
      cw = v1.alphaTheta.x < v2.alphaTheta.x;
    } else if (endSamples.size() > 1) {
      Vertex v1 = getVertex(endSamples.get(0));
      Vertex v2 = getVertex(endSamples.get(1));
      cw = v1.alphaTheta.x < v2.alphaTheta.x;
    }
    
    // if vertices are ordered clockwise, flip alpha to order them CCW
    if (cw) {
      for (Vertex v : verts) {
        v.alphaTheta.x *= -1;
      }
    }

    // concatenate lists to get the outer loop of samples
    outerLoop = new ArrayList<Sample>();
    for (int i = startSamples.size() - 1; i > 0; i--) {
      outerLoop.add(startSamples.get(i));
    }
    for (int i = 0; i < tailSamples.size() - 1; i++) {
      outerLoop.add(tailSamples.get(i));
    }
    for (int i = 0; i < endSamples.size() - 1; i++) {
      outerLoop.add(endSamples.get(i));
    }
    for (int i = headSamples.size() - 1; i > 0; i--) {
      outerLoop.add(headSamples.get(i));
    }
  }

  private double alignVertex2D(Sample s, double prevAlpha) {
    // 2D positions are periodic, so align with a previous vertex alpha
    Vec2d p = getVertex(s).alphaTheta;
    while (p.x - prevAlpha > Math.PI) {
      p.x -= Math.PI * 2;
    }
    while (prevAlpha - p.x > Math.PI) {
      p.x += Math.PI * 2;
    }
    return p.x;
  }

  Vertex getVertex(Sample sample) {
    Vertex v = vertMap.get(sample);
    if (v == null) {
      v = new Vertex(sample);
      verts.add(v);
      vertMap.put(sample, v);
    }
    return v;
  }

  void initSubAdjacency(Map<Sub, SampledSub> subSamplings) {
    if (sub.startTail != null) {
      neighbors.add(subSamplings.get(sub.startTail));
    }
    if (sub.startHead != null && sub.startHead != sub.startTail) {
      neighbors.add(subSamplings.get(sub.startHead));
    }
    if (sub.endTail != null) {
      neighbors.add(subSamplings.get(sub.endTail));
    }
    if (sub.endHead != null && sub.endHead != sub.endTail) {
      neighbors.add(subSamplings.get(sub.endHead));
    }

    for (SampledSub s : subSamplings.values()) {
      if (s != this && (s.sub.tail == sub.head || s.sub.head == sub.tail)) {
        neighbors.add(s);
      }
    }
  }

  void initTriangleAdjacency() {
    for (Triangle t : triangles) {
      addTriNeighbors(t, triangles);
      for (SampledSub sNeighbor : neighbors) {
        addTriNeighbors(t, sNeighbor.triangles);
      }
    }
  }

  private void addTriNeighbors(Triangle t, List<Triangle> others) {
    for (Triangle other : others) {
      if (t != other) {
        if (t.neighbors[0] == null && triSharesEdge(other, t.a, t.b)) {
          t.neighbors[0] = other;
        }
        if (t.neighbors[1] == null && triSharesEdge(other, t.b, t.c)) {
          t.neighbors[1] = other;
        }
        if (t.neighbors[2] == null && triSharesEdge(other, t.c, t.a)) {
          t.neighbors[2] = other;
        }
      }
    }
  }

  private boolean triSharesEdge(Triangle t, Vertex v1, Vertex v2) {
    return ((eq(t.a, v1) || eq(t.b, v1) || eq(t.c, v1)) && (eq(t.a, v2)
        || eq(t.b, v2) || eq(t.c, v2)));
  }

  private boolean eq(Vertex a, Vertex b) {
    return a.sample.index == b.sample.index;
  }

  /** @return The triangle nearest to ray start that the ray intersects */
  public RayTriIntersection intersect(Ray r) {
    Triangle nearestTri = null;
    Vec3f nearestP = null;
    double nearDist = Double.POSITIVE_INFINITY;

    for (Triangle tri : triangles) {
      Vertex a = tri.a;
      Vertex b = tri.b;
      Vertex c = tri.c;
      Vec3f p = r.intersect(new jgl.math.geometry.Triangle(
          a.position.toFloat(), 
          b.position.toFloat(),
          c.position.toFloat()));

      // if no intersection, try shifting by 2pi
      if (p == null) {
        Vec3f pa = a.position.toFloat();
        Vec3f pb = b.position.toFloat();
        Vec3f pc = c.position.toFloat();
        pa.z += Math.PI * 2;
        pb.z += Math.PI * 2;
        pc.z += Math.PI * 2;
        p = r.intersect(new jgl.math.geometry.Triangle(pa, pb, pc));
      }

      // still no intersection, try shifting by -2p
      if (p == null) {
        Vec3f pa = a.position.toFloat();
        Vec3f pb = b.position.toFloat();
        Vec3f pc = c.position.toFloat();
        pa.z -= Math.PI * 2;
        pb.z -= Math.PI * 2;
        pc.z -= Math.PI * 2;
        p = r.intersect(new jgl.math.geometry.Triangle(pa, pb, pc));
      }

      if (p != null) {
        double d = r.p.minus(p.x, p.y, p.z).length();
        if (d < nearDist && d > 0.1) {
          nearDist = d;
          nearestTri = tri;
          nearestP = p;
        }
      }
    }

    return (nearestTri == null) ? null : new RayTriIntersection(nearestTri,
        nearestP);
  }

  // ===========================================================================
  public static class RayTriIntersection {
    public Triangle t; // nearest triangle that the ray intersects
    public Vec3f p; // point on the triangle the ray intersects

    public RayTriIntersection(Triangle t, Vec3f p) {
      this.t = t;
      this.p = p;
    }
  }

  // ===========================================================================
  public class Triangle {

    public final Sample sa, sb, sc;
    public final Vertex a, b, c;
    // neighbors[0] means shared edge with (A,B)
    // neighbors[1] means shared edge with (B,C)
    // neighbors[2] means shared edge with (C,A)
    public final Triangle[] neighbors = new Triangle[3];

    Triangle(Sample a, Sample b, Sample c) {
      this.sa = a;
      this.sb = b;
      this.sc = c;
      this.a = getVertex(a);
      this.b = getVertex(b);
      this.c = getVertex(c);
    }

    public Vec3d normal() {
      return b.position.minus(a.position).cross(c.position.minus(b.position))
          .normalize();
    }

    @Override
    public String toString() {
      return String.format("%d, %d, %d", a.index, b.index, c.index);
    }
    
    public SampledSub getSub() {
      return SampledSub.this;
    }
  }

  // ===========================================================================
  public class Vertex {

    private Sample sample;
    public Vec3d position;
    public Vec2d alphaTheta;
    public Vec2d alpha;
    public Vec2d theta;
    public Vec3d normal;
    public final int index;

    Vertex(Sample sample) {
      this.sample = sample;
      alpha = sample.p.minus(sub.center(sample.u)).over(sub.r);
      theta = sample.u;

      double z = Math.atan2(sample.u.y, sample.u.x);
      if (z - sub.startAngle > Math.PI) {
        z -= Math.PI * 2;
      } else if (sub.startAngle - z > Math.PI) {
        z += Math.PI * 2;
      }
      position = new Vec3d(sample.p.x, sample.p.y, z);
      
      Vec3d pa = new Vec3d(-alpha.y, alpha.x, 0);
      Vec3d pt = new Vec3d(
          complexProduct(new Vec2d(-sample.u.y, sample.u.x), sub.robEdge.c), 1);
      normal = new Vec3d(pt.cross(pa).normalize());


      
      
      
      index = verts.size();

      // initialize 2d position (needs to be aligned w.r.t. other samples)
      Arc arc = sub.arc(sample.u);
      Vec2d n = sample.p.minus(arc.c);
      double a = Math.atan2(n.y, n.x);
      double t = Math.atan2(sample.u.y, sample.u.x) - sub.startAngle;
      if (t < 0) {
        t += Math.PI * 2;
      }
      alphaTheta = new Vec2d(a, t);
    }

    @Override
    public String toString() {
      return Integer.toString(index);
    }
  }
  // ===========================================================================
}
