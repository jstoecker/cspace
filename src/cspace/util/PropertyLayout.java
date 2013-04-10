package cspace.util;

import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.swing.JComponent;
import javax.swing.JLabel;

/**
 * Utility class for organizing Swing components in a two-column layout.
 * 
 * @author justin
 */
public class PropertyLayout {

  public int         labelColumnWidth = 180;
  public int         rowHeight        = 0;
  private List<Item> items            = new ArrayList<Item>();

  public void add(Component component) {
    add(null, component);
  }

  public void add(String label, Component component) {
    items.add(new Item(label, component));
  }

  public void apply(JComponent component) {
    int numRows = items.size();

    GridBagLayout layout = new GridBagLayout();
    layout.columnWidths = new int[] { labelColumnWidth, 0, 0 };
    layout.columnWeights = new double[] { 0.0, 1.0, Double.MIN_VALUE };

    int[] rowHeights = new int[numRows + 1];
    Arrays.fill(rowHeights, rowHeight);
    layout.rowHeights = rowHeights;

    double[] rowWeights = new double[numRows + 1];
    rowWeights[numRows] = Double.MIN_VALUE;
    layout.rowWeights = rowWeights;

    component.setLayout(layout);

    for (int row = 0; row < items.size(); row++) {
      Item item = items.get(row);

      if (item.label != null) {
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.anchor = GridBagConstraints.EAST;
        gbc.ipadx = 10;
        gbc.gridx = 0;
        gbc.gridy = row;
        component.add(new JLabel(item.label), gbc);
      }

      GridBagConstraints gbc = new GridBagConstraints();
      gbc.fill = GridBagConstraints.HORIZONTAL;
      gbc.insets = new Insets(0, 10, 0, 10);
      gbc.gridx = 1;
      gbc.gridy = row;
      component.add(item.component, gbc);
    }
  }

  private class Item {
    String    label;
    Component component;

    Item(String label, Component component) {
      this.label = label;
      this.component = component;
    }
  }
}
