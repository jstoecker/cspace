package cspace.scene;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;

import jgl.math.geometry.Ray;
import jgl.math.vector.ConstVec2d;
import jgl.math.vector.Vec2d;
import jgl.math.vector.Vec3d;
import jgl.math.vector.Vec3f;
import cspace.scene.Path.Waypoint;
import cspace.scene.Sub.Intersection;
import cspace.scene.Sub.Triangle;
import cspace.util.PathSmoother;

/**
 * Creates a path using a starting and ending configuration.
 * 
 * @author justin
 */
public class PathFinder {

  private Set<Triangle>         marked;
  private Map<Triangle, Parent> parents;
  private Queue<Triangle>       queue;

  private Vec2d                 robotStartPos;
  private Vec2d                 robotStartRot;
  private Vec2d                 robotEndPos;
  private Vec2d                 robotEndRot;

  public Vec2d getRobotStartPos() {
    return robotStartPos;
  }

  public Vec2d getRobotStartRot() {
    return robotStartRot;
  }

  public void setRobotStart(ConstVec2d position, ConstVec2d rotation) {
    this.robotStartPos = position.copy();
    this.robotStartRot = rotation.copy();
  }

  public void setRobotEnd(Vec2d position, Vec2d rotation) {
    this.robotEndPos = position;
    this.robotEndRot = rotation;
  }

  public Path makePath(CSpace cspace) {
    if (robotStartPos == null || robotEndPos == null)
      return null;

    Vec3d s = new Vec3d(robotStartPos, robotStartRot.anglePi());
    Vec3d e = new Vec3d(robotEndPos, robotEndRot.anglePi());
    
    Intersection xEnd = cspace.intersect(new Ray(s.toFloat(), e.minus(s).toFloat()));
    if (xEnd == null)
      return null;

    Intersection xStart = cspace.intersect(new Ray(e.toFloat(), s.minus(e).toFloat()));
    if (xStart == null)
      return null;

    List<Waypoint> waypoints = makePath(xStart.t, xEnd.t);
    Collections.reverse(waypoints);
    waypoints.add(0, new Waypoint(s));
    waypoints.add(new Waypoint(e));
    
    Path path = new Path(waypoints);
    // disabled because of bug with rotation in smoothed path
//    path = new PathSmoother().smooth(path, 0.1);
    return path;
  }

  private List<Waypoint> makePath(Triangle start, Triangle goal) {
    queue = new LinkedList<Triangle>();
    marked = new HashSet<Triangle>();
    parents = new HashMap<Triangle, Parent>();

    goal = search(start, goal);

    if (goal == null) {
      // no path found
      return null;
    } else {
      List<Path.Waypoint> wp = new ArrayList<Path.Waypoint>();
      Triangle t = goal;
      Parent p = null;
      while ((p = parents.get(t)) != null) {
        Vec3d mp = p.sharedEdgeMidpoint();
        Path.Waypoint waypoint = new Path.Waypoint(mp.x, mp.y, mp.z);
        wp.add(0, waypoint);
        t = p.t;
      }
      return wp;
    }
  }

  private Triangle search(Triangle start, Triangle goal) {
    marked.add(start);
    queue.add(start);

    while (!queue.isEmpty()) {
      Triangle t = queue.poll();
      if (t == goal) {
        return goal;
      }

      for (int i = 0; i < 3; i++) {
        Triangle child = t.neighbors[i];
        if (child != null && !marked.contains(child)) {
          parents.put(child, new Parent(t, i));
          marked.add(child);
          queue.add(child);
        }
      }
    }

    return null;
  }

  private class Parent {
    final Triangle t;
    final int      edge;

    Parent(Triangle parent, int sharedEdge) {
      this.t = parent;
      this.edge = sharedEdge;
    }

    Vec3d sharedEdgeMidpoint() {
      switch (edge) {
      case 0:
        return t.b.position.plus(t.a.position).over(2);
      case 1:
        return t.c.position.plus(t.b.position).over(2);
      case 2:
        return t.a.position.plus(t.c.position).over(2);
      default:
        return null;
      }
    }
  }

}
