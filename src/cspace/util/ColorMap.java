package cspace.util;

import java.util.HashMap;

import jgl.math.Maths;
import jgl.math.vector.Vec3f;

/**
 * Maps objects to colors. Colors are randomly generated unless manually mapped.
 * 
 * @author justin
 */
public class ColorMap<K> {

  private HashMap<K, Vec3f> colors = new HashMap<K, Vec3f>();

  public Vec3f get(K key) {
    Vec3f color = colors.get(key);
    if (color == null) {
      float r = Maths.random() * 0.8f + 0.2f;
      float g = Maths.random() * 0.8f + 0.2f;
      float b = Maths.random() * 0.8f + 0.2f;
      colors.put(key, color = new Vec3f(r, g, b));
    }
    return color;
  }

  public void set(K key, Vec3f color) {
    colors.put(key, color);
  }

  public void clearColor(K key) {
    colors.put(key, null);
  }
}
