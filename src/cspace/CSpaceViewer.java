package cspace;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;

import cspace.gui.MainWindow;
import cspace.scene.Scene;
import cspace.scene.SceneLoader;

/**
 * Main program.
 */
public class CSpaceViewer {

  private MainWindow      mainWindow;
  private SceneController sceneController;

  public CSpaceViewer(File initialScene) {
    mainWindow = new MainWindow(this);
    mainWindow.addWindowListener(new WindowAdapter() {
      public void windowClosing(WindowEvent we) {
        if (sceneController != null)
          sceneController.shutdown();
        mainWindow.getAnimator().stop();
      }
    });
    mainWindow.setVisible(true);

    if (initialScene != null)
      openScene(initialScene);
  }

  public MainWindow getMainWindow() {
    return mainWindow;
  }

  public SceneController getSceneController() {
    return sceneController;
  }

  public void openScene(File directory) {
    if (sceneController != null) {
      sceneController.shutdown();
    }
    Scene scene = new SceneLoader().load(directory);
    sceneController = new SceneController(scene, mainWindow);
    mainWindow.setController(sceneController);
  }

  public static void main(String[] args) {
    File directory = null;
    if (args.length > 0) {
      directory = new File(args[0]);
      if (!directory.exists() || !directory.isDirectory()) {
        System.err.println("Invalid scene directory: " + args[0]);
        directory = null;
      }
    }
    new CSpaceViewer(directory);
  }
}
