package com.pages.page9;


import com.constants.Constants;
import com.pages.page7.ImgObject;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
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
  private String outPath = Constants.videoPath + "lpr\\";
  private String screenshotPath = "";
  private String contourPath = "";
  private String contourOutPath = "";
  private boolean logger = true;
  private File file;


  // Constructor
  public LPR() {
    System.out.println("LPR");
  }

  // Recognize one image
  public void recognize(File file, Mat originalImg, int frameCounter) {
    System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
    Screenshot screenshot = checkScreenshot(originalImg);
    if (screenshot != null) {

      // create and clear out path for screenshots with video file name
      if (logger) {
        String fileNameWithOutExt = FilenameUtils.removeExtension(file.getName());
        outPath = outPath + fileNameWithOutExt + "\\";
        screenshotPath = outPath + frameCounter + "\\";
        clearFolder(outPath);
      }

      // filter image
      int thresh = 100;
      int blur = 5;
      Mat filteredImg = filterImage(screenshot.getOriginalImg(), thresh, blur);
      screenshot.setFilteredImg(filteredImg);


      // loop throught image contours
      processContours(screenshot);


    }
  }

  private void processContours(Screenshot screenshot) {
    int i = 0;

    for (Contour c : screenshot.getContours()) {
      Mat originalImg = screenshot.getOriginalImg().clone();
      Mat filteredImg = screenshot.getFilteredImg().clone();
      Mat originalContoursImg = screenshot.getOriginalImg().clone();
      Mat filteredContoursImg = screenshot.getFilteredImg().clone();

      Imgproc.rectangle(originalContoursImg, c.getRect().tl(), c.getRect().br(), red, 2);
      Imgproc.rectangle(filteredContoursImg, c.getRect().tl(), c.getRect().br(), red, 2);

      if (logger) {
        contourPath = screenshotPath + i + "\\";
        Imgcodecs.imwrite(contourPath + "original.jpg", originalImg);
        Imgcodecs.imwrite(contourPath + "filtered.jpg", filteredImg);
        Imgcodecs.imwrite(contourPath + "originalContours.jpg", originalContoursImg);
        Imgcodecs.imwrite(contourPath + "filteredContours.jpg", filteredContoursImg);
      }

      screenshot.setFilteredContoursImg(filteredContoursImg);
      screenshot.setOriginalContoursImg(originalContoursImg);

      i++;
    }
  }

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

  // Check if image contains valid contours (potential license plates),
  // if yes create Screenshot object with all contours data and add it to screenshots list
  private Screenshot checkScreenshot(Mat img) {
    List<Contour> contours = getContouts(img);
    if (contours != null) {
      Screenshot screenshot = new Screenshot(img, contours);
      //screenshots.add(screenshot);
      return screenshot;
    }
    return null;
  }


  // Try to get contours from image
  private List<Contour> getContouts(Mat img) {
    List<MatOfPoint> contours = new ArrayList<>();
    List<Contour> validContours = new ArrayList<>();
    Imgproc.findContours(img, contours, new Mat(), RETR_TREE, Imgproc.CHAIN_APPROX_SIMPLE);
    for (MatOfPoint c : contours) {
      MatOfPoint2f pointsArea = new MatOfPoint2f(c.toArray());
      RotatedRect rotatedRectangle = Imgproc.minAreaRect(pointsArea);
      //toDo...change to % procent size
      if ((rotatedRectangle.size.area() > 2000) && (rotatedRectangle.size.area() < 15000)) {
        Point rotatedRectPoints[] = new Point[4];
        rotatedRectangle.points(rotatedRectPoints);
        Rect rect = Imgproc.boundingRect(new MatOfPoint(rotatedRectPoints));
        rect = cutRectIfOutOfImageArea(img, rect);
        if ((rect.width > rect.height) && (rect.width < 6 * rect.height)) {
          //toDo, check average color in contour, if white >50%
          Contour contour = new Contour(c, rotatedRectangle, rect);
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
}
