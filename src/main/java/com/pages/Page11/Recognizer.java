package com.pages.Page11;

import com.constants.Constants;
import org.opencv.core.*;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

public class Recognizer extends Thread {
  protected volatile boolean runnable = false;
  private Mat frame;
  private int frameCounter;
  private String path = Constants.imgPath + "faces\\";


  // Constructor
  public Recognizer(Mat frame, int frameCounter) {
    System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
    this.frame = frame;
    this.frameCounter = frameCounter;
  }

  @Override
  public void run() {
    System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
    Mat frameGray = new Mat();
    Imgproc.cvtColor(frame, frameGray, Imgproc.COLOR_RGB2GRAY);

    MatOfRect faceDetections = new MatOfRect();

    Data.faceDetector.detectMultiScale(frameGray, faceDetections);
    if ((faceDetections != null) && (!faceDetections.empty())) {
      Data.faceRectangles = faceDetections.toArray();
      for (Rect faceRect : Data.faceRectangles) {
        Imgproc.rectangle(frame, faceRect.tl(), faceRect.br(), new Scalar(0, 0, 255), 2);
        if (frameCounter % 10 == 0) {
          Imgcodecs.imwrite(path + "frame" + frameCounter + ".jpg", frame);
        }
      }
    }
    // no face found
    else {
      Data.faceRectangles = null;
    }
    runnable = false;
  }


}
//  public static void main(String[] args) {
//    System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
//
//
//    Mat bg = Imgcodecs.imread(Constants.imgPath + "Horse\\bg.jpg");
//    Mat frame = Imgcodecs.imread(Constants.imgPath + "Horse\\frame.jpg");
//
//    Recognizer r = new Recognizer(frame, 0, bg);
//    r.recognize();
//
//  }
//
//  // Filter image
//  public static Mat filterGrayImage(Mat gray, int thresh, int blur) {
////    Mat tempImg = img.clone();
////    Mat grayImg = new Mat();
//    Mat topHatImg = new Mat();
//    Mat blackHatImg = new Mat();
//    Mat grayPlusTopHatImg = new Mat();
//    Mat grayPlusTopHatMinusBlackHatImg = new Mat();
//    Mat blurImg = new Mat();
//    Mat thresholdImg = new Mat();
//    Mat kernel = new Mat(new Size(3, 3), CvType.CV_8U, new Scalar(255));
////    Imgproc.cvtColor(tempImg, grayImg, Imgproc.COLOR_RGB2GRAY);
//    Imgproc.morphologyEx(gray, topHatImg, Imgproc.MORPH_TOPHAT, kernel);
//    Imgproc.morphologyEx(gray, blackHatImg, Imgproc.MORPH_BLACKHAT, kernel);
//    Core.add(gray, topHatImg, grayPlusTopHatImg);
//    Core.subtract(grayPlusTopHatImg, blackHatImg, grayPlusTopHatMinusBlackHatImg);
//    Imgproc.GaussianBlur(grayPlusTopHatMinusBlackHatImg, blurImg, new Size(blur, blur), 1);
//    Imgproc.threshold(blurImg, thresholdImg, thresh, 255, Imgproc.THRESH_BINARY_INV);
//    return thresholdImg;
//  }
//
//
////  // Create rectangle from RotatedRectangle
////  private Rect createRect(RotatedRect rotRect) {
////    if(rotRect != null){
////      return null;
////    }
////    Point points[] = new Point[4];
//////    rotRect.points(points);
//////    Rect maxRect = Imgproc.boundingRect(new MatOfPoint(maxRotRectPoints));
//////    return rec
////  }
//
//  // Filter image
//  public static Mat filterImage(Mat img, int thresh, int blur) {
//    Mat tempImg = img.clone();
//    Mat grayImg = new Mat();
//    Mat topHatImg = new Mat();
//    Mat blackHatImg = new Mat();
//    Mat grayPlusTopHatImg = new Mat();
//    Mat grayPlusTopHatMinusBlackHatImg = new Mat();
//    Mat blurImg = new Mat();
//    Mat thresholdImg = new Mat();
//    Mat kernel = new Mat(new Size(3, 3), CvType.CV_8U, new Scalar(255));
//    Imgproc.cvtColor(tempImg, grayImg, Imgproc.COLOR_RGB2GRAY);
//    Imgproc.morphologyEx(grayImg, topHatImg, Imgproc.MORPH_TOPHAT, kernel);
//    Imgproc.morphologyEx(grayImg, blackHatImg, Imgproc.MORPH_BLACKHAT, kernel);
//    Core.add(grayImg, topHatImg, grayPlusTopHatImg);
//    Core.subtract(grayPlusTopHatImg, blackHatImg, grayPlusTopHatMinusBlackHatImg);
//    Imgproc.GaussianBlur(grayPlusTopHatMinusBlackHatImg, blurImg, new Size(blur, blur), 1);
//    Imgproc.threshold(blurImg, thresholdImg, thresh, 255, Imgproc.THRESH_BINARY_INV);
//    return thresholdImg;
//  }
//
//  public static Rect cutRectIfOutOfImageArea(Mat image, Rect rect) {
//    double startX = rect.tl().x;
//    double startY = rect.tl().y;
//    double endX = rect.br().x;
//    double endY = rect.br().y;
//    if (startX < 0) {
//      startX = 0;
//    }
//    if (startY < 0) {
//      startY = 0;
//    }
//    if (endX > image.width()) {
//      endX = image.width();
//    }
//    if (endY > image.height()) {
//      endY = image.height();
//    }
//    Rect cuttedRect = new Rect(new Point(startX, startY), new Point(endX, endY));
//    return cuttedRect;
//  }
//
//  //Find object
//  public void recognize() {
//    if ((bg != null) && (!bg.empty())) {
//      System.out.println("bg");
//      Imgcodecs.imwrite(screenshotPath + "bg.jpg", bg);
//      Core.subtract(bg, frame, difference);
//      Mat differenceGray = new Mat();
//      Imgproc.cvtColor(difference, differenceGray, Imgproc.COLOR_BGR2GRAY);
//      Imgcodecs.imwrite(screenshotPath + "difference.png", difference);
//      Imgcodecs.imwrite(screenshotPath + "differenceGray.jpg", differenceGray);
//
//      Mat filtered = filterGrayImage(differenceGray, 5, 5);
//      Imgcodecs.imwrite(screenshotPath + "filtered.jpg", filtered);
//
//      Mat inverted = new Mat();
//      Core.bitwise_not(filtered, inverted);
//      Imgcodecs.imwrite(screenshotPath + "invertedDifferenceGray.jpg", inverted);
//
//      Mat differenceGrayThresh = new Mat();
////      Imgproc.threshold(differenceGray, differenceGrayThresh, 20, 255, Imgproc.THRESH_BINARY_INV);
//      differenceGrayThresh = filterImage(difference, 9, 5);
//      Imgcodecs.imwrite(screenshotPath + "differenceGrayThresh.jpg", differenceGrayThresh);
//
//      //draw white border before
//      Imgproc.rectangle(differenceGrayThresh, new Point(2, 2), new Point(frame.width() - 2, frame.height() - 2), new Scalar(255, 255, 255, 0), 5);
//
//      // find max rotated rect
//      List<MatOfPoint> contours = new ArrayList<>();
//      Imgproc.findContours(differenceGrayThresh, contours, new Mat(), RETR_TREE, Imgproc.CHAIN_APPROX_SIMPLE);
//      if (contours.size() > 0) {
//        System.out.println("total: " + contours.size());
//
//        for (MatOfPoint c : contours) {
//          MatOfPoint2f points = new MatOfPoint2f(c.toArray());
//          RotatedRect rotRect = Imgproc.minAreaRect(points);
//          double rotRectArea = rotRect.size.area();
//          double frameArea = frame.size().area();
//
//          if ((rotRectArea < 0.5 * frameArea) && (rotRectArea > 0.05 * frameArea)) {
////            Rect rect = createRect(rotRect);
//          }
//        }
//        // if exist create Rect
////        if (maxRotRect.size.area() != 0) {
////          Point maxRotRectPoints[] = new Point[4];
////          maxRotRect.points(maxRotRectPoints);
////          Rect maxRect = Imgproc.boundingRect(new MatOfPoint(maxRotRectPoints));
////          Mat contoursFrame = new Mat();
////          frame.copyTo(contoursFrame);
////          Imgproc.rectangle(contoursFrame, maxRect.tl(), maxRect.br(), new Scalar(0, 255, 0, 255), 2);
////          Imgproc.rectangle(differenceGrayThresh, maxRect.tl(), maxRect.br(), new Scalar(0, 255, 0, 255), 2);
////          Imgcodecs.imwrite(screenshotPath + "contoursFrame.jpg", contoursFrame);
////          Imgcodecs.imwrite(screenshotPath + "contoursDifferenceGrayThreshContours.jpg", differenceGrayThresh);
////
////          Page11.rect = maxRect;
////        }
//      } else {
//        System.out.println("contours not found");
//        Page11.rect = null;
//      }
//    }
//    Imgcodecs.imwrite(screenshotPath + "frame.jpg", frame);
//  }
//
//  // Clear folder from old files
//  public void clearFolder(String screenshotPath) {
//    try {
//      FileUtils.deleteDirectory(new File(screenshotPath));
//    } catch (IOException e) {
//      e.printStackTrace();
//    }
//    new File(screenshotPath).mkdirs();
//  }
//
//
//  // Save image
//  private void saveImage(String filename, Mat img) {
//    Imgcodecs.imwrite(screenshotPath + filename + ".jpg", img);
//  }
//}
