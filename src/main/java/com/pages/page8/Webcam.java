package com.pages.page8;

import com.constants.Constants;

import javax.swing.*;
import java.awt.*;

import org.bytedeco.javacpp.opencv_core;
import org.bytedeco.javacpp.opencv_core.IplImage;

import static org.bytedeco.javacpp.opencv_core.*;
import static org.bytedeco.javacpp.opencv_highgui.*;
import static org.bytedeco.javacpp.opencv_imgproc.CV_BGR2HSV;
import static org.bytedeco.javacpp.opencv_imgproc.cvCvtColor;

public class Webcam extends JPanel {
  private int width = Constants.VIDEO_WIDTH;
  private int height = Constants.VIDEO_HEIGHT;
  private IplImage img, imgHsv, imgBin;
  private CvCapture capture;
  private boolean status = true;

  public Webcam() {
    setPreferredSize(new Dimension(width, height));
    imgHsv = cvCreateImage(cvSize(width, height), 8, 3);
    imgBin = cvCreateImage(cvSize(width, height), 8, 1);
    capture = cvCreateCameraCapture(CV_CAP_ANY);
  }

  public void on() {
    while (status) {
      img = cvQueryFrame(capture);
      if (img == null) {
        break;
      }
      // gray filter
      cvCvtColor(img, imgHsv, CV_BGR2HSV);
      // hsv filter
      opencv_core.CvScalar minc = cvScalar(20, 100, 100, 0);
      opencv_core.CvScalar maxc = cvScalar(60, 160, 160, 0);
      cvInRangeS(imgHsv, minc, maxc, imgBin);


//      cvShowImage("color", img);
//      cvShowImage("Binary", imgBin);
      if (!status) {
        break;
      }
    }
//    cvReleaseImage(imghsv);
//    cvReleaseImage(imgbin);
//    cvReleaseCapture(capture1);
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
