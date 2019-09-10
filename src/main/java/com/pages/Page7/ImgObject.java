package com.pages.Page7;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.Size;
import org.opencv.imgcodecs.Imgcodecs;

import java.io.File;

import org.opencv.imgproc.Imgproc;
import java.awt.image.BufferedImage;

public class ImgObject {

  private File file;
  private String licenseNumber = "";
  private Mat original;
  private Mat filtered;
  private Mat contours;
  private Mat plate;
  private Mat filteredPlate;
  private Mat shearedPlate;
  private String outPath;


  public ImgObject() {
    System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
  }

  //Constructor
  public ImgObject(File file) {
    System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
    this.file = file;
    Mat largeImage = Imgcodecs.imread(file.getAbsolutePath());
    float w = largeImage.width();
    float h = largeImage.height();
    float ratio = w / h;
    w = 800;
    h = w / ratio;
    original = new Mat();
    Imgproc.resize(largeImage, original, new Size(w, h));
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

  public Mat getPlate() {
    return plate;
  }

  public void setPlate(Mat plate) {
    this.plate = plate;
  }

  public Mat getFilteredPlate() {
    return filteredPlate;
  }

  public void setFilteredPlate(Mat filteredPlate) {
    this.filteredPlate = filteredPlate;
  }

  public Mat getShearedPlate() {
    return shearedPlate;
  }

  public void setShearedPlate(Mat shearedPlate) {
    this.shearedPlate = shearedPlate;
  }
}
