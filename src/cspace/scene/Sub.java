package cspace.scene;

import static cspace.util.Math.complexProduct;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jgl.math.geometry.Ray;
import jgl.math.vector.Vec2d;
import jgl.math.vector.Vec3d;
import jgl.math.vector.Vec3f;
import cspace.scene.ArcShape.Edge;

/**
 * Sub-edge of a SumEE.
 */
public class Sub extends SumEE {

  /** The sub that shares this sub's tail pnt and start event */
  public Sub                 startTail;

  /** The sub that shares this sub's head pnt and start event */
  public Sub                 startHead;

  /** The sub that shares this sub's tail pnt and end event */
  public Sub                 endTail;

  /** The sub that shares this sub's head pnt and end event */
  public Sub                 endHead;

  public List<Sample>        startSamples = new ArrayList<Sample>();
  public List<Sample>        endSamples   = new ArrayList<Sample>();
  public List<Sample>        tailSamples;
  public List<Sample>        headSamples;
  public List<Sample>        outerLoop;
  public List<List<Sample>>  innerSamples;
  public List<Vertex>        verts        = new ArrayList<Vertex>();
  public List<Triangle>      triangles    = new ArrayList<Triangle>();
  public List<Sub>           neighbors    = new ArrayList<Sub>();
  public Map<Sample, Vertex> vertMap      = new HashMap<Sample, Vertex>();

  public Sub(Edge obsEdge, Edge robEdge, Contact tail, Contact head, Event start, Event end,
      int index) {
    super(obsEdge, robEdge, tail, head, start, end, index);
    start.startSubs.add(this);
    end.endSubs.add(this);
  }

  void initSublists() {
    // extract sublists from tail and head
    tailSamples = subList(tail.samples);
    headSamples = subList(head.samples);
  }

  private List<Sample> subList(List<Sample> pntSamples) {
    int startIndex = 0;
    while (!start.equals(pntSamples.get(startIndex).event)) {
      startIndex++;
    }
    int endIndex = 0;
    while (!end.equals(pntSamples.get(endIndex).event)) {
      endIndex++;
    }

    List<Sample> samples = new ArrayList<Sample>();

    // making a copy in case this sub needs to change the order of the sublist
    // without affecting its neighbors' lists
    if (endIndex < startIndex) {
      samples.addAll(pntSamples.subList(startIndex, pntSamples.size()));
      samples.addAll(pntSamples.subList(0, endIndex + 1));
    } else {
      samples.addAll(pntSamples.subList(startIndex, endIndex + 1));
    }

    return samples;
  }

  /**
   * Generate samples along the start
   */
  void sampleStart(double alphaDetail) {
    if (startTail == null) {
      if (startHead == null) {
        startSamples.add(tail.findSample(start));
      } else {
        if (startHead.endSamples.isEmpty()) {
          Sample tailSample = tail.findSample(start);
          Sample headSample = head.findSample(start);
          sampleBetween(tailSample, headSample, true, alphaDetail);
        } else {
          startSamples = startHead.endSamples;
        }
      }
    } else {
      if (startHead == null) {
        if (startTail.endSamples.isEmpty()) {
          Sample tailSample = tail.findSample(start);
          Sample headSample = head.findSample(start);
          sampleBetween(tailSample, headSample, true, alphaDetail);
        } else {
          startSamples = startTail.endSamples;
        }
      }
    }
  }

  /**
   * Generate samples along the end
   */
  void sampleEnd(double alphaDetail) {
    if (endTail == null) {
      if (endHead == null) {
        endSamples.add(tail.findSample(end));
      } else {
        if (endHead.startSamples.isEmpty()) {
          Sample tailSample = tail.findSample(end);
          Sample headSample = head.findSample(end);
          sampleBetween(tailSample, headSample, false, alphaDetail);
        } else {
          endSamples = endHead.startSamples;
        }
      }
    } else {
      if (endHead == null) {
        if (endTail.startSamples.isEmpty()) {
          Sample tailSample = tail.findSample(end);
          Sample headSample = head.findSample(end);
          sampleBetween(tailSample, headSample, false, alphaDetail);
        } else {
          endSamples = endTail.startSamples;
        }
      }
    }
  }

  /**
   * Finish sampling start & end for postponed case.
   */
  void samplePostponed() {
    if (startTail != null && startHead != null) {
      startSamples.addAll(startTail.endSamples);
      if (!startSamples.isEmpty())
        startSamples.remove(startSamples.size() - 1);
      startSamples.addAll(startHead.endSamples);
    }

    if (endTail != null && endHead != null) {
      endSamples.addAll(endTail.startSamples);
      if (!endSamples.isEmpty())
        endSamples.remove(endSamples.size() - 1);
      endSamples.addAll(endHead.startSamples);
    }
  }

  /**
   * Generates samples for the start or end of the sub
   */
  private void sampleBetween(Sample tail, Sample head, boolean start, double alphaDetail) {
    List<Sample> sampleList = start ? startSamples : endSamples;

    sampleList.add(tail);

    Event event = tail.event;

    Arc arc = arc(event.u);
    int numSamples = (int) Math.max(1, Math.abs(arc.angle / alphaDetail));
    Vec2d step = arc.headN.minus(arc.tailN).over(numSamples + 1);
    for (int i = 0; i < numSamples; i++) {
      Vec2d alpha = arc.tailN.plus(step.times(i + 1)).normalize().times(r);
      sampleList.add(new Sample(arc.c.plus(alpha), event));
    }

    sampleList.add(head);
  }

