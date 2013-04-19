package cspace.gui;

import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import javax.swing.JCheckBox;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingConstants;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import jgl.math.vector.Vec3f;
import cspace.SceneRenderer;
import cspace.scene.Scene;
import cspace.util.ColorPanel;
import cspace.util.PropertyLayout;
import cspace.util.VisibilityWidget;

public class ObstacleSettings extends JPanel {

  public ObstacleSettings(final Scene scene, final SceneRenderer renderer) {
    PropertyLayout layout = new PropertyLayout();

    // color
    ColorPanel colorPanel = new ColorPanel("Obstacle Color", scene.view.obstacle.color);
    colorPanel.addListener(new ColorPanel.Listener() {
      public void colorChanged(ColorPanel panel, Vec3f newColor) {
        scene.view.obstacle.color.set(newColor);
      }
    });
    layout.add("Color", colorPanel);

    // edge width
    final SpinnerNumberModel widthModel = new SpinnerNumberModel(scene.view.obstacle.edgeWidth, 0,
        100, 0.01);
    JSpinner widthSpinner = new JSpinner(widthModel);
    widthSpinner.addChangeListener(new ChangeListener() {
      public void stateChanged(ChangeEvent e) {
        scene.view.obstacle.edgeWidth = widthModel.getNumber().floatValue();
        renderer.get2D().getObstacleRenderer().markDirty();
      }
    });
    layout.add("Edge Width", widthSpinner);

    // visibility
    VisibilityWidget visibility = new VisibilityWidget(scene.view.obstacle.visible2d,
        scene.view.obstacle.visible3d);
    visibility.visible3d.setEnabled(false);
    visibility.addListener(new VisibilityWidget.Listener() {
      public void visibilityChanged(boolean visible2d, boolean visible3d) {
        scene.view.obstacle.visible2d = visible2d;
      }
    });
    layout.add(visibility);

    // draw origin
    final JCheckBox originCheckBox = new JCheckBox("Draw Origin");
    originCheckBox.setSelected(scene.view.obstacle.originVisible);
    originCheckBox.addItemListener(new ItemListener() {
      public void itemStateChanged(ItemEvent e) {
        scene.view.obstacle.originVisible = originCheckBox.isSelected();
        renderer.get2D().getObstacleRenderer().markDirty();
      }
    });
    layout.add(originCheckBox);

    layout.apply(this);
  }
}
