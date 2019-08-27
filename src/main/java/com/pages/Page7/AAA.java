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

import static com.constants.Constants.imgPath;
import static org.bytedeco.javacpp.opencv_core.*;
import static org.opencv.imgproc.Imgproc.contourArea;
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
    Mat source = Imgcodecs.imread(imgPath + "car6.jpg");
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
//    Imgproc.morphologyEx(grayPlusTopHatMinusBlackHat, blur, Imgproc.CV_BLUR, kernel);
    Imgproc.GaussianBlur(grayPlusTopHatMinusBlackHat, blur, new Size(5, 5), 1);
    Imgproc.GaussianBlur(grayPlusTopHatMinusBlackHat, blurPlus5, new Size(5, 5), 1);

    Core.add(blur, Scalar.all(5), blurPlus5);


    //threshold, javadoc: threshold(src, dst, thresh, maxval, type)
    Imgproc.threshold(blurPlus5, threshold, 200, 255, Imgproc.THRESH_BINARY_INV);
//    Imgproc.threshold(blurPlus5, threshold, 19, Imgproc.ADAPTIVE_THRESH_GAUSSIAN_C,Imgproc.THRESH_BINARY_INV);


    //

    Mat cannyOutput = threshold;
//    Imgproc.Canny(threshold, cannyOutput, threshold, threshold * 2);




    //CONTOURS
    List<MatOfPoint> contours = new ArrayList<MatOfPoint>();
    List<MatOfPoint> contours2 = new ArrayList<MatOfPoint>();
    Mat hierarchy = new Mat();
    Imgproc.findContours(cannyOutput, contours, hierarchy, Imgproc.RETR_TREE, Imgproc.CHAIN_APPROX_SIMPLE);
    Scalar green = new Scalar(81, 190, 0);
    if (contours != null) {
      contours.stream().forEach((c) -> {
                double areaC;
                areaC = contourArea(c, false);
                if (c.height() >15 && c.height()<40) {
                  System.out.println("area: " + c.size());
                  contours2.add(c);
                }
              }
      );
      drawContours(source, contours2, -1, new Scalar(200, 0, 0, 0), CV_FILLED);
    }

    Imgcodecs.imwrite(imgPath + "result\\source.jpg", source);
    Imgcodecs.imwrite(imgPath + "result\\gray.jpg", gray);
    Imgcodecs.imwrite(imgPath + "result\\grayOpen.jpg", grayOpen);
    Imgcodecs.imwrite(imgPath + "result\\grayClose.jpg", grayClose);
    Imgcodecs.imwrite(imgPath + "result\\topHat.jpg", topHat);
    Imgcodecs.imwrite(imgPath + "result\\blackHat.jpg", blackHat);
    Imgcodecs.imwrite(imgPath + "result\\grayPlusTopHat.jpg", grayPlusTopHat);
    Imgcodecs.imwrite(imgPath + "result\\grayPlusTopHatMinusBlackHat.jpg", grayPlusTopHatMinusBlackHat);
    Imgcodecs.imwrite(imgPath + "result\\blur.jpg", blur);
    Imgcodecs.imwrite(imgPath + "result\\blurPlus5.jpg", blurPlus5);
    Imgcodecs.imwrite(imgPath + "result\\threshold.jpg", threshold);
    Imgcodecs.imwrite(imgPath + "result\\cannyOtput.jpg", cannyOutput);


    String licencenumbers = recognizeText(imgPath + "result\\threshold.jpg");

    System.out.println("Licencenumbers: " + licencenumbers);


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