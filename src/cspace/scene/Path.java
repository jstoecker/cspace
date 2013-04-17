package cspace.scene;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import jgl.math.vector.Vec2d;
import jgl.math.vector.Vec3d;

/**
 * A list of waypoints (x, y, theta) for the robot.
 */
public class Path {

  public List<Waypoint> waypoints;

  public Path(List<Waypoint> waypoints) {
    this.waypoints = waypoints;
  }

  public static Path load(File file) throws FileNotFoundException {
    Scanner scanner = new Scanner(file);
    ArrayList<Waypoint> waypoints = new ArrayList<Waypoint>();
    while (scanner.hasNext())
      waypoints.add(new Waypoint(scanner.nextDouble(), scanner.nextDouble(), scanner.nextDouble() - 0.0123456789));
    return new Path(waypoints);
  }
  
  public void write(File file) throws IOException {
    BufferedWriter bw = new BufferedWriter(new FileWriter(file));
    for (Waypoint wp : waypoints) {
      bw.write(String.format("%f %f %f\n", wp.p.x, wp.p.y, wp.theta + 0.0123456789));
    }
    bw.close();
  }

  public static class Waypoint {
    public Vec2d p;
    public Vec2d u;
    public double theta;
    
    public Waypoint(double x, double y, double theta) {
      p = new Vec2d(x, y);
      u = new Vec2d(Math.cos(theta), Math.sin(theta));
      this.theta = theta;
    }
    
    public Waypoint(Vec3d p) {
      this.p = new Vec2d(p.x, p.y);
      this.u = new Vec2d(Math.cos(p.z), Math.sin(p.z));
      this.theta = p.z;
    }
    
    public Vec3d toVector() {
      return new Vec3d(p.x, p.y, theta);
    }
  }
}
