package cspace.util;

import java.awt.GridLayout;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JCheckBox;
import javax.swing.JPanel;

/**
 * GUI widget for controlling visibility of an object that can be displayed in 2D, 3D, both, or
 * neither.
 * 
 * @author justin
 */
public class VisibilityWidget extends JPanel {

  public JCheckBox      visible2d;
  public JCheckBox      visible3d;
  private List<Listener> listeners = new ArrayList<Listener>();

  public VisibilityWidget(boolean on2D, boolean on3D) {
    setLayout(new GridLayout(1, 2));

    visible2d = new JCheckBox("Draw 2D");
    visible2d.setSelected(on2D);
    visible2d.setHorizontalAlignment(JCheckBox.RIGHT);
    visible2d.setHorizontalTextPosition(JCheckBox.LEFT);
    visible2d.addItemListener(new Action());
    add(visible2d);

    visible3d = new JCheckBox("Draw 3D");
    visible3d.setSelected(on3D);
    visible3d.setHorizontalAlignment(JCheckBox.RIGHT);
    visible3d.setHorizontalTextPosition(JCheckBox.LEFT);
    visible3d.addItemListener(new Action());
    add(visible3d);
  }

  public void addListener(Listener l) {
    listeners.add(l);
  }

  private class Action implements ItemListener {
    public void itemStateChanged(ItemEvent e) {
      for (Listener l : listeners)
        l.visibilityChanged(visible2d.isSelected(), visible3d.isSelected());
    }
  }

  public interface Listener {
    void visibilityChanged(boolean visible2d, boolean visible3d);
  }
}
