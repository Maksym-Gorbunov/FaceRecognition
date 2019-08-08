package com.pages.page4;//package com.pages.page4;
//
//import com.constants.Constants;
//import org.opencv.core.*;
//import org.opencv.imgproc.Imgproc;
//import org.opencv.objdetect.CascadeClassifier;
//
//
//public class Detector {
//  private static final long serialVersionUID = 1L;
//  private MatOfRect detectedFaces;
//  private Mat coloredImage;
//  private Mat greyImage;
//  private CascadeClassifier cascadeClassifier;
//
//
//  public Detector() {
//    this.detectedFaces = new MatOfRect();
//    this.coloredImage = new Mat();
//    this.greyImage = new Mat();
//    this.cascadeClassifier = new CascadeClassifier(Constants.CASCADE_CLASSIFIER);
//  }
//
//  public Mat detect(Mat inputFrame) {
//    inputFrame.copyTo(coloredImage);
//    inputFrame.copyTo(greyImage);
//
//    // Convert input image from one color space to another
//    Imgproc.cvtColor(coloredImage, greyImage, Imgproc.COLOR_BGR2GRAY);
//    // Equalizes the histogram of a greyscale image
//    Imgproc.equalizeHist(greyImage, greyImage);
//
//    cascadeClassifier.detectMultiScale(greyImage, detectedFaces);
//
//    showFacesOnSCreen();
//    return coloredImage;
//
//  }
//
//  private void showFacesOnSCreen() {
//    // Paint rectangles around recognized faces
//    for (Rect rect : detectedFaces.toArray()) {
//      Imgproc.rectangle(coloredImage,
//              new Point(rect.x, rect.y),
//              new Point(rect.x + rect.width, rect.y + rect.height),
//              new Scalar(100, 100, 250), 10);
//    }
//  }
//
//}
