package opencv_experiment;

import com.constants.Constants;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

public class ImageEdge {
  public static void main(String[] args) {
    System.loadLibrary(Core.NATIVE_LIBRARY_NAME);

    Mat img = Imgcodecs.imread(Constants.imgPath+"car3.png");
    Mat gray = new Mat();
    Mat draw = new Mat();
    Mat edge = new Mat();

    Imgproc.cvtColor(img, gray, Imgproc.COLOR_BGR2GRAY);
    Imgproc.Canny(gray, edge, 50, 150, 3, false);
    edge.convertTo(draw, CvType.CV_8U);

    if(Imgcodecs.imwrite(Constants.imgPath+"result\\gray.png", gray)){
      System.out.println("Gray saved to gray.png");
    }

    if(Imgcodecs.imwrite(Constants.imgPath+"result\\edge.png", draw)){
      System.out.println("Edge saved to edge.png");
    }


  }
}
