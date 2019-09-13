package com.pages.page9;


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


public class LPR {
  //  private List<Screenshot> screenshots = new ArrayList<>();
  private Scalar blue = new Scalar(255, 0, 0, 255);
  private Scalar green = new Scalar(0, 255, 0, 255);
  private Scalar red = new Scalar(0, 0, 255, 255);
  private Scalar gray = new Scalar(20, 20, 20, 255);
  private Scalar randomColor = new Scalar(Math.random() * 255, Math.random() * 255, Math.random() * 255, 0);
  //  private String outPath = Constants.videoPath + "screenshots\\";
  private String fileOutPath = "";
  private String screenshotPath = "";
  private String contourPath = "";
  private String contourOutPath = "";
  private boolean logger = true;
  private File file;


  // Constructor
  public LPR(String fileOutPath) {
    System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
    System.out.println("LPR");
    this.fileOutPath = fileOutPath;
  }


  public static void main(String[] args) {
    LPR lpr = new LPR(Constants.videoPath + "screenshots\\");
    lpr.clearFolder(Constants.videoPath + "screenshots");

    File f = new File(Constants.imgPath + "cars\\111\\30.jpg");
    Mat img = Imgcodecs.imread(f.getAbsolutePath());
    lpr.recognize(f, img, 0);

    File f2 = new File(Constants.imgPath + "cars\\111\\-30.jpg");
    Mat img2 = Imgcodecs.imread(f2.getAbsolutePath());
    lpr.recognize(f2, img2, 1);


  }


  // Recognize one image
  public void recognize(File file, Mat originalImg, int frameCounter) {

    Screenshot screenshot = checkScreenshot(originalImg, frameCounter);
    if (screenshot != null) {

      processValidContours(screenshot);

    }

    // contours not found
    else {
    }

  }

  private void processValidContours(Screenshot screenshot) {
    int i = 0;
    Mat originalImg = screenshot.getOriginalImg().clone();
    Mat filteredImg = screenshot.getFilteredImg().clone();
    Mat originalContoursImg = screenshot.getOriginalImg().clone();
    Mat filteredContoursImg = screenshot.getFilteredImg().clone();

    for (Contour c : screenshot.getContours()) {
      if (logger) {
        contourPath = screenshotPath + i + "\\";
        new File(contourPath).mkdirs();
      }

      Imgproc.rectangle(originalContoursImg, c.getRect().tl(), c.getRect().br(), red, 3);
      Imgproc.rectangle(filteredContoursImg, c.getRect().tl(), c.getRect().br(), red, 3);

      Mat originalPlateImg = new Mat(originalImg, c.getRect());
      Mat rotatedPlateImg = rotateImage(originalPlateImg, c.getRotatedRect());

      cutPlateFromRotatedPlate(rotatedPlateImg, c.getRotatedRect());

      if (logger) {
        //contourPath = screenshotPath + i + "\\";
        //new File(contourPath).mkdirs();
        Imgcodecs.imwrite(contourPath + "originalPlate.jpg", originalPlateImg);
        Imgcodecs.imwrite(contourPath + "rotatedPlate.jpg", rotatedPlateImg);
      }


      i++;
    }
    if (logger) {
      Imgcodecs.imwrite(screenshotPath + "original.jpg", originalImg);
      Imgcodecs.imwrite(screenshotPath + "filtered.jpg", filteredImg);
      Imgcodecs.imwrite(screenshotPath + "originalContours.jpg", originalContoursImg);
      Imgcodecs.imwrite(screenshotPath + "filteredContours.jpg", filteredContoursImg);
    }

    screenshot.setFilteredContoursImg(filteredContoursImg);
    screenshot.setOriginalContoursImg(originalContoursImg);

  }


  private void cutPlateFromRotatedPlate(Mat img, RotatedRect rotatedRect) {
    double width = 0;
    if (rotatedRect.size.width > img.size().height) {
      width = rotatedRect.size.height;
    } else {
      width = rotatedRect.size.width;
    }

    Point startPoint = new Point(0, (img.height() / 2) - (width / 2));
    Point endPoint = new Point(img.width(), (img.height() / 2) + (width / 2));

    if (width != 0) {
      Mat plateContour = img.clone();
      Imgproc.rectangle(plateContour, rect.tl(), rect.br(), red, 2);
      System.out.println(contourPath);
      Imgcodecs.imwrite(contourPath + "contourPlat.jpg", plateContour);

    }


    //    Rect rect = null;
//    if(rotatedRect.size.width > img.size().height){
//      Point tlPoint = new Point(0, (img.height()/2)-(rotatedRect.size.height/2));
//      Point brPoint = new Point(img.width(), (img.height()/2)+(rotatedRect.size.height/2));
//      rect = new Rect(tlPoint, brPoint);
//    }
//    //???
//
//    if(rect != null){
//
//
//      Mat plateContour = img.clone();
//      Imgproc.rectangle(plateContour, rect.tl(), rect.br(), red, 2);
//      System.out.println(contourPath);
//      Imgcodecs.imwrite(contourPath + "contourPlat.jpg", plateContour);
//
//    }


  }


