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
import static org.bytedeco.javacpp.opencv_core.*;
import static org.opencv.imgproc.Imgproc.*;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import java.io.File;
import java.util.ArrayList;
import java.util.List;


public class LicensePlateRecognition {

  public static void main(String[] args) {
    System.loadLibrary(Core.NATIVE_LIBRARY_NAME);

    Mat kernel = new Mat(new Size(3, 3), CvType.CV_8U, new Scalar(255));
    Mat sourceORG = Imgcodecs.imread(imgPath + "car6.jpg");
    Mat source = new Mat();
    sourceORG.copyTo(source);

    Mat gray = new Mat();
    Mat topHat = new Mat();
    Mat blackHat = new Mat();
    Mat grayPlusTopHat = new Mat();
    Mat grayPlusTopHatMinusBlackHat = new Mat();
    Mat blur = new Mat();
    Mat threshold = new Mat();


    ///////////////////////////// FILTERS START ///////////////////////////////////////
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
    ///////////////////////////// FILTERS END ///////////////////////////////////////


    ///////////////////////////// CONTOURS START ///////////////////////////////////////
    List<MatOfPoint> contours = new ArrayList<>();
    Mat markers = Mat.zeros(threshold.size(), CvType.CV_32S);
    Imgproc.findContours(threshold, contours, new Mat(), RETR_TREE, Imgproc.CHAIN_APPROX_SIMPLE);
    Mat licensePlate = new Mat();
    Mat countryPlate = new Mat();

    // find appropriate bounding rectangles
    int i = 0;
    for (MatOfPoint contour : contours) {
      MatOfPoint2f areaPoints = new MatOfPoint2f(contour.toArray());
      RotatedRect boundingRect = Imgproc.minAreaRect(areaPoints);
      double rectangleArea = boundingRect.size.area();
      Scalar green = new Scalar(0, 255, 0, 255);
      Scalar red = new Scalar(0, 0, 255, 255);

      // validate contour rectangle area and sides attitude
      if ((rectangleArea > 1000) && (rectangleArea < 7000)
              && (boundingRect.size.width > boundingRect.size.height * 2)
              && (boundingRect.size.width < boundingRect.size.height * 6)) {
        Point rotated_rect_points[] = new Point[4];
        boundingRect.points(rotated_rect_points);
        Rect rectLicenceNumbers = Imgproc.boundingRect(new MatOfPoint(rotated_rect_points));

        // test horizontal ROI orientation
        if (rectLicenceNumbers.width > rectLicenceNumbers.height) {
          //draw green rectangle around valid contour on original image
          Imgproc.rectangle(source, rectLicenceNumbers.tl(), rectLicenceNumbers.br(), green, 1);

          //create separate image with licence plate from mask
//          mask_image = new Mat(source.size(), CV_8U, new Scalar(0, 0, 0, 0));
//          drawContours(mask_image, contours, i, new Scalar(255), CV_FILLED);
          licensePlate = new Mat(sourceORG, rectLicenceNumbers);



          ///////////////////////  COUNTRY RECTANGLE  //////////////////////////
          Point rotatedRectLeftPoints[] = new Point[4];

          double width = rectLicenceNumbers.size().height * 0.4;
          Point leftTop = new Point(rectLicenceNumbers.tl().x - width, rectLicenceNumbers.tl().y);
          Point bottomRight = new Point(rectLicenceNumbers.br().x- rectLicenceNumbers.size().width, rectLicenceNumbers.br().y);


          Rect rectLEFT = new Rect(leftTop, bottomRight);
          //draw red rect around country plate zone
          Imgproc.rectangle(source, rectLEFT.tl(), rectLEFT.br(), red, 1);
          countryPlate = new Mat(sourceORG, rectLEFT);




        }
      }
      i++;
    }



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
    Imgcodecs.imwrite(imgPath + "result\\licensePlate.jpg", licensePlate);
    Imgcodecs.imwrite(imgPath + "result\\countryPlate.jpg", countryPlate);



    String licencenumbersText = recognizeText(imgPath + "result\\licensePlate.jpg");
    System.out.println("License numbers: " + licencenumbersText);

    String countryText = recognizeText(imgPath + "result\\countryPlate.jpg");
    System.out.println("Country: " + countryText);


    System.out.println("...done");
  }






  // Recognize text with Tesseract-OCR from image file
  public static String recognizeText(String imgPath) {
    Tesseract tesseract = new Tesseract();
    String TESS_DATA = Constants.projectPath + "\\lib\\tesseract-OCR\\";
    tesseract.setDatapath(TESS_DATA);
    String result = "";
    try {
      result = tesseract.doOCR(new File(imgPath));
    } catch (TesseractException e) {
      e.printStackTrace();
    }
    return result;
  }
}