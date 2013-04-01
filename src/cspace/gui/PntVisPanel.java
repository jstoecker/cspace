package cspace.gui;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import jgl.math.vector.Vec3f;
import cspace.visuals.PntVisuals;

public class PntVisPanel extends JPanel {
  public PntVisPanel(final PntVisuals visuals) {
    setBorder(new EmptyBorder(10, 10, 10, 10));

    GridBagLayout gridBagLayout = new GridBagLayout();
    gridBagLayout.columnWidths = new int[]{193, 0, 0};
    gridBagLayout.rowHeights = new int[]{32, 32, 0};
    gridBagLayout.columnWeights = new double[]{0.0, 1.0, Double.MIN_VALUE};
    gridBagLayout.rowWeights = new double[]{0.0, 0.0, Double.MIN_VALUE};
    setLayout(gridBagLayout);
    
    JLabel lblVisibility = new JLabel("Color");
    GridBagConstraints gbc_lblVisibility = new GridBagConstraints();
    gbc_lblVisibility.anchor = GridBagConstraints.EAST;
    gbc_lblVisibility.insets = new Insets(0, 0, 5, 5);
    gbc_lblVisibility.gridx = 0;
    gbc_lblVisibility.gridy = 0;
    add(lblVisibility, gbc_lblVisibility);
    
    ColorPanel pColor = new ColorPanel("Pnt Color", visuals.getColor());
    pColor.listeners.add(new ColorPanel.Listener() {
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
    
    JLabel lblVisibility_1 = new JLabel("Visibility");
    GridBagConstraints gbc_lblVisibility_1 = new GridBagConstraints();
    gbc_lblVisibility_1.anchor = GridBagConstraints.EAST;
    gbc_lblVisibility_1.insets = new Insets(0, 0, 0, 5);
    gbc_lblVisibility_1.gridx = 0;
    gbc_lblVisibility_1.gridy = 1;
    add(lblVisibility_1, gbc_lblVisibility_1);
    
    VisibilityPanel pVisibility = new VisibilityPanel(visuals);
    GridBagConstraints gbc_pVisibility = new GridBagConstraints();
    gbc_pVisibility.fill = GridBagConstraints.BOTH;
    gbc_pVisibility.gridx = 1;
    gbc_pVisibility.gridy = 1;
    add(pVisibility, gbc_pVisibility);
  }

}
