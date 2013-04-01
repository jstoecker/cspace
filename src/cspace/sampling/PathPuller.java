package cspace.sampling;

import java.util.HashSet;
import java.util.Set;

import jgl.math.geometry.Ray;
import jgl.math.vector.Vec3d;
import jgl.math.vector.Vec3f;
import cspace.model.Path;
import cspace.model.Path.Waypoint;
import cspace.model.Sub;
import cspace.sampling.SampledSub.Triangle;
import cspace.sampling.SampledSub.Vertex;
import cspace.visuals.PathVisuals;

public class PathPuller {

  Path path;
  SampledCSpace cs;
  PathVisuals visuals;
  Vec3d[] velocities;
  Vec3d[] forces;
  Path.Waypoint[] waypoints;
  double timestep = 0.5;
  double damping = 0.9;
  double avoidRadiusSqr = 0.025;
  public boolean init = false;

  public PathPuller(SampledCSpace cs, Path path, PathVisuals visuals) {
    this.cs = cs;
    this.visuals = visuals;
    this.path = path;

//    for (Sub sub : cs.cspace.subs) {
//      SampledSub ss = cs.subSamplings.get(sub);
//      for (Vertex v : ss.verts) {
//        PathView.lines.add(new Vec3f[]{
//          new Vec3f(v.position),
//          new Vec3f(v.position.plus(v.normal.times(0.1f)))
//        });
//      }
//    }
  }

  public Path makePath() {
    Triangle start = cs.subSamplings.get(cs.cspace.subs[0]).triangles.get(0);
    Triangle end = cs.subSamplings.get(cs.cspace.subs[0]).triangles.get(0);

    Set<Triangle> available = new HashSet<Triangle>();
    Set<Triangle> visited = new HashSet<Triangle>();
    visited.add(start);
    visited.add(end);
    for (Triangle t : start.neighbors) {
      if (t != null && !visited.contains(t)) {
        available.add(t);
      }
    }
    return null;
  }
  
  public void iterate() {
    visuals.setGeomUpdated(true);

    if (!init) {
      forces = new Vec3d[path.waypoints.length];
      velocities = new Vec3d[path.waypoints.length];
      for (int i = 0; i < path.waypoints.length; i++) {
        forces[i] = new Vec3d(0);
        velocities[i] = new Vec3d(0);
      }
      waypoints = path.waypoints;
//      pushOff();
      init = true;
      return;
    }

    for (int i = 1; i < waypoints.length - 1; i++) {
      forces[i].multiply(0);
      avoidWalls(i);
      followNeighbors(i);
    }

    for (int i = 0; i < waypoints.length; i++) {
      applyForce(i);
    }
  }

  private void pushOff() {
    for (int i = 1; i < waypoints.length - 1; i++) {
      Waypoint wp = waypoints[i];
      wp.locked = true;
      Vec3d push = wp.n.times(-1);

      // intersect -normal with triangles to see how far we can push the wp
      if (push.lengthSquared() > 0) {
        push.normalize();
        Vec3f nearestX = null;
        double nearDist = Double.POSITIVE_INFINITY;

        Ray r = new Ray(new Vec3d(wp.p.x, wp.p.y, wp.theta).toFloat(), push.normalized().toFloat());

//        PathView.lines.add(new Vec3f[]{
//                  new Vec3f(r.p),
//                  r.p.plus(r.d.times(0.3f))
//                });

        for (Sub sub : cs.cspace.subs) {
          SampledSub ss = cs.subSamplings.get(sub);
          for (cspace.sampling.SampledSub.Triangle tri : ss.triangles) {
            Vertex a = tri.a;
            Vertex b = tri.b;
            Vertex c = tri.c;
            Vec3f x = r.intersect(new jgl.math.geometry.Triangle(
                a.position.toFloat(), 
               b.position.toFloat(), 
               c.position.toFloat()));
            
            // if no intersection, try shifting by 2pi
            if (x == null) {
              Vec3f pa = a.position.toFloat();
              Vec3f pb = b.position.toFloat();
              Vec3f pc = c.position.toFloat();
              pa.z += Math.PI * 2;
              pb.z += Math.PI * 2;
              pc.z += Math.PI * 2;
              x = r.intersect(new jgl.math.geometry.Triangle(pa, pb, pc));
            }
            // still no intersection, try shifting by -2p
            if (x == null) {
              Vec3f pa = a.position.toFloat();
              Vec3f pb = b.position.toFloat();
              Vec3f pc = c.position.toFloat();
              pa.z -= Math.PI * 2;
              pb.z -= Math.PI * 2;
              pc.z -= Math.PI * 2;
              x = r.intersect(new jgl.math.geometry.Triangle(pa, pb, pc));
            }
            if (x != null) {
              double d = new Vec3d(wp.p.x, wp.p.y, wp.theta).minus(x.x, x.y, x.z).length();
              if (d < nearDist && d > 0.1) {
                nearDist = d;
                nearestX = x;
                wp.locked = false;
              }
            }
          }
        }

        if (nearestX != null) {
          double dst = nearDist / 2;
          wp.p.x += push.x * dst;
          wp.p.y += push.y * dst;
          wp.theta += push.z * dst;
        }
      }
    }
  }

  private void avoidWalls(int wpIndex) {
    // coulomb repulsion

    Waypoint wp = waypoints[wpIndex];
    if (wp.locked) {
      return;
    }

    Vec3d f = new Vec3d(0);
    int numVerts = 0;

    for (Sub sub : cs.cspace.subs) {
      SampledSub ss = cs.subSamplings.get(sub);
      for (Vertex v : ss.verts) {

        Vec3d u = new Vec3d(
                wp.p.x - v.position.x,
                wp.p.y - v.position.y,
                wp.theta - v.position.z);

        double dSqr = u.lengthSquared();

        if (dSqr < avoidRadiusSqr && dSqr > 0.01) {
          Vec3d push = new Vec3d(-v.normal.x, -v.normal.y, -v.normal.z);
          push.divide(dSqr);
          f.add(push);

          numVerts++;
        }
      }
    }

    if (numVerts > 0) {
      forces[wpIndex].add(f.divide(numVerts).multiply(0.005));
    }
  }

  private void followNeighbors(int wpIndex) {
    Waypoint wp = waypoints[wpIndex];
    if (wp.locked) {
      return;
    }

    Vec3d netForce = forces[wpIndex];
    netForce.add((waypoints[wpIndex - 1]).minus(waypoints[wpIndex]));
    netForce.add((waypoints[wpIndex + 1]).minus(waypoints[wpIndex]));
  }

  private void applyForce(int i) {
    velocities[i] = (velocities[i].plus(forces[i].times(timestep))).times(damping);
    waypoints[i].add(velocities[i].times(timestep));
  }
}
