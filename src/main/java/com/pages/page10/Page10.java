package com.pages.page10;

import com.constants.Constants;
import com.gui.Gui;
import com.pages.Pages;
import org.opencv.core.Mat;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class Page10 extends JPanel implements Pages {

  private Gui gui;
  private JPanel tab10;
  private WebcamPanel webcamPanel = new WebcamPanel(Constants.VIDEO_WIDTH, Constants.VIDEO_HEIGHT);
  private JButton startBtn = new JButton("Start");
  private JButton stopBtn = new JButton("Stop");
  private JButton bgBtn = new JButton("Set background");


  // Constructor
  public Page10(Gui gui){
    this.gui = gui;
    this.tab10 = gui.getTab10();
    initComponents();
    addListeners();

  }


  // Listeners
  private void addListeners() {

    //start
    startBtn.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        startBtn.setEnabled(false);
        webcamPanel.start();
        stopBtn.setEnabled(true);
        bgBtn.setEnabled(true);
      }
    });

    //stop
    stopBtn.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        stopBtn.setEnabled(false);
        webcamPanel.stop();
        startBtn.setEnabled(true);
        bgBtn.setEnabled(false);
      }
    });

    //set background, scene layout
    bgBtn.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        webcamPanel.setBg();
        bgBtn.setEnabled(false);
      }
    });

  }


  // Initialization
  private void initComponents() {
    JPanel mainPanel = new JPanel();
    mainPanel.add(new JLabel("HORSE"));
    mainPanel.add(webcamPanel);
    tab10.add(mainPanel);
    JPanel btnsPanel = new JPanel();
    btnsPanel.add(startBtn);
    btnsPanel.add(stopBtn);
    btnsPanel.add(bgBtn);
    tab10.add(btnsPanel);
    stopBtn.setEnabled(false);
    bgBtn.setEnabled(false);
  }
}
