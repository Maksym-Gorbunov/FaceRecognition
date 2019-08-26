package com.pages.Page7;

import com.constants.Constants;
import org.opencv.core.*;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.RotatedRect;
import org.opencv.core.Scalar;
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


    img = Imgcodecs.imread(Constants.imgPath + "car6.jpg");
    gray = new Mat();
    draw = new Mat();
    edges = new Mat();
    Mat filtered = new Mat();


    // blue filter
    Scalar minc = new Scalar(95, 150, 75, 0);
    Scalar maxc = new Scalar(145, 255, 255, 0);

    Imgproc.cvtColor(img, gray, Imgproc.COLOR_BGR2GRAY);


    int threshold = 150;
    Imgproc.Canny(gray, edges, threshold, threshold * 3);
    edges.convertTo(draw, CvType.CV_8U);

    List<MatOfPoint> contours = new ArrayList<MatOfPoint>();
    List<MatOfPoint> contours2 = new ArrayList<MatOfPoint>();
    Mat hierarchy = new Mat();

    Imgproc.findContours(draw, contours, hierarchy, Imgproc.RETR_EXTERNAL, Imgproc.CHAIN_APPROX_SIMPLE);


    int i = 1;
    Scalar green = new Scalar(81, 190, 0);


    if (contours != null) {
      contours.stream().forEach((c) -> {
                double areaC;
                areaC = contourArea(c, false);
                if (areaC > 500) {
                  System.out.println("area: " + c.size());
                  contours2.add(c);

                  RotatedRect rotatedRect = Imgproc.minAreaRect(new MatOfPoint2f(c.toArray()));
                  drawRotatedRect(draw, rotatedRect, green, 4);

                }
              }
      );
      drawContours(edges, contours2, -1, new Scalar(200, 0, 0, 0), CV_FILLED);
      System.out.println(contours.size());
      System.out.println(contours2.size());
//
//
//      if (areaC < 300) {
//        drawText(new Point(20,20), "DSDSD");
//
////        cvDrawContours(edges, contours, CV_RGB(0, 0, 0), CV_RGB(0, 0, 0),
////                0, CV_FILLED, 8, cvPoint(0, 0));
//      }

//      contours = contours. .h_next();
    }

//
//    MatOfPoint2f matOfPoint2f = new MatOfPoint2f();
//    MatOfPoint2f approxCurve = new MatOfPoint2f();
//
//    for (int idx = 0; idx >= 0; idx = (int) hierarchy.get(0, idx)[0]) {
//      MatOfPoint contour = contours.get(idx);
//      Rect rect = Imgproc.boundingRect(contour);
//      double contourArea = contourArea(contour);
//      matOfPoint2f.fromList(contour.toList());
//      Imgproc.approxPolyDP(matOfPoint2f, approxCurve, Imgproc.arcLength(matOfPoint2f, true) * 0.02, true);
//      long total = approxCurve.total();
//      if (total == 3) { // is triangle
//        // do things for triangle
//      }
//      if (total >= 4 && total <= 6) {
//        List<Double> cos = new ArrayList<>();
//        Point[] points = approxCurve.toArray();
//        for (int j = 2; j < total + 1; j++) {
//          cos.add(angle(points[(int) (j % total)], points[j - 2], points[j - 1]));
//        }
//        Collections.sort(cos);
//        Double minCos = cos.get(0);
//        Double maxCos = cos.get(cos.size() - 1);
//        boolean isRect = total == 4 && minCos >= -0.1 && maxCos <= 0.3;
//        boolean isPolygon = (total == 5 && minCos >= -0.34 && maxCos <= -0.27) || (total == 6 && minCos >= -0.55 && maxCos <= -0.45);
//        if (isRect) {
//          double ratio = Math.abs(1 - (double) rect.width / rect.height);
//          drawText(rect.tl(), ratio <= 0.02 ? "SQU" : "RECT");
//        }
//        if (isPolygon) {
//          drawText(rect.tl(), "Polygon");
//        }
//      }
//    }


//    Imgproc.Canny(gray, edge, 50, 150, 3, false);
    if (Imgcodecs.imwrite(Constants.imgPath + "result\\gray.png", gray)) {
      System.out.println("..gray.png");
    }
    if (Imgcodecs.imwrite(Constants.imgPath + "result\\draw.png", draw)) {
      System.out.println("..draw.png");
    }
    if (Imgcodecs.imwrite(Constants.imgPath + "result\\edges.png", edges)) {
      System.out.println("..edges.png");
    }


//    IplImage i = draw;

    //
//    if(Imgcodecs.imwrite(Constants.imgPath+"result\\edge.png", draw)){
//      System.out.println("Edge saved to edge.png");
//    }


  }


//  public BufferedImage Mat2BufferedImage(Mat m) {
//    // Fastest code
//    // output can be assigned either to a BufferedImage or to an Image
//
//    int type = BufferedImage.TYPE_BYTE_GRAY;
//    if ( m.channels() > 1 ) {
//      type = BufferedImage.TYPE_3BYTE_BGR;
//    }
//    int bufferSize = m.channels()*m.cols()*m.rows();
//    byte [] b = new byte[bufferSize];
//    m.get(0,0,b); // get all the pixels
//    BufferedImage image = new BufferedImage(m.cols(),m.rows(), type);
//    final byte[] targetPixels = ((DataBufferByte) image.getRaster().getDataBuffer()).getData();
//    System.arraycopy(b, 0, targetPixels, 0, b.length);
//    return image;
//  }


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
