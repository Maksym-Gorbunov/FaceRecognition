package com.gui;

import com.constants.Constants;
import com.pages.page1.Page1;
import com.pages.page2.Page2;
import com.pages.page3.Page3;

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

  public Gui() {
    super("Application");
    setSize(Constants.FRAME_WIDTH, Constants.FRAME_HEIGHT);
    setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    setLocationRelativeTo(this);
    setResizable(false);
    setVisible(true);

    createMenuBar();
    initComponents();

    System.out.println(Gui.this.getJMenuBar().getHeight());
  }

  private void initComponents() {

    tabs = new JTabbedPane();
    tabs.setBackground(Color.green);
    tab1 = new JPanel();
    tab2 = new JPanel();
    tab3 = new JPanel();
    tab4 = new JPanel();
    //////////////////////////////////////////////////////////////////////
    tab1.setBackground(Color.CYAN);
    tab2.setBackground(Color.CYAN);
    tab3.setBackground(Color.CYAN);
    tab4.setBackground(Color.CYAN);
    //////////////////////////////////////////////////////////////////////
    tabs.addTab("One", tab1);
    tabs.addTab("Two", tab2);
    tabs.addTab("Three", tab3);
    tabs.addTab("Four", tab4);
    add(tabs);

    tab1 = new Page1(Gui.this);
    tab2 = new Page2(Gui.this);
    tab3 = new Page3(Gui.this);
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
        if(action == JOptionPane.OK_OPTION){
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


}
