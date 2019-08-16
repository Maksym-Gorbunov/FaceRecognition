package com.pages.page6;

import org.bytedeco.javacpp.Loader;
import org.bytedeco.javacpp.opencv_core.*;
import static org.bytedeco.javacpp.opencv_core.*;
import static org.bytedeco.javacpp.opencv_highgui.*;
import static org.bytedeco.javacpp.opencv_imgproc.*;


// Image contour filtering
public class ImageFiltering {
  private IplImage img;
  private String imgOriginalPath;


  // Constructor
  public ImageFiltering(String imgOriginalPath){
    this.imgOriginalPath = imgOriginalPath;
    img = cvLoadImage(imgOriginalPath);
  }


  // Filter IplImage image and show max
  public int maxContourFilter(String resultPath) {
    IplImage imghsv, imgbin;
    // green
    CvScalar minc = cvScalar(40, 150, 75, 0), maxc = cvScalar(80, 255, 255, 0);
    // blue
    // CvScalar minc = cvScalar(95, 150, 75, 0), maxc = cvScalar(145, 255, 255, 0);
    CvSeq contour1 = new CvSeq(), contour2;
    CvMemStorage storage = CvMemStorage.create();
    double areaMax = 1000, areaC = 0;
    imghsv = cvCreateImage(cvGetSize(img), 8, 3);
    imgbin = cvCreateImage(cvGetSize(img), 8, 1);
    cvCvtColor(img, imghsv, CV_BGR2HSV);
    cvInRangeS(imghsv, minc, maxc, imgbin);
    int total = cvFindContours(imgbin, storage, contour1, Loader.sizeof(CvContour.class),
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
        cvDrawContours(imgbin, contour2, CV_RGB(0, 0, 0), CV_RGB(0, 0, 0),
                0, CV_FILLED, 8, cvPoint(0, 0));
      }
      contour2 = contour2.h_next();
    }
    cvSaveImage(resultPath, imgbin);
    if(total <= 0){
      return 0;
    }
    return total;
  }


  // Filter IplImage image and show all contours
  public void contoursFilter(String resultPath) {
    IplImage imghsv, imgbin;
    // green
    CvScalar minc = cvScalar(40, 150, 75, 0), maxc = cvScalar(80, 255, 255, 0);
    // blue
    // CvScalar minc = cvScalar(95, 150, 75, 0), maxc = cvScalar(145, 255, 255, 0);
    imghsv = cvCreateImage(cvGetSize(img), 8, 3);
    imgbin = cvCreateImage(cvGetSize(img), 8, 1);
    cvCvtColor(img, imghsv, CV_BGR2HSV);
    cvInRangeS(imghsv, minc, maxc, imgbin);
    cvSaveImage(resultPath, imgbin);
  }


  // Gray filter
  public void grayFilter(String resultPath) {
    IplImage grayimg = cvCreateImage(cvGetSize(img), IPL_DEPTH_8U, 1);
    cvCvtColor(img, grayimg, CV_BGR2GRAY);
    cvSaveImage(resultPath, grayimg);
  }


  // HSV filter
  public void hsvFilter(String resultPath) {
    IplImage hsvimg = cvCreateImage(cvGetSize(img), IPL_DEPTH_8U, 3);
    cvCvtColor(img, hsvimg, CV_BGR2HSV);
    cvSaveImage(resultPath, hsvimg);
  }

  public IplImage test() {
    IplImage hsvimg = cvCreateImage(cvGetSize(img), IPL_DEPTH_8U, 3);
    cvCvtColor(img, hsvimg, CV_BGR2HSV);
    return hsvimg;
  }
}

