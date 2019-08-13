package opencv_experiment;
import org.bytedeco.javacpp.opencv_core.IplImage;
import static org.bytedeco.javacpp.opencv_core.*;
import static org.bytedeco.javacpp.opencv_imgproc.*;
import static org.bytedeco.javacpp.opencv_highgui.*;

public class ImageTransformations {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		
		IplImage img = cvLoadImage("EV.jpg");
		
		IplImage hsvimg = cvCreateImage(cvGetSize(img),IPL_DEPTH_8U,3);
		IplImage grayimg = cvCreateImage(cvGetSize(img),IPL_DEPTH_8U,1);
		
		cvCvtColor(img,hsvimg,CV_BGR2HSV);
		cvCvtColor(img,grayimg,CV_BGR2GRAY);
		
		cvShowImage("Original",img);
		cvShowImage("HSV",hsvimg);
		cvShowImage("GRAY",grayimg);
		cvWaitKey();
		
		cvSaveImage("Original.jpg",img);
		cvSaveImage("HSV.jpg",hsvimg);
		cvSaveImage("GRAY.jpg",grayimg);
		
		cvReleaseImage(img);
		cvReleaseImage(hsvimg);
		cvReleaseImage(grayimg);
		

	}

}
