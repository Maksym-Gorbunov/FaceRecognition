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

public class Page7 extends JPanel implements Pages {

  private JFrame gui;
  private JPanel tab7;
  private JButton openBtn = new JButton("Open");
  private JButton recognizeBtn = new JButton("Recognize");
//  private File file = null;
  private ImagePanel imagePanel;
  private JList jList;

  private DefaultListModel<ImgFile> data = new DefaultListModel<>();
  private JTextField resultField = new JTextField();
  private LicensePlateRecognizer recognizer;
//  private String result;
//  private String selectedImgFile = "";

  private ImgFile selectedImgFile;


  public Page7(Gui gui) {
    this.gui = gui;
    tab7 = gui.getTab7();
    initComponents();
    addListeners();

    recognizer = new LicensePlateRecognizer();


  }

  private void addListeners() {

    openBtn.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        JFileChooser fc = new JFileChooser(Constants.imgPath);
        fc.setMultiSelectionEnabled(true);
        if (fc.showOpenDialog(gui) == JFileChooser.APPROVE_OPTION) {
          File[] files =  fc.getSelectedFiles();
          for (File file : files) {
            ImgFile imgFile = new ImgFile(file.getAbsolutePath());
            data.addElement(imgFile);
          }
        }
      }
    });

    recognizeBtn.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {

        System.out.println(data.get(jList.getSelectedIndex()));
        System.out.println(data.get(jList.getSelectedIndex()).getLicenseNumber());

//        result = recognizer.findLicensePlate(Constants.imgPath + "cars\\" + selectedImgFile, 100);
//        resultField.setText(result);
      }
    });

    jList.addListSelectionListener(new ListSelectionListener() {
      @Override
      public void valueChanged(ListSelectionEvent arg0) {
        if (!arg0.getValueIsAdjusting()) {
          selectedImgFile = data.get(jList.getSelectedIndex());
        }
      }
    });

  }

  private void initComponents() {
    JPanel mainPanel = new JPanel();
    JPanel btnsPanel = new JPanel();

    tab7.add(mainPanel);
    tab7.add(btnsPanel);

    //    mainPanel.setPreferredSize(new Dimension(Constants.VIDEO_WIDTH, Constants.VIDEO_HEIGHT));
    JPanel mainLeft = new JPanel();
    JPanel mainRight = new JPanel();


    mainRight.setPreferredSize(new Dimension(Constants.FRAME_WIDTH - Constants.VIDEO_WIDTH, Constants.VIDEO_HEIGHT));

    mainRight.setBackground(Color.blue);

    mainPanel.add(mainLeft);
    mainPanel.add(mainRight);

    imagePanel = new ImagePanel(Constants.VIDEO_WIDTH, Constants.VIDEO_HEIGHT);
    mainLeft.add(imagePanel);
    jList = new JList((ListModel) data);
    jList.setFixedCellWidth(20);
    jList.setVisibleRowCount(20);
    jList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    JScrollPane scrollPane = new JScrollPane(jList);
    jList.setFixedCellWidth(120);
    mainRight.add(scrollPane);

    JLabel resultLabel = new JLabel("RESULT");

    mainRight.add(resultLabel);

    mainRight.add(resultField);
    resultField.setText("res");
    resultField.setPreferredSize(new Dimension(Constants.FRAME_WIDTH - Constants.VIDEO_WIDTH, 20));


    btnsPanel.add(openBtn);
    btnsPanel.add(recognizeBtn);

  }

}
