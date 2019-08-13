package opencv_experiment;

import com.constants.Constants;
import org.bytedeco.javacpp.opencv_core.IplImage;

import static org.bytedeco.javacpp.opencv_core.*;
import static org.bytedeco.javacpp.opencv_imgproc.*;
import static org.bytedeco.javacpp.opencv_highgui.*;

public class VideoProcessing {

  /**
   * @param args
   * play video from file or webbcam and grayscale
   */
  public static void main(String[] args) {
    //CvCapture capture = cvCreateFileCapture(Constants.videoPath+"halo.mp4");
    CvCapture capture = cvCreateCameraCapture(CV_CAP_ANY);
    IplImage frame;
    IplImage grayimg = cvCreateImage(cvSize(640, 480), IPL_DEPTH_8U, 1);

    cvNamedWindow("Video", CV_WINDOW_AUTOSIZE);

    for (; ; ) {
      frame = cvQueryFrame(capture);
      if (frame == null) {
        System.out.println("ERROR: NO Video File");
        break;
      }

      cvCvtColor(frame, grayimg, CV_BGR2GRAY);
      cvShowImage("Video", grayimg);
      char c = (char) cvWaitKey(30);
      // Esc button braak the loop
      if (c == 27) break;
    }

    cvReleaseCapture(capture);
    cvDestroyWindow("Video");
  }

}
