package com.pages.page4;//package com.pages.page4;
//
//import org.opencv.core.Core;
//import org.opencv.core.Mat;
//import org.opencv.videoio.VideoCapture;
//
//import javax.swing.*;
//
//public class MainFrame2 extends JFrame {
//  private static final long serialVersionUID = 1L;
//  private Detector detector;
//  private CameraPanel cameraPanel;
//
//  public MainFrame2() {
//    super("Face Detection");
//
//    System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
//    this.detector = new Detector();
//    this.cameraPanel = new CameraPanel();
//
//    setContentPane(this.cameraPanel);
//
//    setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
//    setSize(600, 500);
//    setVisible(true);
//  }
//
//  public void displayScreen() {
//    Mat webcamImage = new Mat();
//    VideoCapture videoCapture = new VideoCapture(0);
//
//
//
//    /*
//    if (videoCapture.isOpened()) {
//      for (int i = 0; i < 8; i++) {
//        System.gc();
//        videoCapture.read(webcamImage);
//        if (!webcamImage.empty()) {
//          setSize(webcamImage.width() + 50, webcamImage.height() + 70);
//          webcamImage = detector.detect(webcamImage);
//          cameraPanel.convertMatToImage(webcamImage);
//          cameraPanel.repaint(); // update camera panel
//        } else {
//          System.out.println("Problem");
//          break;
//        }
//      }
//    }
//    */
//
//
//    System.gc();
//    for (int i = 0; i < 200; i++) {
//      cameraPanel.repaint();
//
//    }
//    videoCapture.read(webcamImage);
//    setSize(webcamImage.width() + 50, webcamImage.height() + 70);
//    cameraPanel.convertMatToImage(webcamImage);
//
//
////    if (videoCapture.isOpened()) {
////      while (true) {
////        videoCapture.read(webcamImage);
////        if (!webcamImage.empty()) {
////          setSize(webcamImage.width() + 50, webcamImage.height() + 70);
////          webcamImage = detector.detect(webcamImage);
////          cameraPanel.convertMatToImage(webcamImage);
////          cameraPanel.repaint(); // update camera panel
////        } else {
////          System.out.println("Problem");
////          break;
////        }
////      }
////    }
//  }
//}
