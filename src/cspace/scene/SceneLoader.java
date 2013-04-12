package cspace.scene;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;


public class SceneLoader {

  public Scene load(File directory) {
    if (directory == null)
      return null;
    CSpace cspace = null;
    Path path = null;
    SceneView view = null;

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

    view = SceneView.load(getDataFile(directory, "visuals.yml"));

    return new Scene(cspace, path, view);
  }

  private File getDataFile(File directory, String name) {
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
