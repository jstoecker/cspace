package cspace.gui;

import java.awt.BorderLayout;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.media.opengl.GLCapabilities;
import javax.media.opengl.GLProfile;
import javax.media.opengl.awt.GLCanvas;
import javax.swing.JFrame;

import cspace.SceneController;
import cspace.SceneRenderer;
import cspace.model.Scene;
import cspace.visuals.Visuals;
import cspace.visuals.Visuals.VisualChangeEvent;

public class SceneWindow extends JFrame {

  VisualsFrame visualsFrame;
  GLCanvas canvas;
  Scene scene;
  SceneRenderer view;
  SceneController controller;

  public SceneWindow(final Scene scene) {
    this.scene = scene;

    GLProfile glp = GLProfile.get(GLProfile.GL2);
    GLCapabilities glc = new GLCapabilities(glp);
    glc.setSampleBuffers(true);
    glc.setNumSamples(8);
    glc.setDepthBits(32);
    canvas = new GLCanvas(glc);
    
    view = new SceneRenderer(scene);
    controller = new SceneController(view);

    canvas.addGLEventListener(view);
    canvas.addMouseListener(controller);
    canvas.addMouseWheelListener(controller);
    canvas.addMouseMotionListener(controller);
    canvas.addKeyListener(controller);

    controller.listeners.add(new SceneController.Listener() {
      public void viewUpdated(SceneRenderer view) {
        canvas.repaint();
      }
    });

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

    visualsFrame = new VisualsFrame(this);

    setTitle("Configuration Space Visualization");
    setLayout(new BorderLayout());
    setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    setSize(1280, 720);
    setLocationRelativeTo(null);
    getContentPane().add(new SettingsPanel(this), BorderLayout.SOUTH);
    getContentPane().add(canvas, BorderLayout.CENTER);
    
//    new TestPanel(scene.sampledCS, scene.path, scene.visuals, canvas).setVisible(true);
  }

  public void repaintGL() {
    canvas.repaint();
  }
}
