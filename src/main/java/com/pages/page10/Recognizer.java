package com.pages.page10;

import com.constants.Constants;
import org.apache.commons.io.FileUtils;
import org.opencv.core.*;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.opencv.imgproc.Imgproc.RETR_TREE;

public class Recognizer {

  private Mat frame = new Mat();
  private Mat frameGray = new Mat();
  private Mat bg;
  private Mat bgGray = new Mat();
  private Mat difference = new Mat();
  private String path = Constants.imgPath + "HorseWebcam\\";
  private int frameCounter = 0;


  // Constructor
  public Recognizer(Mat frame, int frameCounter, Mat bg) {
    System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
    this.frame = frame;
    clearFolder(path);
    this.frameCounter = frameCounter;
    this.bg = bg;
  }

  public static void main(String[] args) {
    System.loadLibrary(Core.NATIVE_LIBRARY_NAME);


    Mat bg = Imgcodecs.imread(Constants.imgPath + "Horse\\bg.jpg");
    Mat frame = Imgcodecs.imread(Constants.imgPath + "Horse\\frame.jpg");

    Recognizer r = new Recognizer(frame, 0, bg);
    r.recognize();

  }

  //Find object
  public void recognize() {
    if ((bg != null) && (!bg.empty())) {
      System.out.println("bg");
      Imgcodecs.imwrite(path + "bg.jpg", bg);
      Core.subtract(bg, frame, difference);
      Mat differenceGray = new Mat();
      Imgproc.cvtColor(difference, differenceGray, Imgproc.COLOR_BGR2GRAY);
      Imgcodecs.imwrite(path + "difference.png", difference);
      Imgcodecs.imwrite(path + "differenceGray.jpg", differenceGray);

      Mat filtered = filterGrayImage(differenceGray, 5, 5);
      Imgcodecs.imwrite(path + "filtered.jpg", filtered);

      Mat inverted = new Mat();
      Core.bitwise_not(filtered, inverted);
      Imgcodecs.imwrite(path + "invertedDifferenceGray.jpg", inverted);

      Mat differenceGrayThresh = new Mat();
//      Imgproc.threshold(differenceGray, differenceGrayThresh, 20, 255, Imgproc.THRESH_BINARY_INV);
      differenceGrayThresh = filterImage(difference, 9, 5);
      Imgcodecs.imwrite(path + "differenceGrayThresh.jpg", differenceGrayThresh);

      //draw white border before
      Imgproc.rectangle(differenceGrayThresh, new Point(2, 2), new Point(frame.width() - 2, frame.height() - 2), new Scalar(255, 255, 255, 0), 5);

      // find max rotated rect
      List<MatOfPoint> contours = new ArrayList<>();
      Imgproc.findContours(differenceGrayThresh, contours, new Mat(), RETR_TREE, Imgproc.CHAIN_APPROX_SIMPLE);
      if (contours.size() > 0) {
        System.out.println("total: " + contours.size());

        RotatedRect maxRotRect = new RotatedRect();

        for (MatOfPoint c : contours) {
          MatOfPoint2f points = new MatOfPoint2f(c.toArray());
          RotatedRect rotatedRect = Imgproc.minAreaRect(points);

          if ((rotatedRect.size.area() < 0.5 * frame.size().area()
                  && (rotatedRect.size.area() > 50)
                  && (rotatedRect.size.area() > maxRotRect.size.area()))) {
            maxRotRect = rotatedRect;
          }
        }
        // if exist create Rect
        if (maxRotRect.size.area() != 0) {
          Point maxRotRectPoints[] = new Point[4];
          maxRotRect.points(maxRotRectPoints);
          Rect maxRect = Imgproc.boundingRect(new MatOfPoint(maxRotRectPoints));
          Mat contoursFrame = new Mat();
          frame.copyTo(contoursFrame);
          Imgproc.rectangle(contoursFrame, maxRect.tl(), maxRect.br(), new Scalar(0, 255, 0, 255), 2);
          Imgproc.rectangle(differenceGrayThresh, maxRect.tl(), maxRect.br(), new Scalar(0, 255, 0, 255), 2);
          Imgcodecs.imwrite(path + "contoursFrame.jpg", contoursFrame);
          Imgcodecs.imwrite(path + "contoursDifferenceGrayThreshContours.jpg", differenceGrayThresh);

          Page10.rect = maxRect;
        }
      } else {
        System.out.println("contours not found");
        Page10.rect = null;
      }
    }
    Imgcodecs.imwrite(path + "frame.jpg", frame);
  }

