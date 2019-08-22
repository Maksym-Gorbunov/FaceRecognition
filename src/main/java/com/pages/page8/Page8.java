package com.pages.page8;

import com.gui.Gui;
import com.pages.Pages;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.*;


public class Page8 extends JPanel implements Pages {

  private Gui gui;
  private JPanel tab;
  private Webcam webcam = new Webcam();
  private JButton startBtn = new JButton("Start");
  private JButton pauseBtn = new JButton("Pause");
  private JButton filtersOffBtn = new JButton("FiltersOFF");
  private JButton grayFilterBtn = new JButton("GrayFilter");
  private JButton hsvFilterBtn = new JButton("HsvFilter");
  private JButton colorFilterBtn = new JButton("ColorFilter");
  private JButton contoursFilterBtn = new JButton("ContoursFilter");
  private JButton momentsBtn = new JButton("Moments");
  private JButton objectsBtn = new JButton("Objects");
  private boolean filtering = false;


  public Page8(Gui gui) {
    this.gui = gui;
    this.tab = gui.getTab8();
    initComponents();
    addListeners();
  }


  private void addListeners() {

    gui.addWindowListener(new WindowAdapter() {
      @Override
      public void windowClosing(WindowEvent e) {
        webcam.off();
        System.out.println("Kill camera Thread on closing");
        e.getWindow().dispose();
      }
    });

    startBtn.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        webcam.on();
      }
    });

    pauseBtn.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        webcam.off();
      }
    });

    filtersOffBtn.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        webcam.setFilter(Webcam.Filter.OFF);
      }
    });



    grayFilterBtn.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        webcam.setFilter(Webcam.Filter.GRAY);
      }
    });

    hsvFilterBtn.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        webcam.setFilter(Webcam.Filter.HSV);
      }
    });

    colorFilterBtn.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        webcam.setFilter(Webcam.Filter.COLOR);
      }
    });

    contoursFilterBtn.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        webcam.setFilter(Webcam.Filter.CONTOURS);
      }
    });

    momentsBtn.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        webcam.setFilter(Webcam.Filter.MOMENTS);
      }
    });

    objectsBtn.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        webcam.setFilter(Webcam.Filter.OBJECTS);

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
    btnPanel.add(filtersOffBtn);
    btnPanel.add(grayFilterBtn);
    btnPanel.add(hsvFilterBtn);
    btnPanel.add(colorFilterBtn);
    btnPanel.add(contoursFilterBtn);
    btnPanel.add(momentsBtn);
    btnPanel.add(objectsBtn);
  }


}
