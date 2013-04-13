package cspace.ui.swing;

import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
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
  private final SceneRenderer renderer;

  public RendererSettings(final Scene scene, SceneRenderer renderer) {
    this.scene = scene;
    this.renderer = renderer;

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
    
    // Draw Periods
    {
      final SpinnerNumberModel model = new SpinnerNumberModel(scene.view.renderer.periods3d, 1, 33, 2);
      JSpinner spinner = new JSpinner(model);
      spinner.addChangeListener(new ChangeListener() {
        public void stateChanged(ChangeEvent e) {
          scene.view.renderer.periods3d = model.getNumber().intValue();
        }
      });
      layout.add("Draw 3D Periods", spinner);
    }

    // fixed-width edges
    {
      JCheckBox checkBox = new JCheckBox("Fixed-Width Edges");
      checkBox.setSelected(scene.view.renderer.fixedWidthEdges);
      checkBox.setHorizontalTextPosition(SwingConstants.LEFT);
      checkBox.addChangeListener(new FixedWidthEdgeAction());
      layout.add(checkBox);
    }
    
    // Pi planes
    {
      final JCheckBox checkBox = new JCheckBox("Draw Pi Planes");
      checkBox.setSelected(scene.view.renderer.drawPiPlanes);
      checkBox.setHorizontalTextPosition(SwingConstants.LEFT);
      checkBox.addItemListener(new ItemListener() {
        public void itemStateChanged(ItemEvent e) {
          scene.view.renderer.drawPiPlanes = checkBox.isSelected();
        }
      });
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
      scene.view.renderer.viewMode = (SceneView.Renderer.ViewMode) e.getItem();
    }
  }

  private class FixedWidthEdgeAction implements ChangeListener {
    public void stateChanged(ChangeEvent e) {
      scene.view.renderer.fixedWidthEdges = ((JCheckBox)e.getSource()).isSelected();
      renderer.get2D().markDirty();
    }
  }
}