  // Filter image
  public static Mat filterGrayImage(Mat gray, int thresh, int blur) {
//    Mat tempImg = img.clone();
//    Mat grayImg = new Mat();
    Mat topHatImg = new Mat();
    Mat blackHatImg = new Mat();
    Mat grayPlusTopHatImg = new Mat();
    Mat grayPlusTopHatMinusBlackHatImg = new Mat();
    Mat blurImg = new Mat();
    Mat thresholdImg = new Mat();
    Mat kernel = new Mat(new Size(3, 3), CvType.CV_8U, new Scalar(255));
//    Imgproc.cvtColor(tempImg, grayImg, Imgproc.COLOR_RGB2GRAY);
    Imgproc.morphologyEx(gray, topHatImg, Imgproc.MORPH_TOPHAT, kernel);
    Imgproc.morphologyEx(gray, blackHatImg, Imgproc.MORPH_BLACKHAT, kernel);
    Core.add(gray, topHatImg, grayPlusTopHatImg);
    Core.subtract(grayPlusTopHatImg, blackHatImg, grayPlusTopHatMinusBlackHatImg);
    Imgproc.GaussianBlur(grayPlusTopHatMinusBlackHatImg, blurImg, new Size(blur, blur), 1);
    Imgproc.threshold(blurImg, thresholdImg, thresh, 255, Imgproc.THRESH_BINARY_INV);
    return thresholdImg;
  }


  // Filter image
  public static Mat filterImage(Mat img, int thresh, int blur) {
    Mat tempImg = img.clone();
    Mat grayImg = new Mat();
    Mat topHatImg = new Mat();
    Mat blackHatImg = new Mat();
    Mat grayPlusTopHatImg = new Mat();
    Mat grayPlusTopHatMinusBlackHatImg = new Mat();
    Mat blurImg = new Mat();
    Mat thresholdImg = new Mat();
    Mat kernel = new Mat(new Size(3, 3), CvType.CV_8U, new Scalar(255));
    Imgproc.cvtColor(tempImg, grayImg, Imgproc.COLOR_RGB2GRAY);
    Imgproc.morphologyEx(grayImg, topHatImg, Imgproc.MORPH_TOPHAT, kernel);
    Imgproc.morphologyEx(grayImg, blackHatImg, Imgproc.MORPH_BLACKHAT, kernel);
    Core.add(grayImg, topHatImg, grayPlusTopHatImg);
    Core.subtract(grayPlusTopHatImg, blackHatImg, grayPlusTopHatMinusBlackHatImg);
    Imgproc.GaussianBlur(grayPlusTopHatMinusBlackHatImg, blurImg, new Size(blur, blur), 1);
    Imgproc.threshold(blurImg, thresholdImg, thresh, 255, Imgproc.THRESH_BINARY_INV);
    return thresholdImg;
  }


  public static Rect cutRectIfOutOfImageArea(Mat image, Rect rect) {
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


  // Clear folder from old files
  public void clearFolder(String path) {
    try {
      FileUtils.deleteDirectory(new File(path));
    } catch (IOException e) {
      e.printStackTrace();
    }
    new File(path).mkdirs();
  }


  // Save image
  private void saveImage(String filename, Mat img) {
    Imgcodecs.imwrite(path + filename + ".jpg", img);
  }
}
