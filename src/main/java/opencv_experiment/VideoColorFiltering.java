package opencv_experiment;

import org.bytedeco.javacpp.opencv_core;
import org.bytedeco.javacpp.opencv_core.IplImage;
import static org.bytedeco.javacpp.opencv_core.*;
import static org.bytedeco.javacpp.opencv_highgui.*;
import static org.bytedeco.javacpp.opencv_imgproc.CV_BGR2HSV;
import static org.bytedeco.javacpp.opencv_imgproc.cvCvtColor;

public class VideoColorFiltering {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

    // Red 160-180
    // Green 40-80
    // Blue 95-145


		IplImage img1, imghsv, imgbin;

		imghsv = cvCreateImage(cvSize(640,480),8,3);
		imgbin = cvCreateImage(cvSize(640,480),8,1);

		CvCapture capture1 = cvCreateCameraCapture(CV_CAP_ANY);

		int i=1;
		
		while(i==1)
		{
				
			img1 = cvQueryFrame(capture1);
			
			if(img1 == null) break;
					
			cvCvtColor(img1,imghsv,CV_BGR2HSV);

			// blue filter
//			opencv_core.CvScalar minc = cvScalar(95,150,75,0);
//      opencv_core.CvScalar maxc = cvScalar(145,255,255,0);

//      //green filter - ikea kopp
//			opencv_core.CvScalar minc = cvScalar(60,55,140,0);
//			opencv_core.CvScalar maxc = cvScalar(140,100,170,0);

			//yellow filter - banan
			CvScalar minc = cvScalar(20,100,100,0);
			CvScalar maxc = cvScalar(60,160,160,0);


			cvInRangeS(imghsv,minc,maxc,imgbin);
		
			cvShowImage("color",img1);
			cvShowImage("Binary",imgbin);
			char c = (char)cvWaitKey(15);
			if(c == 'q') break; 
		
		}
		
		cvReleaseImage(imghsv);
		cvReleaseImage(imgbin);
		cvReleaseCapture(capture1);
	}

}
