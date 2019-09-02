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
    Mat colors = Imgcodecs.imread(Constants.imgPath+"cars\\4.jpg");
    lpr.test(colors);

  }
}

