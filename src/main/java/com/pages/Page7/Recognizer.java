package com.pages.Page7;

import com.constants.Constants;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.opencv.core.*;
import org.opencv.core.Scalar;
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

import static com.constants.Constants.imgPath;
import static org.opencv.imgproc.Imgproc.RETR_TREE;


public class Recognizer {

  private ImgObject object;
  private Scalar blue = new Scalar(255, 0, 0, 255);
  private Scalar green = new Scalar(0, 255, 0, 255);
  private Scalar red = new Scalar(0, 0, 255, 255);
  private Scalar gray = new Scalar(20, 20, 20, 255);
  private Scalar randomColor = new Scalar(Math.random() * 255, Math.random() * 255, Math.random() * 255, 0);
  private String outPathMain = Constants.imgPath + "lpr\\";
  private String outPath = Constants.imgPath + "lpr\\";
  private String contourPath = "";
  private String contourOutPath = "";


  public static void main(String[] args) {
    System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
    Recognizer ir = new Recognizer();
    File f = new File(Constants.imgPath + "\\cars\\regnums\\YRR146.jpg");
//    File f = new File(Constants.imgPath + "\\cars\\111\\-30.jpg");
    int thresh = 100;
    ir.recognize2(f, thresh);
  }

  private void recognize2(File file, int thresh) {
    System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
    object = new ImgObject(file);
    String fileNameWithOutExt = FilenameUtils.removeExtension(file.getName());
    outPath = outPathMain + fileNameWithOutExt + "\\";
    clearFolder(outPath);

    Mat original = copy(object.getOriginal());
    Mat filtered = filterImage(object.getOriginal(), thresh);
    object.setFiltered(copy(filtered));

    List<MatOfPoint> contours = new ArrayList<>();
    contours = findContours(filtered);

    //loop if contours found
    if ((contours != null) && (contours.size() > 0)) {
      System.out.println("total: " + contours.size());
      Rect bestRect = null;
      List<Mat> plates = new ArrayList<>();
      Imgcodecs.imwrite(outPath + "threshold.jpg", filtered);

      //loop through valid contours
      int i = 0;
      for (MatOfPoint c : contours) {
        contourOutPath = outPath + i + "\\";
        new File(contourOutPath).mkdirs();

        //create rect around contour
        MatOfPoint2f pointsArea = new MatOfPoint2f(c.toArray());
        RotatedRect rotatedRect = Imgproc.minAreaRect(pointsArea);
        Point rotatedRectPoints[] = new Point[4];
        rotatedRect.points(rotatedRectPoints);
        Rect rect = Imgproc.boundingRect(new MatOfPoint(rotatedRectPoints));
        //create contour mini image
        Mat contourImg = new Mat(filtered, rect);
        Imgcodecs.imwrite(contourOutPath + "1.contour.jpg", contourImg);
        //rotate contours by rect angle
        Mat rotatedImg = rotateImage2(contourImg, rotatedRect);
        Imgcodecs.imwrite(contourOutPath + "2.rotated.jpg", rotatedImg);

        //cut large contour from rotated image
        Mat cuttedImg = cutLargeContour(rotatedImg);

        int plateThresh = 100;
        Mat filteredPlate = filterPlate2(cuttedImg, plateThresh);


        i++;
      }


    }
    //contours not found
    else {
      System.out.println("Contours not found, change thresh and try again");
    }
    object.saveImages(outPath);
  }

  // Extra filter for license plate with inversion and ...
  public Mat filterPlate2(Mat img, int plateThresh) {
    Mat inverted = new Mat();
    Core.bitwise_not(img, inverted);
    Mat topHat = new Mat();
    Mat blackHat = new Mat();
    Mat grayPlusTopHat = new Mat();
    Mat grayPlusTopHatMinusBlackHat = new Mat();
    Mat kernel = new Mat(new Size(3, 3), CvType.CV_8U, new Scalar(255));
    Imgproc.morphologyEx(inverted, topHat, Imgproc.MORPH_TOPHAT, kernel);
    Imgproc.morphologyEx(inverted, blackHat, Imgproc.MORPH_BLACKHAT, kernel);
    Core.add(inverted, topHat, grayPlusTopHat);
    Core.subtract(grayPlusTopHat, blackHat, grayPlusTopHatMinusBlackHat);

    Imgcodecs.imwrite(contourOutPath + "5.filtered.jpg", grayPlusTopHatMinusBlackHat);

    return grayPlusTopHatMinusBlackHat;
  }

