package cspace.ui.swing;

import java.util.ArrayList;
import java.util.List;

import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import jgl.math.vector.Vec3f;
import cspace.SceneRenderer;
import cspace.scene.EdgePair;
import cspace.scene.Scene;
import cspace.scene.SceneView;
import cspace.util.ColorPanel;
import cspace.util.PropertyLayout;
import cspace.util.VisibilityWidget;

public class SumSettings extends JPanel {

  public SumSettings(final Scene scene, final SceneRenderer renderer) {

    final SceneView.Sums view = scene.view.sums;
    
    PropertyLayout layout = new PropertyLayout();
    
    // color
    ColorPanel colorPanel = new ColorPanel("Sums Color", view.color);
    colorPanel.addListener(new ColorPanel.Listener() {
      public void colorChanged(ColorPanel panel, Vec3f newColor) {
        view.color.set(newColor);
      }
    });
    layout.add("Color", colorPanel);

    // edge width
    final SpinnerNumberModel widthModel = new SpinnerNumberModel(view.edgeWidth, 0, 100, 0.01);
    JSpinner widthSpinner = new JSpinner(widthModel);
    widthSpinner.addChangeListener(new ChangeListener() {
      public void stateChanged(ChangeEvent e) {
        view.edgeWidth = widthModel.getNumber().floatValue();
        renderer.get2D().getSumRenderer().markDirty();
      }
    });
    layout.add("Edge Width", widthSpinner);

    // drawn 3d
    final JTextField drawn3d = new JTextField(pairsToString(view.drawn3d));
    drawn3d.getDocument().addDocumentListener(new DocumentListener() {
      public void removeUpdate(DocumentEvent e) {
        view.drawn3d = stringToPairs(drawn3d.getText());
        renderer.get3D().getSumRenderer().markDirty();
      }
      public void insertUpdate(DocumentEvent e) {
        view.drawn3d = stringToPairs(drawn3d.getText());
        renderer.get3D().getSumRenderer().markDirty();
      }
      public void changedUpdate(DocumentEvent e) {
      }
    });
    layout.add("Edge Pairs 3D", drawn3d);
    
    // visibility
    VisibilityWidget visibility = new VisibilityWidget(view.visible2d, view.visible3d);
    visibility.addListener(new VisibilityWidget.Listener() {
      public void visibilityChanged(boolean visible2d, boolean visible3d) {
        view.visible2d = visible2d;
        view.visible3d = visible3d;
      }
    });
    layout.add(visibility);
    
    layout.apply(this);
  }
  
  private static String pairsToString(List<EdgePair> pairs) {
    StringBuilder s = new StringBuilder();
    for (EdgePair pair : pairs) {
      s.append(pair.robEdge);
      s.append("/");
      s.append(pair.obsEdge);
      s.append(" ");
    }
    return s.toString();
  }
  
  private static List<EdgePair> stringToPairs(String s) {
    List<EdgePair> pairs = new ArrayList<EdgePair>();
    String[] groups = s.split("\\s++");

    for (String group : groups) {
      int i = group.indexOf("/");
      try {
        int robEdge = Integer.parseInt(group.substring(0, i));
        int obsEdge = Integer.parseInt(group.substring(i + 1));
        pairs.add(new EdgePair(robEdge, obsEdge));
      } catch (Exception e) {
        continue;
      }
    }
    
    return pairs;
  }
}
