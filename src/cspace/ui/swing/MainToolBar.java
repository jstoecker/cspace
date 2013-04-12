package cspace.ui.swing;

import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.JToggleButton;
import javax.swing.border.Border;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import cspace.scene.Path.Waypoint;
import cspace.ui.SceneController;

public class MainToolBar extends JPanel {

  private final SceneController controller;

  public MainToolBar(final MainWindow window, SceneController controller) {
    this.controller = controller;
    GridBagLayout gridBagLayout = new GridBagLayout();
    gridBagLayout.columnWidths = new int[] { 0, 0, 0, 0 };
    gridBagLayout.rowHeights = new int[] { 0, 0 };
    gridBagLayout.columnWeights = new double[] { 0.0, 0.0, 1.0, Double.MIN_VALUE };
    gridBagLayout.rowWeights = new double[] { 0.0, Double.MIN_VALUE };
    setLayout(gridBagLayout);

    JButton btnSettings = new JButton(new ImageIcon(getClass().getResource("/gear.png")));
    btnSettings.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent arg0) {
        window.getSettingsDialog().setVisible(true);
      }
    });

    GridBagConstraints gbc_btnSettings = new GridBagConstraints();
    gbc_btnSettings.insets = new Insets(0, 0, 0, 5);
    gbc_btnSettings.gridx = 0;
    gbc_btnSettings.gridy = 0;
    add(btnSettings, gbc_btnSettings);

    final JToggleButton pathButton = new JToggleButton(new ImageIcon(getClass().getResource(
        "/path.png")));
    GridBagConstraints gbc_pathButton = new GridBagConstraints();
    gbc_pathButton.insets = new Insets(0, 0, 0, 5);
    gbc_pathButton.gridx = 1;
    gbc_pathButton.gridy = 0;
    add(pathButton, gbc_pathButton);

    JSlider slRobot = new JSlider(0, controller.getScene().path.waypoints.length - 1, 0);
    slRobot.addChangeListener(new PathSlideAction());
    GridBagConstraints gbc_slRobot = new GridBagConstraints();
    gbc_slRobot.fill = GridBagConstraints.HORIZONTAL;
    gbc_slRobot.gridx = 2;
    gbc_slRobot.gridy = 0;
    add(slRobot, gbc_slRobot);

    Border inner = BorderFactory.createEmptyBorder(0, 5, 0, 5);
    Border outer = BorderFactory.createMatteBorder(1, 1, 1, 1, Color.lightGray);
    setBorder(BorderFactory.createCompoundBorder(outer, inner));

    slRobot.setValue(slRobot.getMaximum());
    slRobot.setValue(0);
  }

  private class PathSlideAction implements ChangeListener {
    public void stateChanged(ChangeEvent e) {
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
