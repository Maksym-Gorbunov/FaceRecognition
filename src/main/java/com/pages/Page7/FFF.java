package com.pages.Page7;


import com.constants.Constants;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.imgcodecs.Imgcodecs;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class FFF {
  public static void main(String[] args) {

    System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
    LicensePlateRecognizer lpr = new LicensePlateRecognizer();
    Mat colors = Imgcodecs.imread(Constants.imgPath+"cars\\2.jpg");
    lpr.test(colors);
//    lpr.findLicensePlate(Constants.imgPath+"cars\\6.jpg");

//    LicensePlateRecognizer lpr2 = new LicensePlateRecognizer();
//    lpr2.findLicensePlate(Constants.imgPath+"cars\\1.jpg", 100);
//    lpr2.findLicensePlate(Constants.imgPath+"cars\\6.jpg", 200);

//    lpr2.findLicensePlate(Constants.imgPath+"cars\\test_001.jpg", 100);
//    lpr2.findLicensePlate(Constants.imgPath+"cars\\test_001.jpg", 200);

//    for (int i = 1; i <= 9; i++) {
//      System.out.print(i+":  ");
//      lpr2.findLicensePlate(Constants.imgPath + "cars\\test_00"+i+".jpg", 80);
//
//    }

  }
}

