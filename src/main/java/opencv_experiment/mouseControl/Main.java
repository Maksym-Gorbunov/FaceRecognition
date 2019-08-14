package opencv_experiment.mouseControl;

import java.awt.AWTException;

import org.bytedeco.javacpp.Loader;
import org.bytedeco.javacpp.opencv_core.IplImage;
import org.bytedeco.javacpp.opencv_imgproc.CvMoments;

import static org.bytedeco.javacpp.opencv_core.*;
import static org.bytedeco.javacpp.opencv_highgui.*;

// Red 160-180
// Green 40-80
// Blue 95-145

public class Main {


  public static void main(String[] args) throws AWTException {

    IplImage img1, imgBinGreen, imgBinBlue;

    CvScalar BlueMinC = cvScalar(95, 150, 75, 0), BlueMaxC = cvScalar(145, 255, 255, 0);
    CvScalar GreenMinC = cvScalar(40, 50, 60, 0), GreenMaxC = cvScalar(80, 255, 255, 0);

    CvArr mask;

    IplImage imghsv;

    int w = 320, h = 240;
    imghsv = cvCreateImage(cvSize(w, h), 8, 3);
    imgBinGreen = cvCreateImage(cvSize(w, h), 8, 1);
    imgBinBlue = cvCreateImage(cvSize(w, h), 8, 1);
    IplImage imgC = cvCreateImage(cvSize(w, h), 8, 1);
    CvSeq contour1 = new CvSeq(), contour2 = null;
    CvMemStorage storage = CvMemStorage.create();
    CvMoments moments = new CvMoments(Loader.sizeof(CvMoments.class));

    CvCapture capture1 = cvCreateCameraCapture(CV_CAP_ANY);
    cvSetCaptureProperty(capture1, CV_CAP_PROP_FRAME_WIDTH, w);
    cvSetCaptureProperty(capture1, CV_CAP_PROP_FRAME_HEIGHT, h);

    while (true) {
      img1 = cvQueryFrame(capture1);
      if (img1 == null) {
        System.err.println("No Image");
        break;
      }

      imgBinBlue = ccmFilter.Filter(img1, imghsv, imgBinBlue, BlueMaxC, BlueMinC, contour1, contour2, storage, moments, 1, 0);
      imgBinGreen = ccmFilter.Filter(img1, imghsv, imgBinGreen, GreenMaxC, GreenMinC, contour1, contour2, storage, moments, 0, 1);

      cvOr(imgBinBlue, imgBinGreen, imgC, mask = null);
      cvShowImage("Combined", imgC);
      cvShowImage("Original", img1);
      char c = (char) cvWaitKey(15);
      if (c == 'q') break;

    }

    // clear memory
    cvReleaseImage(imghsv);
    cvReleaseImage(imgBinGreen);
    cvReleaseImage(imgBinBlue);
    cvReleaseImage(imghsv);
    cvReleaseMemStorage(storage);
    cvReleaseCapture(capture1);
  }
}
