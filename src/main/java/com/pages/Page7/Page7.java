package com.pages.Page7;

import com.constants.Constants;
import com.gui.Gui;
import com.gui.ImagePanel;
import com.pages.Pages;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

public class Page7 extends JPanel implements Pages {

  private JFrame gui;
  private JPanel tab7;
  private ImagePanel imgOriginalPanel;
  private ImagePanel imgFilteredPanel;
  private ImagePanel imgContoursPanel;
  private ImagePanel imgLicensePlatePanel;
  private JButton openBtn = new JButton("Open");
  private JButton recognizeBtn = new JButton("Recognize");
  private JList jList;
  private JTextField resultField = new JTextField("result field");
  private DefaultListModel<ImgFile> data = new DefaultListModel<>();
  private LicensePlateRecognizer recognizer;
  private String result;
  private ImgFile selectedImgFile;
  private JSlider thrashSlider;
  private JSlider blurSlider;
  private int width = 400;
  private int height = 300;


  public Page7(Gui gui) {
    this.gui = gui;
    tab7 = gui.getTab7();
    initComponents();
    addListeners();

    recognizer = new LicensePlateRecognizer();


  }

  private void addListeners() {
    //open btn
    openBtn.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        JFileChooser fc = new JFileChooser(Constants.imgPath + "cars\\");
        fc.setMultiSelectionEnabled(true);
        if (fc.showOpenDialog(gui) == JFileChooser.APPROVE_OPTION) {
          File[] files = fc.getSelectedFiles();
          for (File file : files) {
            ImgFile imgFile = new ImgFile(file.getAbsolutePath());
            data.addElement(imgFile);
          }
        }
        if (!data.isEmpty()) {

          jList.setSelectedIndex(0);
          recognizeBtn.setEnabled(true);
        }
      }
    });

    //recognize btn
    recognizeBtn.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        recognizeBtn.setEnabled(false);
        if (selectedImgFile != null) {
          int thresh = thrashSlider.getValue();
          int blur = blurSlider.getValue();
          result = recognizer.findLicensePlate(selectedImgFile.getAbsolutePath(), thresh, blur);
          resultField.setText(result);
          selectedImgFile.setLicenseNumber(result);




          //load filtered images
//          imgFilteredPanel.loadMatImage(selectedImgFile.getThresholdImg());
//          imgContoursPanel.loadMatImage(selectedImgFile.getContoursImg());
//          imgLicensePlatePanel.loadMatImage(selectedImgFile.getLicensePlateImg());
          if (recognizer.getFilteredImages()[0] != null && !recognizer.getFilteredImages()[0].empty()) {
            selectedImgFile.setThresholdImg(recognizer.getFilteredImages()[0]);
            imgFilteredPanel.loadMatImage(recognizer.getFilteredImages()[0]);
          } else {
            imgFilteredPanel.clear();
          }
          if (recognizer.getFilteredImages()[1] != null && !recognizer.getFilteredImages()[1].empty()) {
            selectedImgFile.setContoursImg(recognizer.getFilteredImages()[1]);
            imgContoursPanel.loadMatImage(recognizer.getFilteredImages()[1]);
          } else {
            imgContoursPanel.clear();
          }

          if (recognizer.getFilteredImages()[2] != null && !recognizer.getFilteredImages()[2].empty()) {
            selectedImgFile.setLicensePlateImg(recognizer.getFilteredImages()[2]);
            imgLicensePlatePanel.loadMatImage(recognizer.getFilteredImages()[2]);
          } else {
            imgLicensePlatePanel.clear();
          }
        }
      }
    });

    //select list item
    jList.addListSelectionListener(new ListSelectionListener() {
      @Override
      public void valueChanged(ListSelectionEvent arg0) {
        if (!arg0.getValueIsAdjusting()) {
          recognizeBtn.setEnabled(true);
          selectedImgFile = data.get(jList.getSelectedIndex());
          imgOriginalPanel.loadImage(selectedImgFile);
          if (selectedImgFile.getThresholdImg() != null) {
            imgFilteredPanel.loadMatImage(selectedImgFile.getThresholdImg());
          } else {
            imgFilteredPanel.clear();
          }
          if (selectedImgFile.getContoursImg() != null) {
            imgContoursPanel.loadMatImage(selectedImgFile.getContoursImg());
          } else {
            imgContoursPanel.clear();
          }
          if (selectedImgFile.getLicensePlateImg() != null) {
            imgLicensePlatePanel.loadMatImage(selectedImgFile.getLicensePlateImg());
          } else {
            imgLicensePlatePanel.clear();
          }

          resultField.setText(selectedImgFile.getLicenseNumber());
        }
      }
    });

    thrashSlider.addChangeListener(new ChangeListener() {
      @Override
      public void stateChanged(ChangeEvent e) {
        recognizeBtn.setEnabled(true);
      }
    });

    blurSlider.addChangeListener(new ChangeListener() {
      @Override
      public void stateChanged(ChangeEvent e) {
        recognizeBtn.setEnabled(true);
      }
    });

  }


  private void initComponents() {
    JPanel top = new JPanel();
    JPanel bottom = new JPanel();
    JPanel topLeft = new JPanel();
    JPanel topRight = new JPanel();
    JPanel bottomLeft = new JPanel();
    JPanel bottomRight = new JPanel();

//    tab7.setLayout(new GridLayout(2,2));

    top.add(topLeft);
    top.add(topRight);
    bottom.add(bottomLeft);
    bottom.add(bottomRight);
    tab7.add(top);
    tab7.add(bottom);

    topLeft.setPreferredSize(new Dimension((int) (Constants.FRAME_WIDTH * 0.7), height + 40));
    topRight.setPreferredSize(new Dimension((int) (Constants.FRAME_WIDTH * 0.3), height + 40));

    bottomLeft.setPreferredSize(new Dimension((int) (Constants.FRAME_WIDTH * 0.7), 200));
    bottomRight.setPreferredSize(new Dimension((int) (Constants.FRAME_WIDTH * 0.3), 200));

    JTabbedPane imgTabPane = new JTabbedPane();
    imgOriginalPanel = new ImagePanel(width, height);
    imgFilteredPanel = new ImagePanel(width, height);
    imgContoursPanel = new ImagePanel(width, height);
    imgLicensePlatePanel = new ImagePanel(width, height);
    imgTabPane.add("Original", imgOriginalPanel);
    imgTabPane.add("Filtered", imgFilteredPanel);
    imgTabPane.add("Contours", imgContoursPanel);
    imgTabPane.add("LPlate", imgLicensePlatePanel);

//    topLeft.setBackground(Color.yellow);

    topLeft.add(imgTabPane);


    topRight.setLayout(new BoxLayout(topRight, BoxLayout.Y_AXIS));
    topRight.setBorder(new EmptyBorder(10, 10, 10, 10));
    bottomLeft.setBorder(new EmptyBorder(0, 10, 0, 0));


    jList = new JList((ListModel) data);
    jList.setFixedCellWidth(20);
    jList.setVisibleRowCount(15);
    jList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    JScrollPane scrollPane = new JScrollPane(jList);
    jList.setFixedCellWidth(120);
    topRight.add(scrollPane);
    JLabel resultLabel = new JLabel("RESULT: ");
    topRight.add(resultLabel);
    topRight.add(resultField);
    JPanel btns = new JPanel();
    btns.add(openBtn);
    btns.add(recognizeBtn);
    topRight.add(btns);

//    bottomRight.add(openBtn);
//    bottomRight.add(recognizeBtn);

    bottomLeft.setLayout(new GridLayout(3, 2));

    thrashSlider = new JSlider(JSlider.HORIZONTAL, 0, 250, 80);
    thrashSlider.setMinorTickSpacing(10);
    thrashSlider.setMajorTickSpacing(50);
    thrashSlider.setPaintTicks(true);
    thrashSlider.setPaintLabels(true);
    bottomLeft.add(thrashSlider);
    bottomLeft.add(new JLabel("Thresh"));

    blurSlider = new JSlider(JSlider.HORIZONTAL, 0, 15, 3);
    blurSlider.setMinorTickSpacing(1);
    blurSlider.setMajorTickSpacing(5);
    blurSlider.setPaintTicks(true);
    blurSlider.setPaintLabels(true);
    bottomLeft.add(blurSlider);
    bottomLeft.add(new JLabel("Blur"));


    recognizeBtn.setEnabled(false);
  }


}
