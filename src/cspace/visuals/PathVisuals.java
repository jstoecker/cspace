package cspace.visuals;

import jgl.math.vector.Vec3f;

public class PathVisuals extends EdgeVisuals {

  private boolean waypointsChanged = false;
  
  public PathVisuals() {
    color = new Vec3f(1, 1, .4f);
    visible2d = visible3d = false;
  }

  public boolean isWaypointsChanged() {
    return waypointsChanged;
  }

  public void setWaypointsChanged(boolean waypointsChanged) {
    this.waypointsChanged = waypointsChanged;
    updateVisuals();
  }
}
