package com.pages.page5;

import com.gui.Gui;
import com.pages.Pages;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class Page5 extends JPanel implements Pages {
  private JFrame gui;
  private JPanel tab5;
  private JPanel mainPanel;
  private JPanel leftPanel;
  private JPanel rightPanel;
  private JTextArea textArea;
  private JPanel buttonsPanel;
  private JButton openFileBtn;
  private JButton recognizeTextBtn;


  // Constructor
  public Page5(final Gui gui) {
    this.gui = gui;
    tab5 = gui.getTab5();
    initComponents();
    addListeners();
  }


  private void initComponents() {
    tab5.setLayout(new GridLayout(2, 1));
    mainPanel = new JPanel();
    buttonsPanel = new JPanel();
    tab5.add(mainPanel);
    tab5.add(buttonsPanel);

    mainPanel.setLayout(new GridLayout(1, 2));
    leftPanel = new JPanel();
    rightPanel = new JPanel();
    mainPanel.add(leftPanel);
    mainPanel.add(rightPanel);

    openFileBtn = new JButton("Open image");
    recognizeTextBtn = new JButton("Recognize Text");
    buttonsPanel.add(openFileBtn);
    buttonsPanel.add(recognizeTextBtn);

    leftPanel.setBackground(Color.green);

    textArea = new JTextArea("some text...");
  }


  private void addListeners() {
    openFileBtn.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        System.out.println("open");
      }
    });

    recognizeTextBtn.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        System.out.println("recognize");
      }
    });
  }

}
