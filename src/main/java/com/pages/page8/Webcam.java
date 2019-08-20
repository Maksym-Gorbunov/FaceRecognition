package com.pages.page8;

import com.constants.Constants;

import org.bytedeco.javacpp.opencv_core;
import org.bytedeco.javacpp.opencv_core.IplImage;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;

import static org.bytedeco.javacpp.opencv_core.*;
import static org.bytedeco.javacpp.opencv_highgui.*;
import static org.bytedeco.javacpp.opencv_imgproc.CV_BGR2HSV;
import static org.bytedeco.javacpp.opencv_imgproc.cvCvtColor;

public class Webcam extends JPanel implements Runnable {
  protected volatile boolean runnable = false;
  private int width = Constants.VIDEO_WIDTH;
  private int height = Constants.VIDEO_HEIGHT;
  private IplImage img, imgHsv, imgBin;
  private CvCapture capture;
  private String filter = "original";

  public Webcam() {
    setPreferredSize(new Dimension(width, height));
  }

  @Override
  public void run() {
    synchronized (this) {
      imgHsv = cvCreateImage(cvSize(width, height), 8, 3);
      imgBin = cvCreateImage(cvSize(width, height), 8, 1);
      capture = cvCreateCameraCapture(CV_CAP_ANY);
      while (runnable) {
        img = cvQueryFrame(capture);
        if (img == null) {
          break;
        }
        // hsv filter
        cvCvtColor(img, imgHsv, CV_BGR2HSV);
        opencv_core.CvScalar minc = cvScalar(20, 100, 100, 0);
        opencv_core.CvScalar maxc = cvScalar(60, 160, 160, 0);
        cvInRangeS(imgHsv, minc, maxc, imgBin);

        // draw each image on JPanel
        BufferedImage buff;
        if (filter.equals("binary")) {
          buff = imgHsv.getBufferedImage();
//          buff = imgBin.getBufferedImage();
        } else {
          buff = img.getBufferedImage();
        }


        this.getGraphics().drawImage(buff, 0, 0, width, height, 0, 0,
                buff.getWidth(), buff.getHeight(), null);
        if (!runnable) {
//          cvReleaseImage(img);
//          cvReleaseImage(imgHsv);
//          cvReleaseImage(imgBin);
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

  public IplImage getImgHsv() {
    return imgHsv;
  }

  public IplImage getImgBin() {
    return imgBin;
  }


}
