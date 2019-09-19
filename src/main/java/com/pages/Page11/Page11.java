package com.pages.Page11;

import com.constants.Constants;
import com.gui.Gui;
import com.pages.Page11.WebcamPanel;
import com.pages.Pages;
import org.apache.commons.io.FileUtils;
import org.opencv.core.Mat;
import org.opencv.core.Rect;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.objdetect.CascadeClassifier;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;

public class Page11 extends JPanel implements Pages {

  public static Rect rect = null;
  private Gui gui;
  private JPanel tab11;
  private WebcamPanel webcamPanel = new WebcamPanel(Constants.VIDEO_WIDTH, Constants.VIDEO_HEIGHT);
  private JButton startBtn = new JButton("Start");
  private JButton stopBtn = new JButton("Stop");
  private JButton bgBtn = new JButton("------------");
  private String path = Constants.imgPath + "faces\\";

  // Constructor
  public Page11(Gui gui) {
    this.gui = gui;
    this.tab11 = gui.getTab11();
    initComponents();
    addListeners();
  }


  // Listeners
  private void addListeners() {

    //start
    startBtn.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        clearFolder(path);
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

      }
    });

  }


  // Initialization
  private void initComponents() {
    JPanel mainPanel = new JPanel();
    mainPanel.add(webcamPanel);
    tab11.add(mainPanel);
    JPanel btnsPanel = new JPanel();
    btnsPanel.add(startBtn);
    btnsPanel.add(stopBtn);
    btnsPanel.add(bgBtn);
    tab11.add(btnsPanel);
    stopBtn.setEnabled(false);
    bgBtn.setEnabled(false);
  }

  // Clear folder from old files
  public void clearFolder(String path) {
    try {
      FileUtils.deleteDirectory(new File(path));
    } catch (IOException e) {
      e.printStackTrace();
    }
    new File(path).mkdirs();
  }
}
