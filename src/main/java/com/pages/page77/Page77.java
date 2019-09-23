package com.pages.page77;

import com.constants.Constants;
import com.gui.Gui;
import com.gui.ImagePanel;
import com.pages.Pages;
import org.apache.commons.io.FileUtils;
import org.opencv.core.Mat;

import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

public class Page77 extends JPanel implements Pages {

  private JFrame gui;
  private JPanel tab77;
  private JTabbedPane imgTabPane;
  private ImagePanel originalPanel;
  private ImagePanel filteredPanel;
  private ImagePanel contoursPanel;
  private ImagePanel platePanelOriginal;
  private ImagePanel platePanelRotated;
  private ImagePanel platePanelRotatedCutted;
  private ImagePanel platePanelFiltered;
  private ImagePanel platePanelSheared;
  private JButton openBtn = new JButton("Open");
  private JButton recognizeBtn = new JButton("Recognize");
  private JList jList;
  private JTextField licenseNumberTextField = new JTextField("");
  private DefaultListModel<Screenshot> data = new DefaultListModel<>();
  private R recognizer;
  private String result;
  private Screenshot selectedObject;
  private JSlider thrashSlider;
  private JSlider blurSlider;
  private JSlider shearAngleSlider;
  private JSlider thrashPlateSlider;
  private int width = 400;
  private int height = 300;
  private JComboBox<Integer> contoursComboBox = new JComboBox<>();


  // Constructor
  public Page77(Gui gui) {
    this.gui = gui;
    tab77 = gui.getTab77();
    initComponents();
    addListeners();
    clearFolder(Constants.imgPath + "lpr\\");
    recognizer = new R();
  }

  // Add listeners to UI components
  private void addListeners() {
    //open btn
    openBtn.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        JFileChooser fc = new JFileChooser(Constants.imgPath + "cars\\regnums\\");
        fc.setMultiSelectionEnabled(true);
        if (fc.showOpenDialog(gui) == JFileChooser.APPROVE_OPTION) {
          File[] files = fc.getSelectedFiles();
          for (File file : files) {
            Screenshot screenshot = new Screenshot(file);
            data.addElement(screenshot);
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
        if (selectedObject != null) {
          int thrash = thrashSlider.getValue();
          Screenshot result = recognizer.recognize(selectedObject.getFile(), thrash);
          if (result != null) {
            selectedObject.setFilteredImg(copy(result.getFilteredImg()));
            selectedObject.setFilteredImg(copy(result.getFilteredImg()));
            selectedObject.setOriginalContoursImg(copy(result.getOriginalContoursImg()));
            selectedObject.setContours(new ArrayList<Contour>(result.getContours()));
            if (result.getContours().size() > 0) {
              for (int i = 0; i < result.getContours().size(); i++) {
                Contour c = selectedObject.getContours().get(i);

                //show text result
                licenseNumberTextField.setText(selectedObject.getContours().get(0).getText());

                c.setPlateOriginal(result.getContours().get(i).getPlateOriginal());
                c.setPlateGray(result.getContours().get(i).getPlateGray());
                c.setPlateRotated(result.getContours().get(i).getPlateRotated());
                c.setPlateRotatedCutted(result.getContours().get(i).getPlateRotatedCutted());
                c.setPlateFiltered(result.getContours().get(i).getPlateFiltered());
                c.setPlateSheared(result.getContours().get(i).getPlateSheared());
              }
              updateContoursComboBox(selectedObject.getContours().size());
              imgTabPane.setSelectedIndex(2);
            }
            updateImages();
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
          selectedObject = data.get(jList.getSelectedIndex());
          updateImages();
          updateContoursComboBox(selectedObject.getContours().size());
          imgTabPane.setSelectedIndex(0);
        }
      }
    });
    //contours chooser
    contoursComboBox.addItemListener(new ItemListener() {
      @Override
      public void itemStateChanged(ItemEvent e) {
        updatePlateImages();
        if (contoursComboBox.getItemCount() > 0) {
          licenseNumberTextField.setText(selectedObject.getContours().get(contoursComboBox.getSelectedIndex()).getText());
        } else {
          licenseNumberTextField.setText("");
        }
      }
    });
    //thrash slider, live changes for temp filtered image
    thrashSlider.addChangeListener(new ChangeListener() {
      @Override
      public void stateChanged(ChangeEvent e) {
        recognizeBtn.setEnabled(true);
        if (selectedObject.getFilteredImg() != null) {
          imgTabPane.setSelectedIndex(1);
          Mat tempThreshImg = recognizer.filterColoredImage(selectedObject.getOriginalImg(), thrashSlider.getValue(), blurSlider.getValue());
          filteredPanel.loadImage(tempThreshImg);
        }
      }
    });

//    blurSlider.addChangeListener(new ChangeListener() {
//      @Override
//      public void stateChanged(ChangeEvent e) {
//        recognizeBtn.setEnabled(true);
//      }
//    });
//    shearAngleSlider.addChangeListener(new ChangeListener() {
//      @Override
//      public void stateChanged(ChangeEvent e) {
//        recognizeBtn.setEnabled(true);
//        if (selectedObject.getShearedPlate() != null) {
//          imgTabPane.setSelectedIndex(4);
//          Mat tempShearedPlateImg = recognizer.shearImageFromSlider(selectedObject.getShearedPlate(), shearAngleSlider.getValue());
//          platePanelSheared.loadImage(tempShearedPlateImg);
//        }
//      }
//    });
//    thrashPlateSlider.addChangeListener(new ChangeListener() {
//      @Override
//      public void stateChanged(ChangeEvent e) {
//        recognizeBtn.setEnabled(true);
//        if (selectedObject.getFilteredPlate() != null) {
//          imgTabPane.setSelectedIndex(5);
//          Mat tempThreshPlateImg = recognizer.filterPlate(selectedObject.getFilteredPlate(), thrashPlateSlider.getValue());
//          platePanelFiltered.loadImage(tempThreshPlateImg);
//        }
//      }
//    });

  }

