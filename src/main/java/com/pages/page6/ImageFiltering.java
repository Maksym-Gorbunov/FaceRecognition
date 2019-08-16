package com.pages.page6;

import org.bytedeco.javacpp.Loader;
import org.bytedeco.javacpp.opencv_core.*;
import static org.bytedeco.javacpp.opencv_core.*;
import static org.bytedeco.javacpp.opencv_highgui.*;
import static org.bytedeco.javacpp.opencv_imgproc.*;


public class ImageFiltering {

  private IplImage img;
  private IplImage grayImg;
  private IplImage hsvImg;
  private IplImage binImg;
  private int totalContours;
  private CvScalar minScale;
  private CvScalar maxScale;


  // Constructor
  public ImageFiltering(){
    //green
    //minScale = cvScalar(40, 150, 75, 0);
    //maxScale = cvScalar(80, 255, 255, 0);

    // blue
    minScale = cvScalar(95, 150, 75, 0);
    maxScale = cvScalar(145, 255, 255, 0);
  }



  // Set minScale scale



  // Filter IplImage image and show all contours
  public IplImage contoursFilter() {
    hsvImg = cvCreateImage(cvGetSize(img), 8, 3);
    binImg = cvCreateImage(cvGetSize(img), 8, 1);
    cvCvtColor(img, hsvImg, CV_BGR2HSV);
    cvInRangeS(hsvImg, minScale, maxScale, binImg);
    return binImg;
  }


  // Filter IplImage image and show maxScale
  public IplImage maxContourFilter() {
    CvSeq contour1 = new CvSeq(), contour2;
    CvMemStorage storage = CvMemStorage.create();
    double areaMax = 1000, areaC = 0;
    hsvImg = cvCreateImage(cvGetSize(img), 8, 3);
    binImg = cvCreateImage(cvGetSize(img), 8, 1);
    cvCvtColor(img, hsvImg, CV_BGR2HSV);
    cvInRangeS(hsvImg, minScale, maxScale, binImg);
    totalContours = cvFindContours(binImg, storage, contour1, Loader.sizeof(CvContour.class),
            CV_RETR_LIST, CV_LINK_RUNS, cvPoint(0, 0));
    contour2 = contour1;
    while (contour1 != null && !contour1.isNull()) {
      areaC = cvContourArea(contour1, CV_WHOLE_SEQ, 1);
      if (areaC > areaMax){
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
  return binImg;
  }


  // Gray filter
  public IplImage grayFilter() {
    grayImg = cvCreateImage(cvGetSize(img), IPL_DEPTH_8U, 1);
    cvCvtColor(img, grayImg, CV_BGR2GRAY);
    return grayImg;
  }


  // HSV filter
  public IplImage hsvFilter() {
    hsvImg = cvCreateImage(cvGetSize(img), IPL_DEPTH_8U, 3);
    cvCvtColor(img, hsvImg, CV_BGR2HSV);
    return hsvImg;
  }


  // Get amount of total contours
  public int getTotalContours() {
    if(totalContours < 0){
      return 0;
    }
    return totalContours;
  }

  public void setImagePath(String imgPath) {
    img = cvLoadImage(imgPath);
  }
}

