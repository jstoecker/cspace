package cspace.scene;

import jgl.math.geometry.Ray;
import jgl.math.vector.Vec3f;
import cspace.scene.Path.Waypoint;
import cspace.scene.trimesh.PathMaker;
import cspace.scene.trimesh.SampledCSpace;
import cspace.scene.trimesh.SampledSub.Triangle;
import cspace.scene.visuals.Visuals;

public class Scene {

  public SampledCSpace sampledCS;
  public CSpace        cspace;
  public Visuals       visuals;
  public Path          path;
  public Path          newPath;  // path being built (NULL if not building path)

  public Scene(CSpace cspace, Path path, Visuals visuals) {
    this.cspace = cspace;
    this.path = path;
    this.visuals = visuals;
    sampleCS();
  }

  public void sampleCS() {
    sampledCS = new SampledCSpace(cspace, visuals.subVisuals.getSamplingThreshold(),
        visuals.subVisuals.getSamplingLength());
  }

  public void updatePath() {
    Vec3f s = new Vec3f((float) newPath.waypoints[0].p.x, (float) newPath.waypoints[0].p.y,
        (float) newPath.waypoints[0].theta);
    Vec3f e = new Vec3f((float) newPath.waypoints[1].p.x, (float) newPath.waypoints[1].p.y,
        (float) newPath.waypoints[1].theta);

    Triangle end = sampledCS.intersect(new Ray(e, s.minus(e).normalize())).t;
    Triangle start = sampledCS.intersect(new Ray(s, e.minus(s).normalize())).t;

    if (start != null && end != null) {
      PathMaker pm = new PathMaker();
      Path p = pm.makePath(start, end, sampledCS);

      Waypoint[] newwp = new Waypoint[p.waypoints.length + 2];
      newwp[0] = newPath.waypoints[0];
      for (int i = 0; i < p.waypoints.length; i++)
        newwp[i + 1] = p.waypoints[i];
      newwp[newwp.length - 1] = newPath.waypoints[1];
      path.waypoints = newwp;
      visuals.pathVisuals.setGeomUpdated(true);
      visuals.pathVisuals.setWaypointsChanged(true);
    }

    newPath = null;
  }
}
