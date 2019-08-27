package com.pages.Page7;

import com.constants.Constants;
import org.opencv.core.*;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.RotatedRect;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

import static org.bytedeco.javacpp.helper.opencv_core.CV_RGB;
import static org.bytedeco.javacpp.opencv_core.*;
import static org.bytedeco.javacpp.opencv_core.cvPoint;
import static org.bytedeco.javacpp.opencv_highgui.*;
import static org.bytedeco.javacpp.opencv_imgproc.*;
import static org.opencv.imgproc.Imgproc.contourArea;
import static org.opencv.imgproc.Imgproc.drawContours;

import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


public class AAA {

  private static Mat img, gray, draw, edges;


  public static void main(String[] args) {
    System.loadLibrary(Core.NATIVE_LIBRARY_NAME);


    Mat kernel = new Mat(new Size(3, 3), CvType.CV_8U, new Scalar(255));
    Mat source = Imgcodecs.imread(Constants.imgPath + "car6.jpg");
    Mat temp = new Mat();
    Mat gray = new Mat();
    Mat topHat = new Mat();
    Mat blackHat = new Mat();
    Mat grayPlusTopHat = new Mat();
    Mat grayPlusTopHatMinusBlackHat = new Mat();

    //gray scale
    Imgproc.cvtColor(source, gray, Imgproc.COLOR_RGB2GRAY);
    //topHat
    Imgproc.morphologyEx(gray, topHat, Imgproc.MORPH_TOPHAT, kernel);
    //blackHat
    Imgproc.morphologyEx(gray, blackHat, Imgproc.MORPH_BLACKHAT, kernel);

    //grays cale + topHat - blackHat
    Core.add(gray, topHat, grayPlusTopHat);
    Core.subtract(grayPlusTopHat, blackHat, grayPlusTopHatMinusBlackHat);




    Imgcodecs.imwrite(Constants.imgPath + "result\\gray.jpg", gray);
    Imgcodecs.imwrite(Constants.imgPath + "result\\topHat.jpg", topHat);
    Imgcodecs.imwrite(Constants.imgPath + "result\\blackHat.jpg", blackHat);
    Imgcodecs.imwrite(Constants.imgPath + "result\\grayPlusTopHat.jpg", grayPlusTopHat);
    Imgcodecs.imwrite(Constants.imgPath + "result\\grayPlusTopHatMinusBlackHat.jpg", grayPlusTopHatMinusBlackHat);











//    cvCvtColor(source, gray, CV_BGR2GRAY);

//    Imgproc.morphologyEx(source, temp, Imgproc.MORPH_OPEN, kernel);
//    Imgproc.morphologyEx(source, gray, Imgproc.COLOR_RGB2GRAY, kernel);
//    Imgproc.morphologyEx(temp, destination, Imgproc.MORPH_CLOSE, kernel);
//    Imgproc.morphologyEx(source, gray, Imgproc.MORPH_GRADIENT, kernel);


//    Imgcodecs.imwrite(Constants.imgPath + "result\\temp.jpg", temp);
//    Imgcodecs.imwrite(Constants.imgPath + "result\\destination1.jpg", destination);
//    Imgcodecs.imwrite(Constants.imgPath + "result\\temp.jpg", temp);
//    Imgcodecs.imwrite(Constants.imgPath + "result\\destination2.jpg", destination);










//    int threshold = 150;
//    Imgproc.Canny(gray, edges, threshold, threshold * 3);
//    edges.convertTo(draw, CvType.CV_8U);
//
//    List<MatOfPoint> contours = new ArrayList<MatOfPoint>();
//    List<MatOfPoint> contours2 = new ArrayList<MatOfPoint>();
//    Mat hierarchy = new Mat();
//
//    Imgproc.findContours(draw, contours, hierarchy, Imgproc.RETR_EXTERNAL, Imgproc.CHAIN_APPROX_SIMPLE);
//
//
//    Scalar green = new Scalar(81, 190, 0);
//
//
//    if (contours != null) {
//      contours.stream().forEach((c) -> {
//                double areaC;
//                areaC = contourArea(c, false);
//                if (areaC > 300) {
//                  System.out.println("area: " + c.size());
//                  contours2.add(c);
//
//
//                  RotatedRect rotatedRect = Imgproc.minAreaRect(new MatOfPoint2f(c.toArray()));
//                  drawRotatedRect(draw, rotatedRect, green, 4);
//
//                }
//              }
//      );
//      drawContours(edges, contours2, -1, new Scalar(200, 0, 0, 0), CV_FILLED);
//      System.out.println(contours.size());
//      System.out.println(contours2.size());
//    }


//    if (Imgcodecs.imwrite(Constants.imgPath + "result\\gray.png", gray)) {
//      System.out.println("..gray.png");
//    }
//    if (Imgcodecs.imwrite(Constants.imgPath + "result\\draw.png", draw)) {
//      System.out.println("..draw.png");
//    }
//    if (Imgcodecs.imwrite(Constants.imgPath + "result\\edges.png", edges)) {
//      System.out.println("..edges.png");
//    }

    System.out.println("...done");
  }

  private static double angle(Point pt1, Point pt2, Point pt0) {
    double dx1 = pt1.x - pt0.x;
    double dy1 = pt1.y - pt0.y;
    double dx2 = pt2.x - pt0.x;
    double dy2 = pt2.y - pt0.y;
    return (dx1 * dx2 + dy1 * dy2) / Math.sqrt((dx1 * dx1 + dy1 * dy1) * (dx2 * dx2 + dy2 * dy2) + 1e-10);
  }

  private static void drawText(Point ofs, String text) {
    Imgproc.putText(img, text, ofs, 1, 0.5, new Scalar(255, 255, 25));
  }


  public static void drawRotatedRect(Mat image, RotatedRect rotatedRect, Scalar color, int thickness) {
    Point[] vertices = new Point[4];
    rotatedRect.points(vertices);
    MatOfPoint points = new MatOfPoint(vertices);
    Imgproc.drawContours(image, Collections.singletonList(points), -1, color, thickness);
  }

}