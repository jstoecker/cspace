package cspace.visuals;

import jgl.math.vector.Vec3f;

public class GeneralVisuals extends VisualGroup {

  ViewMode view = ViewMode.VIEW_SPLIT;
  Vec3f bgColor = new Vec3f(1);
  float scale = 1;
  boolean fixedWidthEdges = true;
  float edgeScale = 1;

  public Vec3f getBgColor() {
    return bgColor;
  }

  public void setBgColor(Vec3f bgColor) {
    this.bgColor = bgColor;
    updateVisuals();
  }

  public ViewMode getView() {
    return view;
  }

  public void setView(ViewMode view) {
    this.view = view;
    updateVisuals();
  }

  public float getScale() {
    return scale;
  }

  public void setScale(float scale) {
    this.scale = scale;
    updateVisuals();
  }

  public boolean isFixedWidthEdges() {
    return fixedWidthEdges;
  }

  public void setFixedWidthEdges(boolean fixedWidthEdges) {
    this.fixedWidthEdges = fixedWidthEdges;
    if (parent != null) {
      parent.obstacleVisuals.geomUpdated2d = true;
      parent.robotVisuals.geomUpdated2d = true;
      parent.pathVisuals.geomUpdated2d = true;
      parent.sumEEVisuals.geomUpdated2d = true;
      parent.subVisuals.geomUpdated2d = true;
    }
    updateVisuals();
  }

  public float getEdgeScale() {
    return fixedWidthEdges ? edgeScale : 1;
  }

  public void setEdgeScale(float edgeScale) {
    this.edgeScale = edgeScale;
    if (parent != null) {
      parent.obstacleVisuals.geomUpdated2d = true;
      parent.robotVisuals.geomUpdated2d = true;
      parent.pathVisuals.geomUpdated2d = true;
      parent.sumEEVisuals.geomUpdated2d = true;
      parent.subVisuals.geomUpdated2d = true;
    }
    updateVisuals();
  }

  // ===========================================================================
  public enum ViewMode {

    VIEW_2D("2D"),
    VIEW_3D("3D"),
    VIEW_SPLIT("Split");
    private final String toString;

    private ViewMode(String toString) {
      this.toString = toString;
    }

    @Override
    public String toString() {
      return toString;
    }
  }
  // ===========================================================================
}
