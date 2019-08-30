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
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

import static com.constants.Constants.imgPath;
import static org.opencv.imgproc.Imgproc.*;

import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;

import java.io.File;
import java.util.ArrayList;
import java.util.List;


public class LicensePlateRecognizer {

  private String licenseNumber = "";
  private Mat kernel = new Mat(new Size(3, 3), CvType.CV_8U, new Scalar(255));
  private Mat sourceORG;
  private Mat source = new Mat();
  private Mat gray = new Mat();
  private Mat topHat = new Mat();
  private Mat blackHat = new Mat();
  private Mat grayPlusTopHat = new Mat();
  private Mat grayPlusTopHatMinusBlackHat = new Mat();
  private Mat blur = new Mat();
  private Mat threshold = new Mat();
  private Scalar green = new Scalar(0, 255, 0, 255);
  private Scalar red = new Scalar(0, 0, 255, 255);


  // Searching license plate on image and recognize it
  public String findLicensePlate(String imagePath, int thresh) {
    System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
    sourceORG = Imgcodecs.imread(imagePath);
    sourceORG.copyTo(source);
    ///////////////////////////// FILTERS START ///////////////////////////////////////
    Imgproc.cvtColor(source, gray, Imgproc.COLOR_RGB2GRAY);
    Imgproc.morphologyEx(gray, topHat, Imgproc.MORPH_TOPHAT, kernel);
    Imgproc.morphologyEx(gray, blackHat, Imgproc.MORPH_BLACKHAT, kernel);
    Core.add(gray, topHat, grayPlusTopHat);
    Core.subtract(grayPlusTopHat, blackHat, grayPlusTopHatMinusBlackHat);
    Imgproc.GaussianBlur(grayPlusTopHatMinusBlackHat, blur, new Size(5, 5), 1);
    //threshold
    Imgproc.threshold(blur, threshold, thresh, 255, Imgproc.THRESH_BINARY_INV);
//    Imgproc.threshold(blur, threshold, 200, 255, Imgproc.THRESH_BINARY_INV);
    ///////////////////////////// FILTERS END ///////////////////////////////////////


    ///////////////////////////// CONTOURS START ///////////////////////////////////////
    List<MatOfPoint> contours = new ArrayList<>();
    Mat markers = Mat.zeros(threshold.size(), CvType.CV_32S);
    Imgproc.findContours(threshold, contours, new Mat(), RETR_TREE, Imgproc.CHAIN_APPROX_SIMPLE);
    Mat licensePlate = new Mat();
    Mat countryPlate = new Mat();
    Mat countryPlate2 = new Mat();

    //filter contours from filtered image
    int i = 0;
    for (MatOfPoint contour : contours) {
      MatOfPoint2f pointsArea = new MatOfPoint2f(contour.toArray());
      RotatedRect rectRot = Imgproc.minAreaRect(pointsArea);
      // validate contour by area
      if ((rectRot.size.area() > 2000) && (rectRot.size.area() < 7000)) {
        Point rotated_rect_points[] = new Point[4];
        rectRot.points(rotated_rect_points);
        Rect rect = Imgproc.boundingRect(new MatOfPoint(rotated_rect_points));
        //validate contour by side ratio
        if ((rect.width > 3 * rect.height) && (rect.width < 6 * rect.height)) {
          //draw green rect around valid contour
          Imgproc.rectangle(source, rect.tl(), rect.br(), new Scalar(Math.random() * 255, Math.random() * 255, Math.random() * 255, 0), 2);
          licensePlate = new Mat(sourceORG, rect);

          //recognize licence plate
          if (licensePlate != null && !licensePlate.empty()) {
            licensePlate = filterPlateImage(licensePlate);
            Imgcodecs.imwrite(imgPath + "result\\licensePlate" + i + ".jpg", licensePlate);
            String tempText = recognizeText(imgPath + "result\\licensePlate" + i + ".jpg");
            if (tempText.length() > licenseNumber.length()) {
              licenseNumber = tempText;
            }
          }
        }
      }
      i++;
    }

    System.out.println(licenseNumber);

    //save files
    Imgcodecs.imwrite(imgPath + "result\\markers.jpg", markers);
    Imgcodecs.imwrite(imgPath + "result\\source.jpg", source);
    Imgcodecs.imwrite(imgPath + "result\\sourceORG.jpg", sourceORG);
    Imgcodecs.imwrite(imgPath + "result\\gray.jpg", gray);
    Imgcodecs.imwrite(imgPath + "result\\topHat.jpg", topHat);
    Imgcodecs.imwrite(imgPath + "result\\blackHat.jpg", blackHat);
    Imgcodecs.imwrite(imgPath + "result\\grayPlusTopHat.jpg", grayPlusTopHat);
    Imgcodecs.imwrite(imgPath + "result\\grayPlusTopHatMinusBlackHat.jpg", grayPlusTopHatMinusBlackHat);
    Imgcodecs.imwrite(imgPath + "result\\blur.jpg", blur);
    Imgcodecs.imwrite(imgPath + "result\\threshold.jpg", threshold);
    Imgcodecs.imwrite(imgPath + "result\\countryPlate.jpg", countryPlate);
    Imgcodecs.imwrite(imgPath + "result\\countryPlate2.jpg", countryPlate2);

    if (licenseNumber.equals(null) || licenseNumber == null || licenseNumber.equals("")) {
      return "not found";
    }
    return licenseNumber;
  }


  private Mat filterPlateImage(Mat source) {
    //gray scale
    Mat gray = new Mat();
    Mat topHat = new Mat();
    Mat blackHat = new Mat();
    Mat grayPlusTopHat = new Mat();
    Mat grayPlusTopHatMinusBlackHat = new Mat();
    Mat blur = new Mat();
    Mat threshold = new Mat();
    Mat kernel = new Mat(new Size(3, 3), CvType.CV_8U, new Scalar(255));

    Mat temp = new Mat();

    Imgproc.cvtColor(source, gray, Imgproc.COLOR_RGB2GRAY);
//    Imgproc.cvtColor(temp, temp, MORPH_CLOSE);
    //topHat
    Imgproc.morphologyEx(gray, topHat, Imgproc.MORPH_TOPHAT, kernel);
    //blackHat
    Imgproc.morphologyEx(gray, blackHat, Imgproc.MORPH_BLACKHAT, kernel);
    //grays cale + topHat - blackHat
    Core.add(gray, topHat, grayPlusTopHat);
    Core.subtract(grayPlusTopHat, blackHat, grayPlusTopHatMinusBlackHat);


    //blur + kernel of 5
    Imgproc.GaussianBlur(grayPlusTopHatMinusBlackHat, blur, new Size(5, 5), 1);
    Imgproc.cvtColor(grayPlusTopHatMinusBlackHat, temp, MORPH_CLOSE);
    //threshold
//    Imgproc.threshold(blur, threshold, 100, 255, Imgproc.THRESH_BINARY_INV);

    return temp;
  }


  // Recognize text with Tesseract-OCR from image file
  public String recognizeText(String imgPath) {
    Tesseract tesseract = new Tesseract();
    String TESS_DATA = Constants.projectPath + "\\lib\\tesseract-OCR\\";
    tesseract.setDatapath(TESS_DATA);
    String result = "";
    try {
      result = tesseract.doOCR(new File(imgPath));
    } catch (TesseractException e) {
      e.printStackTrace();
    }

    String filteredResult = result.replaceAll("[^A-Z0-9]", "");
    return filteredResult;
  }
}