package com.pages.Page7;


import com.constants.Constants;
import net.sourceforge.tess4j.Tesseract;
import net.sourceforge.tess4j.TesseractException;
import org.opencv.core.*;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.RotatedRect;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

import static com.constants.Constants.imgPath;
import static org.opencv.imgproc.Imgproc.*;

import org.opencv.core.CvType;
import org.opencv.core.Mat;

import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import java.io.ByteArrayInputStream;

import org.opencv.core.MatOfByte;


public class LPR {
  private Scalar blue = new Scalar(255, 0, 0, 255);
  private Scalar green = new Scalar(0, 255, 0, 255);
  private Scalar red = new Scalar(0, 0, 255, 255);
  private Scalar randomColor = new Scalar(Math.random() * 255, Math.random() * 255, Math.random() * 255, 0);
  private Mat originalImg;
  private Mat originalContoursImg;
  private Mat filteredContoursImg;

  private BufferedImage Mat2BufferedImage(Mat matrix) throws Exception {
    MatOfByte mob = new MatOfByte();
    Imgcodecs.imencode(".jpg", matrix, mob);
    byte ba[] = mob.toArray();
    BufferedImage bi = ImageIO.read(new ByteArrayInputStream(ba));
    return bi;
  }

  public void recognize(String path) {
    System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
    Mat original = Imgcodecs.imread(path);
    originalImg = new Mat();
    originalContoursImg = new Mat();
    filteredContoursImg = new Mat();
    int thresh = 150;
    Mat filtered = filterImage(original, thresh);
    original.copyTo(originalImg);
    original.copyTo(originalContoursImg);
    filtered.copyTo(filteredContoursImg);

    contours(filtered);


    if (!filteredContoursImg.empty()) {
      Imgcodecs.imwrite(imgPath + "aaa\\filteredContoursImage.jpg", filteredContoursImg);
    }
    if (!originalContoursImg.empty()) {
      Imgcodecs.imwrite(imgPath + "aaa\\contoursImage.jpg", originalContoursImg);
    }
    System.out.println("...done...");
  }

  private void contours(Mat filtered) {
    Mat rotated = new Mat();
    Mat rotated1 = new Mat();
    Mat rotated2 = new Mat();
    BufferedImage buffPlate1;
    BufferedImage buffPlate2;
    List<MatOfPoint> contours = new ArrayList<>();
    Imgproc.findContours(filtered, contours, new Mat(), RETR_TREE, Imgproc.CHAIN_APPROX_SIMPLE);
    int i = 0;
    Mat contourImage = new Mat();
    for (MatOfPoint contour : contours) {
      MatOfPoint2f pointsArea = new MatOfPoint2f(contour.toArray());
      RotatedRect rotatedRectangle = Imgproc.minAreaRect(pointsArea);
      if ((rotatedRectangle.size.area() > 2000) && (rotatedRectangle.size.area() < 15000)) {
        Point rotatedRectPoints[] = new Point[4];
        rotatedRectangle.points(rotatedRectPoints);
        Rect rect = Imgproc.boundingRect(new MatOfPoint(rotatedRectPoints));
        if ((rect.width > rect.height) && (rect.width < 6 * rect.height)) {
          //toDo, check average color in contour, if white >50%
          Imgproc.rectangle(originalContoursImg, rect.tl(), rect.br(), red, 3);
          Imgproc.rectangle(filteredContoursImg, rect.tl(), rect.br(), red, 3);
//          contourImage = new Mat(filtered, rect);
          contourImage = new Mat(filtered, rect);
          Imgcodecs.imwrite(imgPath + "aaa\\" + i + ".jpg", contourImage);
          //toDo rotate and transform, get angle from rect, work with original or filtered???
          int angle = (int) rotatedRectangle.angle;
          rotated1 = rotateImage(contourImage, angle);
          rotated2 = rotateImage(contourImage, -angle);
          Imgcodecs.imwrite(imgPath + "aaa\\rotated" + i + "A.jpg", rotated1);
          Imgcodecs.imwrite(imgPath + "aaa\\rotated" + i + "B.jpg", rotated2);

          buffPlate1 = cutAndShearRotatedPlate(rotated1, i, 'A');
          buffPlate2 = cutAndShearRotatedPlate(rotated2, i, 'B');

          //toDo experiment with buff
          extraFilter(buffPlate1, i, "A");
//          extraFilter(buffPlate2, i, "B");


          String tempText1 = recognizeText(buffPlate1);
          String tempText2 = recognizeText(buffPlate2);

          System.out.println(i + "A: " + tempText1);
          System.out.println(i + "B: " + tempText2);

        }
      }
      i++;
    }
  }

