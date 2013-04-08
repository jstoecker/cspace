package cspace.gui.settings;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.AbstractListModel;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.ListCellRenderer;
import javax.swing.ListSelectionModel;
import javax.swing.border.MatteBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import cspace.gui.main.MainWindow;
import cspace.visuals.Visuals;

/**
 * Contains configuration options for the scene.
 * 
 * @author justin
 */
public class SettingsDialog extends JDialog {

  String[]   menus = { "General", "Subs", "Robot", "Obstacle", "Path", "SumEEs", "Pnts" };
  JPanel     optionsPanel;
  JList      menuList;
  CardLayout optionsLayout;

  public SettingsDialog(final MainWindow window) {
    super(window);
    setResizable(false);
    setAlwaysOnTop(true);
    setSize(550, 420);
    setTitle("Settings");
    getContentPane().setLayout(new BorderLayout());

    Visuals visuals = window.getScene().visuals;
    optionsPanel = new JPanel();
    optionsPanel.setLayout(optionsLayout = new CardLayout());
    optionsPanel.add(new GeneralPanel(visuals.genVisuals), menus[0]);
    optionsPanel.add(new SubPanel(visuals.subVisuals), menus[1]);
    optionsPanel.add(new RobotPanel(visuals.robotVisuals), menus[2]);
    optionsPanel.add(new ObstaclePanel(visuals.obstacleVisuals), menus[3]);
    optionsPanel.add(new PathPanel(visuals.pathVisuals), menus[4]);
    optionsPanel.add(new SumPanel(visuals.sumEEVisuals), menus[5]);
    optionsPanel.add(new PntVisPanel(visuals.pntVisuals), menus[6]);
    getContentPane().add(optionsPanel, BorderLayout.CENTER);

    JPanel bottomPanel = new JPanel();
    FlowLayout fl_bottomPanel = (FlowLayout) bottomPanel.getLayout();
    fl_bottomPanel.setAlignment(FlowLayout.RIGHT);
    bottomPanel.setBorder(new MatteBorder(1, 0, 0, 0, (Color) Color.GRAY));
    getContentPane().add(bottomPanel, BorderLayout.SOUTH);

    menuList = new JList(new MenuModel());
    menuList.setCellRenderer(new MenuRenderer());
    menuList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    menuList.setPreferredSize(new Dimension(100, 0));
    menuList.setBorder(new MatteBorder(0, 0, 0, 1, (Color) Color.GRAY));
    menuList.setBounds(17, 20, 99, 241);
    menuList.addListSelectionListener(new MenuListener());
    menuList.setSelectedIndex(0);
    getContentPane().add(menuList, BorderLayout.WEST);

    JButton btnResample = new JButton("Resample");
    btnResample.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent arg0) {
        window.getScene().sampleCS();
        window.getController().control3d.view.updateGeometry();
        window.repaintGL();
      }
    });
    bottomPanel.add(btnResample);

    JButton btnOk = new JButton("OK");
    btnOk.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        SettingsDialog.this.setVisible(false);
      }
    });
    bottomPanel.add(btnOk);
  }

  // ===========================================================================
  static class MenuRenderer extends JLabel implements ListCellRenderer {

    public MenuRenderer() {
      setHorizontalAlignment(RIGHT);
      setOpaque(true);
      setBorder(BorderFactory.createEmptyBorder(3, 0, 3, 10));
    }

    @Override
    public Component getListCellRendererComponent(JList list, Object value, int index,
        boolean isSelected, boolean cellHasFocus) {
      setBackground(isSelected ? Color.lightGray : Color.white);
      setText(value.toString());
      return this;
    }
  }

  // ===========================================================================
  class MenuModel extends AbstractListModel {

    @Override
    public Object getElementAt(int index) {
      return menus[index];
    }

    @Override
    public int getSize() {
      return menus.length;
    }
  }

  // ===========================================================================
  class MenuListener implements ListSelectionListener {

    @Override
    public void valueChanged(ListSelectionEvent e) {
      optionsLayout.show(optionsPanel, menus[menuList.getSelectedIndex()]);
    }
  }
  // ===========================================================================
}
