package com.pages.Page11;

import com.constants.Constants;
import org.opencv.core.Mat;
import org.opencv.core.MatOfRect;
import org.opencv.core.Rect;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.objdetect.CascadeClassifier;

import java.util.ArrayList;
import java.util.List;

public class Data {
  public static CascadeClassifier faceDetector = new CascadeClassifier(Constants.CASCADE_CLASSIFIER);
  public static CascadeClassifier smileDetector = new CascadeClassifier(Constants.projectPath + "\\lib\\haarcascade_smile.xml");
  public static CascadeClassifier eyesDetector = new CascadeClassifier(Constants.projectPath + "\\lib\\haarcascade_eye.xml");
  public static Rect[] faceRectangles;

  public static Mat hideImg = Imgcodecs.imread(Constants.imgPath + "synteda.jpg");
  // rect here ???
}
