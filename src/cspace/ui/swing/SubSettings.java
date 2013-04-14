package cspace.ui.swing;

import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JPanel;
import javax.swing.JSlider;
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

public class SubSettings extends JPanel {
  public SubSettings(final Scene scene, final SceneRenderer renderer) {
    
    PropertyLayout layout = new PropertyLayout();
    final SceneView.Subs view = scene.view.subs;
    
    // color
    ColorPanel colorPanel = new ColorPanel("Subs Color", view.color);
    colorPanel.addListener(new ColorPanel.Listener() {
      public void colorChanged(ColorPanel panel, Vec3f newColor) {
        view.color.set(newColor);
      }
    });
    layout.add("Color", colorPanel);
    
    // edge width
    final SpinnerNumberModel widthModel = new SpinnerNumberModel(view.edgeWidth, 0, 100, 0.1);
    JSpinner widthSpinner = new JSpinner(widthModel);
    widthSpinner.addChangeListener(new ChangeListener() {
      public void stateChanged(ChangeEvent e) {
        view.edgeWidth = widthModel.getNumber().floatValue();
        renderer.get2D().getSubRenderer().markDirty();
      }
    });
    layout.add("Edge Width", widthSpinner);
    
    // render style
    JComboBox renderComboBox = new JComboBox();
    for (SceneView.Subs.RenderStyle3D style : SceneView.Subs.RenderStyle3D.values())
      renderComboBox.addItem(style);
    renderComboBox.setSelectedItem(view.renderStyle3d);
    renderComboBox.addItemListener(new ItemListener() {
      public void itemStateChanged(ItemEvent e) {
        view.renderStyle3d = (SceneView.Subs.RenderStyle3D)e.getItem();
      }
    });
    layout.add("3D Rendering Style", renderComboBox);
    
    // color style
    JComboBox colorComboBox = new JComboBox();
    for (SceneView.Subs.ColorStyle3D style : SceneView.Subs.ColorStyle3D.values())
      colorComboBox.addItem(style);
    colorComboBox.setSelectedItem(view.colorStyle3d);
    colorComboBox.addItemListener(new ItemListener() {
      public void itemStateChanged(ItemEvent e) {
        view.colorStyle3d = (SceneView.Subs.ColorStyle3D)e.getItem();
      }
    });
    layout.add("3D Coloring Style", colorComboBox);
    
    // draw alpha
    final JSlider drawAlpha = new JSlider(0, 100, (int)(view.drawAlpha * 100));
    drawAlpha.addChangeListener( new ChangeListener() {
      public void stateChanged(ChangeEvent arg0) {
        view.drawAlpha = drawAlpha.getValue() / 100.0f;
      }
    });
    layout.add("Translucency", drawAlpha);
    
    // visibility
    VisibilityWidget visibility = new VisibilityWidget(view.visible2d, view.visible3d);
    visibility.addListener(new VisibilityWidget.Listener() {
      public void visibilityChanged(boolean visible2d, boolean visible3d) {
        view.visible2d = visible2d;
        view.visible3d = visible3d;
      }
    });
    layout.add(visibility);
    
    /// shaded
    final JCheckBox shaded = new JCheckBox("Shaded");
    shaded.setSelected(view.shaded);
    shaded.addItemListener(new ItemListener() {
      public void itemStateChanged(ItemEvent e) {
        view.shaded = shaded.isSelected();
      }
    });
    layout.add(shaded);
    
    /// wireframe
    final JCheckBox wireframed = new JCheckBox("Wireframe");
    wireframed.setSelected(view.wireframed);
    wireframed.addItemListener(new ItemListener() {
      public void itemStateChanged(ItemEvent e) {
        view.wireframed = wireframed.isSelected();
      }
    });
    layout.add(wireframed);
    
    layout.apply(this);
  }
}
