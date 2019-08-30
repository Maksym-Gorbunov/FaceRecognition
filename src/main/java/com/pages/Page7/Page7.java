package com.pages.Page7;

import com.constants.Constants;
import com.gui.Gui;
import com.gui.ImagePanel;
import com.pages.Pages;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.awt.*;

public class Page7 extends JPanel implements Pages {

  private JFrame gui;
  private JPanel tab7;
  private JButton openBtn = new JButton("Open");
  private JButton recognizeBtn = new JButton("Recognize");
//  private JFileChooser fileChooser = new JFileChooser();
//  private List<File> files = new ArrayList<>();
//  private File[] tempFiles;
  private File file = null;
  private ImagePanel imagePanel;
  private JList jList;
  private DefaultListModel<String> data = new DefaultListModel<>();
  private JTextField resultField = new JTextField();

  public Page7(Gui gui){
    this.gui = gui;
    tab7 = gui.getTab7();
    initComponents();
    addListeners();




  }

  private void addListeners() {

    openBtn.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        JFileChooser fc = new JFileChooser(Constants.imgPath);
        fc.setMultiSelectionEnabled(true);
        if (fc.showOpenDialog(gui) == JFileChooser.APPROVE_OPTION) {
          File[] files = fc.getSelectedFiles();
          for (File file : files){
            data.addElement(file.getName());
          }
        }
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
    jList.setVisibleRowCount(5);
    jList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    JScrollPane scrollPane = new JScrollPane(jList);
    jList.setFixedCellWidth(120);
    mainRight.add(scrollPane);

    JLabel resultLabel = new JLabel("RESULT");

    mainRight.add(resultLabel);

    mainRight.add(resultField);
    resultField.setText("res");
    resultField.setPreferredSize(new Dimension(Constants.FRAME_WIDTH - Constants.VIDEO_WIDTH,20));







    btnsPanel.add(openBtn);
    btnsPanel.add(recognizeBtn);

  }

}