  private Mat cutLargeContour(Mat rotatedImg) {
    if (rotatedImg == null) {
      System.out.println("rotated NULL");
      return null;
    }
    Mat copy = copy(rotatedImg);
    List<MatOfPoint> contours = new ArrayList<>();
    Imgproc.findContours(copy, contours, new Mat(), RETR_TREE, Imgproc.CHAIN_APPROX_SIMPLE);
//    Mat cuttedImg = new Mat();
    int j = 0;
    for (MatOfPoint c : contours) {
      MatOfPoint2f points = new MatOfPoint2f(c.toArray());
      RotatedRect rotatedRect = Imgproc.minAreaRect(points);
      Point rotRectPoints[] = new Point[4];
      rotatedRect.points(rotRectPoints);

      double rectArea = rotatedRect.size.area();
      double imgArea = rotatedImg.size().area();
//      Rect rectCrop = new Rect(rect.x, rect.y, rect.width, rect.height);
      Rect rect = new Rect();
      rect = Imgproc.boundingRect(new MatOfPoint(rotRectPoints));

      if ((copy.width() > rect.size().width) && (copy.height() > rect.size().height)
              && (rectArea > imgArea * 0.5)) {

        Mat maxContourImg = copy(copy);
        Imgproc.rectangle(maxContourImg, rect.tl(), rect.br(), blue, 1);
        Imgcodecs.imwrite(contourOutPath + "3.maxContour.jpg", maxContourImg);

        Mat cuttedImg = new Mat(copy, rect);
        Imgcodecs.imwrite(contourOutPath + "4.cutted.jpg", cuttedImg);
        return cuttedImg;
      }
      j++;
    }
    return rotatedImg;
  }


