package opencv_experiment;

import com.constants.Constants;
import org.bytedeco.javacpp.opencv_core.IplImage;
import org.bytedeco.javacv.FrameRecorder;
import org.bytedeco.javacv.OpenCVFrameRecorder;
import static org.bytedeco.javacpp.opencv_highgui.*;

//Record video from webbcam, stop with 'q'
public class VideoRecording {

  /**
   * @param args
   * @throws Exception
   */
  public static void main(String[] args) throws Exception {

    CvCapture capture1 = cvCreateCameraCapture(CV_CAP_ANY);
    cvSetCaptureProperty(capture1,CV_CAP_PROP_FRAME_WIDTH,640);
    cvSetCaptureProperty(capture1,CV_CAP_PROP_FRAME_HEIGHT,480);

    cvNamedWindow("LiveVid",CV_WINDOW_AUTOSIZE);

    FrameRecorder recorder1 = new OpenCVFrameRecorder(Constants.videoPath+"record001.avi",640,480);
//    recorder1.setVideoOption(CV_FOURCC('I','Y','U','V'));
//    recorder1.setVideoCodec(CV_FOURCC('M','J','P','G'));
//    recorder1.setCodecID(CV_FOURCC('M','J','P','G'));
//    recorder1.setVideoCodec(CV_FOURCC('D','I','V','X'));
//    recorder1.setVideoCodec(CV_FOURCC('M','P','4','V'));
    recorder1.setFormat("mp4");

    recorder1.setFrameRate(15);
    recorder1.setPixelFormat(1);
    recorder1.start();

    IplImage img1;

    for(;;){

      img1 = cvQueryFrame(capture1);

      if(img1==null) break;

      cvShowImage("LiveVid",img1);
      recorder1.record(img1);

      char c = (char) cvWaitKey(15);
      if(c == 'q') break;

    }

    recorder1.stop();
    cvDestroyWindow("LiveVid");
    cvReleaseCapture(capture1);


  }

}
