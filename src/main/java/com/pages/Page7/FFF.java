package com.pages.Page7;


import com.constants.Constants;
import org.opencv.core.Core;

public class FFF {
  public static void main(String[] args) {
    System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
//    LicensePlateRecognition lpr = new LicensePlateRecognition();
//    lpr.findLicensePlate(Constants.imgPath+"cars\\6.jpg");

    LicensePlateRecognizer lpr2 = new LicensePlateRecognizer();
//    lpr2.findLicensePlate(Constants.imgPath+"cars\\1.jpg", 100);
    lpr2.findLicensePlate(Constants.imgPath+"cars\\6.jpg", 200);

//    lpr2.findLicensePlate(Constants.imgPath+"cars\\test_001.jpg", 100);
//    lpr2.findLicensePlate(Constants.imgPath+"cars\\test_001.jpg", 200);

//    for (int i = 1; i <= 9; i++) {
//      System.out.print(i+":  ");
//      lpr2.findLicensePlate(Constants.imgPath + "cars\\test_00"+i+".jpg", 80);
//
//    }

  }
}

