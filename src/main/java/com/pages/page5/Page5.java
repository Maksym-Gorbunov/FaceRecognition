package com.pages.page5;

import com.gui.Gui;
import com.intellij.uiDesigner.core.GridConstraints;
import com.pages.Pages;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import static org.opencv.imgcodecs.Imgcodecs.imread;
import static org.opencv.imgcodecs.Imgcodecs.imwrite;
import static org.opencv.imgproc.Imgproc.COLOR_BGR2GRAY;
import static org.opencv.imgproc.Imgproc.cvtColor;
import com.constants.Constants;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import net.sourceforge.tess4j.Tesseract;
import net.sourceforge.tess4j.TesseractException;
import org.apache.commons.io.FilenameUtils;

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
  private JFileChooser fileChooser;
  private File file;
  private String fileName;
  private ImagePanel imagePanel;

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
    rightPanel.setBackground(Color.blue);

    textArea = new JTextArea("some text...");
//    textArea = new JTextArea("some text...",10,30);
    textArea.setBackground(Color.red);
    textArea.setPreferredSize(new Dimension(320,240));
    rightPanel.add(textArea);

    fileChooser = new JFileChooser();

    imagePanel = new ImagePanel(320,240);
    leftPanel.add(imagePanel, new GridConstraints());
  }


  private void addListeners() {
    openFileBtn.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        System.out.println("open");
        if(fileChooser.showOpenDialog(gui) == JFileChooser.APPROVE_OPTION){
          file = fileChooser.getSelectedFile();
          // load the image
          System.out.println("Image url: " + file);
          fileName = file.getName();
          System.out.println(file.getName());
          imagePanel.loadImage(file);
        }
      }
    });

    recognizeTextBtn.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        System.out.println("recognize");


        System.out.println("Start recognize text from image");
        long start = System.currentTimeMillis();

        // Read image
        Mat origin = imread(file.toString());
//        Mat origin = imread(IMG_PATH + fileName);

        String result = extractTextFromImage(origin);
//        String result = new TextRecognizer().extractTextFromImage(origin);
        System.out.println(result);

        textArea.setText(result);

        System.out.println("Time");
        System.out.println(System.currentTimeMillis() - start);
        System.out.println("Done");

        System.out.println("***************** "+FilenameUtils.removeExtension(fileName));
      }
    });
  }



  ////////////////////////// RECOGNITION //////////////////////////
  // Source path content images
  static String IMG_PATH = Constants.projectPath+"\\img\\";
  static String TESS_DATA = Constants.projectPath+"\\lib\\tesseract-OCR\\";    // path to Tesseract-OCR eng.trainedata

  // Create tess obj
  static Tesseract tesseract = new Tesseract();

  // Load OPENCV
  static {
    System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
    tesseract.setDatapath(TESS_DATA);
  }

  String extractTextFromImage(Mat inputMat) {
    String result = "";
    Mat gray = new Mat();

    // Convert to gray scale
    cvtColor(inputMat, gray, COLOR_BGR2GRAY);
    imwrite(IMG_PATH + FilenameUtils.removeExtension(fileName) + "_gray.png", gray);

    try {
      // Recognize text with OCR
      result = tesseract.doOCR(new File(IMG_PATH + FilenameUtils.removeExtension(fileName) +"_gray.png"));
    } catch (TesseractException e) {
      e.printStackTrace();
    }

    return result;
  }


}
