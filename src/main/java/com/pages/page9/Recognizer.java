package com.pages.page9;

import com.constants.Constants;
import com.pages.page8.Webcam;
import org.apache.commons.io.FileUtils;
import org.opencv.core.*;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

import javax.imageio.ImageIO;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.opencv.imgproc.Imgproc.RETR_TREE;

public class Recognizer implements Runnable {

  public static String screenshotPath = "";
  protected volatile boolean runnable = false;
  private Scalar blue = new Scalar(255, 0, 0, 255);
  private Scalar green = new Scalar(0, 255, 0, 255);
  private Scalar red = new Scalar(0, 0, 255, 255);
  //private Scalar gray = new Scalar(20, 20, 20, 255);
  private Scalar randomColor = new Scalar(Math.random() * 255, Math.random() * 255, Math.random() * 255, 0);
  private boolean logger = true;
  private List<Contour> contours = null;
  private Mat frame = new Mat();
  private Mat frameGray = new Mat();
  private int frameCount;


  public Recognizer(Mat frame, int frameCount, String screenshotPath) {
    this.screenshotPath = screenshotPath;
    this.frame = frame;
    this.frameCount = frameCount;
  }

  public static void main(String[] args) {
    System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
    String pth = Constants.imgPath + "aaa\\";
    Mat frame = Imgcodecs.imread(pth + "33.jpg");
//    Mat frame1 = Imgcodecs.imread(pth + "1.jpg");
//    Mat frame2 = Imgcodecs.imread(pth + "2.jpg");
//    Mat frame3 = Imgcodecs.imread(pth + "color.jpg");
    Recognizer r = new Recognizer(frame, 0, pth);
    Mat gray = new Mat();
    Imgproc.cvtColor(frame, gray, Imgproc.COLOR_BGR2GRAY);
    Mat filtered = r.filterImage(gray,100,5);
    Imgcodecs.imwrite(pth+"filtered.jpg", filtered);

    r.getContoursFromPlate(frame,filtered);



//    //Scalar lowerBlue = new Scalar(15, 30, 110);
//    //Scalar upperBlue = new Scalar(150, 255, 255);
//    Scalar lowerBlue = new Scalar(20, 100, 100);
//    Scalar upperBlue = new Scalar(255, 255, 255);
//
//
//    Mat result1 = r.removeColor(frame1, lowerBlue, upperBlue);
//    Imgcodecs.imwrite(pth+"result1.jpg", result1);
//
//    frame2.convertTo(frame2,-1,1,80);
//    Mat result2 = r.removeColor(frame2, lowerBlue, upperBlue);
//    Imgcodecs.imwrite(pth+"result2.jpg", result2);
//
//    Mat result3 = r.removeColor(frame3, lowerBlue, upperBlue);
//    Imgcodecs.imwrite(pth+"result3.jpg", result3);
//
//
////    Mat brigth = new Mat();
////    frame2.convertTo(brigth,-1,1,100);
////    Imgcodecs.imwrite(pth+"result1_bright.jpg", brigth);

  }

  private void getContoursFromPlate(Mat colorPlate, Mat filteredPlate){
    List<MatOfPoint> tempContours = new ArrayList<>();
    Mat colorPlateCopy = new Mat();
    colorPlate.copyTo(colorPlateCopy);
    Imgproc.findContours(filteredPlate, tempContours, new Mat(), RETR_TREE, Imgproc.CHAIN_APPROX_SIMPLE);
    int i = 1;
    double startX = colorPlate.width();
    double startY = colorPlate.height();
    double endX = 0;
    double endY = 0;
    for (MatOfPoint c : tempContours) {
      MatOfPoint2f pointsArea = new MatOfPoint2f(c.toArray());
      RotatedRect rotatedRectangle = Imgproc.minAreaRect(pointsArea);
      Point rotatedRectPoints[] = new Point[4];
      rotatedRectangle.points(rotatedRectPoints);
      Rect rect = Imgproc.boundingRect(new MatOfPoint(rotatedRectPoints));

      rect = cutRectIfOutOfImageArea(filteredPlate, rect);



        if( (rect.height>0.5*filteredPlate.height()) && (rect.height<0.95*filteredPlate.height()) ) {

          Imgproc.rectangle(colorPlate, rect.tl(), rect.br(), blue, 1);
          System.out.println(rotatedRectangle.angle);

          if(rect.tl().x<startX){
            startX = rect.tl().x;
          }
          if(rect.tl().y<startY){
            startY = rect.tl().y;
          }

          if(rect.br().x>endX){
            endX = rect.br().x;
          }
          if(rect.br().y>endY){
            endY = rect.br().y;
          }


        }

    }
    String pth = Constants.imgPath + "aaa\\";
    Imgcodecs.imwrite(pth+"contours.jpg", colorPlate);

    Point start = new Point(startX, startY);
    Point end = new Point(endX, endY);

    Imgproc.rectangle(colorPlate, start, end, red, 2);
    Imgcodecs.imwrite(pth+"contours2.jpg", colorPlate);

    Mat result = new Mat(colorPlateCopy, new Rect(start, end));
    Imgcodecs.imwrite(pth+"result.jpg", result);


  }

