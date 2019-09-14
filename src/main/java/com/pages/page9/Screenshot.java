package com.pages.page9;

import org.opencv.core.Mat;

import java.util.ArrayList;
import java.util.List;

public class Screenshot {

  private Mat originalImg;
  private Mat filteredImg;
  private Mat originalContoursImg;
  private Mat filteredContoursImg;
  private List<Contour> contours = new ArrayList<>();

  // Constructor
  public Screenshot(Mat img, List<Contour> contours) {
    this.originalImg = img;
    this.contours = contours;
  }

  //Getters and Setters

  public Mat getOriginalImg() {
    return originalImg;
  }

  public void setOriginalImg(Mat originalImg) {
    this.originalImg = originalImg;
  }

  public Mat getFilteredImg() {
    return filteredImg;
  }

  public void setFilteredImg(Mat filteredImg) {
    this.filteredImg = filteredImg;
  }

  public Mat getOriginalContoursImg() {
    return originalContoursImg;
  }

  public void setOriginalContoursImg(Mat originalContoursImg) {
    this.originalContoursImg = originalContoursImg;
  }

  public List<Contour> getContours() {
    return contours;
  }

  public void setContours(List<Contour> contours) {
    this.contours = contours;
  }

  public Mat getFilteredContoursImg() {
    return filteredContoursImg;
  }

  public void setFilteredContoursImg(Mat filteredContoursImg) {
    this.filteredContoursImg = filteredContoursImg;
  }
}
