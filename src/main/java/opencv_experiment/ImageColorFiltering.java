package opencv_experiment;

//import com.googlecode.javacpp.Loader;
//import com.googlecode.javacv.*;
//import com.googlecode.javacv.cpp.*;
//import com.googlecode.javacv.cpp.opencv_core.CvPoint;
//import com.googlecode.javacv.cpp.opencv_core.CvScalar;
//import com.googlecode.javacv.cpp.opencv_core.CvSeq;
//import com.googlecode.javacv.cpp.opencv_core.IplImage;
//import com.googlecode.javacv.cpp.opencv_highgui.CvCapture;
//import com.googlecode.javacv.cpp.opencv_imgproc.CvMoments;
//
//import static com.googlecode.javacv.cpp.opencv_core.*;
//import static com.googlecode.javacv.cpp.opencv_imgproc.*;
//import static com.googlecode.javacv.cpp.opencv_calib3d.*;
//import static com.googlecode.javacv.cpp.opencv_objdetect.*;
//import static com.googlecode.javacv.cpp.opencv_highgui.*;

import com.constants.Constants;
import org.bytedeco.javacpp.opencv_core;
import org.bytedeco.javacpp.opencv_core.IplImage;
import org.bytedeco.javacv.FrameRecorder;
import org.bytedeco.javacv.OpenCVFrameRecorder;
import org.opencv.core.Core;

import static org.bytedeco.javacpp.opencv_core.*;
import static org.bytedeco.javacpp.opencv_highgui.*;
import static org.bytedeco.javacpp.opencv_imgproc.CV_BGR2HSV;
import static org.bytedeco.javacpp.opencv_imgproc.cvCvtColor;

import org.opencv.videoio.VideoWriter;


//import org.bytedeco.javacpp.opencv_core. .inRange();

public class ImageColorFiltering {

  /**
   * @param args
   */
  public static void main(String[] args) {
    // TODO Auto-generated method stub

    // Red 160-180
    // Green 40-80
    // Blue 95-145


    IplImage img1, imghsv, imgbin;

    img1 = cvLoadImage(Constants.imgPath + "color.jpg");
    imghsv = cvCreateImage(cvGetSize(img1), 8, 3);
    imgbin = cvCreateImage(cvGetSize(img1), 8, 1);

    int i = 1;


    cvCvtColor(img1, imghsv, CV_BGR2HSV);

    // blue filter
    CvScalar minc = cvScalar(95, 150, 75, 0);
    CvScalar maxc = cvScalar(145, 255, 255, 0);

    cvInRangeS(imghsv, minc, maxc, imgbin);

    cvShowImage("color", img1);
    cvShowImage("Binary", imgbin);

    cvWaitKey();


    cvReleaseImage(imghsv);
    cvReleaseImage(imgbin);
  }

}
