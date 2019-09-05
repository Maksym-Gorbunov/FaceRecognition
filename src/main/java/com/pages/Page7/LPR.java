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

import java.io.File;
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
  }

  private void contours(Mat filtered) {
//    Mat temp = new Mat();
//    filtered.copyTo(temp);
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
          Imgproc.rectangle(originalContoursImg, rect.tl(), rect.br(), red, 3);
          Imgproc.rectangle(filteredContoursImg, rect.tl(), rect.br(), red, 3);
          contourImage = new Mat(filtered, rect);
//          contourImage = new Mat(originalImg, rect);
          Imgcodecs.imwrite(imgPath+"aaa\\"+i+".jpg", contourImage);
          //toDo rotate and transform, get angle from rect, work with original or filtered???
        }
      }
      i++;
    }
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
    Tesseract tesseract = new Tesseract();
    String TESS_DATA = Constants.projectPath + "\\lib\\tesseract-OCR\\";
    tesseract.setDatapath(TESS_DATA);
    String result = "";
    try {
      result = tesseract.doOCR(new File(imgPath));
    } catch (TesseractException e) {
      e.printStackTrace();
    }
    result = result.replaceAll("[^A-Z0-9]", "");
    return result;
  }

  public String recognizeText(Mat img) {
    BufferedImage bufferedImage = null;
    Tesseract tesseract = new Tesseract();
    String TESS_DATA = Constants.projectPath + "\\lib\\tesseract-OCR\\";
    tesseract.setDatapath(TESS_DATA);
    String result = "";
    try {
      bufferedImage = Mat2BufferedImage(img);
      result = tesseract.doOCR(bufferedImage);
    } catch (TesseractException e) {
      e.printStackTrace();
    } catch (Exception e) {
      e.printStackTrace();
    }
    result = result.replaceAll("[^A-Z0-9]", "");
    return result;
  }

  public String recognizeText(BufferedImage bufferedImage) {
    Tesseract tesseract = new Tesseract();
    String TESS_DATA = Constants.projectPath + "\\lib\\tesseract-OCR\\";
    tesseract.setDatapath(TESS_DATA);
    String result = "";
    try {
      result = tesseract.doOCR(bufferedImage);
    } catch (TesseractException e) {
      e.printStackTrace();
    } catch (Exception e) {
      e.printStackTrace();
    }
    result = result.replaceAll("[^A-Z0-9]", "");
    return result;
  }
}
