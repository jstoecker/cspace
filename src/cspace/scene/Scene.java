package cspace.scene;

import cspace.scene.triangulate.SampledCSpace;

/**
 * Stores data associated with a configuration space scene.
 * 
 * @author justin
 */
public class Scene {

  public SampledCSpace sampledCS;
  public CSpace        cspace;
  public Path          path;
  public SceneView     view;

  public Scene(CSpace cspace, Path path, SceneView view) {
    this.cspace = cspace;
    this.path = path;
    this.view = view;
    createMesh();
  }

  public void createMesh() {
    sampledCS = new SampledCSpace(cspace, view.subs.thetaSampling, view.subs.alphaSampling);
  }
}
