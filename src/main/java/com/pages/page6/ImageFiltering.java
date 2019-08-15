package com.pages.page6;

import com.constants.Constants;
import org.bytedeco.javacpp.Loader;
import org.bytedeco.javacpp.opencv_core.*;

import static org.bytedeco.javacpp.opencv_core.*;
import static org.bytedeco.javacpp.opencv_highgui.*;
import static org.bytedeco.javacpp.opencv_imgproc.*;

// Red 160-180
// Green 40-80
// Blue 95-145

public class ImageFiltering {
  public static String maxContourPath = Constants.imgPath + "maxContour.jpg";
  public static String contoursPath = Constants.imgPath + "contours.jpg";







  //toDo
  // clear kod
  // imgpath from here to page6, add extra args in this class
  // clear memory move to own method
  // move common vars 4st to class fields






  public static String filterMaxContour(String imgPath) {

    IplImage img1, imghsv, imgbin;

    // green
    CvScalar minc = cvScalar(40, 150, 75, 0), maxc = cvScalar(80, 255, 255, 0);
    // blue
    // CvScalar minc = cvScalar(95, 150, 75, 0), maxc = cvScalar(145, 255, 255, 0);

    CvSeq contour1 = new CvSeq(), contour2;
    CvMemStorage storage = CvMemStorage.create();
    double areaMax = 1000, areaC = 0;
    img1 = cvLoadImage(imgPath);
    imghsv = cvCreateImage(cvGetSize(img1), 8, 3);
    imgbin = cvCreateImage(cvGetSize(img1), 8, 1);
    cvCvtColor(img1, imghsv, CV_BGR2HSV);
    cvInRangeS(imghsv, minc, maxc, imgbin);
    int total = cvFindContours(imgbin, storage, contour1, Loader.sizeof(CvContour.class),
            CV_RETR_LIST, CV_LINK_RUNS, cvPoint(0, 0));
    System.out.println(total);
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
        cvDrawContours(imgbin, contour2, CV_RGB(0, 0, 0), CV_RGB(0, 0, 0),
                0, CV_FILLED, 8, cvPoint(0, 0));
      }
      contour2 = contour2.h_next();
    }
    cvSaveImage(maxContourPath, imgbin);
    cvReleaseImage(img1);
    cvReleaseImage(imghsv);
    cvReleaseImage(imgbin);
    cvReleaseMemStorage(storage);
    return maxContourPath;
  }

  public static String filterContours(String imgPath) {
    IplImage img1, imghsv, imgbin;

    // green
    CvScalar minc = cvScalar(40, 150, 75, 0), maxc = cvScalar(80, 255, 255, 0);
    // blue
    // CvScalar minc = cvScalar(95, 150, 75, 0), maxc = cvScalar(145, 255, 255, 0);

    CvSeq contour1 = new CvSeq(), contour2;
    CvMemStorage storage = CvMemStorage.create();
    double areaMax = 1000, areaC = 0;
    img1 = cvLoadImage(imgPath);
    imghsv = cvCreateImage(cvGetSize(img1), 8, 3);
    imgbin = cvCreateImage(cvGetSize(img1), 8, 1);
    cvCvtColor(img1, imghsv, CV_BGR2HSV);
    cvInRangeS(imghsv, minc, maxc, imgbin);

    cvSaveImage(contoursPath, imgbin);
    cvReleaseImage(img1);
    cvReleaseImage(imghsv);
    cvReleaseImage(imgbin);
    cvReleaseMemStorage(storage);
    return contoursPath;
  }
}

