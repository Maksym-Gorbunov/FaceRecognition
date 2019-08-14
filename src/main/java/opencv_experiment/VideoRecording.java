package opencv_experiment;
import com.constants.Constants;
import org.bytedeco.javacpp.opencv_core.IplImage;
import org.bytedeco.javacv.FrameRecorder;
import org.bytedeco.javacv.OpenCVFrameRecorder;
import org.opencv.core.Core;
import static org.bytedeco.javacpp.opencv_highgui.*;
import org.opencv.videoio.VideoWriter;

//Record video from webbcam, stop with 'q'
public class VideoRecording {
  static int width = 640;
  static int height = 480;

  public static void main(String[] args) throws Exception {
    System.loadLibrary(Core.NATIVE_LIBRARY_NAME);

    // choose any webbcam or default as usual
    CvCapture capture1 = cvCreateCameraCapture(CV_CAP_ANY);

    cvSetCaptureProperty(capture1,CV_CAP_PROP_FRAME_WIDTH, width);
    cvSetCaptureProperty(capture1,CV_CAP_PROP_FRAME_HEIGHT, height);

    cvNamedWindow("LiveVid",CV_WINDOW_AUTOSIZE);

    FrameRecorder recorder1 = new OpenCVFrameRecorder(Constants.videoPath+"record001.avi",width,height);

    //set video compressor
    int fourcc = VideoWriter.fourcc('I', 'Y', 'U', 'V');
    recorder1.setVideoCodec(fourcc);
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

    // clear memory
    cvDestroyWindow("LiveVid");
    cvReleaseCapture(capture1);


  }

}
