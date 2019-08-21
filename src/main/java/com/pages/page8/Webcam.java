package com.pages.page8;

import com.constants.Constants;
import org.bytedeco.javacpp.opencv_core.IplImage;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;

import static java.lang.System.gc;
import static org.bytedeco.javacpp.opencv_core.*;
import static org.bytedeco.javacpp.opencv_highgui.*;
import static org.bytedeco.javacpp.opencv_imgproc.*;

public class Webcam extends JPanel implements Runnable {

  protected volatile boolean runnable = false;
  private int width = Constants.VIDEO_WIDTH;
  private int height = Constants.VIDEO_HEIGHT;
  private IplImage img, hsvImg, binImg, grayImg;
  private CvCapture capture;
  private CvScalar minc;
  private CvScalar maxc;
  private BufferedImage buffImg;
  private Filter filter;

  public static enum Filter {
    GRAY, HSV, BINARY, OFF
  }


  public Webcam() {
    setPreferredSize(new Dimension(width, height));
//    minc = cvScalar(20, 100, 100, 0);
//    maxc = cvScalar(60, 160, 160, 0);
    // blue filter
    minc = cvScalar(95, 150, 75, 0);
    maxc = cvScalar(145, 255, 255, 0);
    filter = Filter.OFF;
  }

  public void setFilter(Filter filter) {
    this.filter = filter;
  }

  @Override
  public void run() {
    synchronized (this) {
      if (runnable) {

        capture = cvCreateCameraCapture(CV_CAP_ANY);

        while (runnable) {
          img = cvQueryFrame(capture);
          if (img == null) {
            break;
          }
          //draw each image on JPanel


          switch (filter) {
            case OFF:
              buffImg = img.getBufferedImage();
              break;
            case BINARY:
              hsvImg = cvCreateImage(cvSize(width, height), 8, 3);
              binImg = cvCreateImage(cvSize(width, height), 8, 1);
              cvCvtColor(img, hsvImg, CV_BGR2HSV);
              cvInRangeS(hsvImg, minc, maxc, binImg);
              buffImg = binImg.getBufferedImage();
              break;
            case HSV:
              hsvImg = cvCreateImage(cvSize(width, height), 8, 3);
//            cvCvtColor(img, hsvImg, CV_BGR2HSV);
              cvCvtColor(img, hsvImg, CV_BGR2HSV);
              buffImg = hsvImg.getBufferedImage();
              break;
            case GRAY:
              grayImg = cvCreateImage(cvSize(width, height), 8, 1);
//            grayImg = cvCreateImage(cvGetSize(img), IPL_DEPTH_8U, 1);
              cvCvtColor(img, grayImg, CV_BGR2GRAY);
              buffImg = grayImg.getBufferedImage();
              break;
            default:
              buffImg = img.getBufferedImage();
              break;
          }

          //toDo add extra methods for each filter
          //toDo add moments
          // blue/green btn
          // factory object recognition as on video, left item selected
          // countur size experiment

          this.getGraphics().drawImage(buffImg, 0, 0, width, height, 0, 0,
                  buffImg.getWidth(), buffImg.getHeight(), null);
          if (!runnable) {
            //cvReleaseImage(img);
            //cvReleaseImage(hsvImg);
            //cvReleaseImage(binImg);
            break;
          }
        }
      }
      System.out.println("gc...");
      cvReleaseCapture(capture);
//      cvReleaseImage(img);
//      cvReleaseImage(grayImg);
//      cvReleaseImage(hsvImg);
//      cvReleaseImage(binImg);
      runnable = false;
//      gc();
    }
//        System.out.println("run...");
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