  private Mat removeColor(Mat src, Scalar lowerColor, Scalar upperColor){
    Mat mask = new Mat();
    Core.inRange(src, lowerColor, upperColor, mask);
    Imgproc.cvtColor(mask, mask, Imgproc.COLOR_GRAY2RGB);
    Mat result = new Mat();
    Core.subtract(mask, src, result);
    Core.subtract(mask, result, result);
    return result;
  }

  private Mat removeBlueColor(Mat src){
    Scalar lowerBlue = new Scalar(15, 30, 110);
    Scalar upperBlue = new Scalar(150, 255, 255);
    Mat mask = new Mat();
    Core.inRange(src, lowerBlue, upperBlue, mask);
    Imgproc.cvtColor(mask, mask, Imgproc.COLOR_GRAY2RGB);
    Mat result = new Mat();
    Core.subtract(mask, src, result);
    Core.subtract(mask, result, result);
    return result;
  }

  // Convert BufferedImage to Mat
  public static Mat bufferedImageToMat(BufferedImage bi) {
    Mat mat = new Mat(bi.getHeight(), bi.getWidth(), CvType.CV_8UC1);
    byte[] data = ((DataBufferByte) bi.getRaster().getDataBuffer()).getData();
    mat.put(0, 0, data);
    return mat;
  }

  @Override
  public void run() {
    synchronized (this) {
      while (runnable) {
        recognize();
      }
    }
  }

  public void recognize() {
    Imgproc.cvtColor(frame, frameGray, Imgproc.COLOR_RGB2GRAY);

    contours = findContouts(100);
    if (contours == null) {
      return;
    }

//    String contourPath = screenshotPath + frameCount + "\\";
//    new File(contourPath).mkdirs();
    int i = 0;
    for (Contour c : contours) {
//      Mat plate = new Mat(frameGray, c.getRect());
      Mat plate = new Mat(frame, c.getRect());
      Mat rotatedPlate = rotateImage(plate, c.getRotatedRect());
      Mat cuttedRotatedPlate = cutPlateFromRotatedPlate(rotatedPlate, c.getRotatedRect());

      ////
//      cuttedRotatedPlate = removeBlueColor(cuttedRotatedPlate);


      //recognize v 1.0
      String text = TextRecognizer.recognizeText(cuttedRotatedPlate);
      if (text.length() > 5) {
//        System.out.println(text);
        if (!Page9.results.contains(text)) {
          Page9.results.add(text);
          Page9.rect = c.getRect();
          System.out.println(text);
          Imgcodecs.imwrite(screenshotPath + "plate_"+frameCount+"_" + i + ".jpg", cuttedRotatedPlate);

        }

      } else {
        System.out.println("---");
        Page9.rect = null;
      }

      i++;
    }
    runnable = false;
  }

  // Convert Mat to BufferedImage
  private BufferedImage mat2BufferedImage(Mat matrix) throws Exception {
    MatOfByte mob = new MatOfByte();
    Imgcodecs.imencode(".jpg", matrix, mob);
    byte ba[] = mob.toArray();
    BufferedImage bi = ImageIO.read(new ByteArrayInputStream(ba));
    return bi;
  }

