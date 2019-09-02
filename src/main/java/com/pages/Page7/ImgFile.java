package com.pages.Page7;

import org.opencv.core.Mat;

import java.io.File;
import java.net.URI;

public class ImgFile extends File {


  private String licenseNumber = "- - -";
  private Mat thresholdImg = null;
  private Mat contoursImg = null;
  private Mat licensePlateImg = null;

  public ImgFile(String pathname) {
    super(pathname);
  }

  @Override
  public String toString() {
    return super.getName();
  }

  public String getLicenseNumber() {
    return licenseNumber;
  }

  public void setLicenseNumber(String licenseNumber) {
    this.licenseNumber = licenseNumber;
  }


  public Mat getThresholdImg() {
    return thresholdImg;
  }

  public void setThresholdImg(Mat thresholdImg) {
    this.thresholdImg = thresholdImg;
  }

  public Mat getContoursImg() {
    return contoursImg;
  }

  public void setContoursImg(Mat contoursImg) {
    this.contoursImg = contoursImg;
  }

  public Mat getLicensePlateImg() {
    return licensePlateImg;
  }

  public void setLicensePlateImg(Mat licensePlateImg) {
    this.licensePlateImg = licensePlateImg;
  }
}
