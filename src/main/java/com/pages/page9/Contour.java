package com.pages.page9;

import org.opencv.core.MatOfPoint;
import org.opencv.core.Rect;
import org.opencv.core.RotatedRect;

public class Contour {
  private MatOfPoint contour;
  private RotatedRect rotatedRect;
  private Rect rect;
  private double angle;
//  private int index;

  // Constructor
  public Contour(MatOfPoint contour, RotatedRect rotatedRect, Rect rect) {
    this.contour = contour;
    this.rotatedRect = rotatedRect;
    this.rect = rect;
    angle = rotatedRect.angle;
  }

  // Getters and Setters

  public MatOfPoint getContour() {
    return contour;
  }

  public RotatedRect getRotatedRect() {
    return rotatedRect;
  }

  public Rect getRect() {
    return rect;
  }

  public double getAngle() {
    return angle;
  }
}