  // Rotate license plate image
  private Mat rotateImage(Mat img, RotatedRect rotatedRect) {
    double angle = rotatedRect.angle;
    if (rotatedRect.size.width > rotatedRect.size.height) {
      angle = -angle;
    }
    double rotatedAngle = 0;
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

  // Filter image
  private Mat filterImage(Mat img, int thresh, int blur) {
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

  // Check if image contains valid contours (potential license plates)
  private Screenshot checkScreenshot(Mat img, int frameCount) {
    // filter image
    int thresh = 200;
    int blur = 5;
    Mat filteredImg = filterImage(img, thresh, blur);
    List<Contour> contours = findContouts(filteredImg);
    if (contours != null) {
      Screenshot screenshot = new Screenshot(img, contours);
      screenshot.setFilteredImg(filteredImg);
      if (logger) {
        screenshotPath = fileOutPath + "screenshot_" + frameCount + "\\";
        new File(screenshotPath).mkdirs();
      }
      return screenshot;
    }
    //CONTOURS NOT FOUND
    else {
      String undefindPath = fileOutPath + "undefined\\";
      new File(undefindPath).mkdirs();
      Imgproc.putText(img, "NOT FOUND", new Point(img.width() / 2, img.height() / 2), 2, 2, red, 2);
      Imgcodecs.imwrite(undefindPath + frameCount + ".jpg", img);
      Imgcodecs.imwrite(undefindPath + frameCount + "filtered.jpg", filteredImg);
      System.out.println("Not found");
    }

    return null;
  }


  // Try to get contours from image
  private List<Contour> findContouts(Mat filteredImg) {
    List<MatOfPoint> contours = new ArrayList<>();
    List<Contour> validContours = new ArrayList<>();
    // temp gray
//    Mat grayImg = new Mat();
//    Imgproc.cvtColor(img, grayImg, Imgproc.COLOR_RGB2GRAY);
    Imgproc.findContours(filteredImg, contours, new Mat(), RETR_TREE, Imgproc.CHAIN_APPROX_SIMPLE);
    for (MatOfPoint c : contours) {
      MatOfPoint2f pointsArea = new MatOfPoint2f(c.toArray());
      RotatedRect rotatedRectangle = Imgproc.minAreaRect(pointsArea);
      //toDo...change to % procent size
      double imgArea = filteredImg.size().area();
      double rotRectArea = rotatedRectangle.size.area();
//      if ((rotatedRectangle.size.area() > 2000) && (rotatedRectangle.size.area() < 15000)) {
      if (
              (rotRectArea > 0.01 * imgArea) && (rotRectArea < 0.5 * imgArea)) {
        Point rotatedRectPoints[] = new Point[4];
        rotatedRectangle.points(rotatedRectPoints);
        Rect rect = Imgproc.boundingRect(new MatOfPoint(rotatedRectPoints));
        rect = cutRectIfOutOfImageArea(filteredImg, rect);
        if ((rect.width > rect.height) && (rect.width < 6 * rect.height)) {
          //toDo, check average color in contour, if white >50%
          Contour contour = new Contour(c, rotatedRectangle, rect);
          System.out.println("rotated:" + (int) rotatedRectangle.size.width + " x " + (int) rotatedRectangle.size.height);
          System.out.println("rect:   " + (int) rect.size().width + " x " + (int) rect.size().height);
          System.out.println();

          if (contour != null) {
            validContours.add(contour);
          }
        }
      }
    }
    if ((validContours != null) && (!validContours.isEmpty())) {
      return validContours;
    }
    return null;
  }


  // Cut off contours rectangle if out off image area
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

  // Clear folder from old files
  public void clearFolder(String path) {
    try {
      FileUtils.deleteDirectory(new File(path));
    } catch (IOException e) {
      e.printStackTrace();
    }
    new File(path).mkdirs();
  }

  public void test(File file, Mat frame, int count) {
    System.out.println("test");
  }
}
