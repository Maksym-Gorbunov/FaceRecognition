package com.pages.page6;

import java.awt.*;

import com.db.DB2;
import com.db.MongoDB;
import com.gui.Gui;
import com.gui.ImagePanel;
import com.pages.Pages;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import org.bytedeco.javacpp.opencv_core.*;


public class Page6 extends JPanel implements Pages {

  private static final long serialVersionUID = 1L;
  private Gui gui;
  private JPanel tab6;
  private JPanel btnPanel;
  private JPanel mainPanel;
  private ImagePanel imagePanel1;
  private ImagePanel imagePanel2;
  private JButton activeBtn;
  private JButton loadBtn;
  private JButton filterBtn;
  private JButton grayBtn;
  private JButton hsvBtn;
  private JButton contoursBtn;
  private JButton maxContourBtn;
  private JButton blueGreenBtn;
  private File file;
  private int width = 320;
  private int height = 240;
  private JFileChooser fileChooser;
  private String imgPath = "";
  private boolean filterImagesCreated;
  private int totalContours;
  private IplImage grayImg;
  private IplImage hsvImg;
  private IplImage contoursImg;
  private IplImage maxContourImg;
  private ImageFiltering filter;

  private boolean x = false;


  public Page6(final Gui gui) {
//    MongoDB db = new MongoDB();
//    db.createCollection("images");
//    DB2.uploadFile();
    DB2.loadFile();

    this.gui = gui;
    tab6 = gui.getTab6();
    initComponents();
    initButtons();
    addListeners();
    filter = new ImageFiltering();
    filterImagesCreated = false;
    totalContours = 0;
  }


  private void addListeners() {
    loadBtn.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        if (fileChooser.showOpenDialog(gui) == JFileChooser.APPROVE_OPTION) {
          filterBtn.doClick();
          file = fileChooser.getSelectedFile();
          imgPath = file.getAbsolutePath();
          if (filterImagesCreated) {
            initButtons();
            imagePanel2.clear();
          }
          imagePanel1.loadImage(file);
          createFilteringImages();
          filterBtn.setEnabled(true);
          contoursBtn.setText("contours (" + totalContours + ")");
        }
      }
    });
    filterBtn.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        if (file != null) {
          if (!filterImagesCreated) {
            filterBtn.setText("Filter OFF");
            imagePanel2.loadIplImage(grayImg);
            activeBtn = grayBtn;
            turnOnActiveButton();
            filterImagesCreated = !filterImagesCreated;
//            filterBtn.setBackground(Color.red);
          } else {
            filterBtn.setText("Filter ON");
            imagePanel2.clear();
            activeBtn = null;
            turnOffFilterButtons();
            filterImagesCreated = !filterImagesCreated;
//            filterBtn.setBackground(Color.green);
            filterBtn.setOpaque(true);
            filterBtn.setBorderPainted(false);
            blueGreenBtn.setEnabled(false);
          }
        }
      }
    });
    grayBtn.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        imagePanel2.loadIplImage(grayImg);
        activeBtn = grayBtn;
        turnOnActiveButton();
      }
    });
    hsvBtn.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        imagePanel2.loadIplImage(hsvImg);
        activeBtn = hsvBtn;
        turnOnActiveButton();
      }
    });
    contoursBtn.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        imagePanel2.loadIplImage(contoursImg);
        activeBtn = contoursBtn;
        turnOnActiveButton();
      }
    });
    maxContourBtn.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        imagePanel2.loadIplImage(maxContourImg);
        activeBtn = maxContourBtn;
        turnOnActiveButton();
      }
    });


    blueGreenBtn.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        x = !x;
        setImgScale();
        createFilteringImages();
        //repaint active tab
        if (activeBtn == contoursBtn) {
          imagePanel2.loadIplImage(contoursImg);
        }
        if(activeBtn == maxContourBtn){
          imagePanel2.loadIplImage(maxContourImg);
        }
      }
    });
  }


  public void createFilteringImages() {
    filter.setImagePath(imgPath);

    grayImg = filter.grayFilter();
    hsvImg = filter.hsvFilter();
    contoursImg = filter.contoursFilter();
    maxContourImg = filter.maxContourFilter();
    totalContours = filter.getTotalContours();
  }

  public void setImgScale() {
    //green
    //minScale = cvScalar(40, 150, 75, 0);
    //maxScale = cvScalar(80, 255, 255, 0);

    // blue default
    //minScale = cvScalar(95, 150, 75, 0);
    //maxScale = cvScalar(145, 255, 255, 0);
    if (x) {
      filter.setMinScale(40, 150, 75);
      filter.setMaxScale(80, 255, 255);
    } else {
      filter.setMinScale(95, 150, 75);
      filter.setMaxScale(145, 255, 255);
    }
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
//    filterBtn.setBackground(Color.RED);
//    filterBtn.setContentAreaFilled(false);
//    filterBtn.setOpaque(true);
//    filterBtn.setForeground(Color.white);

    grayBtn = new JButton("gray");
    hsvBtn = new JButton("hsv");
    maxContourBtn = new JButton("max contour");
    contoursBtn = new JButton("contours");
    blueGreenBtn = new JButton("blue/green");

    btnPanel.setLayout(new GridLayout(2,2));
    JPanel btnPanel1 = new JPanel();
    JPanel btnPanel2 = new JPanel();
    JPanel btnPanel3 = new JPanel();
    JPanel btnPanel4 = new JPanel();
    btnPanel.add(btnPanel1);
    btnPanel.add(btnPanel2);
    btnPanel.add(btnPanel3);
    btnPanel.add(btnPanel4);


    btnPanel1.add(loadBtn);
    btnPanel1.add(filterBtn);
    btnPanel2.add(grayBtn);
    btnPanel2.add(hsvBtn);
    btnPanel2.add(contoursBtn);
    btnPanel2.add(maxContourBtn);
    btnPanel2.add(blueGreenBtn);

    fileChooser = new JFileChooser();
  }


  public void initButtons() {
    filterBtn.setText("Filter ON");
    filterBtn.setEnabled(false);
    blueGreenBtn.setEnabled(false);
    turnOffFilterButtons();
  }

  public void turnOnFilterButtons() {
    grayBtn.setEnabled(true);
    hsvBtn.setEnabled(true);
    contoursBtn.setEnabled(true);
    maxContourBtn.setEnabled(true);
  }

  public void turnOffFilterButtons() {
    grayBtn.setEnabled(false);
    hsvBtn.setEnabled(false);
    contoursBtn.setEnabled(false);
    maxContourBtn.setEnabled(false);
  }

  public void turnOnActiveButton() {
    turnOnFilterButtons();
    if (activeBtn != null) {
      activeBtn.setEnabled(false);
    }
    if (activeBtn == contoursBtn || activeBtn == maxContourBtn) {
      blueGreenBtn.setEnabled(true);
    } else {
      blueGreenBtn.setEnabled(false);
    }
  }
}
