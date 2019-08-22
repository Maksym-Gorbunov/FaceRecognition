package com.pages.page8;

import com.constants.Constants;
import com.gui.ImagePanel;
import org.bytedeco.javacpp.Loader;
import org.bytedeco.javacpp.opencv_core.*;
import org.bytedeco.javacpp.opencv_highgui;
//import org.bytedeco.javacpp.opencv_videoio.*;
import org.opencv.core.Point;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;

import static org.bytedeco.javacpp.helper.opencv_core.CV_RGB;
//import static org.bytedeco.javacpp.helper.opencv_core.RGB;
import static org.bytedeco.javacpp.opencv_core.*;
import static org.bytedeco.javacpp.opencv_highgui.*;
import static org.bytedeco.javacpp.opencv_imgproc.*;
//import static org.bytedeco.javacpp.opencv_videoio.*;
//import static org.bytedeco.javacpp.opencv_imgcodecs.*;
//import static org.apache.pdfbox.rendering.ImageType.RGB;
//import com.googlecode.javacv.cpp.opencv_core.CvPoint;
//import org.bytedeco.javacpp.;
//import java.awt.Color;

public class Webcam extends JPanel implements Runnable {

  protected volatile boolean runnable = false;
  private int width = Constants.VIDEO_WIDTH;
  private int height = Constants.VIDEO_HEIGHT;
  private IplImage img, hsvImg, binImg, grayImg;
  private opencv_highgui.CvCapture capture;
  private CvScalar minc;
  private CvScalar maxc;
  private BufferedImage buffImg;
  private Filter filter = Filter.OFF;

  private CvSeq contour1;
  private CvSeq contour2;
  private CvMemStorage storage;
  private CvMemStorage mem;
  private double areaMax;
  private double areaC;
  private CvMoments moments;

  private double m10, m01, m_area;
  private int posX = 0, posY = 0;

  public static enum Filter {
    OFF, GRAY, HSV, COLOR, CONTOURS, MOMENTS, OBJECTS
  }


  public Webcam() {
    setPreferredSize(new Dimension(width, height));
//    minc = cvScalar(20, 100, 100, 0);
//    maxc = cvScalar(60, 160, 160, 0);
    // blue filter
    minc = cvScalar(95, 150, 75, 0);
    maxc = cvScalar(145, 255, 255, 0);
  }


  public void setFilter(Filter filter) {
    this.filter = filter;
  }

