package com.pages.Page7;

import com.constants.Constants;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.opencv.core.*;
import org.opencv.core.Scalar;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

import javax.imageio.ImageIO;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.opencv.imgproc.Imgproc.RETR_TREE;


public class Recognizer {

  private ImgObject object;
  private Scalar blue = new Scalar(255, 0, 0, 255);
  private Scalar green = new Scalar(0, 255, 0, 255);
  private Scalar red = new Scalar(0, 0, 255, 255);
  private Scalar gray = new Scalar(20, 20, 20, 255);
  private Scalar randomColor = new Scalar(Math.random() * 255, Math.random() * 255, Math.random() * 255, 0);
  private String outPathMain = Constants.imgPath + "lpr\\";
  private String outPath = Constants.imgPath + "lpr\\";
  private String contourPath = "";
  private String contourOutPath = "";


  public static void main(String[] args) {
    System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
    Recognizer ir = new Recognizer();
    File f = new File(Constants.imgPath + "\\cars\\regnums\\YRR146.jpg");
//    File f = new File(Constants.imgPath + "\\cars\\regnums\\NFW285_mirror.jpg");
    //toDo.. file not found if null, wrong filename
//    File f = new File(Constants.imgPath + "\\cars\\regnums\\NFW285.jpg");
//    File f = new File(Constants.imgPath + "\\cars\\111\\-30.jpg");
    int thresh = 100;
    ir.recognize(f, thresh, 0);
  }


  // Find and recognize license plate on image
  public ImgObject recognize(File file, int thresh, double shearAngleFromSlider) {
    System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
    object = new ImgObject(file);
    String fileNameWithOutExt = FilenameUtils.removeExtension(file.getName());
    outPath = outPathMain + fileNameWithOutExt + "\\";
    clearFolder(outPath);

    Mat original = copy(object.getOriginal());
    Mat filtered = filterImage(object.getOriginal(), thresh);
    object.setFiltered(copy(filtered));

    List<MatOfPoint> contours = new ArrayList<>();
    contours = findContours(filtered);

    //////////////////////////////
    object.saveImages(outPath);

    //loop if contours found
    if ((contours != null) && (contours.size() > 0)) {
      System.out.println("total: " + contours.size());
      Rect bestRect = null;
      List<Mat> plates = new ArrayList<>();
      Imgcodecs.imwrite(outPath + "threshold.jpg", filtered);

      //loop through valid contours
      int i = 0;
      for (MatOfPoint c : contours) {
        System.out.println("____________________ " + i + " ___________________");
        contourOutPath = outPath + i + "\\";
        new File(contourOutPath).mkdirs();

        //create rect around contour
        MatOfPoint2f pointsArea = new MatOfPoint2f(c.toArray());
        RotatedRect rotatedRect = Imgproc.minAreaRect(pointsArea);
        Point rotatedRectPoints[] = new Point[4];
        rotatedRect.points(rotatedRectPoints);
        Rect rect = Imgproc.boundingRect(new MatOfPoint(rotatedRectPoints));
        //create contour mini image
        Mat contourImg = new Mat(filtered, rect);
        Imgcodecs.imwrite(contourOutPath + "1.contour.jpg", contourImg);
        //rotate contours by rect angle
        Mat rotatedImg = rotateImage(contourImg, rotatedRect);
        Imgcodecs.imwrite(contourOutPath + "2.rotated.jpg", rotatedImg);
        //cut large contour from rotated image
        Mat cuttedImg = cutLargeContour(rotatedImg);
        //extra filter license plate before text recognition
        int plateThresh = 100;
        Mat filteredImg = filterPlate(cuttedImg, plateThresh);

        System.out.println(i);
        //shear plate
        //toDo.. try to use angle or move it to slider and remove from args
        //toDo.. maybe find shearing on matrix direct without buffered ???

        //sheared Angle from slider if 0 => rotatedRect.angle
        BufferedImage bufferedShearedPlate = shearImage(filteredImg, rotatedRect, rotatedRect.angle, shearAngleFromSlider);

        //text recognition
        String text = TextRecognizer.recognizeText(bufferedShearedPlate);
        if (object.getLicenseNumber().length() < text.length()) {
          object.setLicenseNumber(text);
          bestRect = rect;
        }
        i++;
      }
      //draw green rectangle on best contour
      Mat bestContoursImg = copy(object.getContours());
      Imgproc.rectangle(bestContoursImg, bestRect.tl(), bestRect.br(), green, 3);
      object.setContours(bestContoursImg);

      System.out.println("RESULT: " + object.getLicenseNumber());
    }
    //contours not found
    else {
      System.out.println("Contours not found, change thresh and try again");
    }
    object.saveImages(outPath);
    return object;
  }


