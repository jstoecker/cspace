package cspace.gui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.JToggleButton;
import javax.swing.border.Border;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import jgl.math.vector.Mat4f;
import jgl.math.vector.Transform;
import jgl.math.vector.Vec3f;
import cspace.CSpaceViewer;
import cspace.SceneController;
import cspace.scene.Path.Waypoint;

public class MainToolBar extends JPanel {

  private JFileChooser       sceneFileChooser;
  private final CSpaceViewer viewer;
  private JSlider            pathSlider;
  private JToggleButton      pathButton;
  private JButton            settingsButton;
  private SceneController    controller;
  private SettingsDialog     settingsDialog;

  public MainToolBar(final CSpaceViewer viewer) {
    this.viewer = viewer;

    Border inner = BorderFactory.createEmptyBorder(0, 5, 0, 5);
    Border outer = BorderFactory.createMatteBorder(1, 1, 1, 1, Color.lightGray);
    setBorder(BorderFactory.createCompoundBorder(outer, inner));

    GridBagLayout layout = new GridBagLayout();
    layout.columnWeights = new double[] { 0.0, 0.0, 0.0, 0.0, 1.0, };
    setLayout(layout);

    // button to open scene
    {
      JButton openButton = new JButton(new ImageIcon(getClass().getResource(
          "/folder_open_icon&16.png")));
      openButton.setToolTipText("Open Scene");
      openButton.setMnemonic(KeyEvent.VK_O);
      openButton.setPreferredSize(new Dimension(32,32));
      openButton.addActionListener(new OpenSceneAction());
      GridBagConstraints gbc = new GridBagConstraints();
      gbc.gridx = 0;
      gbc.gridy = 0;
      add(openButton, gbc);
    }

    // button to open settings dialog
    {
      settingsButton = new JButton(new ImageIcon(getClass().getResource("/cogs_icon&16.png")));
      settingsButton.setToolTipText("View Settings");
      settingsButton.setEnabled(false);
      settingsButton.setMnemonic(KeyEvent.VK_S);
      settingsButton.setPreferredSize(new Dimension(32,32));
      settingsButton.addActionListener(new OpenSettingsAction());
      GridBagConstraints gbc = new GridBagConstraints();
      gbc.gridx = 1;
      gbc.gridy = 0;
      add(settingsButton, gbc);
    }

    // button to toggle path planning mode
    {
      ImageIcon icon = new ImageIcon(getClass().getResource("/push_pin_icon&16.png"));
      pathButton = new JToggleButton(icon);
      pathButton.setToolTipText("New Path");
      pathButton.setEnabled(false);
      pathButton.setPreferredSize(new Dimension(32, 32));
      GridBagConstraints gbc = new GridBagConstraints();
      gbc.gridx = 2;
      gbc.gridy = 0;
      add(pathButton, gbc);
    }

    // inspect/debug mode
    {
      ImageIcon icon = new ImageIcon(getClass().getResource("/zoom_icon&16.png"));
      final JToggleButton button = new JToggleButton(icon);
      button.setToolTipText("Inspect");
      button.setMnemonic(KeyEvent.VK_I);
      button.setPreferredSize(new Dimension(32, 32));
      button.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          controller.setInspectMode(button.isSelected());
        }
      });
      GridBagConstraints gbc = new GridBagConstraints();
      gbc.gridx = 3;
      gbc.gridy = 0;
      add(button, gbc);
    }
    
    // path progress slider
    {
      pathSlider = new JSlider();
      pathSlider.setEnabled(false);
      pathSlider.addChangeListener(new PathSlideAction());
      GridBagConstraints gbc = new GridBagConstraints();
      gbc.fill = GridBagConstraints.HORIZONTAL;
      gbc.gridx = 4;
      gbc.gridy = 0;
      add(pathSlider, gbc);
    }
    
    sceneFileChooser = new JFileChooser();
    sceneFileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
  }

  public void setController(SceneController controller) {
    this.controller = controller;

    pathSlider.setMinimum(0);
    pathSlider.setMaximum(controller.getScene().path.waypoints.size() - 1);
    pathSlider.setValue(0);
    pathSlider.setEnabled(true);
    settingsButton.setEnabled(true);
    pathButton.setEnabled(true);

    if (settingsDialog != null) {
      settingsDialog.setVisible(false);
    }
    settingsDialog = new SettingsDialog(viewer.getMainWindow(), controller.getScene(),
        controller.getRenderer());
  }

  private class OpenSceneAction implements ActionListener {
    public void actionPerformed(ActionEvent e) {
      if (sceneFileChooser.showOpenDialog(viewer.getMainWindow()) == JFileChooser.APPROVE_OPTION) {
        viewer.openScene(sceneFileChooser.getSelectedFile());
      }
    }
  }

  private class OpenSettingsAction implements ActionListener {
    public void actionPerformed(ActionEvent e) {
      settingsDialog.setVisible(true);
    }
  }

  private class PathSlideAction implements ChangeListener {
    public void stateChanged(ChangeEvent e) {
      if (controller != null) {
        JSlider slider = (JSlider) e.getSource();
        List<Waypoint> waypoints = controller.getScene().path.waypoints;
        Waypoint wp = waypoints.get(slider.getValue());
        
        if (controller.getScene().view.robot.cameraRobot && slider.getValue() < waypoints.size() - 1) {
          Vec3f p1 = wp.toVector().toFloat();
          Vec3f p2 = waypoints.get(slider.getValue() + 1).toVector().toFloat();
          Vec3f f = p2;
          Mat4f view = Transform.lookAt(p1.x, p1.y, p1.z, f.x, f.y, f.z, 0, 0, 1);
          controller.getRenderer().get3D().getCamera().setView(view);
        } else {
          controller.getScene().view.robot.position = wp.p.copy();
          controller.getScene().view.robot.rotation = wp.u.copy();
        }
        controller.getRenderer().get2D().getSumRenderer().markDirty();
        controller.getRenderer().get2D().getSubRenderer().markDirty();
        controller.getRenderer().get2D().getContactRenderer().markDirty();
      }
    }
  }
}
