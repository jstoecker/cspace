package cspace;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import cspace.gui.SceneWindow;
import cspace.model.CSpace;
import cspace.model.CSpaceReader;
import cspace.model.Path;
import cspace.model.Scene;
import cspace.visuals.Visuals;

/**
 * Main program that creates both 2D and 3D visualizations, animating the robot
 * moving along a path
 */
public class CSpaceViewer {

  public static void main(String[] args) {
    if (args.length == 1) {
      File directory = new File(args[0]);
      if (!directory.exists() || !directory.isDirectory()) {
        System.out.println("Could not find directory: " + args[0]);
        System.exit(1);
      }

      Scene scene = loadScene(directory);
      SceneWindow window = new SceneWindow(scene);
      window.setVisible(true);
    } else {
      System.out.println("To run, provide the path to a cspace directory:");
      System.out.println("$ ./cspace.sh <cspace_dir>");
      System.exit(1);
    }
  }

  private static Scene loadScene(File directory) {
    CSpace cspace = null;
    Path path = null;
    Visuals visuals = null;

    try {
      cspace = new CSpaceReader().read(getDataFile(directory, "cspace"));
    } catch (FileNotFoundException e) {
      System.err.println("Could not find cspace file");
      return null;
    }

    try {
      path = Path.load(getDataFile(directory, "path"));
    } catch (FileNotFoundException e) {
    }

    visuals = Visuals.load(getDataFile(directory, "visuals.yml"));

    return new Scene(cspace, path, visuals);
  }

  private static File getDataFile(File directory, String name) {
    File file = new File(directory, name);

    // windows can't handle symbolic links and svn expands them to "link <path>"
    try {
      BufferedReader br = new BufferedReader(new FileReader(file));
      String line = br.readLine();
      if (line.startsWith("link ")) {
            file = new File(directory, line.substring(5));
        }
      br.close();
    } catch (IOException e) {
    }

    return file;
  }
}