  @Override
  public void run() {
    synchronized (this) {
      if (runnable) {
        //contours
        contour1 = new CvSeq();
        storage = CvMemStorage.create();

        //objects
        mem = CvMemStorage.create();
//        storage = CvMemStorage.create();

        areaC = 0;
        //moments
        moments = new CvMoments(Loader.sizeof(CvMoments.class));
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
            case GRAY:
              grayImg = cvCreateImage(cvSize(width, height), 8, 1);
              cvCvtColor(img, grayImg, CV_BGR2GRAY);
              buffImg = grayImg.getBufferedImage();
              break;
            case HSV:
              hsvImg = cvCreateImage(cvSize(width, height), 8, 3);
              cvCvtColor(img, hsvImg, CV_BGR2HSV);
              buffImg = hsvImg.getBufferedImage();
              break;
            case COLOR:
              hsvImg = cvCreateImage(cvSize(width, height), 8, 3);
              binImg = cvCreateImage(cvSize(width, height), 8, 1);
              cvCvtColor(img, hsvImg, CV_BGR2HSV);
              cvInRangeS(hsvImg, minc, maxc, binImg);
              buffImg = binImg.getBufferedImage();
              break;
            case CONTOURS:
              contours();
              break;
            case MOMENTS:
              moments();
              break;
            case OBJECTS:
              objects();
              break;
            default:
              buffImg = img.getBufferedImage();
              break;
          }
          // blue/green btn
          // factory object recognition as on video, left item selected
          // countur size experiment
          try {
            this.getGraphics().drawImage(buffImg, 0, 0, width, height, 0, 0,
                    buffImg.getWidth(), buffImg.getHeight(), null);
          } catch (Exception e) {
            System.out.println("...");
          }
          if (!runnable) {
            break;
          }
        }
      }
      cvReleaseCapture(capture);
      runnable = false;
    }
  }





  private void objects() {
    /*
    grayImg = cvCreateImage(cvSize(width, height), 8, 1);
    cvCvtColor(img, grayImg, CV_BGR2GRAY);
//    buffImg = grayImg.getBufferedImage();

//    IplImage gray = toGray(img.clone());
    cvSmooth(grayImg, grayImg, 3, 7, 7, 3, 3);
    cvThreshold(grayImg, grayImg, 150, 255, 1);

//    CvMemStorage mem = CvMemStorage.create();
//    CvSeq contours = new CvSeq();
    cvFindContours(grayImg, mem, contour1, Loader.sizeof(CvContour.class) , RETR_EXTERNAL , CHAIN_APPROX_SIMPLE );
    cvDrawContours(grayImg, contour1, CvScalar.WHITE, CvScalar.WHITE, 3, -1, 0);
    Point minleft = new Point(0, 0);
    while (contour1 != null && !contour1.isNull() && contour1.elem_size() > 0) {
      CvBox2D box = cvMinAreaRect2(contour1, storage);
      CvSize2D32f size = box.size();
      if (size.width() > 50 && size.height() > 50) {
        if (box != null) {

          CvBox2D box1 = cvFitEllipse2(contour1);
          ellipse(cvarrToMat(img), box1.center(),RGB(1,1, 255), -1, 0);
//          ellipse(cvarrToMat(img), box1.asRotatedRect(),RGB(1,1, 255), -1, 0);
          Scalar c = RGB(1,255, 0);
          if (minleft.x() == 0 || minleft.x() > box.center().x()) {
            minleft.x((int) box.center().x());
            minleft.y((int) box.center().y());
          }
          circle(cvarrToMat(img), new Point(Math.round(box.center().x()), Math.round(box.center().y())), 10, c, CV_FILLED, CV_AA, 0);
        }
      }
      contour1 = contour1.h_next();
    }
    circle(cvarrToMat(img), minleft, 10, RGB(255,0, 0), CV_FILLED, CV_AA, 0);

    buffImg = img.getBufferedImage();
    */
  }







  private void moments() {
    hsvImg = cvCreateImage(cvSize(width, height), 8, 3);
    binImg = cvCreateImage(cvSize(width, height), 8, 1);
    cvCvtColor(img, hsvImg, CV_BGR2HSV);
    cvInRangeS(hsvImg, minc, maxc, binImg);
    contour1 = new CvSeq();
    areaMax = 1000;
    cvFindContours(binImg, storage, contour1, Loader.sizeof(CvContour.class),
            CV_RETR_LIST, CV_LINK_RUNS, cvPoint(0, 0));
    contour2 = contour1;
    while (contour1 != null && !contour1.isNull()) {
      areaC = cvContourArea(contour1, CV_WHOLE_SEQ, 1);
      if (areaC > areaMax)
        areaMax = areaC;
      contour1 = contour1.h_next();
    }
    while (contour2 != null && !contour2.isNull()) {
      areaC = cvContourArea(contour2, CV_WHOLE_SEQ, 1);
      if (areaC < areaMax) {
        cvDrawContours(binImg, contour2, CV_RGB(0, 0, 0), CV_RGB(0, 0, 0),
                0, CV_FILLED, 8, cvPoint(0, 0));
      }
      contour2 = contour2.h_next();
    }
    cvMoments(binImg, moments, 1);
    m10 = cvGetSpatialMoment(moments, 1, 0);
    m01 = cvGetSpatialMoment(moments, 0, 1);
    m_area = cvGetCentralMoment(moments, 0, 0);
    posX = (int) (m10 / m_area);
    posY = (int) (m01 / m_area);
    if (posX > 0 && posY > 0) {
      System.out.println("x = " + posX + ", y= " + posY);
    }
    cvCircle(img, cvPoint(posX, posY), 5, cvScalar(0, 255, 0, 0), 20, 0, 0);
    buffImg = img.getBufferedImage();
  }


  private void contours() {
    hsvImg = cvCreateImage(cvSize(width, height), 8, 3);
    binImg = cvCreateImage(cvSize(width, height), 8, 1);
    cvCvtColor(img, hsvImg, CV_BGR2HSV);
    cvInRangeS(hsvImg, minc, maxc, binImg);
    contour1 = new CvSeq();
    areaMax = 1000;
    cvFindContours(binImg, storage, contour1, Loader.sizeof(CvContour.class),
            CV_RETR_LIST, CV_LINK_RUNS, cvPoint(0, 0));
    contour2 = contour1;
    while (contour1 != null && !contour1.isNull()) {
      areaC = cvContourArea(contour1, CV_WHOLE_SEQ, 1);
      if (areaC > areaMax) {
        areaMax = areaC;
      }
      contour1 = contour1.h_next();
    }
    while (contour2 != null && !contour2.isNull()) {
      areaC = cvContourArea(contour2, CV_WHOLE_SEQ, 1);
      if (areaC < areaMax) {
        cvDrawContours(binImg, contour2, CV_RGB(0, 0, 0), CV_RGB(0, 0, 0),
                0, CV_FILLED, 8, cvPoint(0, 0));
      }
      contour2 = contour2.h_next();
    }
    buffImg = binImg.getBufferedImage();
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


  public Filter getFilter() {
    return filter;
  }
}
