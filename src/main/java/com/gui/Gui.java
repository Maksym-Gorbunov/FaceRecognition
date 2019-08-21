package com.gui;

import com.constants.Constants;
import com.pages.Page7.Page7;
import com.pages.page1.Page1;
import com.pages.page2.Page2;
import com.pages.page3.Page3;
import com.pages.page4.Page4;
import com.pages.page5.Page5;
import com.pages.page6.Page6;
import com.pages.page8.Page8;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.*;

public class Gui extends JFrame {
  private JTabbedPane tabs;
  private JPanel tab1;
  private JPanel tab2;
  private JPanel tab3;
  private JPanel tab4;
  private JPanel tab5;
  private JPanel tab6;
  private JPanel tab7;
  private JPanel tab8;

  public Gui() {
    super("Application");
    setSize(Constants.FRAME_WIDTH, Constants.FRAME_HEIGHT);
    setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    setLocationRelativeTo(this);
    setResizable(false);
    setVisible(true);

    initComponents();
    createMenuBar();

//    System.out.println(Gui.this.getJMenuBar().getHeight());
  }

  private void initComponents() {

    tabs = new JTabbedPane();
    tabs.setBackground(Color.green);
    tab1 = new JPanel();
    tab2 = new JPanel();
    tab3 = new JPanel();
    tab4 = new JPanel();
    tab5 = new JPanel();
    tab6 = new JPanel();
    tab7 = new JPanel();
    tab8 = new JPanel();
    //////////////////////////////////////////////////////////////////////
    tab1.setBackground(Color.CYAN);
    tab2.setBackground(Color.CYAN);
    tab3.setBackground(Color.CYAN);
    tab4.setBackground(Color.CYAN);
    tab5.setBackground(Color.CYAN);
    tab6.setBackground(Color.CYAN);
    tab7.setBackground(Color.CYAN);
    tab8.setBackground(Color.CYAN);
    //////////////////////////////////////////////////////////////////////
    tabs.addTab("Webbcam", tab1);
    tabs.addTab("Mongo CRUD", tab2);
    tabs.addTab("Image Recognition", tab3);
    tabs.addTab("Webbcam Recognition", tab4);
    tabs.addTab("Text Recognition", tab5);
    tabs.addTab("Image Manipulations", tab6);
    tabs.addTab("Video Manipulations", tab7);
    tabs.addTab("VIDEO", tab8);
    add(tabs);

    tab1 = new Page1(Gui.this);
    tab2 = new Page2(Gui.this);
    tab3 = new Page3(Gui.this);
    tab4 = new Page4(Gui.this);
    tab5 = new Page5(Gui.this);
    tab6 = new Page6(Gui.this);
    tab7 = new Page7(Gui.this);
    tab8 = new Page8(Gui.this);

    tabs.setSelectedIndex(7);
  }

  private void createMenuBar() {
    JMenuBar menuBar = new JMenuBar();
    JMenu fileMenu = new JMenu("File");
    JMenuItem exitMenuItem = new JMenuItem("Exit");
    fileMenu.add(exitMenuItem);
    menuBar.add(fileMenu);
    setJMenuBar(menuBar);

    exitMenuItem.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent arg0) {
        int action = JOptionPane.showConfirmDialog(Gui.this, Constants.EXIT_WARNING);
        if (action == JOptionPane.OK_OPTION) {
          System.gc();
          System.exit(0);
        }
      }
    });
  }

  // Getters
  public JPanel getTab1() {
    return tab1;
  }

  public JPanel getTab2() {
    return tab2;
  }

  public JPanel getTab3() {
    return tab3;
  }

  public JPanel getTab4() {
    return tab4;
  }

  public JTabbedPane getTabs() {
    return tabs;
  }

  public JPanel getTab5() {
    return tab5;
  }

  public JPanel getTab6() {
    return tab6;
  }

  public JPanel getTab7() {
    return tab7;
  }

  public JPanel getTab8() {
    return tab8;
  }
}
