package com.pages.page9;


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


public class LPR {
  private List<Screenshot> screenshots = new ArrayList<>();

  public LPR() {
    System.out.println("LPR");
  }

  // Loop throught all images and try to find valid
  public void filterScreenshots(Mat screenshot) {
    checkScreenshot(screenshot);
  }

  // Check if image contains valid contours (potential license plates),
  // if yes create Screenshot object with all contours data and add it to screenshots list
  private void checkScreenshot(Mat screenshot) {
    List<MatOfPoint> contours = new ArrayList<>();
    List<MatOfPoint> validContours = new ArrayList<>();
    Imgproc.findContours(screenshot, contours, new Mat(), RETR_TREE, Imgproc.CHAIN_APPROX_SIMPLE);
    for (MatOfPoint c : contours) {
      MatOfPoint2f pointsArea = new MatOfPoint2f(c.toArray());
      RotatedRect rotatedRectangle = Imgproc.minAreaRect(pointsArea);
      if ((rotatedRectangle.size.area() > 2000) && (rotatedRectangle.size.area() < 15000)) {
        Point rotatedRectPoints[] = new Point[4];
        rotatedRectangle.points(rotatedRectPoints);
        Rect rect = Imgproc.boundingRect(new MatOfPoint(rotatedRectPoints));
        rect = cutRectIfOutOfImageArea(screenshot, rect);
        if ((rect.width > rect.height) && (rect.width < 6 * rect.height)) {
          //toDo, check average color in contour, if white >50%
          Contour contour = new Contour(c, rotatedRectangle, rect);




          Imgproc.rectangle(filteredImg, rect.tl(), rect.br(), red, 3);
          Imgproc.rectangle(contoursImg, rect.tl(), rect.br(), red, 3);


          validContours.add(c);
        }
      }
    }
    if (validContours.size() > 0) {
      object.setFiltered(filteredImg);
      object.setContours(contoursImg);
      return validContours;
    }
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
