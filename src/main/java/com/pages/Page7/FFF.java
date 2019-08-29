package com.pages.Page7;


import com.constants.Constants;
import org.opencv.core.Core;

public class FFF {
  public static void main(String[] args) {
    System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
    LicensePlateRecognition lpr = new LicensePlateRecognition();
    lpr.findLicensePlate(Constants.imgPath+"cars\\1.jpg");
  }
}

