package com.pages.Page7;

import com.constants.Constants;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.opencv.core.*;
import org.opencv.core.Scalar;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
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
  private Scalar randomColor = new Scalar(Math.random() * 255, Math.random() * 255, Math.random() * 255, 0);
  private String outPathMain = Constants.imgPath + "lpr\\";
  private String outPath = Constants.imgPath + "lpr\\";
  private String contourPath = "";

//  public static void main(String[] args) {
//    System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
//    Recognizer ir = new Recognizer();
//    File f = new File(Constants.imgPath + "\\cars\\regnums\\YRR146.jpg");
//    int thresh = 100;
//    ir.recognize(f, thresh);
//  }


  public ImgObject recognize(File file, int thresh) {
    System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
    object = new ImgObject(file);
    String fileNameWithOutExt = FilenameUtils.removeExtension(file.getName());
    outPath = outPathMain + fileNameWithOutExt + "\\";
    clearFolder(outPath);
    Mat filtered = filterImage(object.getOriginal(), thresh);
    Mat filteredCopy = new Mat();
    filtered.copyTo(filteredCopy);
    object.setFiltered(filteredCopy);
    List<MatOfPoint> contours = new ArrayList<>();
    contours = findContours(filtered);
    Mat contourMono = new Mat();

    if ((contours != null) && (contours.size() > 0)) {
      System.out.println("total: " + contours.size());
      int i = 0;
      //work with valid contours
      Rect bestRect = null;

      // simple recognition
      for (MatOfPoint c : contours) {
        MatOfPoint2f pointsArea = new MatOfPoint2f(c.toArray());
        RotatedRect rotatedRectangle = Imgproc.minAreaRect(pointsArea);
        Point rotatedRectPoints[] = new Point[4];
        rotatedRectangle.points(rotatedRectPoints);
        Rect rect = Imgproc.boundingRect(new MatOfPoint(rotatedRectPoints));
        //create contour images
        //toDO ... init in wrong place maybe
        contourMono = new Mat(filtered, rect);
//        Mat contourMono = new Mat(filtered, rect);
//        Mat contourColor = new Mat(object.getOriginal(), rect);
        //save contour images
        contourPath = outPath + "\\" + i + "\\";
        new File(contourPath).mkdirs();
        Imgcodecs.imwrite(contourPath + "contMono.jpg", contourMono);
//        Imgcodecs.imwrite(contourPath + "conColor.jpg", contourColor);

        //rotate contours by rect angle

        Mat rotated1 = rotateImage(contourMono, (int) rotatedRectangle.angle);
        Mat rotated2 = rotateImage(contourMono, -(int) rotatedRectangle.angle);
        Imgcodecs.imwrite(contourPath + "rotated1.jpg", rotated1);
        Imgcodecs.imwrite(contourPath + "rotated2.jpg", rotated2);

        //try to recognize text if not => deep recognize
        String tempText1 = TextRecognizer.recognizeText(rotated1);
        String tempText2 = TextRecognizer.recognizeText(rotated2);
        String tempText;
        Mat tempPlate = new Mat();
        if (tempText1.length() < tempText2.length()) {
          tempText = tempText2;
          rotated2.copyTo(tempPlate);
        } else {
          tempText = tempText1;
          rotated1.copyTo(tempPlate);
        }
        System.out.println("Text " + i + ": " + tempText);
        if (object.getLicenseNumber().length() < tempText.length()) {
          object.setLicenseNumber(tempText);
          object.setPlate(tempPlate);
          bestRect = rect;


        }


        i++;
      }
      if (object.getLicenseNumber().length() < 5) {
        bestRect = null;
        System.out.println("deep recognition");
      }

      //draw green rect on contours if good result exist
      if (bestRect != null) {
        Mat tempContours = new Mat();
        object.getContours().copyTo(tempContours);
        Imgproc.rectangle(tempContours, bestRect.tl(), bestRect.br(), green, 3);
        object.setContours(tempContours);
      }
    }

    object.saveImages(outPath);
    System.out.println("...done...");
    return object;
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
//    Imgcodecs.imwrite(outPath + "fff.jpg", filtered);
    if (validContours.size() > 0) {
      object.setFiltered(filteredImg);
      object.setContours(contoursImg);
      return validContours;
    }
    return null;
  }

  // Rotate license plate image
  private Mat rotateImage(Mat img, int angle) {
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

  // Convert BufferedImage to Mat
  private Mat bufferedImage2Mat(BufferedImage bi) {
    Mat mat = new Mat(bi.getHeight(), bi.getWidth(), CvType.CV_8UC1);
    //Mat mat = new Mat(bi.getHeight(), bi.getWidth(), CvType.CV_8UC3);
    byte[] data = ((DataBufferByte) bi.getRaster().getDataBuffer()).getData();
    mat.put(0, 0, data);
    return mat;
  }
}
