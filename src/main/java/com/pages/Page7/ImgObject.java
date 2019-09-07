package com.pages.Page7;

import org.opencv.core.Mat;
import org.opencv.imgcodecs.Imgcodecs;

import java.io.File;

import org.apache.commons.io.FilenameUtils;


public class ImgObject extends File {

  private String licenseNumber = "";
  private Mat original = new Mat();
  private Mat filtered = new Mat();
  private Mat contours = new Mat();

  //Constructor
  public ImgObject(File file) {
    super(file.getAbsolutePath());
    original = Imgcodecs.imread(file.getAbsolutePath());
  }

  @Override
  public String toString() {
    return super.getName();
  }


  public void saveImages(String path) {
    Imgcodecs.imwrite(path+"\\original.jpg", original);
    Imgcodecs.imwrite(path+"\\filtered.jpg", filtered);
    Imgcodecs.imwrite(path+"\\contours.jpg", contours);
  }

  // Getters & Settters

  public String getLicenseNumber() {
    return licenseNumber;
  }

  public void setLicenseNumber(String licenseNumber) {
    this.licenseNumber = licenseNumber;
  }

  public Mat getOriginal() {
    return original;
  }

  public void setOriginal(Mat original) {
    this.original = original;
  }

  public Mat getFiltered() {
    return filtered;
  }

  public void setFiltered(Mat filtered) {
    this.filtered = filtered;
  }

  public Mat getContours() {
    return contours;
  }

  public void setContours(Mat contours) {
    this.contours = contours;
  }
}
