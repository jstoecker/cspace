package cspace.gui;

import java.awt.Color;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JColorChooser;
import javax.swing.JPanel;
import javax.swing.border.Border;

import jgl.math.vector.Vec3f;

public class ColorPanel extends JPanel implements MouseListener {

  String         colorName;
  List<Listener> listeners = new ArrayList<Listener>();

  public ColorPanel(String colorName, Vec3f initialColor) {
    this.colorName = colorName;
    setOpaque(true);
    Border b1 = BorderFactory.createMatteBorder(1, 1, 1, 1, Color.black);
    Border b2 = BorderFactory.createMatteBorder(1, 1, 1, 1, Color.lightGray);
    Border b = BorderFactory.createCompoundBorder(b1, b2);
    setBorder(b);
    setBackground(toColor(initialColor));
    addMouseListener(this);
  }

  @Override
  public void mouseClicked(MouseEvent e) {
  }

  @Override
  public void mouseEntered(MouseEvent e) {
  }

  @Override
  public void mouseExited(MouseEvent e) {
  }

  @Override
  public void mouseReleased(MouseEvent e) {
  }

  @Override
  public void mousePressed(MouseEvent e) {
    Color color = JColorChooser.showDialog(ColorPanel.this, colorName,
        getBackground());
    if (color != null) {
      setBackground(color);
      Vec3f c = toVec3f(color);
      for (Listener l : listeners)
        l.colorChanged(this, c);
    }
  }

  static Color toColor(Vec3f v) {
    return new Color(v.x, v.y, v.z);
  }

  static Vec3f toVec3f(Color c) {
    return new Vec3f(c.getRed() / 255f, c.getGreen() / 255f, c.getBlue() / 255f);
  }

  public interface Listener {
    void colorChanged(ColorPanel panel, Vec3f newColor);
  }
}
