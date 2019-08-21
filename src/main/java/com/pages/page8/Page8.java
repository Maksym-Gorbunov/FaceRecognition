package com.pages.page8;

import com.gui.Gui;
import com.pages.Pages;
import com.pages.page8.Webcam;
import javafx.stage.WindowEvent;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import static com.sun.deploy.uitoolkit.ToolkitStore.dispose;
import static com.sun.java.accessibility.util.AWTEventMonitor.addWindowListener;
import java.awt.event.*;


public class Page8 extends JPanel implements Pages {

  private Gui gui;
  private JPanel tab;
  private Webcam webcam = new Webcam();
  private JButton startBtn = new JButton("Start");
  private JButton pauseBtn = new JButton("Pause");
  private JButton grayFilterBtn = new JButton("GrayFilter");
  private JButton binaryFilterBtn = new JButton("BinaryFilter");
  private JButton hsvFilterBtn = new JButton("HsvFilter");
  private JButton filtersOffBtn = new JButton("FiltersOFF");
  private boolean filtering = false;


  public Page8(Gui gui) {
    this.gui = gui;
    this.tab = gui.getTab8();
    initComponents();
    addListeners();

//    addWindowListener(new WindowAdapter() {
//      public void windowClosing(WindowEvent e) {
//        int confirmed = JOptionPane.showConfirmDialog(null,
//                "Are you sure you want to exit the program?", "Exit Program Message Box",
//                JOptionPane.YES_NO_OPTION);
//
//        if (confirmed == JOptionPane.YES_OPTION) {
//          try {
//            dispose();
//          } catch (Exception e1) {
//            e1.printStackTrace();
//          }
//        }
//      }
//    });
  }



  private void addListeners() {

    startBtn.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        Thread thread = new Thread(webcam);
        thread.setDaemon(true);
        webcam.runnable = true;
        thread.start();
//                webcam.on();
      }
    });

    pauseBtn.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        webcam.off();
      }
    });

    grayFilterBtn.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        webcam.setFilter(Webcam.Filter.GRAY);
      }
    });

    binaryFilterBtn.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        webcam.setFilter(Webcam.Filter.BINARY);
      }
    });

    hsvFilterBtn.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        webcam.setFilter(Webcam.Filter.HSV);
      }
    });

    filtersOffBtn.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        webcam.setFilter(Webcam.Filter.OFF);
      }
    });


  }


  private void initComponents() {
    JPanel mainPanel = new JPanel();
    JPanel btnPanel = new JPanel();
    tab.add(mainPanel);
    tab.add(btnPanel);
    mainPanel.add(webcam);
    btnPanel.add(startBtn);
    btnPanel.add(pauseBtn);
    btnPanel.add(grayFilterBtn);
    btnPanel.add(binaryFilterBtn);
    btnPanel.add(hsvFilterBtn);
    btnPanel.add(filtersOffBtn);
  }

  public Webcam getWebcam() {
    return webcam;
  }
}
