package com.pages.page77;

import org.opencv.core.Mat;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class Screenshot {

  private Mat originalImg = new Mat();
  private Mat grayImg = new Mat();
  private Mat filteredImg = new Mat();
  private Mat originalContoursImg = new Mat();
  private Mat filteredContoursImg = new Mat();
//  private Mat plateOriginal = new Mat();
//  private Mat plateGray = new Mat();
//  private Mat plateRotated = new Mat();
//  private Mat plateRotatedCutted = new Mat();
//  private Mat plateSheared = new Mat();
  private Contour resultContour;
  private List<Contour> contours = new ArrayList<>();

  // Constructor
  public Screenshot(Mat img) {
    this.originalImg = img;
    Imgproc.cvtColor(img, grayImg, Imgproc.COLOR_RGB2GRAY);
  }
  public Screenshot(Mat img, List<Contour> contours) {
    this.originalImg = img;
    Imgproc.cvtColor(img, grayImg, Imgproc.COLOR_RGB2GRAY);
    this.contours = contours;
  }

  //Getters and Setters
  public Mat getOriginalImg() {
    return originalImg;
  }
  public void setOriginalImg(Mat originalImg) {
    this.originalImg = originalImg;
  }
  public Mat getGrayImg() {
    return grayImg;
  }
  public void setGrayImg(Mat grayImg) {
    this.grayImg = grayImg;
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
  public Mat getFilteredContoursImg() {
    return filteredContoursImg;
  }
  public void setFilteredContoursImg(Mat filteredContoursImg) {
    this.filteredContoursImg = filteredContoursImg;
  }
//  public Mat getPlateOriginal() {
//    return plateOriginal;
//  }
//  public void setPlateOriginal(Mat plateOriginal) {
//    this.plateOriginal = plateOriginal;
//  }
//  public Mat getPlateGray() {
//    return plateGray;
//  }
//  public void setPlateGray(Mat plateGray) {
//    this.plateGray = plateGray;
//  }
//  public Mat getPlateRotated() {
//    return plateRotated;
//  }
//  public void setPlateRotated(Mat plateRotated) {
//    this.plateRotated = plateRotated;
//  }
//  public Mat getPlateRotatedCutted() {
//    return plateRotatedCutted;
//  }
//  public void setPlateRotatedCutted(Mat plateRotatedCutted) {
//    this.plateRotatedCutted = plateRotatedCutted;
//  }
//  public Mat getPlateSheared() {
//    return plateSheared;
//  }
//  public void setPlateSheared(Mat plateSheared) {
//    this.plateSheared = plateSheared;
//  }
  public List<Contour> getContours() {
    return contours;
  }
  public void setContours(List<Contour> contours) {
    this.contours = contours;
  }
  public  void addContour(Contour c){
    contours.add(c);
  }

  // Save images
  public void saveImages(String path) {
    Imgcodecs.imwrite(path+"original.jpg", originalImg);
    if(!filteredImg.empty()){
      Imgcodecs.imwrite(path+"filtered.jpg", filteredImg);
    }
    if(!originalContoursImg.empty()){
      Imgcodecs.imwrite(path+"originalContours.jpg", originalContoursImg);
    }
    if(!filteredContoursImg.empty()){
      Imgcodecs.imwrite(path+"filteredContours.jpg", filteredContoursImg);
    }


    if(contours.size() > 0){
      for(Contour c : contours){
        String contourPath = path+c.getIndex()+"\\";
        new File(contourPath).mkdirs();

        if(!c.getPlateOriginal().empty()){
          Imgcodecs.imwrite(contourPath+"plateOriginal.jpg", c.getPlateOriginal());
        }
        if(!c.getPlateGray().empty()){
          Imgcodecs.imwrite(contourPath+"plateGray.jpg", c.getPlateGray());
        }
        if(!c.getPlateRotated().empty()){
          Imgcodecs.imwrite(contourPath+"plateRotated.jpg", c.getPlateRotated());
        }
        if(!c.getPlateRotatedCutted().empty()){
          Imgcodecs.imwrite(contourPath+"plateRotatedCutted.jpg", c.getPlateRotatedCutted());
        }
        if(!c.getPlateSheared().empty()){
          Imgcodecs.imwrite(contourPath+"plateSheared.jpg", c.getPlateSheared());
        }
      }
    }

  }
}
