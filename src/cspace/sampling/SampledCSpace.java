package cspace.sampling;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jgl.math.geometry.Ray;
import cspace.model.CSPnt;
import cspace.model.CSpace;
import cspace.model.Sub;
import cspace.sampling.SampledSub.RayTriIntersection;

/**
 * Performs "watertight" sampling of the configuration space.
 */
public class SampledCSpace {

  public CSpace cspace;
  public Map<Sub, SampledSub> subSamplings = new HashMap<Sub, SampledSub>();
  public Map<CSPnt, SampledPnt> pntSamplings = new HashMap<CSPnt, SampledPnt>();
  public List<List<Sample>> eventSamples;

  /**
   * @param threshold
   *          - lower number results in smoother sampling along Pnts
   * @param samplingLength
   *          - lower number results in smoother sampling inside Subs
   */
  public SampledCSpace(CSpace cspace, double threshold, double samplingLength) {
    Sample.NUM_SAMPLES = 0;
    this.cspace = cspace;
    eventSamples = new ArrayList<List<Sample>>(cspace.events.length);
    for (int i = 0; i < cspace.events.length; i++) {
      eventSamples.add(new ArrayList<Sample>());
    }

    // begin pnt samplings by adding samples at sub start & end events
    for (Sub sub : cspace.subs) {
      initPntSampling(sub, sub.tail);
      initPntSampling(sub, sub.head);
    }

    // finish pnt samplings by adding samples between sub event samples
    double thresholdSquared = threshold * threshold;
    for (SampledPnt pnt : pntSamplings.values()) {
      pnt.sampleInner(thresholdSquared);
    }

    // begin sub samplings by adding samples along start & end
    for (Sub sub : cspace.subs) {
      SampledSub sampling = new SampledSub(sub, pntSamplings);
      subSamplings.put(sub, sampling);
      sampling.sampleStart(pntSamplings, subSamplings, samplingLength);
      sampling.sampleEnd(pntSamplings, subSamplings, samplingLength);
    }

    // finish sub samplings by adding samples for postponed lists then inside
    for (SampledSub sampling : subSamplings.values()) {
      sampling.samplePostponed(subSamplings);
      sampling.sampleInside(samplingLength);
      sampling.triangulate();
      sampling.initSubAdjacency(subSamplings);
    }

    // connect adjacent triangles across subs
    for (SampledSub sampling : subSamplings.values()) {
      sampling.initTriangleAdjacency();
    }
  }

  private void initPntSampling(Sub sub, CSPnt pnt) {
    // the pnt may be shared by many subs, so only create anew if it doesn't
    // exist
    SampledPnt sampling = pntSamplings.get(pnt);
    if (sampling == null) {
      pntSamplings.put(pnt, sampling = new SampledPnt(pnt));
    }

    // add samples to the pnt at the sub's start & end events
    sampling.sampleAtSubEvents(sub, pnt, eventSamples);
  }

  public RayTriIntersection intersect(Ray r) {
    RayTriIntersection nearest = null;
    double nearestDist = Double.POSITIVE_INFINITY;
    for (SampledSub ss : subSamplings.values()) {
      RayTriIntersection x = ss.intersect(r);
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
}
