package cspace.ui.swing;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import jgl.math.vector.Vec3f;
import cspace.scene.Scene;
import cspace.util.ColorPanel;
import cspace.util.VisibilityWidget;

public class PathSettings extends JPanel {

  public PathSettings(Scene scene) {
//    setBorder(new EmptyBorder(10, 10, 10, 10));
//    GridBagLayout gridBagLayout = new GridBagLayout();
//    gridBagLayout.columnWidths = new int[] { 193, 34, 0 };
//    gridBagLayout.rowHeights = new int[] { 32, 32, 32, 32, 0 };
//    gridBagLayout.columnWeights = new double[] { 0.0, 1.0, Double.MIN_VALUE };
//    gridBagLayout.rowWeights = new double[] { 0.0, 0.0, 0.0, 0.0, Double.MIN_VALUE };
//    setLayout(gridBagLayout);
//
//    JLabel lblEdgeColor = new JLabel("Edge Color");
//    GridBagConstraints gbc_lblEdgeColor = new GridBagConstraints();
//    gbc_lblEdgeColor.fill = GridBagConstraints.VERTICAL;
//    gbc_lblEdgeColor.anchor = GridBagConstraints.EAST;
//    gbc_lblEdgeColor.insets = new Insets(0, 0, 5, 5);
//    gbc_lblEdgeColor.gridx = 0;
//    gbc_lblEdgeColor.gridy = 0;
//    add(lblEdgeColor, gbc_lblEdgeColor);
//
//    ColorPanel pColor = new ColorPanel("Path Color", visuals.getColor());
//    pColor.addListener(new ColorPanel.Listener() {
//      public void colorChanged(ColorPanel panel, Vec3f newColor) {
//        visuals.setColor(newColor);
//      }
//    });
//    GridBagConstraints gbc_pColor = new GridBagConstraints();
//    gbc_pColor.insets = new Insets(2, 2, 7, 2);
//    gbc_pColor.fill = GridBagConstraints.BOTH;
//    gbc_pColor.gridx = 1;
//    gbc_pColor.gridy = 0;
//    add(pColor, gbc_pColor);
//
//    JLabel lblEdgeWidth = new JLabel("Edge Width");
//    GridBagConstraints gbc_lblEdgeWidth = new GridBagConstraints();
//    gbc_lblEdgeWidth.fill = GridBagConstraints.VERTICAL;
//    gbc_lblEdgeWidth.anchor = GridBagConstraints.EAST;
//    gbc_lblEdgeWidth.insets = new Insets(0, 0, 5, 5);
//    gbc_lblEdgeWidth.gridx = 0;
//    gbc_lblEdgeWidth.gridy = 1;
//    add(lblEdgeWidth, gbc_lblEdgeWidth);
//
//    final SpinnerNumberModel widthModel = new SpinnerNumberModel(
//        visuals.getWidth2d(), 0, 100, 0.01);
//    JSpinner spnWidth = new JSpinner(widthModel);
//    spnWidth.addChangeListener(new ChangeListener() {
//      public void stateChanged(ChangeEvent e) {
//        visuals.setWidth2d(widthModel.getNumber().floatValue());
//      }
//    });
//    GridBagConstraints gbc_spnWidth = new GridBagConstraints();
//    gbc_spnWidth.fill = GridBagConstraints.BOTH;
//    gbc_spnWidth.insets = new Insets(0, 0, 5, 0);
//    gbc_spnWidth.gridx = 1;
//    gbc_spnWidth.gridy = 1;
//    add(spnWidth, gbc_spnWidth);
//
//    JLabel lblEdgeSmoothFactor = new JLabel("Edge Smooth Factor");
//    GridBagConstraints gbc_lblEdgeSmoothFactor = new GridBagConstraints();
//    gbc_lblEdgeSmoothFactor.anchor = GridBagConstraints.EAST;
//    gbc_lblEdgeSmoothFactor.insets = new Insets(0, 0, 5, 5);
//    gbc_lblEdgeSmoothFactor.gridx = 0;
//    gbc_lblEdgeSmoothFactor.gridy = 2;
//    add(lblEdgeSmoothFactor, gbc_lblEdgeSmoothFactor);
//
//    final SpinnerNumberModel smoothModel = new SpinnerNumberModel(
//        visuals.getSmooth2d(), 0, 0.1, 0.001);
//    JSpinner spnEdgeSmooth = new JSpinner(smoothModel);
//    spnEdgeSmooth.addChangeListener(new ChangeListener() {
//      public void stateChanged(ChangeEvent e) {
//        visuals.setSmooth2d(smoothModel.getNumber().floatValue());
//      }
//    });
//    GridBagConstraints gbc_spnEdgeSmooth = new GridBagConstraints();
//    gbc_spnEdgeSmooth.fill = GridBagConstraints.HORIZONTAL;
//    gbc_spnEdgeSmooth.insets = new Insets(0, 0, 5, 0);
//    gbc_spnEdgeSmooth.gridx = 1;
//    gbc_spnEdgeSmooth.gridy = 2;
//    add(spnEdgeSmooth, gbc_spnEdgeSmooth);
//    
//    JLabel lblVisibility = new JLabel("Visibility");
//    GridBagConstraints gbc_lblVisibility = new GridBagConstraints();
//    gbc_lblVisibility.anchor = GridBagConstraints.EAST;
//    gbc_lblVisibility.insets = new Insets(0, 0, 5, 5);
//    gbc_lblVisibility.gridx = 0;
//    gbc_lblVisibility.gridy = 3;
//    add(lblVisibility, gbc_lblVisibility);
//    
//    VisibilityWidget visibilityPanel = new VisibilityWidget(visuals);
//    GridBagConstraints gbc_visibilityPanel = new GridBagConstraints();
//    gbc_visibilityPanel.insets = new Insets(0, 0, 5, 0);
//    gbc_visibilityPanel.fill = GridBagConstraints.BOTH;
//    gbc_visibilityPanel.gridx = 1;
//    gbc_visibilityPanel.gridy = 3;
//    add(visibilityPanel, gbc_visibilityPanel);
  }
}