  //toDo invert black and white, mayby extra contours filtering???
  private Mat extraFilter(BufferedImage bufferedImage, int i, char c) {
    Mat img = bufferedImage2Mat(bufferedImage);
    List<MatOfPoint> contours = new ArrayList<>();
    Imgproc.findContours(img, contours, new Mat(), RETR_TREE, Imgproc.CHAIN_APPROX_SIMPLE);
    Mat contourImage = new Mat();
    img.copyTo(contourImage);
    for (MatOfPoint contour : contours) {
      MatOfPoint2f pointsArea = new MatOfPoint2f(contour.toArray());
      RotatedRect rotatedRectangle = Imgproc.minAreaRect(pointsArea);
      if (rotatedRectangle.size.area() > img.size().area() * 0.6) {
        Point rotatedRectPoints[] = new Point[4];
        rotatedRectangle.points(rotatedRectPoints);
        Rect rect = Imgproc.boundingRect(new MatOfPoint(rotatedRectPoints));
        Imgproc.rectangle(originalContoursImg, rect.tl(), rect.br(), red, 3);
        Imgproc.rectangle(filteredContoursImg, rect.tl(), rect.br(), red, 3);
        contourImage = new Mat(img, rect);
        Imgcodecs.imwrite(imgPath+"aaa\\extraContour"+i+c+".jpg" , contourImage);
      }
    }


    Mat inverted = new Mat();
    Core.bitwise_not(contourImage, inverted);
//    Core.bitwise_not(img, inverted);
    return inverted;
  }

  private Mat bufferedImage2Mat(BufferedImage bufferedImage) {

  }


  public BufferedImage cutAndShearRotatedPlate(Mat img, int i, char c) {
    double angle = 0;
    // Cut off plate from horizontal rotated plate image
    BufferedImage shearedPLate;
    Mat copy = new Mat();
    img.copyTo(copy);
    Mat cuttedPlate = new Mat();
    List<MatOfPoint> contours = new ArrayList<>();
    Imgproc.findContours(copy, contours, new Mat(), RETR_TREE, Imgproc.CHAIN_APPROX_SIMPLE);
    for (MatOfPoint contour : contours) {
      MatOfPoint2f points = new MatOfPoint2f(contour.toArray());
      RotatedRect rotatedRect2 = Imgproc.minAreaRect(points);
      double imgArea = copy.size().area();
      double rotArea = rotatedRect2.size.area();
      if ((rotArea > imgArea * 0.3) && (rotArea < imgArea * 0.9)) {
        angle = rotatedRect2.angle;
        Point rotRectPoints[] = new Point[4];
        rotatedRect2.points(rotRectPoints);
        Rect rect = Imgproc.boundingRect(new MatOfPoint(rotRectPoints));
        Imgproc.rectangle(img, rect.tl(), rect.br(), red, 2);
        cuttedPlate = new Mat(img, rect);
        Imgcodecs.imwrite(imgPath + "aaa\\contourPlate" + i + c + ".jpg", img);
        Imgcodecs.imwrite(imgPath + "aaa\\copy" + i + c + ".jpg", copy);

//        toDO experiment

//        cuttedPlate = extraFilter(cuttedPlate, i, c);

        Imgcodecs.imwrite(imgPath + "aaa\\cuttedPlate" + i + c + ".jpg", cuttedPlate);

        shearedPLate = shearImage(cuttedPlate, angle);
        if (shearedPLate != null) {
          File output = new File(imgPath + "aaa\\buff" + i + c + ".jpg");
          try {
            ImageIO.write(shearedPLate, "jpg", output);
          } catch (IOException e) {
            e.printStackTrace();
          }
        }
        return shearedPLate;
      }
    }
    return null;
  }