  // Filter image
  public Mat filterImage(Mat gray, int thresh, int blur) {
    Mat grayImg = new Mat();
    gray.copyTo(grayImg);
    Mat topHatImg = new Mat();
    Mat blackHatImg = new Mat();
    Mat grayPlusTopHatImg = new Mat();
    Mat grayPlusTopHatMinusBlackHatImg = new Mat();
    Mat blurImg = new Mat();
    Mat thresholdImg = new Mat();
    Mat kernel = new Mat(new Size(3, 3), CvType.CV_8U, new Scalar(255));
//    Imgproc.cvtColor(tempImg, grayImg, Imgproc.COLOR_RGB2GRAY);
    Imgproc.morphologyEx(grayImg, topHatImg, Imgproc.MORPH_TOPHAT, kernel);
    Imgproc.morphologyEx(grayImg, blackHatImg, Imgproc.MORPH_BLACKHAT, kernel);
    Core.add(grayImg, topHatImg, grayPlusTopHatImg);
    Core.subtract(grayPlusTopHatImg, blackHatImg, grayPlusTopHatMinusBlackHatImg);
    Imgproc.GaussianBlur(grayPlusTopHatMinusBlackHatImg, blurImg, new Size(blur, blur), 1);
    Imgproc.threshold(blurImg, thresholdImg, thresh, 255, Imgproc.THRESH_BINARY_INV);
    return thresholdImg;
  }

  //shear cutted plate with rotated rectangle angle or slider
  public Mat shearImage(Mat cuttedPlate, RotatedRect rotatedRect) {
    Mat grayImg = new Mat();
    Imgproc.cvtColor(cuttedPlate, grayImg, Imgproc.COLOR_RGB2GRAY);
    double x = 0;

    if (rotatedRect.size.width > rotatedRect.size.height) {
//        System.out.println("plus");
      x = 0.2;
    } else {
//        System.out.println("minus");
      x = -0.45;
    }
    System.out.println("angle=" + (int) rotatedRect.angle + ", x=" + x);
    BufferedImage buffer = null;
    try {
      buffer = mat2BufferedImage(grayImg);
      AffineTransform tx = new AffineTransform();
      //tx.translate(buffer.getHeight() / 2, buffer.getWidth() / 2);
      tx.shear(x, 0);
      //tx.shear(-0.4, 0);
      //tx.translate(-buffer.getWidth() / 2, -buffer.getHeight() / 2);
      AffineTransformOp op = new AffineTransformOp(tx, AffineTransformOp.TYPE_BILINEAR);
      BufferedImage shearedPLateBuffered = new BufferedImage(buffer.getWidth(), buffer.getHeight(), buffer.getType());
      op.filter(buffer, shearedPLateBuffered);
      Mat shearedPLate = bufferedImageToMat(shearedPLateBuffered);
      return shearedPLate;
    } catch (Exception e) {
      e.printStackTrace();
    }
    return cuttedPlate;
  }

  // Filter colored image, thresh only
  public Mat filterColoredImage(Mat img, int thresh, int blur) {
    Mat grayImg = new Mat();
//    gray.copyTo(grayImg);
    Mat topHatImg = new Mat();
    Mat blackHatImg = new Mat();
    Mat grayPlusTopHatImg = new Mat();
    Mat grayPlusTopHatMinusBlackHatImg = new Mat();
    Mat blurImg = new Mat();
    Mat thresholdImg = new Mat();
    Mat kernel = new Mat(new Size(3, 3), CvType.CV_8U, new Scalar(255));
    Imgproc.cvtColor(img, grayImg, Imgproc.COLOR_RGB2GRAY);
    Imgproc.morphologyEx(grayImg, topHatImg, Imgproc.MORPH_TOPHAT, kernel);
    Imgproc.morphologyEx(grayImg, blackHatImg, Imgproc.MORPH_BLACKHAT, kernel);
    Core.add(grayImg, topHatImg, grayPlusTopHatImg);
    Core.subtract(grayPlusTopHatImg, blackHatImg, grayPlusTopHatMinusBlackHatImg);
    Imgproc.GaussianBlur(grayPlusTopHatMinusBlackHatImg, blurImg, new Size(blur, blur), 1);
    Imgproc.threshold(blurImg, thresholdImg, thresh, 255, Imgproc.THRESH_BINARY_INV);
    return thresholdImg;
  }


