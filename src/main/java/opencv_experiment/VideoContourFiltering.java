package opencv_experiment;

import org.bytedeco.javacpp.Loader;
import org.bytedeco.javacpp.opencv_core;
import org.bytedeco.javacpp.opencv_core.IplImage;

import static org.bytedeco.javacpp.opencv_core.*;
import static org.bytedeco.javacpp.opencv_highgui.*;
import static org.bytedeco.javacpp.opencv_imgproc.*;

public class VideoContourFiltering {

  /**
   * @param args
   */
  public static void main(String[] args) {
    // TODO Auto-generated method stub


    IplImage img1, imghsv, imgbin;
    CvScalar minc = cvScalar(95, 150, 75, 0), maxc = cvScalar(145, 255, 255, 0);
    CvSeq contour1 = new CvSeq(), contour2;
    CvMemStorage storage = CvMemStorage.create();
    double areaMax, areaC = 0;

    CvCapture capture1 = cvCreateCameraCapture(CV_CAP_ANY);
    imghsv = cvCreateImage(cvSize(640, 480), 8, 3);
    imgbin = cvCreateImage(cvSize(640, 480), 8, 1);

    int i = 1;
    while (i == 1) {

      img1 = cvQueryFrame(capture1);

      if (img1 == null) break;

      cvCvtColor(img1, imghsv, CV_BGR2HSV);
      cvInRangeS(imghsv, minc, maxc, imgbin);

      contour1 = new CvSeq();
      areaMax = 1000;

      cvFindContours(imgbin, storage, contour1, Loader.sizeof(CvContour.class),
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
          cvDrawContours(imgbin, contour2, CV_RGB(0, 0, 0), CV_RGB(0, 0, 0),
                  0, CV_FILLED, 8, cvPoint(0, 0));
        }

        contour2 = contour2.h_next();
      }

      cvShowImage("Color", img1);
      cvShowImage("CF", imgbin);
      char c = (char) cvWaitKey(15);
      if (c == 'q') break;


    }


    cvReleaseImage(imghsv);
    cvReleaseImage(imgbin);
    cvReleaseMemStorage(storage);
    cvReleaseCapture(capture1);

  }

}
