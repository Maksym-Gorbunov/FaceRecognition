package com.pages.Page7;

import org.opencv.core.Mat;
import org.opencv.imgcodecs.Imgcodecs;

import java.io.File;

import org.apache.commons.io.FilenameUtils;


public class ImgObject {

  private File file;
  private String licenseNumber = "";
  private Mat original;
  private Mat filtered;
  private Mat contours;
  private String outPath;

  //Constructor
  public ImgObject(File file) {
    this.file = file;
    original = Imgcodecs.imread(file.getAbsolutePath());

  }

  @Override
  public String toString() {
    return file.getName();
  }


  public void saveImages(String outPath) {
    if ((original != null) && (!original.empty())) {
      Imgcodecs.imwrite(outPath + "\\original.jpg", original);
    }
    if ((filtered != null) && (!filtered.empty())) {
      Imgcodecs.imwrite(outPath + "\\filtered.jpg", filtered);
    }
    if ((contours != null) && (!contours.empty())) {
      Imgcodecs.imwrite(outPath + "\\contours.jpg", contours);
    }
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

  public File getFile() {
    return file;
  }

  public void setFile(File file) {
    this.file = file;
  }
}