  private void updateContoursComboBox(int total) {
    contoursComboBox.removeAllItems();
    for (int i = 1; i <= total; i++) {
      contoursComboBox.addItem(i);
    }
    if (total > 0) {
      contoursComboBox.setSelectedIndex(0);
    }
  }

  // Update UI
  private void updateImages() {
    if (!selectedObject.getOriginalImg().empty()) {
      originalPanel.loadImage(selectedObject.getOriginalImg());
    } else {
      originalPanel.clear();
    }
    if (!selectedObject.getFilteredImg().empty()) {
      filteredPanel.loadImage(selectedObject.getFilteredImg());
    } else {
      filteredPanel.clear();
    }
    if (!selectedObject.getOriginalContoursImg().empty()) {
      contoursPanel.loadImage(selectedObject.getOriginalContoursImg());
    } else {
      contoursPanel.clear();
    }
    updatePlateImages();
  }

  private void updatePlateImages() {
    if (selectedObject.getContours().size() > 0) {
      if (contoursComboBox.getSelectedItem() != null) {
        int index = (int) contoursComboBox.getSelectedItem() - 1;
        if ((selectedObject.getContours().get(index).getPlateOriginal() != null)
                && (!selectedObject.getContours().get(index).getPlateOriginal().empty())) {
          platePanelOriginal.loadImage(selectedObject.getContours().get(index).getPlateOriginal());
        }
        if ((selectedObject.getContours().get(index).getPlateRotated() != null)
                && (!selectedObject.getContours().get(index).getPlateRotated().empty())) {
          platePanelRotated.loadImage(selectedObject.getContours().get(index).getPlateRotated());
        }
        if ((selectedObject.getContours().get(index).getPlateRotatedCutted() != null)
                && (!selectedObject.getContours().get(index).getPlateRotatedCutted().empty())) {
          platePanelRotatedCutted.loadImage(selectedObject.getContours().get(index).getPlateRotatedCutted());
        }
        if ((selectedObject.getContours().get(index).getPlateFiltered() != null)
                && (!selectedObject.getContours().get(index).getPlateFiltered().empty())) {
          platePanelFiltered.loadImage(selectedObject.getContours().get(index).getPlateFiltered());
        }
        if ((selectedObject.getContours().get(index).getPlateSheared() != null)
                && (!selectedObject.getContours().get(index).getPlateSheared().empty())) {
          platePanelSheared.loadImage(selectedObject.getContours().get(index).getPlateSheared());
        }
      }
    } else {
      platePanelOriginal.clear();
      platePanelRotated.clear();
      platePanelRotatedCutted.clear();
      platePanelFiltered.clear();
      platePanelSheared.clear();
    }
  }


