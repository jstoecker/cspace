package cspace.gui;

import java.awt.BorderLayout;

import javax.media.opengl.GL;
import javax.media.opengl.GL2;
import javax.media.opengl.GLAutoDrawable;
import javax.media.opengl.GLCapabilities;
import javax.media.opengl.GLEventListener;
import javax.media.opengl.GLProfile;
import javax.media.opengl.awt.GLCanvas;
import javax.swing.JFrame;
import javax.swing.JSlider;

import com.jogamp.opengl.util.FPSAnimator;

import cspace.CSpaceViewer;
import cspace.SceneController;

/**
 * Main application JFrame that contains the GLCanvas and other components.
 * 
 * @author justin
 */
public class MainWindow extends JFrame {

  private FPSAnimator animator;
  private GLCanvas    canvas;
  private MainToolBar toolBar;
  private EmptyScene  emptyScene = new EmptyScene();

  public MainWindow(CSpaceViewer viewer) {
    GLProfile glp = GLProfile.get(GLProfile.GL2);
    GLCapabilities glc = new GLCapabilities(glp);
    glc.setSampleBuffers(true);
    glc.setNumSamples(8);
    glc.setDepthBits(32);
    canvas = new GLCanvas(glc);
    animator = new FPSAnimator(canvas, 60);

    toolBar = new MainToolBar(viewer);

    setTitle("Configuration Space Visualization");
    setLayout(new BorderLayout());
    setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    setSize(1280, 720);
    setLocationRelativeTo(null);

    getContentPane().add(toolBar, BorderLayout.SOUTH);
    getContentPane().add(canvas, BorderLayout.CENTER);

    canvas.addGLEventListener(emptyScene);
    animator.start();
  }

  public FPSAnimator getAnimator() {
    return animator;
  }

  public void setController(SceneController controller) {
    canvas.removeGLEventListener(emptyScene);
    toolBar.setController(controller);
  }

  public GLCanvas getCanvas() {
    return canvas;
  }
  
  public MainToolBar getToolBar() {
    return toolBar;
  }
  
  private class EmptyScene implements GLEventListener {

    public void display(GLAutoDrawable d) {
      GL2 gl = d.getGL().getGL2();
      gl.glClearColor(0.2f, 0.2f, 0.2f, 1);
      gl.glClear(GL.GL_COLOR_BUFFER_BIT);
    }

    public void dispose(GLAutoDrawable d) {
    }

    public void init(GLAutoDrawable d) {
    }

    public void reshape(GLAutoDrawable d, int x, int y, int w, int h) {
    }
  }
}
