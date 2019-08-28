package com.pages.Page7;

import com.constants.Constants;
import net.sourceforge.tess4j.Tesseract;
import net.sourceforge.tess4j.TesseractException;
import org.opencv.core.*;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.RotatedRect;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.highgui.HighGui;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

import static com.constants.Constants.imgPath;
import static org.bytedeco.javacpp.opencv_core.*;
import static org.bytedeco.javacpp.opencv_imgproc.cvContourArea;
import static org.opencv.highgui.HighGui.*;
import static org.opencv.imgproc.Imgproc.*;

import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;


public class AAA {

  public static void main(String[] args) {
    System.loadLibrary(Core.NATIVE_LIBRARY_NAME);


    Mat kernel = new Mat(new Size(3, 3), CvType.CV_8U, new Scalar(255));
    Mat source = Imgcodecs.imread(imgPath + "car6.jpg");
    Mat gray = new Mat();
    Mat grayOpen = new Mat();
    Mat grayClose = new Mat();
    Mat topHat = new Mat();
    Mat blackHat = new Mat();
    Mat grayPlusTopHat = new Mat();
    Mat grayPlusTopHatMinusBlackHat = new Mat();
    Mat blur = new Mat();
    Mat threshold = new Mat();
    Mat contoursImg = new Mat();

    //gray scale
    Imgproc.cvtColor(source, gray, Imgproc.COLOR_RGB2GRAY);

    //topHat
    Imgproc.morphologyEx(gray, topHat, Imgproc.MORPH_TOPHAT, kernel);

    //blackHat
    Imgproc.morphologyEx(gray, blackHat, Imgproc.MORPH_BLACKHAT, kernel);

    //grays cale + topHat - blackHat
    Core.add(gray, topHat, grayPlusTopHat);
    Core.subtract(grayPlusTopHat, blackHat, grayPlusTopHatMinusBlackHat);

    //blur + kernel of 5
    Imgproc.GaussianBlur(grayPlusTopHatMinusBlackHat, blur, new Size(5, 5), 1);

    //threshold
    Imgproc.threshold(blur, threshold, 200, 255, Imgproc.THRESH_BINARY_INV);



  ////////////////////////////////////////////////////////////////////////////

//    Mat a = new Mat();
    //javadoc: distanceTransform(src, dst, distanceType, maskSize, dstType)
//    distanceTransform(threshold, a, CV_DIST_L2,3);








    // Find total markers
    List<MatOfPoint> contours = new ArrayList<>();
    Mat hierarchy = new Mat();
    Mat markers = Mat.zeros(threshold.size(), CvType.CV_32S);
    Imgproc.findContours(threshold,contours,new Mat(), RETR_TREE,Imgproc.CHAIN_APPROX_SIMPLE);


    // find appropriate bounding rectangles
    for (MatOfPoint contour : contours) {
      MatOfPoint2f areaPoints = new MatOfPoint2f(contour.toArray());
      RotatedRect boundingRect = Imgproc.minAreaRect(areaPoints);

      double rectangleArea = boundingRect.size.area();
      Scalar green = new Scalar(0, 255, 0, 255);

      // test min ROI area in pixels
      if ((rectangleArea > 3000) && (rectangleArea < 5000) && (boundingRect.size.width > boundingRect.size.height*2)) {
        Point rotated_rect_points[] = new Point[4];
        boundingRect.points(rotated_rect_points);

        Rect rect = Imgproc.boundingRect(new MatOfPoint(rotated_rect_points));

        // test horizontal ROI orientation
        if (rect.width > rect.height) {
          Imgproc.rectangle(source, rect.tl(), rect.br(), green, 3);
        }
      }
    }








    ////////////////////////////////////////////////////////
    /*
    for(int i=0; i<contours.size(); i++){
      // manual set index to 17
      double areaC = contourArea(contours.get(17), false);
      System.out.println(areaC);
      Imgproc.drawContours(source, contours, 17, new Scalar(0,0,255,0),2);
    }
    */
    ///////////////////////////////////////////////////////////












//    cvtColor(threshold, threshold, Imgproc.COLOR_RGBA2RGB, 0);
//
//
//
//    Imgproc.watershed(threshold, markers);
//
//    System.out.println(markers.rows());
//    System.out.println(markers.cols());



//    drawContours(threshold, (List<MatOfPoint>) markers, -1, new Scalar(200, 0, 0, 0), CV_FILLED);






    Imgcodecs.imwrite(imgPath + "result\\markers.jpg", markers);







    //CONTOURS
    /*
    Mat cont = new Mat();
    cont = source;
    cont.setTo(new Scalar(0,0,0,0));

    List<MatOfPoint> contours = new ArrayList<MatOfPoint>();
    List<MatOfPoint> contours2 = new ArrayList<MatOfPoint>();
    Mat hierarchy = new Mat();
    Imgproc.findContours(cannyOutput, contours, hierarchy, Imgproc.RETR_TREE, Imgproc.CHAIN_APPROX_SIMPLE);
    Scalar green = new Scalar(81, 190, 0);
    if (contours != null) {
      contours.stream().forEach((c) -> {
                double areaC = contourArea(c, false);
                if (areaC>50 && areaC<1000) {
                  System.out.println("area: " + c.size());
                  contours2.add(c);
                }
              }
      );
      drawContours(cont, contours2, -1, new Scalar(200, 0, 0, 0), CV_FILLED);
    }
*/



    Imgcodecs.imwrite(imgPath + "result\\source.jpg", source);
    Imgcodecs.imwrite(imgPath + "result\\gray.jpg", gray);
    Imgcodecs.imwrite(imgPath + "result\\grayOpen.jpg", grayOpen);
    Imgcodecs.imwrite(imgPath + "result\\grayClose.jpg", grayClose);
    Imgcodecs.imwrite(imgPath + "result\\topHat.jpg", topHat);
    Imgcodecs.imwrite(imgPath + "result\\blackHat.jpg", blackHat);
    Imgcodecs.imwrite(imgPath + "result\\grayPlusTopHat.jpg", grayPlusTopHat);
    Imgcodecs.imwrite(imgPath + "result\\grayPlusTopHatMinusBlackHat.jpg", grayPlusTopHatMinusBlackHat);
    Imgcodecs.imwrite(imgPath + "result\\blur.jpg", blur);
    Imgcodecs.imwrite(imgPath + "result\\threshold.jpg", threshold);





//    String licencenumbers = recognizeText(imgPath + "result\\cont.jpg");
//    String licencenumbers = recognizeText(imgPath + "result\\source.jpg");

//    System.out.println("Licencenumbers: " + licencenumbers);


    System.out.println("...done");
  }


  public static String recognizeText(String imgPath) {
    Tesseract tesseract = new Tesseract();
    String TESS_DATA = Constants.projectPath + "\\lib\\tesseract-OCR\\";
    tesseract.setDatapath(TESS_DATA);
    String result = "";
    try {
      // Recognize text with OCR
      result = tesseract.doOCR(new File(imgPath));
    } catch (TesseractException e) {
      e.printStackTrace();
    }
    return result;
  }


//  public static String recognizeText(String imgPath) {
//    Tesseract tesseract = new Tesseract();
//    String TESS_DATA = Constants.projectPath + "\\lib\\tesseract-OCR\\";
//    tesseract.setDatapath(TESS_DATA);
//    String result = "";
//    try {
//      // Recognize text with OCR
//      result = tesseract.doOCR(new File(imgPath));
//    } catch (TesseractException e) {
//      e.printStackTrace();
//    }
//    return result;
//  }






}