  // Initialize UI components
  private void initComponents() {
    JPanel top = new JPanel();
    JPanel bottom = new JPanel();
    JPanel topLeft = new JPanel();
    JPanel topRight = new JPanel();
    JPanel bottomLeft = new JPanel();
    JPanel bottomRight = new JPanel();
    top.add(topLeft);
    top.add(topRight);
    bottom.add(bottomLeft);
    bottom.add(bottomRight);
    tab77.add(top);
    tab77.add(bottom);
    topLeft.setPreferredSize(new Dimension((int) (Constants.FRAME_WIDTH * 0.7), height + 40));
    topRight.setPreferredSize(new Dimension((int) (Constants.FRAME_WIDTH * 0.3), height + 40));
    bottomLeft.setPreferredSize(new Dimension((int) (Constants.FRAME_WIDTH * 0.7), 200));
    bottomRight.setPreferredSize(new Dimension((int) (Constants.FRAME_WIDTH * 0.3), 200));
    imgTabPane = new JTabbedPane();
    originalPanel = new ImagePanel(width, height);
    filteredPanel = new ImagePanel(width, height);
    contoursPanel = new ImagePanel(width, height);
    platePanelOriginal = new ImagePanel(width, height);
    platePanelRotated = new ImagePanel(width, height);
    platePanelRotatedCutted = new ImagePanel(width, height);
    platePanelFiltered = new ImagePanel(width, height);
    platePanelSheared = new ImagePanel(width, height);
    imgTabPane.add("Original", originalPanel);
    imgTabPane.add("Filtered", filteredPanel);
    imgTabPane.add("Contours", contoursPanel);
    imgTabPane.add("P-Original", platePanelOriginal);
    imgTabPane.add("P-Rotated", platePanelRotated);
    imgTabPane.add("P-Cutted", platePanelRotatedCutted);
    imgTabPane.add("P-Sheared", platePanelSheared);
    imgTabPane.add("P-Filtered", platePanelFiltered);
    topLeft.add(imgTabPane);

    topLeft.add(contoursComboBox);

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
    topRight.add(licenseNumberTextField);
    JPanel btns = new JPanel();
    btns.add(openBtn);
    btns.add(recognizeBtn);
    topRight.add(btns);
    bottomLeft.setLayout(new GridLayout(3, 2));

    thrashSlider = new JSlider(JSlider.HORIZONTAL, 0, 250, 80);
    thrashSlider.setMinorTickSpacing(10);
    thrashSlider.setMajorTickSpacing(50);
    thrashSlider.setPaintTicks(true);
    thrashSlider.setPaintLabels(true);
    bottomLeft.add(thrashSlider);

    bottomLeft.add(new JLabel("Main Thresh"));
    blurSlider = new JSlider(JSlider.HORIZONTAL, 0, 10, 5);
    blurSlider.setMinorTickSpacing(1);
    blurSlider.setMajorTickSpacing(5);
    blurSlider.setPaintTicks(true);
    blurSlider.setPaintLabels(true);
    bottomLeft.add(blurSlider);
    bottomLeft.add(new JLabel("Main Blur"));

    bottomRight.add(new JLabel("Transformation - Shear angle"));
    shearAngleSlider = new JSlider(JSlider.HORIZONTAL, -10, 10, -6);
    shearAngleSlider.setMinorTickSpacing(1);
    shearAngleSlider.setMajorTickSpacing(5);
    shearAngleSlider.setPaintTicks(true);
    shearAngleSlider.setPaintLabels(true);
    bottomRight.add(shearAngleSlider);

    bottomRight.add(new JLabel("Plate Thrash"));
    thrashPlateSlider = new JSlider(JSlider.HORIZONTAL, 0, 250, 100);
    thrashPlateSlider.setMinorTickSpacing(10);
    thrashPlateSlider.setMajorTickSpacing(50);
    thrashPlateSlider.setPaintTicks(true);
    thrashPlateSlider.setPaintLabels(true);
    bottomRight.add(thrashPlateSlider);


    recognizeBtn.setEnabled(false);
  }

  // Clear folder from old files
  public void clearFolder(String path) {
    try {
      FileUtils.deleteDirectory(new File(path));
    } catch (IOException e) {
      e.printStackTrace();
    }
    new File(path).mkdirs();
  }

  //Copy Mat, return new one
  private Mat copy(Mat original) {
    if (original == null) {
      return null;
    }
    Mat copy = new Mat();
    original.copyTo(copy);
    return copy;
  }
}
