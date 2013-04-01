package cspace.visuals;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;
import org.yaml.snakeyaml.error.YAMLException;

/**
 * Visual attributes for the configuration space parts.
 */
public class Visuals {

  public GeneralVisuals genVisuals = new GeneralVisuals();
  public SubVisuals subVisuals = new SubVisuals();
  public SumEEVisuals sumEEVisuals = new SumEEVisuals();
  public ObstacleVisuals obstacleVisuals = new ObstacleVisuals();
  public PathVisuals pathVisuals = new PathVisuals();
  public RobotVisuals robotVisuals = new RobotVisuals();
  public PntVisuals pntVisuals = new PntVisuals();
  public CameraVisuals cameraVisuals = new CameraVisuals();
  private List<Listener> listeners = new ArrayList<Listener>();
  private File file;

  public Visuals() {
  }

  Visuals(File file) {
    this.file = file;
    init();
  }

  final void init() {
    genVisuals.parent = this;
    robotVisuals.parent = this;
    subVisuals.parent = this;
    sumEEVisuals.parent = this;
    obstacleVisuals.parent = this;
    pathVisuals.parent = this;
    pntVisuals.parent = this;
    cameraVisuals.parent = this;
  }

  void updateVisuals(VisualGroup source) {
    for (Listener l : listeners) {
      l.visualsUpdated(this, new VisualChangeEvent(source));
    }
  }

  public static Visuals load(File file) {
    try {
      Yaml yaml = new Yaml(new Constructor(Visuals.class));
      Visuals visuals = (Visuals) yaml.load(new FileInputStream(file));
      visuals.init();
      visuals.file = file;
      return visuals;
    } catch (FileNotFoundException e) {
      return new Visuals(file);
    } catch (YAMLException e) {
      System.err.println("Could not parse visuals.yml -- using defaults");
      return new Visuals(file);
    }
  }

  public void save() {
    try {
      Yaml yaml = new Yaml(new Constructor(Visuals.class));
      FileOutputStream out = new FileOutputStream(file);
      out.write(yaml.dump(this).getBytes());
      out.close();
    } catch (IOException e) {
      System.err.println("ERROR writing file: " + file);
    }
  }

  public void addListener(Listener l) {
    listeners.add(l);
  }

  // ===========================================================================
  public interface Listener {

    void visualsUpdated(Visuals visuals, VisualChangeEvent evt);
  }

  // ===========================================================================
  public class VisualChangeEvent {

    public final VisualGroup source;

    public VisualChangeEvent(VisualGroup source) {
      this.source = source;
    }
  }
}
