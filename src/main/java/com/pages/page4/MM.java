package com.pages.page4;

import com.constants.Constants;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.imgcodecs.Imgcodecs;

public class MM {
  public static void main(String[] args) {
    System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
    MM m = new MM();
    m.smileTest();
  }

  public void smileTest() {
    String path = Constants.imgPath+"face\\";
    Mat img = Imgcodecs.imread(path+"face23.jpg");
//    Mat filteredImg = some code
  }
}
