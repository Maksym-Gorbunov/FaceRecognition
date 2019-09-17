package com.pages.page4;

import com.constants.Constants;
import com.gui.Gui;
import com.pages.Pages;

import java.awt.*;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import org.opencv.core.*;
import org.opencv.core.Point;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.videoio.VideoCapture;
import org.opencv.imgproc.Imgproc;
import org.opencv.objdetect.CascadeClassifier;

public class Page4 extends JPanel implements Pages {
  private static final long serialVersionUID = 1L;
  private Gui gui;
  private JPanel tab4;
  private JPanel mainPanel = new JPanel();
  private JPanel buttonsPanel = new JPanel();
  private JButton startButton = new JButton("Start");
  public static boolean eyes = false;
  public static boolean smile = false;
  private JButton stopButton = new JButton("Pause");
  private JButton hideButton = new JButton("Hide");
  public static boolean hide = false;
  private JButton eyesButton = new JButton("Eyes");
  private JButton smileButton = new JButton("Smile");
  private JPanel webcamPanel = new JPanel();

  private DaemonThread myThread = null;
  private int count = 0;
  private VideoCapture webSource = null;
  private Mat frame = new Mat();
  private MatOfByte mem = new MatOfByte();
  private CascadeClassifier faceDetector = new CascadeClassifier(Constants.CASCADE_CLASSIFIER);
  private CascadeClassifier smileDetector = new CascadeClassifier(Constants.projectPath + "\\lib\\haarcascade_smile.xml");
  private CascadeClassifier eyesDetector = new CascadeClassifier(Constants.projectPath + "\\lib\\haarcascade_eye.xml");

  // Webb camera face recognition, OpenCV
  public Page4(final Gui gui) {
    this.gui = gui;
    tab4 = gui.getTab4();
    initComponents();
    addListeners();
  }

  private void initComponents() {
    ///////////////////////////////////////////////////////////////
    mainPanel.setBackground(Color.blue);
    buttonsPanel.setBackground(Color.green);
    ///////////////////////////////////////////////////////////////

    webcamPanel.setPreferredSize(new Dimension(Constants.VIDEO_WIDTH, Constants.VIDEO_HEIGHT));
    mainPanel.add(webcamPanel);

    buttonsPanel.add(startButton);
    buttonsPanel.add(stopButton);
    buttonsPanel.add(hideButton);
    buttonsPanel.add(eyesButton);
    buttonsPanel.add(smileButton);

    mainPanel.setPreferredSize(new Dimension(800, 500));
    buttonsPanel.setPreferredSize(new Dimension(800, 100));
    tab4.add(mainPanel);
    tab4.add(buttonsPanel);
    stopButton.setEnabled(false);
    hideButton.setEnabled(false);
    eyesButton.setEnabled(false);
    smileButton.setEnabled(false);
  }