  //shear cutted plate with rotated rectangle angle
  private BufferedImage shearImage(Mat cuttedPlate, double angle) {
    BufferedImage buffer = null;
    try {
      buffer = Mat2BufferedImage(cuttedPlate);
      AffineTransform tx = new AffineTransform();
      //tx.translate(buffer.getHeight() / 2, buffer.getWidth() / 2);
      tx.shear(angle, 0);
      //tx.shear(-0.4, 0);
      //tx.translate(-buffer.getWidth() / 2, -buffer.getHeight() / 2);
      AffineTransformOp op = new AffineTransformOp(tx, AffineTransformOp.TYPE_BILINEAR);
      BufferedImage shearedPLate = new BufferedImage(buffer.getWidth(), buffer.getHeight(), buffer.getType());
      op.filter(buffer, shearedPLate);
      //todo extra filter() on plate ???
      return shearedPLate;
    } catch (Exception e) {
      e.printStackTrace();
    }
    return null;
  }


  // Rotate license plate image
  private Mat rotateImage(Mat img, int angle) {
    Imgcodecs.imwrite(imgPath + "result\\img.jpg", img);
    int rotatedAngle = 0;
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


  // Filter main image, method translated from Python->C++
  private Mat filterImage(Mat img, int thresh) {
    int blurValue = 5;
    Mat temp = new Mat();
    img.copyTo(temp);
    Mat gray = new Mat();
    Mat topHat = new Mat();
    Mat blackHat = new Mat();
    Mat grayPlusTopHat = new Mat();
    Mat grayPlusTopHatMinusBlackHat = new Mat();
    Mat blur = new Mat();
    Mat threshold = new Mat();
    Mat kernel = new Mat(new Size(3, 3), CvType.CV_8U, new Scalar(255));
    Imgproc.cvtColor(temp, gray, Imgproc.COLOR_RGB2GRAY);
    Imgproc.morphologyEx(gray, topHat, Imgproc.MORPH_TOPHAT, kernel);
    Imgproc.morphologyEx(gray, blackHat, Imgproc.MORPH_BLACKHAT, kernel);
    Core.add(gray, topHat, grayPlusTopHat);
    Core.subtract(grayPlusTopHat, blackHat, grayPlusTopHatMinusBlackHat);
    Imgproc.GaussianBlur(grayPlusTopHatMinusBlackHat, blur, new Size(blurValue, blurValue), 1);
    Imgproc.threshold(blur, threshold, thresh, 255, Imgproc.THRESH_BINARY_INV);
    Imgcodecs.imwrite(imgPath + "aaa\\filteredImage.jpg", threshold);
    Imgcodecs.imwrite(imgPath + "aaa\\originalImage.jpg", img);
    Imgcodecs.imwrite(imgPath + "aaa\\tempImage.jpg", temp);
    return threshold;
  }

  public String recognizeText(String imgPath) {
    if ((imgPath == null) || (imgPath.equals(""))) {
      return "";
    }
    Tesseract tesseract = new Tesseract();
    String TESS_DATA = Constants.projectPath + "\\lib\\tesseract-OCR\\";
    tesseract.setDatapath(TESS_DATA);
    try {
      String result = "";
      result = tesseract.doOCR(new File(imgPath));
      result = result.replaceAll("[^A-Z0-9]", "");
      return result;
    } catch (TesseractException e) {
      e.printStackTrace();
    }
    return "";
  }

  public String recognizeText(Mat img) {
    if (img == null) {
      return "";
    }
    BufferedImage bufferedImage = null;
    Tesseract tesseract = new Tesseract();
    String TESS_DATA = Constants.projectPath + "\\lib\\tesseract-OCR\\";
    tesseract.setDatapath(TESS_DATA);
    try {
      String result = "";
      bufferedImage = Mat2BufferedImage(img);
      result = tesseract.doOCR(bufferedImage);
      result = result.replaceAll("[^A-Z0-9]", "");
      return result;
    } catch (TesseractException e) {
      e.printStackTrace();
    } catch (Exception e) {
      e.printStackTrace();
    }
    return "";
  }

  public String recognizeText(BufferedImage bufferedImage) {
    if (bufferedImage == null) {
      return "";
    }
    Tesseract tesseract = new Tesseract();
    String TESS_DATA = Constants.projectPath + "\\lib\\tesseract-OCR\\";
    tesseract.setDatapath(TESS_DATA);
    try {
      String result = "";
      result = tesseract.doOCR(bufferedImage);
      result = result.replaceAll("[^A-Z0-9]", "");
      return result;
    } catch (TesseractException e) {
      e.printStackTrace();
    } catch (Exception e) {
      e.printStackTrace();
    }
    return "";
  }
}
