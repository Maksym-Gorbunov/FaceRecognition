package com.pages.Page7;

import com.constants.Constants;
import com.gui.Gui;
import com.gui.ImagePanel;
import com.pages.Pages;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;


public class Page7 extends JPanel implements Pages {

  private JFrame gui;
  private JPanel tab7;
  private JButton startBtn = new JButton("Start");
  private JButton recognizeBtn = new JButton("none");
  private JFileChooser fileChooser = new JFileChooser();
  private File file = null;
  private ImagePanel imagePanel;

  public Page7(Gui gui){
    this.gui = gui;
    tab7 = gui.getTab7();
    initComponents();
    addListeners();

  }

  private void addListeners() {

    startBtn.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {

      }
    });

    recognizeBtn.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {

      }
    });
  }

  private void initComponents() {
    JPanel mainPanel = new JPanel();
//    mainPanel.setPreferredSize(new Dimension(Constants.VIDEO_WIDTH, Constants.VIDEO_HEIGHT));
    imagePanel = new ImagePanel(Constants.VIDEO_WIDTH, Constants.VIDEO_HEIGHT);
//    ImagePanel.setPreferredSize(new Dimension(Constants.VIDEO_WIDTH, Constants.VIDEO_HEIGHT));
    mainPanel.add(imagePanel);

    tab7.add(mainPanel);

    JPanel btnPanel = new JPanel();
    btnPanel.add(startBtn);
    btnPanel.add(recognizeBtn);
    tab7.add(btnPanel);

  }

}
