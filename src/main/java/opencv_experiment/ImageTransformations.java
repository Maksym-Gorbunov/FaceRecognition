package opencv_experiment;

import com.constants.Constants;
import org.bytedeco.javacpp.opencv_core.IplImage;
import static org.bytedeco.javacpp.opencv_core.*;
import static org.bytedeco.javacpp.opencv_imgproc.*;
import static org.bytedeco.javacpp.opencv_highgui.*;

public class ImageTransformations {

	/**
	 * @param args
	 */
	public static void main(String[] args) {

		IplImage img = cvLoadImage(Constants.imgPath+"family.png");
		
		IplImage hsvimg = cvCreateImage(cvGetSize(img),IPL_DEPTH_8U,3);
		IplImage grayimg = cvCreateImage(cvGetSize(img),IPL_DEPTH_8U,1);
		
		cvCvtColor(img,hsvimg,CV_BGR2HSV);
		cvCvtColor(img,grayimg,CV_BGR2GRAY);
		
		cvShowImage(Constants.imgPath+"Original",img);
		cvShowImage(Constants.imgPath+"HSV",hsvimg);
		cvShowImage(Constants.imgPath+"GRAY",grayimg);
		cvWaitKey();
		
		cvSaveImage(Constants.imgPath+"Original.jpg",img);
		cvSaveImage(Constants.imgPath+"HSV.jpg",hsvimg);
		cvSaveImage(Constants.imgPath+"GRAY.jpg",grayimg);

		// Clear memory
		cvReleaseImage(img);
		cvReleaseImage(hsvimg);
		cvReleaseImage(grayimg);
	}

}
