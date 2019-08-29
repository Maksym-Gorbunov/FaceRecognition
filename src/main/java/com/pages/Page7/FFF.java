package com.pages.Page7;


import com.constants.Constants;
import org.opencv.core.Core;

public class FFF {
  public static void main(String[] args) {
    System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
//    LicensePlateRecognition lpr = new LicensePlateRecognition();
//    lpr.findLicensePlate(Constants.imgPath+"cars\\6.jpg");
    LPR lpr2 = new LPR();
    lpr2.findLicensePlate(Constants.imgPath+"cars\\1.jpg");
//    lpr2.findLicensePlate(Constants.imgPath+"cars\\test_004.jpg");

//    System.out.println(lpr.recognizeText(Constants.imgPath+""));
  }
}