  // Cut off contours rectangle if out off image area
  private Rect cutRectIfOutOfImageArea(Mat image, Rect rect) {
    double startX = rect.tl().x;
    double startY = rect.tl().y;
    double endX = rect.br().x;
    double endY = rect.br().y;
    if (startX < 0) {
      startX = 0;
    }
    if (startY < 0) {
      startY = 0;
    }
    if (endX > image.width()) {
      endX = image.width();
    }
    if (endY > image.height()) {
      endY = image.height();
    }
    Rect cuttedRect = new Rect(new Point(startX, startY), new Point(endX, endY));
    return cuttedRect;
  }

  // Cut plate from rotated plate image
  private Mat cutPlateFromRotatedPlate(Mat img, RotatedRect rotatedRect) {
    double width = 0;
    if (rotatedRect.size.width > img.size().height) {
      width = rotatedRect.size.height;
    } else {
      width = rotatedRect.size.width;
    }
    Point startPoint = new Point(0, (img.height() / 2) - (width / 2));
    Point endPoint = new Point(img.width(), (img.height() / 2) + (width / 2));
    if (width != 0) {
      Mat plate = new Mat(img, new Rect(startPoint, endPoint));
      return plate;
    }
    return img;
  }


  // Rotate license plate image
  private Mat rotateImage(Mat img, RotatedRect rotatedRect) {
    double angle = rotatedRect.angle;
    if (rotatedRect.size.width > rotatedRect.size.height) {
      angle = -angle;
    }
    double rotatedAngle = 0;
    if (angle == 0) {
      return img;
    }
    if (angle < 0) {
      rotatedAngle = 90 - Math.abs(angle);
    }
    if (angle > 0) {
      rotatedAngle = -angle;
    }
    Mat temp = new Mat();
    img.copyTo(temp);
    Mat rotatedImg = new Mat(2, 3, CvType.CV_32FC1);
    Mat destination = new Mat(img.rows(), img.cols(), img.type());
    Point center = new Point(destination.cols() / 2, destination.rows() / 2);
    rotatedImg = Imgproc.getRotationMatrix2D(center, rotatedAngle, 1);
    Imgproc.warpAffine(temp, destination, rotatedImg, destination.size());
    return destination;
  }


  // Clear folder from old files
  public void clearFolder(String path) {
    try {
      FileUtils.deleteDirectory(new File(path));
    } catch (IOException e) {
      e.printStackTrace();
    }
    new File(path).mkdirs();
  }

  // Try to get contours from image
  private List<Contour> findContouts(int thresh) {
    Mat filteredImg = filterImage(frameGray, thresh, 5);
    List<MatOfPoint> tempContours = new ArrayList<>();
    List<Contour> contours = null;
    Imgproc.findContours(filteredImg, tempContours, new Mat(), RETR_TREE, Imgproc.CHAIN_APPROX_SIMPLE);
    double imgArea = filteredImg.size().area();
    int i = 1;
    for (MatOfPoint c : tempContours) {
      MatOfPoint2f pointsArea = new MatOfPoint2f(c.toArray());
      RotatedRect rotatedRectangle = Imgproc.minAreaRect(pointsArea);
      double rotRectArea = rotatedRectangle.size.area();
      if ((rotRectArea > 0.01 * imgArea) && (rotRectArea < 0.4 * imgArea)) {
        Point rotatedRectPoints[] = new Point[4];
        rotatedRectangle.points(rotatedRectPoints);
        Rect rect = Imgproc.boundingRect(new MatOfPoint(rotatedRectPoints));
        rect = cutRectIfOutOfImageArea(filteredImg, rect);
        if ((rect.width > rect.height) && (rect.width < 6 * rect.height)) {
          if (contours == null) {
            contours = new ArrayList<>();
          }
          Contour contour = new Contour(c, rotatedRectangle, rect);
          contours.add(contour);
          i++;
        }
      }
    }
    if (contours != null) {
      return contours;
    }
    return null;
  }


}
