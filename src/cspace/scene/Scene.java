package cspace.scene;


/**
 * Stores data associated with a configuration space scene.
 * 
 * @author justin
 */
public class Scene {

  public CSpace        cspace;
  public Path          path;
  public SceneView     view;

  public Scene(CSpace cspace, Path path, SceneView view) {
    this.cspace = cspace;
    this.path = path;
    this.view = view;
    cspace.sample(view.subs.thetaSampling, view.subs.alphaSampling);
  }
}