  /**
   * Generates samples for the interior of the sub
   */
  void sampleInside(double samplingLength) {
    int numSamplesTheta = (int) Math.max(1, Math.abs(angle / samplingLength));
    innerSamples = new ArrayList<List<Sample>>(numSamplesTheta);

    // NOTE: this approach will have problems if (end-start) % pi == 0 exactly
    // use non-vector angle?

    Vec2d stepTheta = ((end.u).minus(start.u)).over(numSamplesTheta + 1);

    for (int sampleTheta = 0; sampleTheta < numSamplesTheta; sampleTheta++) {
      Vec2d thetaBase = start.u.plus(stepTheta.times(sampleTheta + 1)).normalize();
      Arc arc = arc(thetaBase);
      int numSamplesAlpha = (int) Math.max(2, Math.abs(arc.angle / samplingLength));
      Vec2d stepAlpha = ((arc.headN).minus(arc.tailN)).over(numSamplesAlpha + 1);

      List<Sample> row = new ArrayList<Sample>();
      for (int sampleX = 0; sampleX < numSamplesAlpha; sampleX++) {
        Vec2d theta = start.u.plus(
            stepTheta.times(sampleTheta + 1 + (Math.random() - 0.5) * 2 * 0.1)).normalize();
        arc = arc(theta);
        Vec2d alpha = arc.tailN
            .plus(stepAlpha.times(sampleX + 1 + (Math.random() - 0.5) * 2 * 0.1)).normalize();
        Sample s = new Sample(alpha.times(r).plus(arc.c), theta);
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

  void initNeighbors(Sub[] subs) {
    if (startTail != null)
      neighbors.add(startTail);
    if (startHead != null && startHead != startTail)
      neighbors.add(startHead);
    if (endTail != null)
      neighbors.add(endTail);
    if (endHead != null && endHead != endTail)
      neighbors.add(endHead);

    // NOTE: this adds subs that don't necessarily overlap; could check
    // events to ensure this is true
    for (Sub sub : subs) {
      if (sub != this && (sub.tail == head || sub.head == tail))
        neighbors.add(sub);
    }
  }

  void initTriangleAdjacency() {
    for (Triangle t : triangles) {
      addTriNeighbors(t, triangles);
      for (Sub neighbor : neighbors) {
        addTriNeighbors(t, neighbor.triangles);
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
    return ((eq(t.a, v1) || eq(t.b, v1) || eq(t.c, v1)) && (eq(t.a, v2) || eq(t.b, v2) || eq(t.c,
        v2)));
  }

  private boolean eq(Vertex a, Vertex b) {
    return a.sample.index == b.sample.index;
  }

  /** @return The triangle nearest to ray start that the ray intersects */
  public Intersection intersect(Ray r) {
    Triangle nearestTri = null;
    Vec3f nearestP = null;
    double nearDist = Double.POSITIVE_INFINITY;

    for (Triangle tri : triangles) {
      Vertex a = tri.a;
      Vertex b = tri.b;
      Vertex c = tri.c;
      Vec3f p = r.intersect(new jgl.math.geometry.Triangle(a.position.toFloat(), b.position
          .toFloat(), c.position.toFloat()));

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

    return (nearestTri == null) ? null : new Intersection(nearestTri, nearestP);
  }

  // ===========================================================================
  public class Intersection {
    public Triangle t; // nearest triangle that the ray intersects
    public Vec3f    p; // point on the triangle the ray intersects

    public Intersection(Triangle t, Vec3f p) {
      this.t = t;
      this.p = p;
    }
    
    public Sub getSub() {
      return Sub.this;
    }
  }

  // ===========================================================================
  public class Triangle {

    public final Sample     sa, sb, sc;
    public final Vertex     a, b, c;
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
      return b.position.minus(a.position).cross(c.position.minus(b.position)).normalize();
    }

    @Override
    public String toString() {
      return String.format("%d, %d, %d", a.index, b.index, c.index);
    }

    public Sub getSub() {
      return Sub.this;
    }
  }

  // ===========================================================================
  public class Vertex {

    private Sample   sample;
    public Vec3d     position;
    public Vec2d     alphaTheta;
    public Vec2d     alpha;
    public Vec2d     theta;
    public Vec3d     normal;
    public final int index;

    Vertex(Sample sample) {
      this.sample = sample;
      alpha = sample.p.minus(center(sample.u)).over(r);
      theta = sample.u;

      double z = Math.atan2(sample.u.y, sample.u.x);
      if (z - startAngle > Math.PI) {
        z -= Math.PI * 2;
      } else if (startAngle - z > Math.PI) {
        z += Math.PI * 2;
      }
      position = new Vec3d(sample.p.x, sample.p.y, z);

      Vec3d pa = new Vec3d(-alpha.y, alpha.x, 0);
      Vec3d pt = new Vec3d(complexProduct(new Vec2d(-sample.u.y, sample.u.x), robEdge.c), 1);
      normal = new Vec3d(pt.cross(pa).normalize());

      index = verts.size();

      // initialize 2d position (needs to be aligned w.r.t. other samples)
      Arc arc = arc(sample.u);
      Vec2d n = sample.p.minus(arc.c);
      double a = Math.atan2(n.y, n.x);
      double t = Math.atan2(sample.u.y, sample.u.x) - startAngle;
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