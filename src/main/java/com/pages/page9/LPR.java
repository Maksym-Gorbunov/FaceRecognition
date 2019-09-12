package com.pages.page9;


import org.opencv.core.*;
import org.opencv.imgproc.Imgproc;

import java.util.ArrayList;
import java.util.List;

import static org.opencv.imgproc.Imgproc.RETR_TREE;


public class LPR {

//  private List<Screenshot> screenshots = new ArrayList<>();

  // Constructor
  public LPR() {
    System.out.println("LPR");
  }

  // Recognize one image
  public void recognize(Mat frame) {
  }

  // Loop throught all images and try to find valid
  public void filterScreenshots(Mat screenshot) {
    checkScreenshot(screenshot);
  }

  // Check if image contains valid contours (potential license plates),
  // if yes create Screenshot object with all contours data and add it to screenshots list
  private Screenshot checkScreenshot(Mat img) {
    List<Contour> contours = getContouts(img);
    if(contours != null) {
      Screenshot screenshot = new Screenshot(img, contours);
      //screenshots.add(screenshot);
      return screenshot;
    }
    return null;
  }


  // Try to get contours from image
  private List<Contour> getContouts(Mat img){
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
          if(contour != null){
            validContours.add(contour);
          }
        }
      }
    }
    if((validContours != null) && (!validContours.isEmpty()) ){
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


}
