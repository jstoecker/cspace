package cspace.ui.swing;

import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import jgl.math.vector.Vec3f;
import cspace.SceneRenderer;
import cspace.scene.Scene;
import cspace.scene.SceneView;
import cspace.scene.SceneView.Renderer.ViewMode;
import cspace.util.ColorPanel;
import cspace.util.PropertyLayout;

public class RendererSettings extends JPanel {

  private final Scene         scene;

  public RendererSettings(Scene scene, SceneRenderer renderer) {
    this.scene = scene;

    PropertyLayout layout = new PropertyLayout();

    // background color
    {
      ColorPanel colorPanel = new ColorPanel("Background Color", scene.view.renderer.background);
      colorPanel.addListener(new BackgroundColorAction());
      layout.add("Background Color", colorPanel);
    }

    // view mode
    {
      JComboBox comboBox = new JComboBox();
      for (ViewMode mode : SceneView.Renderer.ViewMode.values())
        comboBox.addItem(mode);
      comboBox.setSelectedItem(scene.view.renderer.viewMode);
      comboBox.addItemListener(new ViewModeAction());
      layout.add("View Mode", comboBox);
    }

    // fixed-width edges
    {
      JCheckBox checkBox = new JCheckBox("Fixed-Width Edges");
      checkBox.setSelected(scene.view.renderer.fixedWidthEdges);
      checkBox.setHorizontalTextPosition(SwingConstants.LEFT);
      checkBox.addChangeListener(new FixedWidthEdgeAction());
      layout.add(checkBox);
    }
    
    layout.apply(this);
  }

  private class BackgroundColorAction implements ColorPanel.Listener {
    public void colorChanged(ColorPanel panel, Vec3f newColor) {
      scene.view.renderer.background = newColor;
    }
  }

  private class ViewModeAction implements ItemListener {
    public void itemStateChanged(ItemEvent e) {
      scene.view.renderer.viewMode = (SceneView.Renderer.ViewMode)e.getItem();
    }
  }
  
  private class FixedWidthEdgeAction implements ChangeListener {
    public void stateChanged(ChangeEvent e) {
      scene.view.renderer.fixedWidthEdges = ((JCheckBox)e.getSource()).isSelected();
    }
  }
}
