package cspace.ui.swing;

import java.awt.BorderLayout;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.media.opengl.GLCapabilities;
import javax.media.opengl.GLProfile;
import javax.media.opengl.awt.GLCanvas;
import javax.swing.JFrame;

import cspace.scene.Scene;
import cspace.ui.SceneController;

/**
 * Main application JFrame that contains the GLCanvas and other components.
 * 
 * @author justin
 */
public class MainWindow extends JFrame {

  private SettingsDialog  settingsDialog;
  private GLCanvas        canvas;
  private SceneController controller;

  public MainWindow(Scene scene) {

    GLProfile glp = GLProfile.get(GLProfile.GL2);
    GLCapabilities glc = new GLCapabilities(glp);
    glc.setSampleBuffers(true);
    glc.setNumSamples(8);
    glc.setDepthBits(32);
    canvas = new GLCanvas(glc);

    controller = new SceneController(scene, canvas);

    canvas.addMouseListener(controller);
    canvas.addMouseWheelListener(controller);
    canvas.addMouseMotionListener(controller);
    canvas.addKeyListener(controller);

    addWindowListener(new WindowAdapter() {
      public void windowClosing(WindowEvent we) {
        controller.shutdown();
      }
    });

    settingsDialog = new SettingsDialog(this, scene, controller.getRenderer());

    setTitle("Configuration Space Visualization");
    setLayout(new BorderLayout());
    setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    setSize(1280, 720);
    setLocationRelativeTo(null);
    getContentPane().add(new MainToolBar(this, controller), BorderLayout.SOUTH);
    getContentPane().add(canvas, BorderLayout.CENTER);
  }

  public SettingsDialog getSettingsDialog() {
    return settingsDialog;
  }

  public SceneController getController() {
    return controller;
  }
}
