package cspace.visuals;

import java.util.HashMap;
import java.util.Map;

import jgl.math.Maths;
import jgl.math.vector.Vec3f;
import cspace.model.Sub;

public class SubVisuals extends EdgeVisuals {
  Map<Sub, Vec3f> colors            = new HashMap<Sub, Vec3f>();
  boolean         shading           = true;
  boolean         wireframe         = false;
  SubColoring     coloring          = SubColoring.SOLID_COLOR;
  SubRenderStyle  style             = SubRenderStyle.SOLID;
  double          samplingLength    = 0.1;
  double          samplingThreshold = 0.01;
  int             selected          = -1;

  public SubVisuals() {
    color = new Vec3f(.4f, .8f, .28f);
  }

  public Vec3f getColor(Sub sub) {
    Vec3f color = colors.get(sub);
    if (color == null) {
      float r = Maths.random() * 0.8f + 0.2f;
      float g = Maths.random() * 0.8f + 0.2f;
      float b = Maths.random() * 0.8f + 0.2f;
      colors.put(sub, color = new Vec3f(r, g, b));
    }
    return color;
  }

  public boolean isShading() {
    return shading;
  }

  public void setShading(boolean shading) {
    this.shading = shading;
    updateVisuals();
  }

  public boolean isWireframe() {
    return wireframe;
  }

  public void setWireframe(boolean wireframe) {
    this.wireframe = wireframe;
    updateVisuals();
  }

  public SubColoring getColoring() {
    return coloring;
  }

  public void setColoring(SubColoring coloring) {
    this.coloring = coloring;
    setGeomUpdated2d(true);
    updateVisuals();
  }

  public SubRenderStyle getStyle() {
    return style;
  }

  public void setStyle(SubRenderStyle style) {
    this.style = style;
    updateVisuals();
  }

  public double getSamplingLength() {
    return samplingLength;
  }

  public void setSamplingLength(double samplingLength) {
    this.samplingLength = samplingLength;
  }

  public double getSamplingThreshold() {
    return samplingThreshold;
  }

  public void setSamplingThreshold(double samplingThreshold) {
    this.samplingThreshold = samplingThreshold;
  }
  
  public int getSelected() {
    return selected;
  }
  
  public void setSelected(int selected) {
    this.selected = selected;
  }

  public enum SubRenderStyle {
    SOLID("Solid"),
    TRANSLUCENT("Translucent"),
    CLIP_ABOVE("Clip Above Theta"),
    CLIP_BELOW("Clip Below Theta"),
    CLIP_AROUND("Clip Around Theta"),
    BORDER_THETA("Arcs Border Theta");

    private final String toString;

    private SubRenderStyle(String toString) {
      this.toString = toString;
    }

    @Override
    public String toString() {
      return toString;
    }
  }

  public enum SubColoring {
    SOLID_COLOR("Edge Color"),
    UNIQUE_COLOR("Random / Unique"),
    NORMAL_COLOR("Normal Vectors"),
    SUMEE_COLOR("SumEE Color");

    private final String toString;

    private SubColoring(String toString) {
      this.toString = toString;
    }

    @Override
    public String toString() {
      return toString;
    }
  }
}
