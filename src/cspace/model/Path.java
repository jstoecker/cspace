package cspace.model;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;
import jgl.math.vector.Vec2d;
import jgl.math.vector.Vec3d;
import jgl.math.vector.Vec3f;

/**
 * A list of waypoints (x, y, theta) for the robot.
 */
public class Path {

  public Waypoint[] waypoints;

  public Path(Waypoint[] waypoints) {
    this.waypoints = waypoints;
  }

  public static Path load(File file) throws FileNotFoundException {
    Scanner scanner = new Scanner(file);
    ArrayList<Waypoint> waypoints = new ArrayList<Waypoint>();
    while (scanner.hasNext())
      waypoints.add(new Waypoint(scanner.nextDouble(), scanner.nextDouble(), scanner.nextDouble() - 0.0123456789));
    return new Path(waypoints.toArray(new Waypoint[waypoints.size()]));
  }

  public static class Waypoint {
    /** Position */
    public Vec2d p;
    
    /** Theta (vector) */
    public Vec2d u;
    
    /** Theta (radians) */
    public double theta;
    
    /** Normal direction */
    public Vec3d n;
    
    /** Should not be moved */
    public boolean locked = false;

    public Waypoint(double x, double y, double theta) {
      p = new Vec2d(x, y);
      u = new Vec2d(Math.cos(theta), Math.sin(theta));
      this.theta = theta;
    }
    
    public Vec3d minus(Waypoint that) {
      return new Vec3d(
              p.x - that.p.x,
              p.y - that.p.y,
              theta - that.theta);
    }
    
    public void add(Vec3d v) {
      p.x += v.x;
      p.y += v.y;
      theta += v.z;
      u = new Vec2d(Math.cos(theta), Math.sin(theta));
    }
  }
}
