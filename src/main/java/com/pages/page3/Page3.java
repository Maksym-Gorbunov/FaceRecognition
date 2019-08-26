package com.pages.page3;

import com.constants.Constants;
import com.gui.Gui;
import com.gui.ImagePanel;
import com.intellij.uiDesigner.core.GridConstraints;
import com.pages.Pages;
import com.pages.page3.algoritm.FaceDetection;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;


// Image face recognition, OpenCV
public class Page3  extends JPanel implements Pages {
  private static final long serialVersionUID = 1L;
  private Gui gui;
  private JPanel tab3;
  private JMenuBar menuBar;
  private JLabel imageLabel;
  private ImageIcon transformedImageIcon;
  private ImagePanel imagePanel;
  private JFileChooser fileChooser;
  private FaceDetection faceDetection;
  private File file;

  public Page3(final Gui gui) {
    this.gui = gui;
    tab3 = gui.getTab3();
    menuBar = gui.getJMenuBar();
    this.imagePanel = new ImagePanel(Constants.VIDEO_WIDTH,Constants.VIDEO_HEIGHT);
    this.fileChooser = new JFileChooser(Constants.imgPath);
    this.faceDetection = new FaceDetection();
    initComponents();
    populateMenuBar();
  }

  private void initComponents() {
//    imagePanel.setPreferredSize(new Dimension(Constants.VIDEO_WIDTH,Constants.VIDEO_HEIGHT));
    tab3.add(imagePanel, new GridConstraints());
  }

  public void populateMenuBar(){
    JMenu fileMenu = gui.getJMenuBar().getMenu(0);
    JMenuItem loadMenuItem = new JMenuItem("Load image");
    JMenuItem detectMenuItem = new JMenuItem("Detect faces");
    fileMenu.add(loadMenuItem);
    fileMenu.add(detectMenuItem);

    loadMenuItem.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        if(fileChooser.showOpenDialog(gui) == JFileChooser.APPROVE_OPTION){
          file = fileChooser.getSelectedFile();
          // load the image
          System.out.println("Image url: " + file);
          imagePanel.loadImage(file);
          gui.getTabs().setSelectedComponent(tab3);
        }
      }
    });
    detectMenuItem.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        // detect algorithm
        faceDetection.detectFaces(file, imagePanel);
        gui.getTabs().setSelectedComponent(tab3);
      }
    });

    JMenu aboutMenu = new JMenu("About");
    JMenu helpMenu = new JMenu("Help");
    menuBar.add(aboutMenu);
    menuBar.add(helpMenu);
  }
}
