package opencv_experiment;

import com.constants.Constants;
import org.bytedeco.javacpp.opencv_core.IplImage;

import static org.bytedeco.javacpp.opencv_core.*;
import static org.bytedeco.javacpp.opencv_imgproc.*;
import static org.bytedeco.javacpp.opencv_highgui.*;

public class VideoProcessing {

  /**
   * @param args
   * play video from file or webbcam and greyscale
   */
  public static void main(String[] args) {
    //play video file
    CvCapture capture = cvCreateFileCapture(Constants.videoPath+"Vid.mp4");

    //play webbcam
    //CvCapture capture = cvCreateCameraCapture(CV_CAP_ANY);

    IplImage frame;
    IplImage grayimg = cvCreateImage(cvSize(640, 480), IPL_DEPTH_8U, 1);

    //create window
    cvNamedWindow("Video", CV_WINDOW_AUTOSIZE);

    //infinity loop
    for (; ; ) {
      frame = cvQueryFrame(capture);
      if (frame == null) {
        // if no file
        System.out.println("ERROR: NO Video File");
        break;
      }

      cvCvtColor(frame, grayimg, CV_BGR2GRAY);

      //play original color
      cvShowImage("Video", frame);

      //play greyscale
      //cvShowImage("Video", grayimg);

      char c = (char) cvWaitKey(30);
      // 'Esc' button break the loop
      if (c == 27) break;
    }

    //clean memory
    cvReleaseCapture(capture);
    cvDestroyWindow("Video");
  }

}