  // Rotate license plate image
  private Mat rotateImage2(Mat img, RotatedRect rotatedRect) {
    int angle = (int) rotatedRect.angle;
    if (rotatedRect.size.width > rotatedRect.size.height) {
      angle = -angle;
    }
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


  // Filter and search license number on image
  public ImgObject recognize(File file, int thresh, double angle) {
    System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
    object = new ImgObject(file);
    String fileNameWithOutExt = FilenameUtils.removeExtension(file.getName());
    outPath = outPathMain + fileNameWithOutExt + "\\";
    clearFolder(outPath);
    Mat filtered = new Mat();
    filtered = filterImage(object.getOriginal(), thresh);
    Mat filteredCopy = new Mat();
    filtered.copyTo(filteredCopy);
    object.setFiltered(filteredCopy);
    List<MatOfPoint> contours = new ArrayList<>();
    Mat contourMono = new Mat();
    contours = findContours(filtered);
    if ((contours != null) && (contours.size() > 0)) {
      System.out.println("total: " + contours.size());
      int i = 0;
      Rect bestRect = null;


      List<Mat> plates = new ArrayList<>();

      // simple recognition of valid contours
      for (MatOfPoint c : contours) {
        MatOfPoint2f pointsArea = new MatOfPoint2f(c.toArray());
        RotatedRect rotatedRectangle = Imgproc.minAreaRect(pointsArea);
        Point rotatedRectPoints[] = new Point[4];
        rotatedRectangle.points(rotatedRectPoints);
        Rect rect = Imgproc.boundingRect(new MatOfPoint(rotatedRectPoints));
        //create contour images
        //toDO ... filter after rotation mayby, filter contours or blackhat...
        //toDo ... copy same as simple plus shear text by angle from contour???
        //toDo ... remove -+rotation, from 2 rotation make 1 only
        contourMono = new Mat(filtered, rect);
        //save contour images
        contourPath = outPath + "\\" + i + "\\";
        new File(contourPath).mkdirs();
        Imgcodecs.imwrite(contourPath + "contMono.jpg", contourMono);
        //rotate contours by rect angle
        Mat rotated1 = rotateImage(contourMono, (int) rotatedRectangle.angle);
        Mat rotated2 = rotateImage(contourMono, -(int) rotatedRectangle.angle);
        Imgcodecs.imwrite(contourPath + "rotated1.jpg", rotated1);
        Imgcodecs.imwrite(contourPath + "rotated2.jpg", rotated2);
        //extra plate filter after rotation
        Mat rotatedFiltered1 = filterPlate(rotated1);
        Mat rotatedFiltered2 = filterPlate(rotated2);
        Imgcodecs.imwrite(contourPath + "rotatedFiltered1.jpg", rotatedFiltered1);
        Imgcodecs.imwrite(contourPath + "rotatedFiltered2.jpg", rotatedFiltered2);

        //add rotated and filtered plate images to list
        plates.add(rotatedFiltered1);
        plates.add(rotatedFiltered2);

        //text recognition
        String tempText1 = TextRecognizer.recognizeText(rotatedFiltered1);
        String tempText2 = TextRecognizer.recognizeText(rotatedFiltered2);
        String tempText;
        Mat rotatedPlate = new Mat();
        Mat rotatedFilteredPlate = new Mat();
        if (tempText1.length() < tempText2.length()) {
          tempText = tempText2;
          rotatedFiltered2.copyTo(rotatedFilteredPlate);
          rotated2.copyTo(rotatedPlate);
        } else {
          tempText = tempText1;
          rotatedFiltered1.copyTo(rotatedFilteredPlate);
          rotated1.copyTo(rotatedPlate);
        }
        System.out.println("Text " + i + ": " + tempText);
        if (object.getLicenseNumber().length() < tempText.length()) {
          object.setLicenseNumber(tempText);
          object.setPlate(rotatedPlate);
          object.setFilteredPlate(rotatedFilteredPlate);
          bestRect = rect;
        }
        i++;
      }

      // deep recognition
//      if (object.getLicenseNumber().length() < 5) {
      if (true) {
        bestRect = null;
        System.out.println("deep recognition");
        deepRecognition(plates, angle);
      }

      //draw green rect on contours if good result exist
      if (bestRect != null) {
        Mat tempContours = new Mat();
        object.getContours().copyTo(tempContours);
        Imgproc.rectangle(tempContours, bestRect.tl(), bestRect.br(), green, 3);
        object.setContours(tempContours);
      }
    }

    object.saveImages(outPath);
    System.out.println("...done...");
    return object;
  }


  //Deep recognition with shearing and extra cutting
  private void deepRecognition(List<Mat> plates, double angle) {
    System.out.println("Total plates: " + plates.size());
    for (Mat plate : plates) {
      BufferedImage shearedPlate = cutAndShearRotatedPlate(plate, angle);
      if (shearedPlate != null) {
        Mat result = bufferedImage2Mat(shearedPlate);

//        Mat blur = new Mat();
//        Mat threshold = new Mat();
//        Imgproc.GaussianBlur(result, blur, new Size(3, 3), 1);
//        Imgproc.threshold(blur, threshold, 120, 255, Imgproc.THRESH_BINARY_INV);
//        result = threshold;

        String text = TextRecognizer.recognizeText(result);
//      String text = TextRecognizer.recognizeText(shearedPlate);

        if (object.getLicenseNumber().length() <= text.length()) {
          object.setLicenseNumber(text);
          object.setFilteredPlate(result);

        }
      }
    }

  }


  public BufferedImage cutAndShearRotatedPlate(Mat img, double angle) {
//    double angle = 0;
    // Cut off plate from horizontal rotated plate image
    BufferedImage shearedPLate;
    Mat copy = new Mat();
    img.copyTo(copy);
    Mat cuttedPlate = new Mat();
    List<MatOfPoint> contours = new ArrayList<>();
    Imgproc.findContours(copy, contours, new Mat(), RETR_TREE, Imgproc.CHAIN_APPROX_SIMPLE);
    int i = 0;
    new File(outPath + "plates\\").mkdirs();
    for (MatOfPoint contour : contours) {
      MatOfPoint2f points = new MatOfPoint2f(contour.toArray());
      RotatedRect rotatedRect2 = Imgproc.minAreaRect(points);
      double imgArea = copy.size().area();
      double rotArea = rotatedRect2.size.area();
//      if ((rotArea > imgArea * 0.3) && (rotArea < imgArea * 0.9)) {
//        angle = rotatedRect2.angle/3;
//        angle = -0.6;
      Point rotRectPoints[] = new Point[4];
      rotatedRect2.points(rotRectPoints);

      Rect rect = Imgproc.boundingRect(new MatOfPoint(rotRectPoints));
      if (rect.area() > imgArea * 0.5) {
        Imgproc.rectangle(img, rect.tl(), rect.br(), red, 2);

        if (img != null && !img.empty() && rect != null) {
          System.out.println("********");
          System.out.println(i);
          Imgcodecs.imwrite(outPath + "plates\\" + i + "___img.jpg", img);
          Imgproc.rectangle(copy, rect.tl(), rect.br(), green, 1);
          Imgcodecs.imwrite(outPath + "plates\\" + i + "___copy.jpg", copy);
          cuttedPlate = new Mat(img, rect);
//        cuttedPlate = new Mat(img, rect);

//        toDO experiment

//        cuttedPlate = extraFilter(cuttedPlate, i, c);

          Imgcodecs.imwrite(outPath + "plates\\" + i + "cuttedPlate.jpg", cuttedPlate);

          shearedPLate = shearImage(cuttedPlate, angle);
          if (shearedPLate != null) {
            File output = new File(outPath + "plates\\" + i + "result.jpg");
            try {
              ImageIO.write(shearedPLate, "jpg", output);
            } catch (IOException e) {
              e.printStackTrace();
            }
          }
          return shearedPLate;
        }
      }
      i++;
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


  // Find contours on image
  private List<MatOfPoint> findContours(Mat img) {
    Mat filteredImg = object.getFiltered();
    Mat contoursImg = new Mat();
    object.getOriginal().copyTo(contoursImg);
    List<MatOfPoint> contours = new ArrayList<>();
    List<MatOfPoint> validContours = new ArrayList<>();
    Imgproc.findContours(img, contours, new Mat(), RETR_TREE, Imgproc.CHAIN_APPROX_SIMPLE);
    for (MatOfPoint c : contours) {
      MatOfPoint2f pointsArea = new MatOfPoint2f(c.toArray());
      RotatedRect rotatedRectangle = Imgproc.minAreaRect(pointsArea);
      if ((rotatedRectangle.size.area() > 2000) && (rotatedRectangle.size.area() < 15000)) {
        Point rotatedRectPoints[] = new Point[4];
        rotatedRectangle.points(rotatedRectPoints);
        Rect rect = Imgproc.boundingRect(new MatOfPoint(rotatedRectPoints));
        if ((rect.width > rect.height) && (rect.width < 6 * rect.height)) {
          //toDo, check average color in contour, if white >50%
          validContours.add(c);
          Imgproc.rectangle(filteredImg, rect.tl(), rect.br(), red, 3);
          Imgproc.rectangle(contoursImg, rect.tl(), rect.br(), red, 3);
        }
      }
    }
    if (validContours.size() > 0) {
      object.setFiltered(filteredImg);
      object.setContours(contoursImg);
      return validContours;
    }
    return null;
  }

  // Extra filter for license plate with inversion and ...
  public Mat filterPlate(Mat img) {
    Mat inverted = new Mat();
    Core.bitwise_not(img, inverted);
    Mat topHat = new Mat();
    Mat blackHat = new Mat();
    Mat grayPlusTopHat = new Mat();
    Mat grayPlusTopHatMinusBlackHat = new Mat();
    Mat kernel = new Mat(new Size(3, 3), CvType.CV_8U, new Scalar(255));
    Imgproc.morphologyEx(inverted, topHat, Imgproc.MORPH_TOPHAT, kernel);
    Imgproc.morphologyEx(inverted, blackHat, Imgproc.MORPH_BLACKHAT, kernel);
    Core.add(inverted, topHat, grayPlusTopHat);
    Core.subtract(grayPlusTopHat, blackHat, grayPlusTopHatMinusBlackHat);
    return grayPlusTopHatMinusBlackHat;
  }


  // Rotate license plate image
  private Mat rotateImage(Mat img, int angle) {
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
    return threshold;
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

  // Convert Mat to BufferedImage
  private BufferedImage Mat2BufferedImage(Mat matrix) throws Exception {
    MatOfByte mob = new MatOfByte();
    Imgcodecs.imencode(".jpg", matrix, mob);
    byte ba[] = mob.toArray();
    BufferedImage bi = ImageIO.read(new ByteArrayInputStream(ba));
    return bi;
  }

  // Convert BufferedImage to Mat
  private Mat bufferedImage2Mat(BufferedImage bi) {
    Mat mat = new Mat(bi.getHeight(), bi.getWidth(), CvType.CV_8UC1);
    //Mat mat = new Mat(bi.getHeight(), bi.getWidth(), CvType.CV_8UC3);
    byte[] data = ((DataBufferByte) bi.getRaster().getDataBuffer()).getData();
    mat.put(0, 0, data);
    return mat;
  }


  private Mat copy(Mat original) {
    if (original == null) {
      return null;
    }
    Mat copy = new Mat();
    original.copyTo(copy);
    return copy;
  }
}
