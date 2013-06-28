package cspace.scene;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import jgl.math.vector.Vec2d;
import jgl.math.vector.Vec2f;
import jgl.math.vector.Vec3f;

import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;
import org.yaml.snakeyaml.error.YAMLException;

import cspace.util.ColorMap;

/**
 * Contains properties for scene components used in rendering. This configuration is loaded on
 * start-up and saved on exit. If no properties file exists (or a corrupt file is read), a default
 * configuration is generated.
 * 
 * @author justin
 */
public class SceneView {

  public static class Renderer {

    public enum ViewMode {
      VIEW_2D("2D Only"),
      VIEW_3D("3D Only"),
      VIEW_SPLIT("Split 2D/3D");

      private final String toString;

      private ViewMode(String toString) {
        this.toString = toString;
      }

      @Override
      public String toString() {
        return toString;
      }
    }

    public Vec3f    background      = new Vec3f(0.0f);
    public ViewMode viewMode        = ViewMode.VIEW_SPLIT;
    public boolean  fixedWidthEdges = true;
    public boolean  drawPiPlanes    = false;
    public boolean  drawAxes        = false;
    public boolean  drawPiClipped   = false;
    public int      periods3d       = 1;
  }

  public static class Camera2D {
    public Vec2f initialCenter = new Vec2f();
    public float initialScale  = 1;
  }

  public static class Camera3D {
    public Vec3f initialEye    = new Vec3f(0, -10, 10);
    public Vec3f initialCenter = new Vec3f();
    public float fieldOfView   = 60;
  }

  public static class Contacts {
    public Vec3f   intnColor     = new Vec3f(0.6f, 0.0f, 0.6f);
    public Vec3f   sveColor      = new Vec3f(0.6f, 0.3f, 0.0f);
    public Vec3f   sevColor      = new Vec3f(0.3f, 0.0f, 0.6f);
    public boolean sveVisible2d  = false;
    public boolean sveVisible3d  = false;
    public boolean sevVisible2d  = false;
    public boolean sevVisible3d  = false;
    public boolean intnVisible2d = false;
    public boolean intnVisible3d = false;
  }

  public static class Obstacle {
    public Vec3f   color         = new Vec3f(0.35f);
    public float   edgeWidth     = 0.03f;
    public float   edgeDetail    = 0.001f;
    public boolean visible2d     = true;
    public boolean visible3d     = false;
    public boolean originVisible = false;
  }

  public static class Path {
    public Vec3f   color     = new Vec3f(1);
    public float   edgeWidth = 0.03f;
    public boolean visible2d = false;
    public boolean visible3d = false;
  }

  public static class Robot {
    public Vec3f   color         = new Vec3f(1, 0.4f, 0.4f);
    public float   edgeWidth     = 0.03f;
    public float   edgeDetail    = 0.001f;
    public boolean visible2d     = true;
    public boolean visible3d     = true;
    public boolean originVisible = true;
    public boolean cameraRobot   = false;
    public Vec2d   position      = new Vec2d();
    public Vec2d   rotation      = new Vec2d(1, 0);
  }

  public static class Subs {

    public enum ColorStyle3D {
      UNIFORM,
      NORMALS,
      PER_SUB,
      PER_SUM
    }

    public enum ClipStyle3D {
      DEFAULT,
      CLIP_ABOVE_THETA,
      CLIP_BELOW_THETA,
      HIGHLIGHT_THETA
    }

    public Vec3f          color         = new Vec3f(0.6f, 1.0f, 0.4f);
    public float          edgeWidth     = 0.03f;
    public float          edgeDetail    = 0.001f;
    public float          drawAlpha     = 1.0f;
    public boolean        visible2d     = true;
    public boolean        visible3d     = true;
    public boolean        shaded        = true;
    public boolean        wireframed    = false;
    public ColorStyle3D   colorStyle3d  = ColorStyle3D.UNIFORM;
    public ClipStyle3D    clipStyle3d   = ClipStyle3D.DEFAULT;
    public double         alphaSampling = 0.1;
    public double         thetaSampling = 0.01;
    private ColorMap<Sub> colors        = new ColorMap<Sub>();

    public Vec3f getColor(Sub sub) {
      return colors.get(sub);
    }
  }

  public static class Sums {
    public Vec3f               color      = new Vec3f(0.3f, 0.3f, 0.3f);
    public float               edgeWidth  = 0.03f;
    public float               edgeDetail = 0.001f;
    public boolean             visible2d  = false;
    public boolean             visible3d  = false;
    public List<EdgePair>      drawn3d    = new ArrayList<EdgePair>();
    private ColorMap<EdgePair> colors     = new ColorMap<EdgePair>();

    public Vec3f getColor(EdgePair sum) {
      return colors.get(sum);
    }
  }

  public Renderer renderer = new Renderer();
  public Camera2D camera2d = new Camera2D();
  public Camera3D camera3d = new Camera3D();
  public Contacts contacts = new Contacts();
  public Obstacle obstacle = new Obstacle();
  public Path     path     = new Path();
  public Robot    robot    = new Robot();
  public Subs     subs     = new Subs();
  public Sums     sums     = new Sums();
  private File    file;

  public SceneView() {
  }

  private SceneView(File file) {
    this.file = file;
  }

  public static SceneView load(File file) {
    try {
      Yaml yaml = new Yaml(new Constructor(SceneView.class));
      SceneView visuals = (SceneView) yaml.load(new FileInputStream(file));
      visuals.file = file;
      return visuals;
    } catch (FileNotFoundException e) {
      return new SceneView(file);
    } catch (YAMLException e) {
      System.out.println("Error parsing " + file.getName() + " -- creating default scene view.");
      return new SceneView(file);
    }
  }

  public void save() {
    try {
      Yaml yaml = new Yaml(new Constructor(SceneView.class));
      FileOutputStream out = new FileOutputStream(file);
      out.write(yaml.dump(this).getBytes());
      out.close();
    } catch (IOException e) {
      System.err.println("ERROR writing file: " + file);
    }
  }
}
