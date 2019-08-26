package opencv_experiment;

import com.constants.Constants;
import org.bytedeco.javacpp.Loader;
import org.bytedeco.javacpp.opencv_core.IplImage;

import static org.bytedeco.javacpp.opencv_core.*;
import static org.bytedeco.javacpp.opencv_highgui.*;
import static org.bytedeco.javacpp.opencv_imgproc.*;

public class ImageContourFiltering {

  /**
   * @param args
   */
  public static void main(String[] args) {
    // Red 160-180
    // Green 40-80
    // Blue 95-145

    IplImage img1, imghsv, imgbin;

    // green
    //CvScalar minc = cvScalar(40,150,75,0), maxc = cvScalar(80,255,255,0);

    // blue
    CvScalar minc = cvScalar(95, 150, 75, 0), maxc = cvScalar(145, 255, 255, 0);

    CvSeq contour1 = new CvSeq(), contour2;
    CvMemStorage storage = CvMemStorage.create();
    double areaMax = 1000, areaC = 0;


    img1 = cvLoadImage(Constants.imgPath + "figures.jpg");
    imghsv = cvCreateImage(cvGetSize(img1), 8, 3);
    imgbin = cvCreateImage(cvGetSize(img1), 8, 1);

    cvCvtColor(img1, imghsv, CV_BGR2HSV);
    cvInRangeS(imghsv, minc, maxc, imgbin);

    cvShowImage("Binary", imgbin);

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
    cvWaitKey();

//    cvReleaseImage(img1);
//    cvReleaseImage(imghsv);
//    cvReleaseImage(imgbin);
//    cvReleaseMemStorage(storage);

  }

}