  private void addListeners() {
    startButton.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        webSource = new VideoCapture(0); // video capture from default cam
        myThread = new DaemonThread(); //create object of threat class
        Thread thread = new Thread(myThread);
        thread.setDaemon(true);
        myThread.runnable = true;
        thread.start();
        startButton.setEnabled(false);
        stopButton.setEnabled(true);
        gui.getTabs().setEnabled(false);
        hideButton.setEnabled(true);
        eyesButton.setEnabled(true);
        smileButton.setEnabled(true);
      }
    });

    stopButton.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        myThread.runnable = false;            // stop thread
        webSource.release();  // stop caturing fron cam
        stopButton.setEnabled(false);
        startButton.setEnabled(true);
        gui.getTabs().setEnabled(true);
      }
    });

    hideButton.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        hide = !hide;
        eyes = false;
        smile = false;
        if(hide){
          eyesButton.setEnabled(false);
          smileButton.setEnabled(false);
        } else {
          eyesButton.setEnabled(true);
          smileButton.setEnabled(true);
        }
      }
    });

    eyesButton.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        eyes = !eyes;
      }
    });

    smileButton.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        smile = !smile;
      }
    });
  }

  class DaemonThread implements Runnable {
    protected volatile boolean runnable = false;
    private MatOfRect faceDetections = new MatOfRect();
    private MatOfRect eyeDetections = new MatOfRect();
    private MatOfRect smileDetections = new MatOfRect();

    @Override
    public void run() {
      synchronized (this) {
        while (runnable) {
          if (webSource.grab()) {
            try {
              webSource.retrieve(frame);
              Graphics g = webcamPanel.getGraphics();

              Mat frameGray = new Mat();
              Imgproc.cvtColor(frame, frameGray, Imgproc.COLOR_RGB2GRAY);
              faceDetector.detectMultiScale(frameGray, faceDetections);
              for (Rect faceRect : faceDetections.toArray()) {
                //hide face
                if (hide) {
                  Mat f = Imgcodecs.imread(Constants.imgPath + "synteda.jpg");
                  Mat resizedF = new Mat();
                  Imgproc.resize(f, resizedF, new Size(faceRect.width, faceRect.height));
                  resizedF.copyTo(frame
                          .rowRange((int) faceRect.tl().y, (int) (faceRect.tl().y + faceRect.height))
                          .colRange((int) faceRect.tl().x, (int) faceRect.tl().x + faceRect.width));
                } else {
                  //show face
                  Imgproc.rectangle(frame, faceRect.tl(), faceRect.br(), new Scalar(255, 0, 0), 2);
                  //show eyes
                  if (eyes) {
                    Mat face = new Mat(frameGray, faceRect);
                    eyesDetector.detectMultiScale(face, eyeDetections);
                    Rect leftEye = null;
                    Rect rightEye = null;
                    for (Rect eyeRect : eyeDetections.toArray()) {
                      Point startPoint = new Point(faceRect.tl().x + eyeRect.tl().x, faceRect.tl().y + eyeRect.tl().y);
                      Point endPoint = new Point(startPoint.x + eyeRect.width, startPoint.y + eyeRect.height);
                      if (endPoint.y < faceRect.tl().y + (0.55 * faceRect.height)) {
                        if (leftEye == null) {
                          leftEye = eyeRect;
                        }
                        if ((!eyeRect.contains(leftEye.tl())) && (!eyeRect.contains(leftEye.br()))) {
                          rightEye = eyeRect;
                        }
                      }
                    }
                    if ((leftEye != null) && rightEye != null) {

                      if (leftEye.br().x > rightEye.tl().x) {
                        Rect temp = leftEye;
                        leftEye = rightEye;
                        rightEye = temp;
                      }
                      Imgproc.rectangle(frame,
                              new Point(leftEye.tl().x + faceRect.tl().x, leftEye.tl().y + faceRect.tl().y),
                              new Point(leftEye.tl().x + faceRect.tl().x + leftEye.width, leftEye.tl().y + faceRect.tl().y + leftEye.y),
                              new Scalar(255, 0, 0), 1);
                      Imgproc.rectangle(frame,
                              new Point(rightEye.tl().x + faceRect.tl().x, rightEye.tl().y + faceRect.tl().y),
                              new Point(rightEye.tl().x + faceRect.tl().x + rightEye.width, rightEye.tl().y + faceRect.tl().y + rightEye.y),
                              new Scalar(0, 0, 255), 1);
                    }
                  }
                  //smile
                  if (smile) {
                    smileDetector.detectMultiScale(frame, smileDetections);
                    Rect[] smiles = smileDetections.toArray();
                    Rect smile = null;
                    Rect faceBest = null;
                    for (Rect tempSmile : smiles) {
                      if (faceRect.contains(tempSmile.tl()) && faceRect.contains(tempSmile.br())) {
                        if ((tempSmile.tl().y + tempSmile.height / 2 > 0.7 * frame.height())
                                && (tempSmile.tl().y + tempSmile.height / 2 < frame.height())) {

                          faceBest = faceRect;
                          smile = tempSmile;
                        }
                      }
                    }
                    if (smile != null) {
                      if (smile.width >= 0.3 * faceBest.width) {
                        Imgproc.rectangle(frame, smile.tl(), smile.br(), new Scalar(0, 255, 0), 2);
                      }
                    }
                  }
                }
              }

              Imgcodecs.imencode(".bmp", frame, mem);
              Image im = ImageIO.read(new ByteArrayInputStream(mem.toArray()));
              BufferedImage buff = (BufferedImage) im;
              if (g.drawImage(buff, 0, 0, Constants.VIDEO_WIDTH, Constants.VIDEO_HEIGHT, 0, 0, buff.getWidth(), buff.getHeight(), null)) {
                if (runnable == false) {
                  System.out.println("Paused ..... ");
                  this.wait();
                }
              }
            } catch (Exception ex) {
              System.out.println("Error!!");
              ex.printStackTrace();
            }
          }
        }
      }
    }


    private boolean smileInFaceArea(Rect face, Rect smile) {
      Point smileCenter = new Point(smile.tl().x + smile.width / 2, smile.tl().y + smile.height / 2);

      if (face.contains(smile.tl()) && (face.contains(smile.br()))) {
        if ((smileCenter.x > face.tl().x) && (smileCenter.x < face.br().x)
                && (smileCenter.y > face.tl().y + face.height * 0.7) && (smileCenter.y < face.br().y)) {
          return true;
        }
      }

      //if smile center pointer on 30% from bottom face

      return false;
    }
  }
}
