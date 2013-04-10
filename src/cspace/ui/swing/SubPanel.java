package cspace.ui.swing;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import jgl.math.vector.Vec3f;
import cspace.scene.view.SubVisuals;
import cspace.scene.view.SubVisuals.SubColoring;
import cspace.scene.view.SubVisuals.SubRenderStyle;
import cspace.util.ColorPanel;
import cspace.util.VisibilityWidget;

public class SubPanel extends JPanel {
  public SubPanel(final SubVisuals visuals) {
    setBorder(new EmptyBorder(10, 10, 10, 10));
    GridBagLayout gridBagLayout = new GridBagLayout();
    gridBagLayout.columnWidths = new int[] { 193, 34, 0 };
    gridBagLayout.rowHeights = new int[] { 32, 32, 32, 32, 32, 32, 32, 32, 32,
        32, 0 };
    gridBagLayout.columnWeights = new double[] { 0.0, 1.0, Double.MIN_VALUE };
    gridBagLayout.rowWeights = new double[] { 0.0, 0.0, 0.0, 0.0, 0.0, 0.0,
        0.0, 0.0, 0.0, 0.0, Double.MIN_VALUE };
    setLayout(gridBagLayout);

    JLabel lblEdgeColor = new JLabel("Edge Color");
    GridBagConstraints gbc_lblEdgeColor = new GridBagConstraints();
    gbc_lblEdgeColor.fill = GridBagConstraints.VERTICAL;
    gbc_lblEdgeColor.anchor = GridBagConstraints.EAST;
    gbc_lblEdgeColor.insets = new Insets(0, 0, 5, 5);
    gbc_lblEdgeColor.gridx = 0;
    gbc_lblEdgeColor.gridy = 0;
    add(lblEdgeColor, gbc_lblEdgeColor);

    ColorPanel pColor = new ColorPanel("Sub Color", visuals.getColor());
    pColor.addListener(new ColorPanel.Listener() {
      public void colorChanged(ColorPanel panel, Vec3f newColor) {
        visuals.setColor(newColor);
      }
    });
    GridBagConstraints gbc_pColor = new GridBagConstraints();
    gbc_pColor.insets = new Insets(2, 2, 7, 2);
    gbc_pColor.fill = GridBagConstraints.BOTH;
    gbc_pColor.gridx = 1;
    gbc_pColor.gridy = 0;
    add(pColor, gbc_pColor);

    JLabel lblEdgeWidth = new JLabel("Edge Width");
    GridBagConstraints gbc_lblEdgeWidth = new GridBagConstraints();
    gbc_lblEdgeWidth.fill = GridBagConstraints.VERTICAL;
    gbc_lblEdgeWidth.anchor = GridBagConstraints.EAST;
    gbc_lblEdgeWidth.insets = new Insets(0, 0, 5, 5);
    gbc_lblEdgeWidth.gridx = 0;
    gbc_lblEdgeWidth.gridy = 1;
    add(lblEdgeWidth, gbc_lblEdgeWidth);

    final SpinnerNumberModel widthModel = new SpinnerNumberModel(
        visuals.getWidth2d(), 0, 100, 0.01);
    JSpinner spnWidth = new JSpinner(widthModel);
    spnWidth.addChangeListener(new ChangeListener() {
      public void stateChanged(ChangeEvent e) {
        visuals.setWidth2d(widthModel.getNumber().floatValue());
      }
    });
    GridBagConstraints gbc_spnWidth = new GridBagConstraints();
    gbc_spnWidth.fill = GridBagConstraints.BOTH;
    gbc_spnWidth.insets = new Insets(0, 0, 5, 0);
    gbc_spnWidth.gridx = 1;
    gbc_spnWidth.gridy = 1;
    add(spnWidth, gbc_spnWidth);

    JLabel lblEdgeSmoothFactor = new JLabel("Edge Smooth Factor");
    GridBagConstraints gbc_lblEdgeSmoothFactor = new GridBagConstraints();
    gbc_lblEdgeSmoothFactor.anchor = GridBagConstraints.EAST;
    gbc_lblEdgeSmoothFactor.insets = new Insets(0, 0, 5, 5);
    gbc_lblEdgeSmoothFactor.gridx = 0;
    gbc_lblEdgeSmoothFactor.gridy = 2;
    add(lblEdgeSmoothFactor, gbc_lblEdgeSmoothFactor);

    final SpinnerNumberModel smoothModel = new SpinnerNumberModel(
        visuals.getSmooth2d(), 0, 0.1, 0.001);
    JSpinner spnEdgeSmooth = new JSpinner(smoothModel);
    spnEdgeSmooth.addChangeListener(new ChangeListener() {
      public void stateChanged(ChangeEvent e) {
        visuals.setSmooth2d(smoothModel.getNumber().floatValue());
      }
    });
    GridBagConstraints gbc_spnEdgeSmooth = new GridBagConstraints();
    gbc_spnEdgeSmooth.fill = GridBagConstraints.HORIZONTAL;
    gbc_spnEdgeSmooth.insets = new Insets(0, 0, 5, 0);
    gbc_spnEdgeSmooth.gridx = 1;
    gbc_spnEdgeSmooth.gridy = 2;
    add(spnEdgeSmooth, gbc_spnEdgeSmooth);

    JLabel lblSamplingDistance = new JLabel("Sampling Factor (Interior)");
    GridBagConstraints gbc_lblSamplingDistance = new GridBagConstraints();
    gbc_lblSamplingDistance.anchor = GridBagConstraints.EAST;
    gbc_lblSamplingDistance.insets = new Insets(0, 0, 5, 5);
    gbc_lblSamplingDistance.gridx = 0;
    gbc_lblSamplingDistance.gridy = 3;
    add(lblSamplingDistance, gbc_lblSamplingDistance);

    final SpinnerNumberModel iSampModel = new SpinnerNumberModel(
        visuals.getSamplingLength(), 0.01, Math.PI, 0.05);
    JSpinner spnSamplingInner = new JSpinner(iSampModel);
    spnSamplingInner.addChangeListener(new ChangeListener() {
      public void stateChanged(ChangeEvent arg0) {
        visuals.setSamplingLength(iSampModel.getNumber().doubleValue());
      }
    });
    GridBagConstraints gbc_spnSamplingInner = new GridBagConstraints();
    gbc_spnSamplingInner.fill = GridBagConstraints.HORIZONTAL;
    gbc_spnSamplingInner.insets = new Insets(0, 0, 5, 0);
    gbc_spnSamplingInner.gridx = 1;
    gbc_spnSamplingInner.gridy = 3;
    add(spnSamplingInner, gbc_spnSamplingInner);

    JLabel lblSamplingThreshold = new JLabel("Sampling Factor (Border)");
    GridBagConstraints gbc_lblSamplingThreshold = new GridBagConstraints();
    gbc_lblSamplingThreshold.anchor = GridBagConstraints.EAST;
    gbc_lblSamplingThreshold.insets = new Insets(0, 0, 5, 5);
    gbc_lblSamplingThreshold.gridx = 0;
    gbc_lblSamplingThreshold.gridy = 4;
    add(lblSamplingThreshold, gbc_lblSamplingThreshold);

    final SpinnerNumberModel oSampModel = new SpinnerNumberModel(
        visuals.getSamplingThreshold(), 0.0001, Math.PI, 0.05);
    JSpinner spnSamplingOuter = new JSpinner(oSampModel);
    spnSamplingOuter.addChangeListener(new ChangeListener() {
      public void stateChanged(ChangeEvent e) {
        visuals.setSamplingThreshold(oSampModel.getNumber().doubleValue());
      }
    });
    GridBagConstraints gbc_spnSamplingOuter = new GridBagConstraints();
    gbc_spnSamplingOuter.fill = GridBagConstraints.HORIZONTAL;
    gbc_spnSamplingOuter.insets = new Insets(0, 0, 5, 0);
    gbc_spnSamplingOuter.gridx = 1;
    gbc_spnSamplingOuter.gridy = 4;
    add(spnSamplingOuter, gbc_spnSamplingOuter);

    JLabel lblRenderingStyle = new JLabel("Rendering Style");
    GridBagConstraints gbc_lblRenderingStyle = new GridBagConstraints();
    gbc_lblRenderingStyle.fill = GridBagConstraints.VERTICAL;
    gbc_lblRenderingStyle.anchor = GridBagConstraints.EAST;
    gbc_lblRenderingStyle.insets = new Insets(0, 0, 5, 5);
    gbc_lblRenderingStyle.gridx = 0;
    gbc_lblRenderingStyle.gridy = 5;
    add(lblRenderingStyle, gbc_lblRenderingStyle);

    JComboBox cbRendering = new JComboBox();
    for (SubRenderStyle style : SubRenderStyle.values())
      cbRendering.addItem(style);
    cbRendering.setSelectedItem(visuals.getStyle());
    cbRendering.addItemListener(new ItemListener() {
      public void itemStateChanged(ItemEvent e) {
        if (e.getStateChange() == ItemEvent.SELECTED)
          visuals.setStyle((SubRenderStyle) e.getItem());
      }
    });
    GridBagConstraints gbc_cbRendering = new GridBagConstraints();
    gbc_cbRendering.insets = new Insets(0, 0, 5, 0);
    gbc_cbRendering.fill = GridBagConstraints.BOTH;
    gbc_cbRendering.gridx = 1;
    gbc_cbRendering.gridy = 5;
    add(cbRendering, gbc_cbRendering);

    JLabel lblColoringStyle = new JLabel("Coloring Style");
    GridBagConstraints gbc_lblColoringStyle = new GridBagConstraints();
    gbc_lblColoringStyle.fill = GridBagConstraints.VERTICAL;
    gbc_lblColoringStyle.anchor = GridBagConstraints.EAST;
    gbc_lblColoringStyle.insets = new Insets(0, 0, 5, 5);
    gbc_lblColoringStyle.gridx = 0;
    gbc_lblColoringStyle.gridy = 6;
    add(lblColoringStyle, gbc_lblColoringStyle);

    JComboBox cbColoring = new JComboBox();
    for (SubColoring coloring : SubColoring.values())
      cbColoring.addItem(coloring);
    cbColoring.setSelectedItem(visuals.getColoring());
    cbColoring.addItemListener(new ItemListener() {
      public void itemStateChanged(ItemEvent e) {
        if (e.getStateChange() == ItemEvent.SELECTED)
          visuals.setColoring((SubColoring) e.getItem());
      }
    });
    GridBagConstraints gbc_cbColoring = new GridBagConstraints();
    gbc_cbColoring.insets = new Insets(0, 0, 5, 0);
    gbc_cbColoring.fill = GridBagConstraints.BOTH;
    gbc_cbColoring.gridx = 1;
    gbc_cbColoring.gridy = 6;
    add(cbColoring, gbc_cbColoring);

    JLabel lblVisibility = new JLabel("Visibility");
    GridBagConstraints gbc_lblVisibility = new GridBagConstraints();
    gbc_lblVisibility.anchor = GridBagConstraints.EAST;
    gbc_lblVisibility.insets = new Insets(0, 0, 5, 5);
    gbc_lblVisibility.gridx = 0;
    gbc_lblVisibility.gridy = 7;
    add(lblVisibility, gbc_lblVisibility);

    VisibilityWidget visibilityPanel = new VisibilityWidget(visuals);
    GridBagConstraints gbc_visibilityPanel = new GridBagConstraints();
    gbc_visibilityPanel.insets = new Insets(0, 0, 5, 0);
    gbc_visibilityPanel.fill = GridBagConstraints.BOTH;
    gbc_visibilityPanel.gridx = 1;
    gbc_visibilityPanel.gridy = 7;
    add(visibilityPanel, gbc_visibilityPanel);
    
    final JCheckBox cxbWireframe = new JCheckBox("Draw Wireframe");
    cxbWireframe.setSelected(visuals.isWireframe());
    cxbWireframe.addChangeListener(new ChangeListener() {
      public void stateChanged(ChangeEvent e) {
        visuals.setWireframe(cxbWireframe.isSelected());
      }
    });
    cxbWireframe.setHorizontalTextPosition(SwingConstants.LEFT);
    GridBagConstraints gbc_cxbWireframe = new GridBagConstraints();
    gbc_cxbWireframe.anchor = GridBagConstraints.EAST;
    gbc_cxbWireframe.gridwidth = 3;
    gbc_cxbWireframe.insets = new Insets(0, 0, 5, 0);
    gbc_cxbWireframe.gridx = 0;
    gbc_cxbWireframe.gridy = 8;
    add(cxbWireframe, gbc_cxbWireframe);
    
    final JCheckBox cxbShading = new JCheckBox("Smooth Shading");
    cxbShading.setSelected(visuals.isShading());
    cxbShading.addChangeListener(new ChangeListener() {
      public void stateChanged(ChangeEvent e) {
        visuals.setShading(cxbShading.isSelected());
      }
    });
    cxbShading.setHorizontalTextPosition(SwingConstants.LEFT);
    GridBagConstraints gbc_cxbShading = new GridBagConstraints();
    gbc_cxbShading.anchor = GridBagConstraints.EAST;
    gbc_cxbShading.gridwidth = 3;
    gbc_cxbShading.gridx = 0;
    gbc_cxbShading.gridy = 9;
    add(cxbShading, gbc_cxbShading);
  }
}
