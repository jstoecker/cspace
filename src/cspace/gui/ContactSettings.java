package cspace.gui;

import javax.swing.JPanel;

import jgl.math.vector.Vec3f;

import cspace.SceneRenderer;
import cspace.scene.Scene;
import cspace.scene.SceneView;
import cspace.util.ColorPanel;
import cspace.util.PropertyLayout;
import cspace.util.VisibilityWidget;

public class ContactSettings extends JPanel {

  private final Scene         scene;
  private final SceneRenderer renderer;

  public ContactSettings(Scene scene, SceneRenderer renderer) {
    this.scene = scene;
    this.renderer = renderer;

    SceneView.Contacts view = scene.view.contacts;
    PropertyLayout layout = new PropertyLayout();

    ColorPanel intnColor = new ColorPanel("Intersection Color", view.intnColor);
    intnColor.addListener(new ColorAction(view.intnColor));
    layout.add("Intersection Color", intnColor);
    
    VisibilityWidget intnVisibility = new VisibilityWidget(view.intnVisible2d, view.intnVisible3d);
    intnVisibility.addListener(new IntnVisAction());
    layout.add("Intersection Visibility", intnVisibility);
    
    ColorPanel sveColor = new ColorPanel("SumVE Color", view.sveColor);
    sveColor.addListener(new ColorAction(view.sveColor));
    layout.add("SumVE Color", sveColor);
    
    VisibilityWidget sveVisibility = new VisibilityWidget(view.sveVisible2d, view.sveVisible3d);
    sveVisibility.addListener(new SveVisAction());
    layout.add("SumVE Visibility", sveVisibility);
    
    ColorPanel sevColor = new ColorPanel("SumEV Color", view.sevColor);
    sevColor.addListener(new ColorAction(view.sevColor));
    layout.add("SumEV Color", sevColor);
    
    VisibilityWidget sevVisibility = new VisibilityWidget(view.sevVisible2d, view.sevVisible3d);
    sevVisibility.addListener(new SevVisAction());
    layout.add("SumEV Visibility", sevVisibility);

    layout.apply(this);
  }
  
  private class ColorAction implements ColorPanel.Listener {
    private Vec3f color;
    public ColorAction(Vec3f color) {
      this.color = color;
    }
    public void colorChanged(ColorPanel panel, Vec3f newColor) {
      color.set(newColor);
      renderer.get2D().getContactRenderer().markDirty();
    }
  }

  private class IntnVisAction implements VisibilityWidget.Listener {
    public void visibilityChanged(boolean visible2d, boolean visible3d) {
      scene.view.contacts.intnVisible2d = visible2d;
      scene.view.contacts.intnVisible3d = visible3d;
      renderer.get2D().getContactRenderer().markDirty();
      renderer.get3D().getContactRenderer().markDirty();
    }
  }
  
  private class SveVisAction implements VisibilityWidget.Listener {
    public void visibilityChanged(boolean visible2d, boolean visible3d) {
      scene.view.contacts.sveVisible2d = visible2d;
      scene.view.contacts.sveVisible3d = visible3d;
      renderer.get2D().getContactRenderer().markDirty();
      renderer.get3D().getContactRenderer().markDirty();
    }
  }
  
  private class SevVisAction implements VisibilityWidget.Listener {
    public void visibilityChanged(boolean visible2d, boolean visible3d) {
      scene.view.contacts.sevVisible2d = visible2d;
      scene.view.contacts.sevVisible3d = visible3d;
      renderer.get2D().getContactRenderer().markDirty();
      renderer.get3D().getContactRenderer().markDirty();
    }
  }
}
