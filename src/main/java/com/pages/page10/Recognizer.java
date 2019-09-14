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

//  public static void main(String[] args) {
//    System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
//  }

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

  public void test() {
    if ((bg != null) && (!bg.empty())) {
      System.out.println("bg");
      Imgcodecs.imwrite(path + "bg.jpg", bg);
      Core.subtract(bg, frame, difference);
      Mat differenceGray = new Mat();
      Imgproc.cvtColor(difference, differenceGray, Imgproc.COLOR_BGR2GRAY);
      Imgcodecs.imwrite(path + "difference.png", difference);
      Imgcodecs.imwrite(path + "resultGray.jpg", differenceGray);

      Mat differenceGrayThresh = new Mat();
      //Imgproc.threshold(differenceGray, differenceGrayThresh, 10, 255, Imgproc.THRESH_BINARY_INV);
      differenceGrayThresh = filterImage(difference, 10, 5);
      Imgcodecs.imwrite(path + "resultGrayThresh.jpg", differenceGrayThresh);

      List<MatOfPoint> contours = new ArrayList<>();
      Imgproc.findContours(differenceGrayThresh, contours, new Mat(), RETR_TREE, Imgproc.CHAIN_APPROX_SIMPLE);

      RotatedRect maxRotatedRect = new RotatedRect();
      maxRotatedRect = new RotatedRect();
      double max = 0;

      System.out.println("total: " + contours.size());
      for (MatOfPoint c : contours) {
        MatOfPoint2f points = new MatOfPoint2f(c.toArray());
        RotatedRect rotatedRect = Imgproc.minAreaRect(points);
        if ((rotatedRect.size.area() < differenceGrayThresh.size().area() * 0.8) && (max < rotatedRect.size.area())) {
          max = rotatedRect.size.area();
          maxRotatedRect = rotatedRect;
        }
      }
      System.out.println(maxRotatedRect.size.area());
      Point maxRotatedRectPoints[] = new Point[4];
      maxRotatedRect.points(maxRotatedRectPoints);
      Rect rect = Imgproc.boundingRect(new MatOfPoint(maxRotatedRectPoints));

      Mat frameContours = new Mat();
      frame.copyTo(frameContours);
      Mat differenceGrayThreshContours = new Mat();
      differenceGray.copyTo(differenceGrayThreshContours);

      Imgproc.rectangle(frameContours, rect.tl(), rect.br(), new Scalar(0, 255, 0, 255), 2);
      Imgproc.rectangle(differenceGrayThreshContours, rect.tl(), rect.br(), new Scalar(0, 255, 0, 255), 2);


      Imgcodecs.imwrite(path + "frameContours.jpg", differenceGrayThresh);
      Imgcodecs.imwrite(path + "differenceGrayThreshContours.jpg", frame);


    }
    Imgcodecs.imwrite(path + "original.jpg", frame);
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
