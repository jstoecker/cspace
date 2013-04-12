package cspace.ui.swing;

import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import javax.swing.JCheckBox;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import jgl.math.vector.Vec3f;

import cspace.SceneRenderer;
import cspace.scene.Scene;
import cspace.scene.SceneView;
import cspace.util.ColorPanel;
import cspace.util.PropertyLayout;
import cspace.util.VisibilityWidget;

public class RobotSettings extends JPanel {

  public RobotSettings(final Scene scene, final SceneRenderer renderer) {

    final SceneView.Robot view = scene.view.robot;
    PropertyLayout layout = new PropertyLayout();

    // color
    ColorPanel colorPanel = new ColorPanel("Robot Color", view.color);
    colorPanel.addListener(new ColorPanel.Listener() {
      public void colorChanged(ColorPanel panel, Vec3f newColor) {
        view.color.set(newColor);
      }
    });
    layout.add("Color", colorPanel);

    // edge width
    final SpinnerNumberModel widthModel = new SpinnerNumberModel(view.edgeWidth, 0, 100, 0.01);
    JSpinner widthSpinner = new JSpinner(widthModel);
    widthSpinner.addChangeListener(new ChangeListener() {
      public void stateChanged(ChangeEvent e) {
        view.edgeWidth = widthModel.getNumber().floatValue();
        renderer.get2D().getRobotRenderer().markDirty();
      }
    });
    layout.add("Edge Width", widthSpinner);

    // visibility
    VisibilityWidget visibility = new VisibilityWidget(view.visible2d, view.visible3d);
    visibility.addListener(new VisibilityWidget.Listener() {
      public void visibilityChanged(boolean visible2d, boolean visible3d) {
        view.visible2d = visible2d;
        view.visible3d = visible3d;
      }
    });
    layout.add("Visibility", visibility);

    // draw origin
    final JCheckBox originCheckBox = new JCheckBox("Draw Origin");
    originCheckBox.setSelected(view.originVisible);
    originCheckBox.addItemListener(new ItemListener() {
      public void itemStateChanged(ItemEvent e) {
        view.originVisible = originCheckBox.isSelected();
        renderer.get2D().getRobotRenderer().markDirty();
      }
    });
    layout.add(originCheckBox);
    
    layout.apply(this);
  }
}
