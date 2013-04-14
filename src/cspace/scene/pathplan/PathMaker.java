package cspace.scene.pathplan;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;

import jgl.math.geometry.Ray;
import jgl.math.vector.Vec3d;
import jgl.math.vector.Vec3f;
import cspace.scene.Path;
import cspace.scene.triangulate.SampledCSpace;
import cspace.scene.triangulate.SampledSub.RayTriIntersection;
import cspace.scene.triangulate.SampledSub.Triangle;

/**
 * Makes a path along the subs of the sampled cspace using a breadth first
 * search of the triangles.
 * 
 * @author justin
 */
public class PathMaker {

  private Set<Triangle> marked;
  private Map<Triangle, Parent> parents;
  private Queue<Triangle> queue;

  public Path makePath(Triangle start, Triangle goal, SampledCSpace cs) {
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
        waypoint.n = t.normal().plus(p.t.normal()).normalize();
        wp.add(0, waypoint);
        t = p.t;
      }
//      trimPath(wp, cs);
      return new Path(wp.toArray(new Path.Waypoint[wp.size()]));
    }
  }

  private void trimPath(List<Path.Waypoint> wp, SampledCSpace cs) {

    // push vertices inward
    for (int i = 0; i < wp.size(); i++) {
      Path.Waypoint p = wp.get(i);
      Vec3f pp = new Vec3f((float) p.p.x, (float) p.p.y, (float) p.theta);
      RayTriIntersection x = cs.intersect(new Ray(pp, p.n.toFloat().times(-1)));
      if (x != null) {
        float d = x.p.minus(pp).length();
        if (d <= 0) {
        }
        p.add(new Vec3d(p.n.x, p.n.y, p.n.z).times(-d/2));
      } else {
        System.out.println("NULL X");
      }
    }
//    if (true)
//      return;
//
//    for (int i = 0; i < wp.size() - 2; i++) {
//      Path.Waypoint pi = wp.get(i);
//
//      // NEED TO CHECK THAT TRIMMING WONT BE A LINE OUTSIDE OF CSPACE
//      // OR PUSHOFF SO VERTS ON INSIDE, NOT ON EDGES
//      for (int j = i + 2; j < wp.size(); j++) {
//        Path.Waypoint pj = wp.get(j);
//        Vec3d v = pj.minus(pi);
//        Vec3f pip = new Vec3d(pi.p.x, pi.p.y, pi.theta).toFloat();
//        Ray r = new Ray(pip, v.toFloat());
//        RayTriIntersection x = cs.intersect(r);
//
//        // remove at j-1 if there is no obstacle between pi and pj
//        if (x == null || x.p.minus(pip).lengthSquared() > v.lengthSquared()) {
//          wp.remove(j - 1);
//        }
//      }
//    }
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
    final int edge;

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
