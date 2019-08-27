package com.pages.Page7;

import com.constants.Constants;
import net.sourceforge.tess4j.Tesseract;
import net.sourceforge.tess4j.TesseractException;
import org.opencv.core.*;
import org.opencv.core.Point;
import org.opencv.core.RotatedRect;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
import static org.bytedeco.javacpp.opencv_core.*;
import static org.opencv.imgproc.Imgproc.drawContours;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


public class AAA {

  public static void main(String[] args) {
    System.loadLibrary(Core.NATIVE_LIBRARY_NAME);


    Mat kernel = new Mat(new Size(3, 3), CvType.CV_8U, new Scalar(255));
    Mat source = Imgcodecs.imread(Constants.imgPath + "car6.jpg");
    Mat gray = new Mat();
    Mat grayOpen = new Mat();
    Mat grayClose = new Mat();
    Mat topHat = new Mat();
    Mat blackHat = new Mat();
    Mat grayPlusTopHat = new Mat();
    Mat grayPlusTopHatMinusBlackHat = new Mat();
    Mat blur = new Mat();
    Mat blurPlus5 = new Mat();
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

    //blur, blur plus kernel of 5
    Imgproc.morphologyEx(grayPlusTopHatMinusBlackHat, blur, Imgproc.CV_BLUR, kernel);
    Core.add(blur, Scalar.all(5), blurPlus5);


    //threshold, javadoc: threshold(src, dst, thresh, maxval, type)
    Imgproc.threshold(blurPlus5, threshold, 200, 255,Imgproc.THRESH_BINARY_INV);
//    Imgproc.threshold(blurPlus5, threshold, 19, Imgproc.ADAPTIVE_THRESH_GAUSSIAN_C,Imgproc.THRESH_BINARY_INV);






    //contours
    List<MatOfPoint> contours = new ArrayList<MatOfPoint>();
    Mat hierarchy = new Mat();

    Imgproc.findContours(threshold, contours, hierarchy, Imgproc.RETR_EXTERNAL, Imgproc.CHAIN_APPROX_SIMPLE);
    if (contours != null) {
      drawContours(source, contours, -1, new Scalar(0, 0, 255, 0), CV_FILLED);
    }



    Imgcodecs.imwrite(Constants.imgPath + "result\\source.jpg", source);
    Imgcodecs.imwrite(Constants.imgPath + "result\\gray.jpg", gray);
    Imgcodecs.imwrite(Constants.imgPath + "result\\grayOpen.jpg", grayOpen);
    Imgcodecs.imwrite(Constants.imgPath + "result\\grayClose.jpg", grayClose);
    Imgcodecs.imwrite(Constants.imgPath + "result\\topHat.jpg", topHat);
    Imgcodecs.imwrite(Constants.imgPath + "result\\blackHat.jpg", blackHat);
    Imgcodecs.imwrite(Constants.imgPath + "result\\grayPlusTopHat.jpg", grayPlusTopHat);
    Imgcodecs.imwrite(Constants.imgPath + "result\\grayPlusTopHatMinusBlackHat.jpg", grayPlusTopHatMinusBlackHat);
    Imgcodecs.imwrite(Constants.imgPath + "result\\blur.jpg", blur);
    Imgcodecs.imwrite(Constants.imgPath + "result\\blurPlus5.jpg", blurPlus5);
    Imgcodecs.imwrite(Constants.imgPath + "result\\threshold.jpg", threshold);



    String licencenumbers = recognizeText(Constants.imgPath + "result\\threshold.jpg");

    System.out.println("Licencenumbers: "+licencenumbers);





































//    cvCvtColor(source, gray, CV_BGR2GRAY);

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


  public static String recognizeText(String imgPath){
    Tesseract tesseract = new Tesseract();
    String TESS_DATA = Constants.projectPath+"\\lib\\tesseract-OCR\\";
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

}