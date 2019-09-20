package com.pages.page7;

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

import java.awt.image.DataBufferByte;


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
  private boolean logger = true;

  // Time with logger:    2.72
  // Time without logger: 2.518

  //toDO..TENSOR FLOW

  // Convert BufferedImage to Mat
  public static Mat bufferedImageToMat(BufferedImage bi) {
    Mat mat = new Mat(bi.getHeight(), bi.getWidth(), CvType.CV_8UC1);
    byte[] data = ((DataBufferByte) bi.getRaster().getDataBuffer()).getData();
    mat.put(0, 0, data);
    return mat;
  }

  // Find and recognize license plate on image
  public ImgObject recognize(File file, int thresh, int blur, int plateThresh, double shearAngleFromSlider) {
    long start = System.currentTimeMillis();
    System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
    object = new ImgObject(file);
    if (logger) {
      String fileNameWithOutExt = FilenameUtils.removeExtension(file.getName());
      outPath = outPathMain + fileNameWithOutExt + "\\";
      clearFolder(outPath);
    }
    Mat filtered = filterImage(object.getOriginal(), thresh, blur);
    object.setFiltered(copy(filtered));
    List<MatOfPoint> contours = new ArrayList<>();
    contours = findContours(filtered);
    if (logger) {
      object.saveImages(outPath);
    }
    //loop if contours found
    if ((contours != null) && (contours.size() > 0)) {
      Rect bestRect = null;
      List<Mat> plates = new ArrayList<>();
      if (logger) {
        System.out.println("total: " + contours.size());
        Imgcodecs.imwrite(outPath + "threshold.jpg", filtered);
      }
      //loop through valid contours
      int i = 0;
      for (MatOfPoint c : contours) {
        if (logger) {
          System.out.println("____________________ " + i + " ___________________");
          contourOutPath = outPath + i + "\\";
          new File(contourOutPath).mkdirs();
        }
        //create rect around contour
        MatOfPoint2f pointsArea = new MatOfPoint2f(c.toArray());
        RotatedRect rotatedRect = Imgproc.minAreaRect(pointsArea);
        Point rotatedRectPoints[] = new Point[4];
        rotatedRect.points(rotatedRectPoints);
        Rect rect = Imgproc.boundingRect(new MatOfPoint(rotatedRectPoints));
        //create contour mini image
        rect = cutRectIfOutOfImageArea(filtered, rect);

        Mat plateColored = new Mat(object.getOriginal(), rect);
        Mat plateGray = new Mat();
        Imgproc.cvtColor(plateColored, plateGray, Imgproc.COLOR_RGB2GRAY);


        //rotate contours by rect angle
        Mat rotatedImg = rotateImage(plateColored, rotatedRect);



        //cut large contour from rotated image
//        Mat cuttedImg = cutLargeContour(rotatedImg);
        if (logger) {
          Imgcodecs.imwrite(contourOutPath + "1.contour.jpg", plateGray);
          Imgcodecs.imwrite(contourOutPath + "2.rotated.jpg", rotatedImg);
        }



//        Mat shearedPlate = shearImage(cuttedImg, rotatedRect, rotatedRect.angle, shearAngleFromSlider);
//        //extra filter license plate before text recognition
//        Mat filteredImg = filterPlate(shearedPlate, plateThresh);
//        //text recognition
//        String text = TextRecognizer.recognizeText(shearedPlate);
//        if (object.getLicenseNumber().length() < text.length()) {
//          object.setLicenseNumber(text);
//          object.setPlate(cuttedImg);
//          object.setFilteredPlate(filteredImg);
//          object.setShearedPlate(shearedPlate);
//          bestRect = rect;
//        }
        i++;
      }
      //draw green rectangle on best contour
      if (bestRect != null) {
        Mat bestContoursImg = copy(object.getContours());
        Imgproc.rectangle(bestContoursImg, bestRect.tl(), bestRect.br(), green, 3);
        object.setContours(bestContoursImg);
      }

      System.out.println("RESULT: " + object.getLicenseNumber());
    }
    //contours not found
    else {
      System.out.println("Contours not found, change thresh and try again");
    }
    if (logger) {
      object.saveImages(outPath);
    }


    long elapsedTimeMillis = System.currentTimeMillis() - start;
    float elapsedTimeSec = elapsedTimeMillis / 1000F;
    if (logger) {
      System.out.println("Time with logger: " + elapsedTimeSec);
    } else {
      System.out.println("Time without logger: " + elapsedTimeSec);
    }
    return object;
  }

  private Rect cutRectIfOutOfImageArea(Mat image, Rect rect) {
    double startX = rect.tl().x;
    double startY = rect.tl().y;
    double endX = rect.br().x;
    double endY = rect.br().y;
    if (startX < 0) {
      startX = 0;
    }
    if (startY < 0) {
      startY = 0;
    }
    if (endX > image.width()) {
      endX = image.width();
    }
    if (endY > image.height()) {
      endY = image.height();
    }
    Rect cuttedRect = new Rect(new Point(startX, startY), new Point(endX, endY));
    return cuttedRect;
  }

  //////////////// MAT ////////////////////////
  //shear cutted plate with rotated rectangle angle or slider
  public Mat shearImage(Mat cuttedPlate, RotatedRect rotatedRect, double rotatedRectAngle, double shearAngleFromSlider) {
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
      BufferedImage shearedPLateBuffered = new BufferedImage(buffer.getWidth(), buffer.getHeight(), buffer.getType());
      op.filter(buffer, shearedPLateBuffered);
      if (logger) {
        File outputfile = new File(contourOutPath + "6.sheared.jpg");
        ImageIO.write(shearedPLateBuffered, "jpg", outputfile);
      }
      Mat shearedPLate = bufferedImageToMat(shearedPLateBuffered);
      return shearedPLate;
    } catch (Exception e) {
      e.printStackTrace();
    }
    return null;
  }

  // Shear plate by sladers value
  public Mat shearImageFromSlider(Mat cuttedPlate, double shearAngleFromSlider) {
    double x = 0;
    // shear text angle controls from slider if not 0
    if (shearAngleFromSlider != 0) {
      x = shearAngleFromSlider;
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
      BufferedImage shearedPLateBuffered = new BufferedImage(buffer.getWidth(), buffer.getHeight(), buffer.getType());
      op.filter(buffer, shearedPLateBuffered);
      if (logger) {
        File outputfile = new File(contourOutPath + "6.sheared.jpg");
        ImageIO.write(shearedPLateBuffered, "jpg", outputfile);
      }
      Mat shearedPLate = bufferedImageToMat(shearedPLateBuffered);
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
    Mat threshold = new Mat();
    Mat blur = new Mat();
    Mat grayPlusTopHat = new Mat();
    Mat grayPlusTopHatMinusBlackHat = new Mat();
    Mat kernel = new Mat(new Size(3, 3), CvType.CV_8U, new Scalar(255));
    Imgproc.morphologyEx(inverted, topHat, Imgproc.MORPH_TOPHAT, kernel);
    Imgproc.morphologyEx(inverted, blackHat, Imgproc.MORPH_BLACKHAT, kernel);
    Core.add(inverted, topHat, grayPlusTopHat);
    Core.subtract(grayPlusTopHat, blackHat, grayPlusTopHatMinusBlackHat);
    if (logger) {
      Imgcodecs.imwrite(contourOutPath + "5.filtered.jpg", grayPlusTopHatMinusBlackHat);
    }
    Imgproc.GaussianBlur(grayPlusTopHatMinusBlackHat, blur, new Size(5, 5), 1);
    Imgproc.threshold(blur, threshold, plateThresh, 255, Imgproc.THRESH_BINARY_INV);
    return threshold;
  }

  // Rotate license plate image
  private Mat rotateImage(Mat img, RotatedRect rotatedRect) {
    int angle = (int) rotatedRect.angle;
    if (rotatedRect.size.width > rotatedRect.size.height) {
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

  private Mat cutLargeContour(Mat rotatedImg) {
    if (rotatedImg == null) {
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
          Mat cuttedImg = new Mat(copy, rect);
          Imgproc.rectangle(maxContourImg, rect.tl(), rect.br(), blue, 1);
          if (logger) {
            Imgcodecs.imwrite(contourOutPath + "3.maxContour.jpg", maxContourImg);
            Imgcodecs.imwrite(contourOutPath + "4.cutted.jpg", cuttedImg);
          }
          return cuttedImg;
        }
      }
      j++;
    }
    return rotatedImg;
  }

  // Filter main image, method translated from Python->C++
  public Mat filterImage(Mat img, int thresh, int blurValue) {
//    int blurValue = 5;
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

  //shear cutted plate with rotated rectangle angle or slider
  private BufferedImage shearImageBuffered(Mat cuttedPlate, RotatedRect rotatedRect, double rotatedRectAngle, double shearAngleFromSlider) {
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

  //Copy Mat, return new one
  private Mat copy(Mat original) {
    if (original == null) {
      return null;
    }
    Mat copy = new Mat();
    original.copyTo(copy);
    return copy;
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
        rect = cutRectIfOutOfImageArea(img, rect);
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

}
