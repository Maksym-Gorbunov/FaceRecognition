package com.pages.Page7;

import com.constants.Constants;
import com.gui.Gui;
import com.gui.ImagePanel;
import com.pages.Pages;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.awt.*;

public class Page7A extends JPanel implements Pages {

  private JFrame gui;
  private JPanel tab7;
  private JButton openBtn = new JButton("Open");
  private JButton recognizeBtn = new JButton("Recognize");
  private ImagePanel imagePanel;
  private ImagePanel imagePanel2;
  private JList jList;
  private JTextField resultField = new JTextField();

  private DefaultListModel<ImgFile> data = new DefaultListModel<>();
  private LicensePlateRecognizer recognizer;
  private String result;
  private ImgFile selectedImgFile;
  private int selectedIndex;
  private JSlider thrashSlider;

  private int width = 300;
  private int height = 220;


  public Page7A(Gui gui) {
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
        JFileChooser fc = new JFileChooser(Constants.imgPath);
        fc.setMultiSelectionEnabled(true);
        if (fc.showOpenDialog(gui) == JFileChooser.APPROVE_OPTION) {
          File[] files = fc.getSelectedFiles();
          for (File file : files) {
            ImgFile imgFile = new ImgFile(file.getAbsolutePath());
            data.addElement(imgFile);
          }
        }
        recognizeBtn.setEnabled(true);
      }
    });

    //recognize btn
    recognizeBtn.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        recognizeBtn.setEnabled(false);
        if (selectedImgFile != null) {
          int thresh = thrashSlider.getValue();
          result = recognizer.findLicensePlate(selectedImgFile.getAbsolutePath(), thresh);
          resultField.setText(result);
          selectedImgFile.setLicenseNumber(result);
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
          imagePanel.loadImage(selectedImgFile);
          resultField.setText(selectedImgFile.getLicenseNumber());
        }
      }
    });

  }

  private void initComponents() {
    JPanel mainPanel = new JPanel();
    JPanel btnsPanel = new JPanel();
    tab7.add(mainPanel);
    tab7.add(btnsPanel);
    JPanel mainLeft = new JPanel();
    JPanel mainRight = new JPanel();
    mainRight.setPreferredSize(new Dimension(Constants.FRAME_WIDTH - Constants.VIDEO_WIDTH, Constants.VIDEO_HEIGHT));
    mainRight.setBackground(Color.blue);
    mainPanel.add(mainLeft);
    mainPanel.add(mainRight);
    imagePanel = new ImagePanel(width, height);
    imagePanel2 = new ImagePanel(width, height);
    mainLeft.add(imagePanel);
    mainLeft.add(imagePanel2);
    jList = new JList((ListModel) data);
    jList.setFixedCellWidth(20);
    jList.setVisibleRowCount(15);
    jList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    JScrollPane scrollPane = new JScrollPane(jList);
    jList.setFixedCellWidth(120);
    mainRight.add(scrollPane);
    JLabel resultLabel = new JLabel("RESULT: ");
    mainRight.add(resultLabel);
    mainRight.add(resultField);
    resultField.setPreferredSize(new Dimension(Constants.FRAME_WIDTH - Constants.VIDEO_WIDTH, 20));
    btnsPanel.add(openBtn);
    btnsPanel.add(recognizeBtn);
    recognizeBtn.setEnabled(false);


    thrashSlider = new JSlider(JSlider.HORIZONTAL, 0, 250, 80);
    thrashSlider.setMinorTickSpacing(10);
    thrashSlider.setMajorTickSpacing(50);
    thrashSlider.setPaintTicks(true);
    thrashSlider.setPaintLabels(true);
//    mainLeftBottom.add(thrashSlider);
    //toDo add grid on mailLeft
    //toDo add grid on mailLeft
    //toDo add grid on mailLeft
    //toDo add grid on mailLeft
    //toDo add grid on mailLeft
    //toDo add grid on mailLeft
    //toDo add grid on mailLeft
    //toDo add grid on mailLeft
    // change design op page 7, need more
  }

}
