package cspace.gui;

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

public class PathSettings extends JPanel {

  public PathSettings(final Scene scene, final SceneRenderer renderer) {
    
    final SceneView.Path view = scene.view.path;
    PropertyLayout layout = new PropertyLayout();

    ColorPanel colorPanel = new ColorPanel("Color", view.color);
    colorPanel.addListener(new ColorPanel.Listener() {
      public void colorChanged(ColorPanel panel, Vec3f newColor) {
        view.color.set(newColor);
      }
    });
    layout.add("Color", colorPanel);

    final SpinnerNumberModel widthModel = new SpinnerNumberModel(view.edgeWidth, 0, 100, 0.1);
    JSpinner widthSpinner = new JSpinner(widthModel);
    widthSpinner.addChangeListener(new ChangeListener() {
      public void stateChanged(ChangeEvent arg0) {
        view.edgeWidth = widthModel.getNumber().floatValue();
        renderer.get2D().getPathRenderer().markDirty();
      }
    });
    layout.add("Edge Width", widthSpinner);

    VisibilityWidget visibility = new VisibilityWidget(view.visible2d, view.visible3d);
    visibility.addListener(new VisibilityWidget.Listener() {
      public void visibilityChanged(boolean visible2d, boolean visible3d) {
        view.visible2d = visible2d;
        view.visible3d = visible3d;
      }
    });
    layout.add(visibility);
    
    layout.apply(this);
  }
}