  //shear cutted plate with rotated rectangle angle or slider
  private BufferedImage shearImage(Mat cuttedPlate, RotatedRect rotatedRect, double rotatedRectAngle, double shearAngleFromSlider) {
    double x = 0;

    // shear text angle controls from slider if not 0
    if (shearAngleFromSlider != 0) {
      x = shearAngleFromSlider;
    }
    // some shear logic algoritm will be here
    else {
      if (rotatedRect.size.width > rotatedRect.size.height) {
        System.out.println("plus");
        x = 0.2;
      } else {
        System.out.println("minus");
        x = -0.2;
      }
    }


    System.out.println("x = " + x);

    System.out.println("angle = " + (int) shearAngleFromSlider);
    BufferedImage buffer = null;
    try {
      buffer = Mat2BufferedImage(cuttedPlate);
      AffineTransform tx = new AffineTransform();
      //tx.translate(buffer.getHeight() / 2, buffer.getWidth() / 2);
      tx.shear(x, 0);
      //tx.shear(-0.4, 0);
      //tx.translate(-buffer.getWidth() / 2, -buffer.getHeight() / 2);
      AffineTransformOp op = new AffineTransformOp(tx, AffineTransformOp.TYPE_BILINEAR);
      BufferedImage shearedPLate = new BufferedImage(buffer.getWidth(), buffer.getHeight(), buffer.getType());
      op.filter(buffer, shearedPLate);
      File outputfile = new File(contourOutPath + "6.sheared.jpg");
      ImageIO.write(shearedPLate, "jpg", outputfile);
      return shearedPLate;
    } catch (Exception e) {
      e.printStackTrace();
    }
    return null;
  }


  // Extra filter for license plate with inversion and ...
  public Mat filterPlate(Mat img, int plateThresh) {
    Mat inverted = new Mat();
    Core.bitwise_not(img, inverted);
    Mat topHat = new Mat();
    Mat blackHat = new Mat();
    Mat grayPlusTopHat = new Mat();
    Mat grayPlusTopHatMinusBlackHat = new Mat();
    Mat kernel = new Mat(new Size(3, 3), CvType.CV_8U, new Scalar(255));
    Imgproc.morphologyEx(inverted, topHat, Imgproc.MORPH_TOPHAT, kernel);
    Imgproc.morphologyEx(inverted, blackHat, Imgproc.MORPH_BLACKHAT, kernel);
    Core.add(inverted, topHat, grayPlusTopHat);
    Core.subtract(grayPlusTopHat, blackHat, grayPlusTopHatMinusBlackHat);

    Imgcodecs.imwrite(contourOutPath + "5.filtered.jpg", grayPlusTopHatMinusBlackHat);

    return grayPlusTopHatMinusBlackHat;
  }

  private Mat cutLargeContour(Mat rotatedImg) {
    if (rotatedImg == null) {
      System.out.println("rotated NULL");
      return null;
    }
    Mat copy = copy(rotatedImg);
    List<MatOfPoint> contours = new ArrayList<>();
    Imgproc.findContours(copy, contours, new Mat(), RETR_TREE, Imgproc.CHAIN_APPROX_SIMPLE);
    int j = 0;
    for (MatOfPoint c : contours) {
      MatOfPoint2f points = new MatOfPoint2f(c.toArray());
      RotatedRect rotatedRect = Imgproc.minAreaRect(points);
      Point rotRectPoints[] = new Point[4];
      rotatedRect.points(rotRectPoints);
      double rectArea = rotatedRect.size.area();
      double imgArea = rotatedImg.size().area();
      Rect rect = new Rect();
      rect = Imgproc.boundingRect(new MatOfPoint(rotRectPoints));
      //try to find largest contour > 50%, if few overwrite, if none return same image
      if ((copy.width() > rect.size().width) && (copy.height() > rect.size().height)
              && (rectArea > imgArea * 0.5) && (rectArea < imgArea)) {
        if (rotatedRect.size.width > rotatedRect.size.height) {
          Mat maxContourImg = copy(copy);
          Imgproc.rectangle(maxContourImg, rect.tl(), rect.br(), blue, 1);
          Imgcodecs.imwrite(contourOutPath + "3.maxContour.jpg", maxContourImg);
          Mat cuttedImg = new Mat(copy, rect);
          Imgcodecs.imwrite(contourOutPath + "4.cutted.jpg", cuttedImg);
          return cuttedImg;
        }
      }
      j++;
    }
    return rotatedImg;
  }

