package cspace.util;

import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
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

  private JCheckBox      visible2d;
  private JCheckBox      visible3d;
  private List<Listener> listeners = new ArrayList<Listener>();

  public VisibilityWidget(boolean on2D, boolean on3D) {
    setLayout(new GridLayout(1, 2));

    visible2d = new JCheckBox("2D");
    visible2d.setSelected(on2D);
    visible2d.addActionListener(new Action());
    add(visible2d);

    visible3d = new JCheckBox("3D");
    visible3d.setSelected(on3D);
    visible3d.addActionListener(new Action());
    add(visible3d);
  }

  public void addListener(Listener l) {
    listeners.add(l);
  }

  private class Action implements ActionListener {
    public void actionPerformed(ActionEvent e) {
      for (Listener l : listeners)
        l.visibilityChanged(visible2d.isSelected(), visible3d.isSelected());
    }
  }

  public interface Listener {
    void visibilityChanged(boolean visible2d, boolean visible3d);
  }
}
