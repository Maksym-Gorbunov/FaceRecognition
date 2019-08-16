package com.pages.page6;

import java.awt.*;

import com.constants.Constants;
import com.gui.Gui;
import com.gui.ImagePanel;
import com.pages.Pages;
import org.bytedeco.javacpp.opencv_core;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import org.bytedeco.javacpp.Loader;
import org.bytedeco.javacpp.opencv_core.*;
import static org.bytedeco.javacpp.opencv_core.*;
import static org.bytedeco.javacpp.opencv_highgui.*;
import static org.bytedeco.javacpp.opencv_imgproc.*;


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
  private JButton contoursBtn;
  private JButton maxContourBtn;
  private JButton testBtn;
  private File file;
  private int width = 320;
  private int height = 240;
  private JFileChooser fileChooser;
  private String imgOriginalPath = "";
  private String grayImgPath = Constants.imgPath + "grayImg.jpg";
  private String hsvImgPath = Constants.imgPath + "hsvImg.jpg";
  private String maxContourPath = Constants.imgPath + "maxContour.jpg";
  private String contoursPath = Constants.imgPath + "contours.jpg";
  private boolean filter;
  private int totalContours;


  public Page6(final Gui gui) {
    this.gui = gui;
    tab6 = gui.getTab6();
    initComponents();
    initButtons();
    addListeners();
    filter = false;
    totalContours = 0;
  }

  private void addListeners() {

    loadBtn.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        if (fileChooser.showOpenDialog(gui) == JFileChooser.APPROVE_OPTION) {
          filterBtn.doClick();
          file = fileChooser.getSelectedFile();
          imgOriginalPath = file.getAbsolutePath();
          if(filter){
            initButtons();
            imagePanel2.clear();
//            filterBtn.doClick();
          }
          imagePanel1.loadImage(file);
          createFilters();
          filterBtn.setEnabled(true);
          contoursBtn.setText("contours ("+totalContours+")");
        }
      }
    });

    filterBtn.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        if(file != null) {
          if(!filter){
            filterBtn.setText("Filter OFF");
            imagePanel2.loadIplImage(grayImgPath);
            grayBtn.setEnabled(false);
            hsvBtn.setEnabled(true);
            maxContourBtn.setEnabled(true);
            contoursBtn.setEnabled(true);
            filter = !filter;
          } else {
            filterBtn.setText("Filter ON");
            imagePanel2.clear();
            grayBtn.setEnabled(false);
            hsvBtn.setEnabled(false);
            maxContourBtn.setEnabled(false);
            contoursBtn.setEnabled(false);
            filter = !filter;
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
        maxContourBtn.setEnabled(true);
        contoursBtn.setEnabled(true);
      }
    });

    hsvBtn.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        imagePanel2.loadIplImage(hsvImgPath);
        hsvBtn.setEnabled(false);
        grayBtn.setEnabled(true);
        maxContourBtn.setEnabled(true);
        contoursBtn.setEnabled(true);
      }
    });

    contoursBtn.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        System.out.println("all");
        contoursBtn.setEnabled(false);
        maxContourBtn.setEnabled(true);
        hsvBtn.setEnabled(true);
        grayBtn.setEnabled(true);
        imagePanel2.loadIplImage(contoursPath);
      }
    });

    maxContourBtn.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        System.out.println("xxx");
        maxContourBtn.setEnabled(false);
        hsvBtn.setEnabled(true);
        grayBtn.setEnabled(true);
        contoursBtn.setEnabled(true);
        imagePanel2.loadIplImage(maxContourPath);
      }
    });

    testBtn.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        ImageFiltering f = new ImageFiltering(imgOriginalPath);
        IplImage filteredImage = f.test();
        imagePanel2.add(filteredImage);
      }
    });
  }


  public void createFilters(){
    ImageFiltering filter = new ImageFiltering(imgOriginalPath);
    filter.grayFilter(grayImgPath);
    filter.hsvFilter(hsvImgPath);
    totalContours = filter.maxContourFilter(maxContourPath);
    filter.contoursFilter(contoursPath);
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
    maxContourBtn = new JButton("max contour");
    contoursBtn = new JButton("contours");
    testBtn = new JButton("test");
    btnPanel.add(loadBtn);
    btnPanel.add(filterBtn);
    btnPanel.add(grayBtn);
    btnPanel.add(hsvBtn);
    btnPanel.add(contoursBtn);
    btnPanel.add(maxContourBtn);
    btnPanel.add(testBtn);

    fileChooser = new JFileChooser();
  }

  public void initButtons() {
    filterBtn.setText("Filter ON");
    filterBtn.setEnabled(false);
    grayBtn.setEnabled(false);
    hsvBtn.setEnabled(false);
    maxContourBtn.setEnabled(false);
    maxContourBtn.setEnabled(false);
    contoursBtn.setEnabled(false);
  }
}
