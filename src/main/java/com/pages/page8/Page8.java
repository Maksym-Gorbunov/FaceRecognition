package com.pages.page8;

import com.gui.Gui;
import com.pages.Pages;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class Page8 extends JPanel implements Pages {

  private Gui gui;
  private JPanel tab;
  private Webcam webcam = new Webcam();
  private JButton startBtn = new JButton("Start");
  private JButton pauseBtn = new JButton("Pause");
  private JButton binaryFilter = new JButton("Binary Filter");
  private JButton filterOffBtn = new JButton("FilterOff");
  private boolean filtering = false;

  public Page8(Gui gui) {
    this.gui = gui;
    this.tab = gui.getTab8();
    initComponents();
    addListeners();

  }

  private void addListeners() {

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

    binaryFilter.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        webcam.binaryFilterOn();
      }
    });

    filterOffBtn.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        webcam.filterOff();
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
    btnPanel.add(binaryFilter);
    btnPanel.add(filterOffBtn);
  }
}
