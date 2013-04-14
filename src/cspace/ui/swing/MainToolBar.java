package cspace.ui.swing;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

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

import cspace.CSpaceViewer;
import cspace.scene.Path.Waypoint;
import cspace.ui.SceneController;

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
    layout.columnWeights = new double[] { 0.0, 0.0, 0.0, 0.0, 0.0, 1.0, };
    setLayout(layout);

    // button to open scene
    {
      JButton openButton = new JButton(new ImageIcon(getClass().getResource(
          "/folder_open_icon&16.png")));
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
      settingsButton.setEnabled(false);
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
      pathButton.setEnabled(false);
      GridBagConstraints gbc = new GridBagConstraints();
      gbc.gridx = 2;
      gbc.gridy = 0;
      add(pathButton, gbc);
    }

    // inspect mode
    {
      ImageIcon icon = new ImageIcon(getClass().getResource("/zoom_icon&16.png"));
      final JToggleButton button = new JToggleButton(icon);
      button.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent arg0) {
          controller.setInspectMode(button.isSelected());
        }
      });
      GridBagConstraints gbc = new GridBagConstraints();
      gbc.gridx = 3;
      gbc.gridy = 0;
      add(button, gbc);
    }
    
    // about button
    {
      ImageIcon icon = new ImageIcon(getClass().getResource("/info_icon&16.png"));
      JButton button = new JButton(icon);
      button.setPreferredSize(new Dimension(32,32));
      GridBagConstraints gbc = new GridBagConstraints();
      gbc.gridx = 4;
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
      gbc.gridx = 5;
      gbc.gridy = 0;
      add(pathSlider, gbc);
    }
    
    sceneFileChooser = new JFileChooser();
    sceneFileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
  }

  public void setController(SceneController controller) {
    this.controller = controller;

    pathSlider.setMinimum(0);
    pathSlider.setMaximum(controller.getScene().path.waypoints.length - 1);
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
        Waypoint wp = controller.getScene().path.waypoints[slider.getValue()];
        controller.getScene().view.robot.position = wp.p.copy();
        controller.getScene().view.robot.rotation = wp.u.copy();
        controller.getRenderer().get2D().getSumRenderer().markDirty();
        controller.getRenderer().get2D().getSubRenderer().markDirty();
        controller.getRenderer().get2D().getContactRenderer().markDirty();
      }
    }
  }
}
