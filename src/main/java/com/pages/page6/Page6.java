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
import java.awt.image.BufferedImage;
import java.io.File;

import com.constants.Constants;
import org.bytedeco.javacpp.opencv_core.IplImage;

import static org.bytedeco.javacpp.opencv_core.*;
import static org.bytedeco.javacpp.opencv_imgproc.*;
import static org.bytedeco.javacpp.opencv_highgui.*;


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
  private JButton contourBtn;
  private File file;
  private int width = 320;
  private int height = 240;
  private JFileChooser fileChooser;
  private String grayImgPath = Constants.imgPath + "grayImg.jpg";
  private String hsvImgPath = Constants.imgPath + "hsvImg.jpg";
  private String maxContourImgPath;
  private boolean filter;
//  private boolean filtersCreated;

  public Page6(final Gui gui) {
    this.gui = gui;
    tab6 = gui.getTab6();
    initComponents();
    initButtons();
    addListeners();
    filter = false;
//    filtersCreated = false;
  }

  private void addListeners() {
    loadBtn.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        if (fileChooser.showOpenDialog(gui) == JFileChooser.APPROVE_OPTION) {
          file = fileChooser.getSelectedFile();
          // load the image
          System.out.println("Image url: " + file);
          imagePanel1.loadImage(file);
          gui.getTabs().setSelectedComponent(tab6);
          filterBtn.setEnabled(true);
          createFilters();

        }
      }
    });

    filterBtn.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        if(file != null) {
          if(!filter){

            filterBtn.setText("Filter OFF");
//            if(!filtersCreated){
//              createFilters();
//            }
            imagePanel2.loadIplImage(grayImgPath);

            grayBtn.setEnabled(false);
            hsvBtn.setEnabled(true);
            contourBtn.setEnabled(true);

            // Clear memory
//            cvReleaseImage(img);
//            cvReleaseImage(hsvimg);
//            cvReleaseImage(grayimg);

            filter = !filter;
          } else {
            filterBtn.setText("Filter ON");

            imagePanel2.clear();
//            imagePanel2.updadeImage(null);

            grayBtn.setEnabled(false);
            hsvBtn.setEnabled(false);
            contourBtn.setEnabled(false);
            filter = !filter;
//            imagePanel2.setBackground(Color.black);

            filterBtn.setBackground(Color.green);
            filterBtn.setOpaque(true);
            filterBtn.setBorderPainted(false);


          }
        }
      }
    });

    grayBtn.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        imagePanel2.loadIplImage(grayImgPath);
        grayBtn.setEnabled(false);
        hsvBtn.setEnabled(true);
        contourBtn.setEnabled(true);
      }
    });

    hsvBtn.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        imagePanel2.loadIplImage(hsvImgPath);
        hsvBtn.setEnabled(false);
        grayBtn.setEnabled(true);
        contourBtn.setEnabled(true);
      }
    });

    contourBtn.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        System.out.println("xxx");
        contourBtn.setEnabled(false);
        hsvBtn.setEnabled(true);
        grayBtn.setEnabled(true);


        imagePanel2.loadIplImage(maxContourImgPath);

      }
    });
  }

  public void createFilters(){
    // create gray and hvs filters
    IplImage img = cvLoadImage(file.getAbsolutePath());

    IplImage hsvimg = cvCreateImage(cvGetSize(img), IPL_DEPTH_8U, 3);
    IplImage grayimg = cvCreateImage(cvGetSize(img), IPL_DEPTH_8U, 1);

    cvCvtColor(img, hsvimg, CV_BGR2HSV);
    cvCvtColor(img, grayimg, CV_BGR2GRAY);

    cvSaveImage(grayImgPath, grayimg);
    cvSaveImage(hsvImgPath, hsvimg);
    // create max contour filter
    maxContourImgPath = ImageFiltering.filterMaxContour(file.getAbsolutePath());

//    filtersCreated = true;
  }

  private void initComponents() {
    btnPanel = new JPanel();
    btnPanel.setPreferredSize(new Dimension(800, 200));
    btnPanel.setBackground(Color.green);

    mainPanel = new JPanel();
    mainPanel.setPreferredSize(new Dimension(800, 300));
    mainPanel.setBackground(Color.MAGENTA);
    tab6.add(btnPanel);
    tab6.add(mainPanel);

    mainPanel.setLayout(new FlowLayout());
    imagePanel1 = new ImagePanel(width, height);
    imagePanel1.setBackground(Color.black);
    imagePanel2 = new ImagePanel(width, height);
    imagePanel2.setBackground(Color.black);
    mainPanel.add(imagePanel1);
    mainPanel.add(imagePanel2);

    loadBtn = new JButton("Load");
    filterBtn = new JButton("Filter ON");
    filterBtn.setBackground(Color.green);
    filterBtn.setOpaque(true);
    filterBtn.setBorderPainted(false);


    grayBtn = new JButton("gray");
    hsvBtn = new JButton("hsv");
    contourBtn = new JButton("contour");
    btnPanel.add(loadBtn);
    btnPanel.add(filterBtn);
    btnPanel.add(grayBtn);
    btnPanel.add(hsvBtn);
    btnPanel.add(contourBtn);

    fileChooser = new JFileChooser();
  }

  public void initButtons() {
    filterBtn.setEnabled(false);
    grayBtn.setEnabled(false);
    hsvBtn.setEnabled(false);
    contourBtn.setEnabled(false);
  }
}
