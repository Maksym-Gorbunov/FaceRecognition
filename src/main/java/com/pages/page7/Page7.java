package com.pages.page7;

import com.constants.Constants;
import com.gui.Gui;
import com.gui.ImagePanel;
import com.pages.Pages;
import org.apache.commons.io.FileUtils;
import org.opencv.core.Mat;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

public class Page7 extends JPanel implements Pages {

  private JFrame gui;
  private JPanel tab7;
  private JTabbedPane imgTabPane;
  private ImagePanel originalPanel;
  private ImagePanel filteredPanel;
  private ImagePanel contoursPanel;
  private ImagePanel platePanel;
  private ImagePanel filteredPlatePanel;
  private ImagePanel shearedPlatePanel;
  private JButton openBtn = new JButton("Open");
  private JButton recognizeBtn = new JButton("Recognize");
  private JList jList;
  private JTextField licenseNumberTextField = new JTextField("");
  private DefaultListModel<ImgObject> data = new DefaultListModel<>();
  private Recognizer recognizer;
  private String result;
  private ImgObject selectedObject;
  private JSlider thrashSlider;
  private JSlider blurSlider;
  private JSlider shearAngleSlider;
  private JSlider thrashPlateSlider;
  private int width = 400;
  private int height = 300;

  // Constructor
  public Page7(Gui gui) {
    this.gui = gui;
    tab7 = gui.getTab7();
    initComponents();
    addListeners();
    clearFolder(Constants.imgPath + "lpr\\");
    recognizer = new Recognizer();
  }

  // Add listeners to UI components
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
            ImgObject imgFile = new ImgObject(file);
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
        if (selectedObject != null) {
          int thrash = thrashSlider.getValue();
          int blur = blurSlider.getValue();
          int plateThrash = thrashPlateSlider.getValue();
          double shearAngle = shearAngleSlider.getValue() * 0.1;
          ImgObject result = recognizer.recognize(selectedObject.getFile(), thrash, blur, plateThrash, shearAngle);
          if (result.getFiltered() != null) {
            selectedObject.setFiltered(copy(result.getFiltered()));
          }
          if (result.getContours() != null) {
            selectedObject.setContours(copy(result.getContours()));
          }
          if (result.getPlate() != null) {
            selectedObject.setPlate(copy(result.getPlate()));
          }
          if (result.getFilteredPlate() != null) {
            selectedObject.setFilteredPlate(copy(result.getFilteredPlate()));
          }
          if (result.getShearedPlate() != null) {
            selectedObject.setShearedPlate(copy(result.getShearedPlate()));
          }
          selectedObject.setLicenseNumber(result.getLicenseNumber());
          updateImages();
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
          imgTabPane.setSelectedIndex(0);
        }
      }
    });

    thrashSlider.addChangeListener(new ChangeListener() {
      @Override
      public void stateChanged(ChangeEvent e) {
        recognizeBtn.setEnabled(true);
        if (selectedObject.getFiltered() != null) {
          imgTabPane.setSelectedIndex(1);
          Mat tempThreshImg = recognizer.filterImage(selectedObject.getOriginal(), thrashSlider.getValue(), blurSlider.getValue());
          filteredPanel.loadMatImage(tempThreshImg);
        }
      }
    });

    blurSlider.addChangeListener(new ChangeListener() {
      @Override
      public void stateChanged(ChangeEvent e) {
        recognizeBtn.setEnabled(true);
      }
    });
    shearAngleSlider.addChangeListener(new ChangeListener() {
      @Override
      public void stateChanged(ChangeEvent e) {
        recognizeBtn.setEnabled(true);
        if (selectedObject.getShearedPlate() != null) {
          imgTabPane.setSelectedIndex(4);
          Mat tempShearedPlateImg = recognizer.shearImageFromSlider(selectedObject.getShearedPlate(), shearAngleSlider.getValue());
          shearedPlatePanel.loadMatImage(tempShearedPlateImg);
        }
      }
    });
    thrashPlateSlider.addChangeListener(new ChangeListener() {
      @Override
      public void stateChanged(ChangeEvent e) {
        recognizeBtn.setEnabled(true);
        if (selectedObject.getFilteredPlate() != null) {
          imgTabPane.setSelectedIndex(5);
          Mat tempThreshPlateImg = recognizer.filterPlate(selectedObject.getFilteredPlate(), thrashPlateSlider.getValue());
          filteredPlatePanel.loadMatImage(tempThreshPlateImg);
        }
      }
    });

  }

  // Update UI
  private void updateImages() {
    if (selectedObject.getOriginal() != null) {
      originalPanel.loadMatImage(selectedObject.getOriginal());
    } else {
      originalPanel.clear();
    }
    if (selectedObject.getFiltered() != null) {
      filteredPanel.loadMatImage(selectedObject.getFiltered());
    } else {
      filteredPanel.clear();
    }
    if (selectedObject.getContours() != null) {
      contoursPanel.loadMatImage(selectedObject.getContours());
    } else {
      contoursPanel.clear();
    }
    if (selectedObject.getPlate() != null) {
      platePanel.loadMatImage(selectedObject.getPlate());
    } else {
      platePanel.clear();
    }
    if (selectedObject.getFilteredPlate() != null) {
      filteredPlatePanel.loadMatImage(selectedObject.getFilteredPlate());
    } else {
      filteredPlatePanel.clear();
    }
    if (selectedObject.getShearedPlate() != null) {
      shearedPlatePanel.loadMatImage(selectedObject.getShearedPlate());
    } else {
      shearedPlatePanel.clear();
    }
    licenseNumberTextField.setText(selectedObject.getLicenseNumber());
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
    tab7.add(top);
    tab7.add(bottom);
    topLeft.setPreferredSize(new Dimension((int) (Constants.FRAME_WIDTH * 0.7), height + 40));
    topRight.setPreferredSize(new Dimension((int) (Constants.FRAME_WIDTH * 0.3), height + 40));
    bottomLeft.setPreferredSize(new Dimension((int) (Constants.FRAME_WIDTH * 0.7), 200));
    bottomRight.setPreferredSize(new Dimension((int) (Constants.FRAME_WIDTH * 0.3), 200));
    imgTabPane = new JTabbedPane();
    originalPanel = new ImagePanel(width, height);
    filteredPanel = new ImagePanel(width, height);
    contoursPanel = new ImagePanel(width, height);
    platePanel = new ImagePanel(width, height);
    filteredPlatePanel = new ImagePanel(width, height);
    shearedPlatePanel = new ImagePanel(width, height);
    imgTabPane.add("Original", originalPanel);
    imgTabPane.add("Filtered", filteredPanel);
    imgTabPane.add("Contours", contoursPanel);
    imgTabPane.add("Plate", platePanel);
    imgTabPane.add("Plate Sheared", shearedPlatePanel);
    imgTabPane.add("Plate Filtered", filteredPlatePanel);
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
