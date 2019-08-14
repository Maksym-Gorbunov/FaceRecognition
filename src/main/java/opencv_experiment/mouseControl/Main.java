package opencv_experiment.mouseControl;

import java.awt.AWTException;
import org.bytedeco.javacpp.Loader;
import org.bytedeco.javacpp.opencv_core;
import org.bytedeco.javacpp.opencv_core.IplImage;
import org.bytedeco.javacpp.opencv_imgproc;
import org.bytedeco.javacpp.opencv_imgproc.CvMoments;

import static org.bytedeco.javacpp.opencv_core.*;
import static org.bytedeco.javacpp.opencv_highgui.*;
import static org.bytedeco.javacpp.opencv_imgproc.*;
//import com.googlecode.javacpp.Loader;
//import com.googlecode.javacv.*;
//import com.googlecode.javacv.cpp.*;
//import com.googlecode.javacv.cpp.opencv_core.CvMemStorage;
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
public class Main {


	public static void main(String[] args) throws AWTException {
		// Red 160-180
		// Green 40-80
		// Blue 95-145

		IplImage img1,imgbinG, imgbinB;		//150.74.93  			//346.132.153
//		CvScalar Rminc = cvScalar(320,100,130,0), Rmaxc = cvScalar(350,160,170,0);
//		CvScalar Rminc = cvScalar(160,150,75,0), Rmaxc = cvScalar(180,255,255,0);
		CvScalar Bminc = cvScalar(95,150,75,0), Bmaxc = cvScalar(145,255,255,0);
		CvScalar Gminc = cvScalar(40,50,60,0), Gmaxc = cvScalar(80,255,255,0);

		//img1 = cvLoadImage("Pic.jpg");
		CvArr mask;

		IplImage imghsv;
		
		int w=320,h=240;
		imghsv = cvCreateImage(cvSize(w,h),8,3);
		imgbinG = cvCreateImage(cvSize(w,h),8,1);
		imgbinB = cvCreateImage(cvSize(w,h),8,1);
		IplImage imgC = cvCreateImage(cvSize(w,h),8,1);
		CvSeq contour1 = new CvSeq(), contour2=null;
		CvMemStorage storage = CvMemStorage.create();
		CvMoments moments = new CvMoments(Loader.sizeof(CvMoments.class));
		
		CvCapture capture1 = cvCreateCameraCapture(CV_CAP_ANY);
		cvSetCaptureProperty(capture1,CV_CAP_PROP_FRAME_WIDTH,w);
		cvSetCaptureProperty(capture1,CV_CAP_PROP_FRAME_HEIGHT,h);
		
		//int i=1;
		while(true)
		{
				
			img1 = cvQueryFrame(capture1);
			if(img1 == null){
				System.err.println("No Image");
				break;
				}
				
			imgbinB = ccmFilter.Filter(img1,imghsv,imgbinB,Bmaxc, Bminc, contour1, contour2, storage,moments,1,0);
//			imgbinB = ccmFilter.Filter(img1,imghsv,imgbinB,Rmaxc, Rminc, contour1, contour2, storage,moments,1,0);
			imgbinG = ccmFilter.Filter(img1,imghsv,imgbinG,Gmaxc, Gminc, contour1, contour2, storage,moments,0,1);
					
			cvOr(imgbinB,imgbinG,imgC,mask=null);
			cvShowImage("Combined",imgC);	
			cvShowImage("Original",img1);
			char c = (char)cvWaitKey(15);
			if(c=='q') break;
					
		}
		cvReleaseImage(imghsv);
		cvReleaseImage(imgbinG);
		cvReleaseImage(imgbinB);
		cvReleaseImage(imghsv);
		cvReleaseMemStorage(storage);
		cvReleaseCapture(capture1);
				
	}

}
