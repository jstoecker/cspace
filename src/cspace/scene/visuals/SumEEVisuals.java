package cspace.scene.visuals;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jgl.math.Maths;
import jgl.math.vector.Vec3f;
import cspace.scene.SumEE;

public class SumEEVisuals extends EdgeVisuals {

  /** Used to hash colors based on pair of edge indices */
  private static class EdgeSum {
    final int robEdge, obsEdge;

    public EdgeSum(int robEdge, int obsEdge) {
      this.robEdge = robEdge;
      this.obsEdge = obsEdge;
    }

    @Override
    public int hashCode() {
      final int prime = 31;
      int result = 1;
      result = prime * result + obsEdge;
      result = prime * result + robEdge;
      return result;
    }

    @Override
    public boolean equals(Object obj) {
      if (obj == null || obj.getClass() != getClass())
        return false;
      EdgeSum that = (EdgeSum) obj;
      return that.robEdge == robEdge && that.obsEdge == obsEdge;
    }
  }

  Map<EdgeSum, Vec3f> colors = new HashMap<EdgeSum, Vec3f>();
  List<Integer> selected = new ArrayList<Integer>();

  public SumEEVisuals() {
    color = new Vec3f(0, .6f, 1);
    visible3d = false;
    visible2d = false;
  }

  public List<Integer> getSelected() {
    return selected;
  }

  public void setSelected(List<Integer> selected) {
    this.selected = selected;
  }

  public Vec3f getColor(int robEdge, int obsEdge) {
    EdgeSum sum = new EdgeSum(robEdge, obsEdge);
    Vec3f color = colors.get(sum);
    if (color == null) {
      float r = Maths.random() * 0.8f + 0.2f;
      float g = Maths.random() * 0.8f + 0.2f;
      float b = Maths.random() * 0.8f + 0.2f;
      color = new Vec3f(r, g, b);
      colors.put(sum, color = new Vec3f(r, g, b));
    }
    return color;
  }

  public boolean isSelected(SumEE see) {
    for (int i = 0; i < selected.size(); i += 2) {
      int robEdge = selected.get(i);
      int obsEdge = selected.get(i + 1);
      if (see.robEdge.index == robEdge && see.obsEdge.index == obsEdge)
        return true;
    }
    return false;
  }
}