  // Rotate license plate image
  private Mat rotateImage(Mat img, RotatedRect rotatedRect) {
    int angle = (int) rotatedRect.angle;
    if (rotatedRect.size.width > rotatedRect.size.height) {
      System.out.println("left");
      angle = -angle;
    }
    int rotatedAngle = 0;
    if (angle == 0) {
      return img;
    }
    if (angle < 0) {
      rotatedAngle = 90 - Math.abs(angle);
    }
    if (angle > 0) {
      rotatedAngle = -angle;
    }
    Mat temp = new Mat();
    img.copyTo(temp);
    Mat rotatedImg = new Mat(2, 3, CvType.CV_32FC1);
    Mat destination = new Mat(img.rows(), img.cols(), img.type());
    Point center = new Point(destination.cols() / 2, destination.rows() / 2);
    rotatedImg = Imgproc.getRotationMatrix2D(center, rotatedAngle, 1);
    Imgproc.warpAffine(temp, destination, rotatedImg, destination.size());
    return destination;
  }

  // Find contours on image
  private List<MatOfPoint> findContours(Mat img) {
    Mat filteredImg = object.getFiltered();
    Mat contoursImg = new Mat();
    object.getOriginal().copyTo(contoursImg);
    List<MatOfPoint> contours = new ArrayList<>();
    List<MatOfPoint> validContours = new ArrayList<>();
    Imgproc.findContours(img, contours, new Mat(), RETR_TREE, Imgproc.CHAIN_APPROX_SIMPLE);
    for (MatOfPoint c : contours) {
      MatOfPoint2f pointsArea = new MatOfPoint2f(c.toArray());
      RotatedRect rotatedRectangle = Imgproc.minAreaRect(pointsArea);
      if ((rotatedRectangle.size.area() > 2000) && (rotatedRectangle.size.area() < 15000)) {
        Point rotatedRectPoints[] = new Point[4];
        rotatedRectangle.points(rotatedRectPoints);
        Rect rect = Imgproc.boundingRect(new MatOfPoint(rotatedRectPoints));
        if ((rect.width > rect.height) && (rect.width < 6 * rect.height)) {
          //toDo, check average color in contour, if white >50%
          validContours.add(c);
          Imgproc.rectangle(filteredImg, rect.tl(), rect.br(), red, 3);
          Imgproc.rectangle(contoursImg, rect.tl(), rect.br(), red, 3);
        }
      }
    }
    if (validContours.size() > 0) {
      object.setFiltered(filteredImg);
      object.setContours(contoursImg);
      return validContours;
    }
    return null;
  }

  // Filter main image, method translated from Python->C++
  private Mat filterImage(Mat img, int thresh) {
    int blurValue = 5;
    Mat temp = new Mat();
    img.copyTo(temp);
    Mat gray = new Mat();
    Mat topHat = new Mat();
    Mat blackHat = new Mat();
    Mat grayPlusTopHat = new Mat();
    Mat grayPlusTopHatMinusBlackHat = new Mat();
    Mat blur = new Mat();
    Mat threshold = new Mat();
    Mat kernel = new Mat(new Size(3, 3), CvType.CV_8U, new Scalar(255));
    Imgproc.cvtColor(temp, gray, Imgproc.COLOR_RGB2GRAY);
    Imgproc.morphologyEx(gray, topHat, Imgproc.MORPH_TOPHAT, kernel);
    Imgproc.morphologyEx(gray, blackHat, Imgproc.MORPH_BLACKHAT, kernel);
    Core.add(gray, topHat, grayPlusTopHat);
    Core.subtract(grayPlusTopHat, blackHat, grayPlusTopHatMinusBlackHat);
    Imgproc.GaussianBlur(grayPlusTopHatMinusBlackHat, blur, new Size(blurValue, blurValue), 1);
    Imgproc.threshold(blur, threshold, thresh, 255, Imgproc.THRESH_BINARY_INV);
    return threshold;
  }

  // Clear folder from old files
  public void clearFolder(String path) {
    try {
      FileUtils.deleteDirectory(new File(path));
    } catch (IOException e) {
      e.printStackTrace();
    }
    new File(path).mkdirs();
  }

  // Convert Mat to BufferedImage
  private BufferedImage Mat2BufferedImage(Mat matrix) throws Exception {
    MatOfByte mob = new MatOfByte();
    Imgcodecs.imencode(".jpg", matrix, mob);
    byte ba[] = mob.toArray();
    BufferedImage bi = ImageIO.read(new ByteArrayInputStream(ba));
    return bi;
  }

  //Copy Mat, return new one
  private Mat copy(Mat original) {
    if (original == null) {
      return null;
    }
    Mat copy = new Mat();
    original.copyTo(copy);
    return copy;
  }
}
