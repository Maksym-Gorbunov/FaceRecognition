package com.pages.Page7;


import com.constants.Constants;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class FFF {
  public static void main(String[] args) {

    System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
    LicensePlateRecognizer lpr = new LicensePlateRecognizer();
    Mat colors = Imgcodecs.imread(Constants.imgPath+"cars\\4.jpg");
    lpr.test(colors);


    Mat source = Imgcodecs.imread(Constants.imgPath+"cars\\regnums\\COS799.jpg");
    Mat rotMat = new Mat(2, 3, CvType.CV_32FC1);
    Mat destination = new Mat(source.rows(), source.cols(), source.type());
    Point center = new Point(destination.cols() / 2, destination.rows() / 2);
    rotMat = Imgproc.getRotationMatrix2D(center, 30, 1);
    Imgproc.warpAffine(source, destination, rotMat, destination.size());
    Imgcodecs.imwrite(Constants.imgPath+"result\\rotated.jpg", destination);





  }
}

