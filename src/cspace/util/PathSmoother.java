package cspace.util;

import java.util.ArrayList;
import java.util.List;

import jgl.math.vector.Vec3d;
import cspace.scene.Path;
import cspace.scene.Path.Waypoint;

/**
 * Smooths a path by linearly interpolating between existing waypoints.
 * 
 * @author justin
 */
public class PathSmoother {


  /**
   * Returns a new smoothed path without modifying the input.
   * 
   * @param input - input path
   * @param samplingDist - distance between samples
   */
  public Path smooth(Path input, double samplingDist) {
    return smooth(input, samplingDist, new Vec3d(1));
  }
  
  /**
   * Returns a new smoothed path without modifying the input.
   * 
   * @param input - input path
   * @param samplingDist - maximum distance between two waypoints before a new waypoint is added.
   * @param weights - (x,y,z) weights for distance
   */
  public Path smooth(Path input, double samplingDist, Vec3d weights) {
    if (input.waypoints.size() < 2)
      return input;

    List<Waypoint> smoothed = new ArrayList<Waypoint>();
    
    for (int i = 0; i < input.waypoints.size() - 1; i++) {
      Vec3d a = input.waypoints.get(i).toVector();
      Vec3d b = input.waypoints.get(i + 1).toVector();
      double dist = b.minus(a).times(weights).length();
      int numToAdd = (int)(dist / samplingDist);
      Vec3d step = b.minus(a).over(numToAdd);
      
      smoothed.add(new Waypoint(a));
      for (int j = 1; j <= numToAdd; j++)
        smoothed.add(new Waypoint(a.plus(step.times(j))));
      smoothed.add(new Waypoint(b));
    }
    
    return new Path(smoothed);
  }
}
