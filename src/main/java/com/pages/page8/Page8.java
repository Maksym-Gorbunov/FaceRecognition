package com.pages.page8;

import com.constants.Constants;
import com.gui.Gui;
import com.pages.Pages;

import javax.swing.*;
import java.awt.*;

public class Page8 extends JPanel implements Pages {

  private Gui gui;
  private JPanel tab;
  private Webcam webcam = new Webcam();
  private JButton startBtn =  new JButton("Start");
  private JButton stopBtn =  new JButton("Start");
  private JButton testBtn =  new JButton("Test");

  public Page8(Gui gui) {
    this.gui = gui;
    this.tab = gui.getTab8();
    initComponents();
    addListeners();

  }

  private void addListeners() {
    webcam.on();
  }


  private void initComponents() {
    JPanel mainPanel = new JPanel();
    JPanel btnPanel = new JPanel();
    tab.add(mainPanel);
    tab.add(btnPanel);
    mainPanel.add(webcam);

    btnPanel.add(startBtn);
    btnPanel.add(stopBtn);
    btnPanel.add(testBtn);



  }
}
