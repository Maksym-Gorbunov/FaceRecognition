package com.pages.page6;

import java.awt.*;
import com.constants.Constants;
import com.gui.Gui;
import com.gui.ImagePanel;
import com.intellij.uiDesigner.core.GridLayoutManager;
import com.pages.Pages;
import com.pages.page3.algoritm.FaceDetection;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

public class Page6 extends JPanel implements Pages {
  private static final long serialVersionUID = 1L;
  private Gui gui;
  private JPanel tab6;
  private JPanel btnPanel;
  private JPanel mainPanel;
  private ImagePanel imagePanel1;
  private ImagePanel imagePanel2;
  private JButton loadBtn;
  private JButton filterBtn;
  private JButton grayBtn;
  private JButton hsvBtn;
  private File file;
  private int width = 320;
  private int height = 240;
  private JFileChooser fileChooser;

  public Page6(final Gui gui) {
    this.gui = gui;
    tab6 = gui.getTab6();
    initComponents();
    initButtons();
    addListeners();
  }

  private void addListeners() {
    loadBtn.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        if(fileChooser.showOpenDialog(gui) == JFileChooser.APPROVE_OPTION){
          file = fileChooser.getSelectedFile();
          // load the image
          System.out.println("Image url: " + file);
          imagePanel1.loadImage(file);
          gui.getTabs().setSelectedComponent(tab6);
        }
      }
    });
  }

  private void initComponents() {
    btnPanel = new JPanel();
    btnPanel.setPreferredSize(new Dimension(800, 200));
    btnPanel.setBackground(Color.green);

    mainPanel = new JPanel();
    mainPanel.setPreferredSize(new Dimension(800,300));
    mainPanel.setBackground(Color.MAGENTA);

//    tab6.setLayout(new GridLayout(2,1));
    tab6.add(btnPanel);
    tab6.add(mainPanel);

    mainPanel.setLayout(new FlowLayout());
    imagePanel1 = new ImagePanel(width,height);
    imagePanel1.setBackground(Color.black);
    imagePanel2 = new ImagePanel(width,height);
    imagePanel2.setBackground(Color.black);
    mainPanel.add(imagePanel1);
    mainPanel.add(imagePanel2);

    loadBtn = new JButton("Load");
    loadBtn.setBackground(Color.green);
    filterBtn = new JButton("Filter ON/OFF");
    grayBtn = new JButton("gray");
    hsvBtn = new JButton("hsv");
    btnPanel.add(loadBtn);
    btnPanel.add(filterBtn);
    btnPanel.add(grayBtn);
    btnPanel.add(hsvBtn);

    fileChooser = new JFileChooser();
  }

  public void initButtons(){
    grayBtn.setEnabled(false);
    hsvBtn.setEnabled(false);
  }
}
