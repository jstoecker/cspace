package cspace.ui.swing;

import java.awt.BorderLayout;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.media.opengl.GLCapabilities;
import javax.media.opengl.GLProfile;
import javax.media.opengl.awt.GLCanvas;
import javax.swing.JFrame;

import cspace.SceneRenderer;
import cspace.scene.Scene;
import cspace.scene.visuals.Visuals;
import cspace.scene.visuals.Visuals.VisualChangeEvent;
import cspace.ui.SceneController;

/**
 * Main application JFrame that contains the GLCanvas and other components.
 * 
 * @author justin
 */
public class MainWindow extends JFrame {

  private SettingsDialog  settingsDialog;
  private GLCanvas        canvas;
  private Scene           scene;
  private SceneRenderer   renderer;
  private SceneController controller;

  public MainWindow(final Scene scene) {
    this.scene = scene;

    GLProfile glp = GLProfile.get(GLProfile.GL2);
    GLCapabilities glc = new GLCapabilities(glp);
    glc.setSampleBuffers(true);
    glc.setNumSamples(8);
    glc.setDepthBits(32);
    canvas = new GLCanvas(glc);

    renderer = new SceneRenderer(scene);
    controller = new SceneController(scene, renderer, canvas);

    canvas.addMouseListener(controller);
    canvas.addMouseWheelListener(controller);
    canvas.addMouseMotionListener(controller);
    canvas.addKeyListener(controller);

    scene.visuals.addListener(new Visuals.Listener() {
      public void visualsUpdated(Visuals visuals, VisualChangeEvent evt) {
        canvas.repaint();
      }
    });

    addWindowListener(new WindowAdapter() {
      public void windowClosing(WindowEvent we) {
        scene.visuals.save();
      }
    });

    settingsDialog = new SettingsDialog(this);

    setTitle("Configuration Space Visualization");
    setLayout(new BorderLayout());
    setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    setSize(1280, 720);
    setLocationRelativeTo(null);
    getContentPane().add(new MainWidgetPanel(this), BorderLayout.SOUTH);
    getContentPane().add(canvas, BorderLayout.CENTER);

    // new TestPanel(scene.sampledCS, scene.path, scene.visuals, canvas).setVisible(true);
  }

  public SettingsDialog getSettingsDialog() {
    return settingsDialog;
  }

  public Scene getScene() {
    return scene;
  }

  public SceneController getController() {
    return controller;
  }

  public void repaintGL() {
    canvas.repaint();
  }
}
