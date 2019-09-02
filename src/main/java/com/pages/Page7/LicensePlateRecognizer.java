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

  private String licenseNumber;
  private Mat kernel;
  private Mat sourceORG;
  private Mat source;
  private Mat licensePlateImg;
  private Mat gray;
  private Mat topHat;
  private Mat blackHat;
  private Mat grayPlusTopHat;
  private Mat grayPlusTopHatMinusBlackHat;
  private Mat blur;
  private Mat threshold;
  private Scalar green = new Scalar(0, 255, 0, 255);
  private Scalar red = new Scalar(0, 0, 255, 255);
  private List<MatOfPoint> contours;
  private Mat licensePlate;
  //  private int thresh;
  private Mat[] filteredImages = new Mat[3];


  // Searching license plate on image and recognize it
  public String findLicensePlate(String imagePath, int thresh, int blurValue) {
    System.loadLibrary(Core.NATIVE_LIBRARY_NAME);

    licenseNumber = "";
    kernel = new Mat(new Size(3, 3), CvType.CV_8U, new Scalar(255));
    sourceORG = Imgcodecs.imread(imagePath);
    source = new Mat();
    sourceORG.copyTo(source);
    gray = new Mat();
    topHat = new Mat();
    blackHat = new Mat();
    grayPlusTopHat = new Mat();
    grayPlusTopHatMinusBlackHat = new Mat();
    blur = new Mat();
    threshold = new Mat();
    contours = new ArrayList<>();
    licensePlateImg = new Mat();




    ///////////////////////////// FILTERS START ///////////////////////////////////////
    Imgproc.cvtColor(source, gray, Imgproc.COLOR_RGB2GRAY);
    Imgproc.morphologyEx(gray, topHat, Imgproc.MORPH_TOPHAT, kernel);
    Imgproc.morphologyEx(gray, blackHat, Imgproc.MORPH_BLACKHAT, kernel);
    Core.add(gray, topHat, grayPlusTopHat);
    Core.subtract(grayPlusTopHat, blackHat, grayPlusTopHatMinusBlackHat);
    Imgproc.GaussianBlur(grayPlusTopHatMinusBlackHat, blur, new Size(blurValue, blurValue), 1);

    Imgproc.threshold(blur, threshold, thresh, 255, Imgproc.THRESH_BINARY_INV);
    Imgproc.findContours(threshold, contours, new Mat(), RETR_TREE, Imgproc.CHAIN_APPROX_SIMPLE);
    contors();
    if (threshold != null) {
      Mat t = new Mat();
      threshold.copyTo(t);
      filteredImages[0] = t;
    }
    if (source != null) {
      Mat s = new Mat();
      source.copyTo(s);
      filteredImages[1] = s;
    }
    if(licensePlateImg != null){
      Mat l = new Mat();
      licensePlateImg.copyTo(l);
      filteredImages[2] = l;
    }

    if (licenseNumber.equals(null) || licenseNumber == null || licenseNumber.equals("")) {
      return "not found";
    }
    return licenseNumber;
  }


  public Mat[] getFilteredImages() {
    return filteredImages;
  }






  private Mat filterPlateImage(Mat sourceImage) {
    Mat grayImage = new Mat();
    Mat topHatImage = new Mat();
    Mat blackHatImage = new Mat();
    Mat grayPlusTopHatImage = new Mat();
    Mat grayPlusTopHatMinusBlackHatImage = new Mat();
    Mat blurImage = new Mat();
    Mat kernelImage = new Mat(new Size(3, 3), CvType.CV_8U, new Scalar(255));
    Mat tempImage = new Mat();
    Imgproc.cvtColor(sourceImage, grayImage, Imgproc.COLOR_RGB2GRAY);
    Imgproc.morphologyEx(grayImage, topHatImage, Imgproc.MORPH_TOPHAT, kernelImage);
    Imgproc.morphologyEx(grayImage, blackHatImage, Imgproc.MORPH_BLACKHAT, kernelImage);
    Core.add(grayImage, topHatImage, grayPlusTopHatImage);
    Core.subtract(grayPlusTopHatImage, blackHatImage, grayPlusTopHatMinusBlackHatImage);
    Imgproc.GaussianBlur(grayPlusTopHatMinusBlackHatImage, blurImage, new Size(5, 5), 1);
    Imgproc.cvtColor(grayPlusTopHatMinusBlackHatImage, tempImage, MORPH_CLOSE);
    return tempImage;
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
    result = result.replaceAll("[^A-Z0-9]", "");
    return result;
  }


  private void contors() {
    int i = 0;
    for (MatOfPoint contour : contours) {
      MatOfPoint2f pointsArea = new MatOfPoint2f(contour.toArray());
      RotatedRect rectRot = Imgproc.minAreaRect(pointsArea);
      // validate contour by area
      if ((rectRot.size.area() > 500) && (rectRot.size.area() < 7000)) {
        Point rotated_rect_points[] = new Point[4];
        rectRot.points(rotated_rect_points);
        Rect rect = Imgproc.boundingRect(new MatOfPoint(rotated_rect_points));
        //validate contour by side ratio
        if ((rect.width > 3 * rect.height) && (rect.width < 6 * rect.height)) {
          //draw green rect around valid contour
          Imgproc.rectangle(source, rect.tl(), rect.br(), new Scalar(Math.random() * 255, Math.random() * 255, Math.random() * 255, 0), 3);
          licensePlate = new Mat(sourceORG, rect);
          //recognize licence plate
          if (licensePlate != null && !licensePlate.empty()) {
            licensePlate = filterPlateImage(licensePlate);
            Imgcodecs.imwrite(imgPath + "result\\licensePlate" + i + ".jpg", licensePlate);
            String tempText = recognizeText(imgPath + "result\\licensePlate" + i + ".jpg");
            if (tempText.length() > licenseNumber.length()) {
              licensePlateImg = licensePlate;
              licenseNumber = tempText;
            }
          }
        }
      }
      i++;
    }
  }

  public void saveImages() {
    Imgcodecs.imwrite(imgPath + "result\\source.jpg", source);
    Imgcodecs.imwrite(imgPath + "result\\sourceORG.jpg", sourceORG);
    Imgcodecs.imwrite(imgPath + "result\\gray.jpg", gray);
    Imgcodecs.imwrite(imgPath + "result\\topHat.jpg", topHat);
    Imgcodecs.imwrite(imgPath + "result\\blackHat.jpg", blackHat);
    Imgcodecs.imwrite(imgPath + "result\\grayPlusTopHat.jpg", grayPlusTopHat);
    Imgcodecs.imwrite(imgPath + "result\\grayPlusTopHatMinusBlackHat.jpg", grayPlusTopHatMinusBlackHat);
    Imgcodecs.imwrite(imgPath + "result\\blur.jpg", blur);
    Imgcodecs.imwrite(imgPath + "result\\threshold.jpg", threshold);
  }




  public void test(Mat sourceImage) {
    Mat grayImage = new Mat();
    Mat tempImage = new Mat();

    Imgcodecs.imwrite(imgPath + "result\\000.jpg", sourceImage);

    Scalar minBlue = new Scalar(0, 0, 0, 0);
    Scalar maxBlue = new Scalar(255, 0, 0, 0);

    Core.inRange(sourceImage, minBlue, maxBlue, tempImage);

    Imgcodecs.imwrite(imgPath + "result\\111.jpg", tempImage);

  }
}
