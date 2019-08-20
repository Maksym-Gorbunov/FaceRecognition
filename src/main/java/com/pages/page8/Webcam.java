package com.pages.page8;

import com.constants.Constants;

import org.bytedeco.javacpp.opencv_core;
import org.bytedeco.javacpp.opencv_core.IplImage;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;

import static org.bytedeco.javacpp.opencv_core.*;
import static org.bytedeco.javacpp.opencv_highgui.*;
import static org.bytedeco.javacpp.opencv_imgproc.*;

public class Webcam extends JPanel implements Runnable {
  protected volatile boolean runnable = false;
  private int width = Constants.VIDEO_WIDTH;
  private int height = Constants.VIDEO_HEIGHT;
  private IplImage img, hsvImg, binImg, grayImg;
  private CvCapture capture;
  private String filter = "original";

  public Webcam() {
    setPreferredSize(new Dimension(width, height));
  }

  @Override
  public void run() {
    synchronized (this) {
      hsvImg = cvCreateImage(cvSize(width, height), 8, 3);
      binImg = cvCreateImage(cvSize(width, height), 8, 1);
      capture = cvCreateCameraCapture(CV_CAP_ANY);
      while (runnable) {
        img = cvQueryFrame(capture);
        if (img == null) {
          break;
        }

        //hsv filter
        cvCvtColor(img, hsvImg, CV_BGR2HSV);

        //color filter
        opencv_core.CvScalar minc = cvScalar(20, 100, 100, 0);
        opencv_core.CvScalar maxc = cvScalar(60, 160, 160, 0);
        cvInRangeS(hsvImg, minc, maxc, binImg);

        //gray filter
        grayImg = cvCreateImage(cvGetSize(img), IPL_DEPTH_8U, 1);
        cvCvtColor(img, grayImg, CV_BGR2GRAY);

        //draw each image on JPanel
        BufferedImage buff;
        if (filter.equals("binary")) {
          buff = grayImg.getBufferedImage();
          //toDo add extra methods for each filter
          //toDo add moments
          // blue/green btn
          // factory object recognition as on video, left item selected
          // countur size experiment

//          buff = hsvImg.getBufferedImage();
//          buff = binImg.getBufferedImage();
        } else {
          buff = img.getBufferedImage();
        }


        this.getGraphics().drawImage(buff, 0, 0, width, height, 0, 0,
                buff.getWidth(), buff.getHeight(), null);
        if (!runnable) {
//          cvReleaseImage(img);
//          cvReleaseImage(hsvImg);
//          cvReleaseImage(binImg);
          cvReleaseCapture(capture);
          break;
        }
      }
    }
  }

  public void binaryFilterOn(){
    filter = "binary";
  }

  public void filterOff(){
    filter = "original";
  }

  public void on() {
    Thread thread = new Thread(this);
    thread.setDaemon(true);
    this.runnable = true;
    thread.start();
  }

  public void off() {
    runnable = false;
  }

  // Getters
  public IplImage getImg() {
    return img;
  }

  public IplImage getHsvImg() {
    return hsvImg;
  }

  public IplImage getBinImg() {
    return binImg;
  }


}
