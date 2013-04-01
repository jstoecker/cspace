package cspace.gui;

import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.ButtonGroup;
import javax.swing.JPanel;
import javax.swing.JRadioButton;

import cspace.visuals.VisibleModel;

public class VisibilityPanel extends JPanel {

  JRadioButton rbOn;
  JRadioButton rb2d;
  JRadioButton rb3d;
  JRadioButton rbOff;
  VisibleModel visuals;

  public VisibilityPanel(VisibleModel visuals) {
    this.visuals = visuals;
    setLayout(new GridLayout(1, 4));
    ButtonGroup group = new ButtonGroup();
    VisibilityActionListener al = new VisibilityActionListener();
    addButton(rbOn = new JRadioButton("On"), group, al);
    addButton(rb2d = new JRadioButton("2D"), group, al);
    addButton(rb3d = new JRadioButton("3D"), group, al);
    addButton(rbOff = new JRadioButton("Off"), group, al);

    if (visuals.isVisible2d() && visuals.isVisible3d()) {
      rbOn.setSelected(true);
    } else if (visuals.isVisible2d() && !visuals.isVisible3d()) {
      rb2d.setSelected(true);
    } else if (!visuals.isVisible2d() && visuals.isVisible3d()) {
      rb3d.setSelected(true);
    } else {
      rbOff.setSelected(true);
    }
  }

  void addButton(JRadioButton button, ButtonGroup group,
      VisibilityActionListener al) {
    add(button);
    group.add(button);
    button.addActionListener(al);
  }

  class VisibilityActionListener implements ActionListener {
    @Override
    public void actionPerformed(ActionEvent e) {
      visuals.setVisible2d(rbOn.isSelected() || rb2d.isSelected());
      visuals.setVisible3d(rbOn.isSelected() || rb3d.isSelected());
    }
  }
}
