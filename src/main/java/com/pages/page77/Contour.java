package com.pages.page77;

import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.Rect;
import org.opencv.core.RotatedRect;

public class Contour {

  private MatOfPoint contour;
  private int index;
  private RotatedRect rotatedRect;
  private Rect rect;
  private double angle;
  private Mat plateOriginal = new Mat();
  private Mat plateGray = new Mat();
  private Mat plateRotated = new Mat();
  private Mat plateRotatedCutted = new Mat();
  private Mat plateSheared = new Mat();

  //Constructor
  public Contour(MatOfPoint contour, int index, RotatedRect rotatedRect, Rect rect) {
    this.contour = contour;
    this.index = index;
    this.rotatedRect = rotatedRect;
    this.rect = rect;
    this.angle = rotatedRect.angle;
  }

  //Getters and Setters

  public MatOfPoint getContour() {
    return contour;
  }
  public int getIndex() {
    return index;
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

  public Mat getPlateOriginal() {
    return plateOriginal;
  }

  public void setPlateOriginal(Mat plateOriginal) {
    this.plateOriginal = plateOriginal;
  }

  public Mat getPlateGray() {
    return plateGray;
  }

  public void setPlateGray(Mat plateGray) {
    this.plateGray = plateGray;
  }

  public Mat getPlateRotated() {
    return plateRotated;
  }

  public void setPlateRotated(Mat plateRotated) {
    this.plateRotated = plateRotated;
  }

  public Mat getPlateRotatedCutted() {
    return plateRotatedCutted;
  }

  public void setPlateRotatedCutted(Mat plateRotatedCutted) {
    this.plateRotatedCutted = plateRotatedCutted;
  }

  public Mat getPlateSheared() {
    return plateSheared;
  }

  public void setPlateSheared(Mat plateSheared) {
    this.plateSheared = plateSheared;
  }
}
