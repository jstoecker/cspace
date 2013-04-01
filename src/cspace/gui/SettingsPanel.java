package cspace.gui;

import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
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

import jgl.math.vector.Vec2d;
import cspace.model.Path;
import cspace.model.Path.Waypoint;
import cspace.util.OBJExporter;
import cspace.visuals.Visuals;

public class SettingsPanel extends JPanel {

  SceneWindow window;
  JSlider     slRobot;

  public SettingsPanel(final SceneWindow window) {
    this.window = window;
    GridBagLayout gridBagLayout = new GridBagLayout();
    gridBagLayout.columnWidths = new int[] { 0, 0, 0, 0 };
    gridBagLayout.rowHeights = new int[] { 0, 0 };
    gridBagLayout.columnWeights = new double[] { 0.0, 0.0, 1.0, Double.MIN_VALUE };
    gridBagLayout.rowWeights = new double[] { 0.0, Double.MIN_VALUE };
    setLayout(gridBagLayout);

    JButton btnSettings = new JButton(new ImageIcon(getClass().getResource("/gear.png")));
    btnSettings.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent arg0) {
        window.visualsFrame.setVisible(true);
      }
    });

    GridBagConstraints gbc_btnSettings = new GridBagConstraints();
    gbc_btnSettings.insets = new Insets(0, 0, 0, 5);
    gbc_btnSettings.gridx = 0;
    gbc_btnSettings.gridy = 0;
    add(btnSettings, gbc_btnSettings);

    final JToggleButton pathButton = new JToggleButton(new ImageIcon(getClass().getResource("/path.png")));
    pathButton.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent arg0) {
        if (pathButton.isSelected()) {
          Waypoint[] waypoints = new Waypoint[2];
          waypoints[0] = new Waypoint(0, 0, 0);
          waypoints[1] = new Waypoint(0, 0, 0);
          window.scene.newPath = new Path(waypoints);
          window.canvas.repaint();
        } else {
          window.scene.updatePath();
          slRobot.setMaximum(window.scene.path.waypoints.length - 1);
          slRobot.setValue(0);
          window.canvas.repaint();
        }
      }
    });

    final JButton objButton = new JButton("S");
    objButton.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        JFileChooser fc = new JFileChooser();
        if (fc.showSaveDialog(SettingsPanel.this) == JFileChooser.APPROVE_OPTION) {
          new OBJExporter(window.scene.sampledCS).write(fc.getSelectedFile());
        }
      }
    });
    // add(objButton);

    GridBagConstraints gbc_pathButton = new GridBagConstraints();
    gbc_pathButton.insets = new Insets(0, 0, 0, 5);
    gbc_pathButton.gridx = 1;
    gbc_pathButton.gridy = 0;
    add(pathButton, gbc_pathButton);

    slRobot = new JSlider(0, window.scene.path.waypoints.length - 1, 0);
    slRobot.addChangeListener(new SlideListener());
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

  class SlideListener implements ChangeListener {

    @Override
    public void stateChanged(ChangeEvent arg0) {
      Visuals visuals = window.scene.visuals;
      Path path = window.scene.path;

      if (visuals.robotVisuals.isOnPath()) {
        Waypoint wp = path.waypoints[slRobot.getValue()];
        visuals.robotVisuals.setP(wp.p);
        visuals.robotVisuals.setTheta(wp.theta);
      } else {
        double theta = slRobot.getValue() / (double) slRobot.getMaximum() * Math.PI * 2;
        Vec2d u = new Vec2d(Math.cos(theta), Math.sin(theta));
        visuals.robotVisuals.setU(u);
      }
    }
  }
}
