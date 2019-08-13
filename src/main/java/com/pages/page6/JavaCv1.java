package com.pages.page6;

import com.googlecode.javacpp.Loader;
import com.googlecode.javacv.*;
import com.googlecode.javacv.cpp.*;
import com.googlecode.javacv.cpp.opencv_core.CvPoint;
import com.googlecode.javacv.cpp.opencv_core.CvScalar;
import com.googlecode.javacv.cpp.opencv_core.CvSeq;
import com.googlecode.javacv.cpp.opencv_core.IplImage;
import com.googlecode.javacv.cpp.opencv_highgui.CvCapture;



import static com.googlecode.javacv.cpp.opencv_core.*;
import static com.googlecode.javacv.cpp.opencv_imgproc.*;
import static com.googlecode.javacv.cpp.opencv_calib3d.*;
import static com.googlecode.javacv.cpp.opencv_objdetect.*;
import static com.googlecode.javacv.cpp.opencv_highgui.*;

public class JavaCv1 {

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
