package cspace.gui;

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
import cspace.visuals.GeneralVisuals;
import cspace.visuals.GeneralVisuals.ViewMode;

public class GeneralVisPanel extends JPanel {
  public GeneralVisPanel(final GeneralVisuals visuals) {
    setBorder(new EmptyBorder(10, 10, 10, 10));
    GridBagLayout gridBagLayout = new GridBagLayout();
    gridBagLayout.columnWidths = new int[] { 193, 0, 0 };
    gridBagLayout.rowHeights = new int[] { 32, 32, 0, 0, 0 };
    gridBagLayout.columnWeights = new double[] { 0.0, 1.0, Double.MIN_VALUE };
    gridBagLayout.rowWeights = new double[] { 0.0, 0.0, 0.0, 0.0,
        Double.MIN_VALUE };
    setLayout(gridBagLayout);

    JLabel lblNewLabel = new JLabel("Background Color");
    GridBagConstraints gbc_lblNewLabel = new GridBagConstraints();
    gbc_lblNewLabel.insets = new Insets(0, 0, 5, 5);
    gbc_lblNewLabel.anchor = GridBagConstraints.EAST;
    gbc_lblNewLabel.gridx = 0;
    gbc_lblNewLabel.gridy = 0;
    add(lblNewLabel, gbc_lblNewLabel);

    ColorPanel pColor = new ColorPanel("Background Color", visuals.getBgColor());
    pColor.listeners.add(new ColorPanel.Listener() {
      public void colorChanged(ColorPanel panel, Vec3f newColor) {
        visuals.setBgColor(newColor);
      }
    });
    GridBagConstraints gbc_pColor = new GridBagConstraints();
    gbc_pColor.insets = new Insets(2, 2, 7, 2);
    gbc_pColor.fill = GridBagConstraints.BOTH;
    gbc_pColor.gridx = 1;
    gbc_pColor.gridy = 0;
    add(pColor, gbc_pColor);

    JLabel lblNewLabel_1 = new JLabel("Viewing Mode");
    GridBagConstraints gbc_lblNewLabel_1 = new GridBagConstraints();
    gbc_lblNewLabel_1.anchor = GridBagConstraints.EAST;
    gbc_lblNewLabel_1.insets = new Insets(0, 0, 5, 5);
    gbc_lblNewLabel_1.gridx = 0;
    gbc_lblNewLabel_1.gridy = 1;
    add(lblNewLabel_1, gbc_lblNewLabel_1);

    final JComboBox cbView = new JComboBox();
    for (ViewMode mode : GeneralVisuals.ViewMode.values())
      cbView.addItem(mode);
    cbView.setSelectedItem(visuals.getView());
    cbView.addItemListener(new ItemListener() {
      public void itemStateChanged(ItemEvent arg0) {
        visuals.setView((ViewMode)cbView.getSelectedItem());
      }
    });
    GridBagConstraints gbc_cbView = new GridBagConstraints();
    gbc_cbView.insets = new Insets(0, 0, 5, 0);
    gbc_cbView.fill = GridBagConstraints.HORIZONTAL;
    gbc_cbView.gridx = 1;
    gbc_cbView.gridy = 1;
    add(cbView, gbc_cbView);

    JLabel lblScale = new JLabel("Scale");
    GridBagConstraints gbc_lblScale = new GridBagConstraints();
    gbc_lblScale.anchor = GridBagConstraints.EAST;
    gbc_lblScale.insets = new Insets(0, 0, 5, 5);
    gbc_lblScale.gridx = 0;
    gbc_lblScale.gridy = 2;
    add(lblScale, gbc_lblScale);

    final SpinnerNumberModel scaleModel = new SpinnerNumberModel(
        visuals.getScale(), -100, 100, 0.1);
    JSpinner spnScale = new JSpinner(scaleModel);
    spnScale.addChangeListener(new ChangeListener() {
      public void stateChanged(ChangeEvent e) {
        visuals.setScale(scaleModel.getNumber().floatValue());
      }
    });
    GridBagConstraints gbc_spnScale = new GridBagConstraints();
    gbc_spnScale.insets = new Insets(0, 0, 5, 0);
    gbc_spnScale.fill = GridBagConstraints.HORIZONTAL;
    gbc_spnScale.gridx = 1;
    gbc_spnScale.gridy = 2;
    add(spnScale, gbc_spnScale);

    final JCheckBox cxbFWEdges = new JCheckBox("Fixed-Width Edges");
    cxbFWEdges.setSelected(visuals.isFixedWidthEdges());
    cxbFWEdges.setHorizontalTextPosition(SwingConstants.LEFT);
    cxbFWEdges.addChangeListener(new ChangeListener() {
      public void stateChanged(ChangeEvent arg0) {
        visuals.setFixedWidthEdges(cxbFWEdges.isSelected());
      }
    });
    GridBagConstraints gbc_cxbFWEdges = new GridBagConstraints();
    gbc_cxbFWEdges.anchor = GridBagConstraints.EAST;
    gbc_cxbFWEdges.gridx = 1;
    gbc_cxbFWEdges.gridy = 3;
    add(cxbFWEdges, gbc_cxbFWEdges);

  }

}
