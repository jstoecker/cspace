package cspace.util;

import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.media.opengl.awt.GLCanvas;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import cspace.scene.Path;
import cspace.scene.trimesh.PathPuller;
import cspace.scene.trimesh.SampledCSpace;
import cspace.scene.visuals.Visuals;

/**
 * For debug only.
 * 
 * @author justin
 */
public class DebugPanel extends JFrame {

  PathPuller puller;

  public DebugPanel(final SampledCSpace cs, final Path path, final Visuals visuals,
      final GLCanvas canvas) {
    puller = new PathPuller(cs, path, visuals.pathVisuals);

    setLayout(new FlowLayout());

    JButton button = new JButton("iterate");
    button.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        puller.iterate();
        visuals.pathVisuals.setWaypointsChanged(true);
        canvas.repaint();
      }
    });
    add(button);

    final SpinnerNumberModel model = new SpinnerNumberModel(-1, -1, 100000, 1);
    JSpinner selectedSpn = new JSpinner(model);
    model.addChangeListener(new ChangeListener() {
      public void stateChanged(ChangeEvent e) {
        int value = model.getNumber().intValue();
        if (value < cs.cspace.subs.length) {
          visuals.subVisuals.setSelected(value);
          if (value >= 0) {
            cspace.render3d.SubView.selected = cs.subSamplings.get(cs.cspace.subs[model.getNumber()
                .intValue()]);
          } else {
            cspace.render3d.SubView.selected = null;
          }
          canvas.repaint();
        }
      }
    });
    add(selectedSpn);

    final SpinnerNumberModel triModel = new SpinnerNumberModel(0, 0, 300, 1);
    JSpinner triSpn = new JSpinner(triModel);
    triModel.addChangeListener(new ChangeListener() {
      public void stateChanged(ChangeEvent e) {
        cspace.render3d.SubView.selectedTri = triModel.getNumber().intValue();
        canvas.repaint();
      }
    });
    add(triSpn);

    JButton newPath = new JButton("init puller");
    newPath.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        puller.init = false;
      }
    });
    add(newPath);

    pack();
  }
}
