package com.pages.Page7;

import com.constants.Constants;
import net.sourceforge.tess4j.Tesseract;
import net.sourceforge.tess4j.TesseractException;
import org.apache.commons.io.FileUtils;
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
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import java.io.ByteArrayInputStream;

import org.opencv.core.MatOfByte;

import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;

//import java.awt.geom.*;


public class LicensePlateRecognizer {

  private String licenseNumber;
  private Mat kernel;
  private Mat sourceORG;
  private Mat source;
  private Mat licensePlateImg;
  private Mat gray;
  private Mat topHat;
  private Mat blackHat;
  private Mat grayPlusTopHat;
  private Mat grayPlusTopHatMinusBlackHat;
  private Mat blur;
  private Mat threshold;
  private Scalar blue = new Scalar(255, 0, 0, 255);
  private Scalar green = new Scalar(0, 255, 0, 255);
  private Scalar red = new Scalar(0, 0, 255, 255);
  private List<MatOfPoint> contours;
  private Mat licensePlate;
  private Mat[] filteredImages = new Mat[3];
  private Scalar randomColor = new Scalar(Math.random() * 255, Math.random() * 255, Math.random() * 255, 0);
  private Mat getLicensePlateTemp;
  private Mat rotatedPlate1;
  private Mat rotatedPlate2;
  //  private String temp1;
//  private String temp2;
  private Mat cuttedPlate;
  private Mat cuttedP;

  // Searching license plate on image and recognize it
  public String findLicensePlate(String imagePath, int thresh, int blurValue, boolean rotation) {
    System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
    clearFolder(imgPath + "result");
    licenseNumber = "";
    kernel = new Mat(new Size(3, 3), CvType.CV_8U, new Scalar(255));
    rotatedPlate1 = new Mat();
    rotatedPlate2 = new Mat();
    Mat largeImage = new Mat();
    sourceORG = new Mat();
    //resize image
    largeImage = Imgcodecs.imread(imagePath);
    float w = largeImage.width();
    float h = largeImage.height();
    float ratio = w / h;
    w = 800;
    h = w / ratio;
    Imgproc.resize(largeImage, sourceORG, new Size(w, h));
    Imgcodecs.imwrite(imgPath + "result\\aaa.jpg", sourceORG);
    source = new Mat();
    sourceORG.copyTo(source);
    gray = new Mat();
    topHat = new Mat();
    blackHat = new Mat();
    grayPlusTopHat = new Mat();
    grayPlusTopHatMinusBlackHat = new Mat();
    blur = new Mat();
    threshold = new Mat();
    contours = new ArrayList<>();
    licensePlateImg = new Mat();
    licensePlate = new Mat();
//    temp1 = "";
//    temp2 = "";


    //filter
    filterImage(thresh, blurValue);
    //check contours
    contors(rotation);
    if (threshold != null) {
      Mat t = new Mat();
      threshold.copyTo(t);
      filteredImages[0] = t;
    }
    if (source != null) {
      Mat s = new Mat();
      source.copyTo(s);
      filteredImages[1] = s;
    }
    if (licensePlateImg != null) {
      Mat l = new Mat();
      licensePlateImg.copyTo(l);
      filteredImages[2] = l;
    }
    if (licenseNumber.equals(null) || licenseNumber == null || licenseNumber.equals("")) {
      return "not found";
    }
    return licenseNumber;
  }

  private void clearFolder(String folderPath) {
    try {
      FileUtils.deleteDirectory(new File(folderPath));
      File dir = new File(folderPath);
      dir.mkdir();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  private void filterImage(int thresh, int blurValue) {
    Imgproc.cvtColor(source, gray, Imgproc.COLOR_RGB2GRAY);
    Imgproc.morphologyEx(gray, topHat, Imgproc.MORPH_TOPHAT, kernel);
    Imgproc.morphologyEx(gray, blackHat, Imgproc.MORPH_BLACKHAT, kernel);
    Core.add(gray, topHat, grayPlusTopHat);
    Core.subtract(grayPlusTopHat, blackHat, grayPlusTopHatMinusBlackHat);
    Imgproc.GaussianBlur(grayPlusTopHatMinusBlackHat, blur, new Size(blurValue, blurValue), 1);
    Imgproc.threshold(blur, threshold, thresh, 255, Imgproc.THRESH_BINARY_INV);
//    Imgproc.findContours(threshold, contours, new Mat(), RETR_TREE, Imgproc.CHAIN_APPROX_SIMPLE);
  }


  public Mat[] getFilteredImages() {
    return filteredImages;
  }


  private Mat filterPlateImage(Mat sourceImage) {
    Mat grayImage = new Mat();
    Mat topHatImage = new Mat();
    Mat blackHatImage = new Mat();
    Mat grayPlusTopHatImage = new Mat();
    Mat grayPlusTopHatMinusBlackHatImage = new Mat();
    Mat blurImage = new Mat();
    Mat kernelImage = new Mat(new Size(3, 3), CvType.CV_8U, new Scalar(255));
    Mat tempImage = new Mat();
    Imgproc.cvtColor(sourceImage, grayImage, Imgproc.COLOR_RGB2GRAY);
    Imgproc.morphologyEx(grayImage, topHatImage, Imgproc.MORPH_TOPHAT, kernelImage);
    Imgproc.morphologyEx(grayImage, blackHatImage, Imgproc.MORPH_BLACKHAT, kernelImage);
    Core.add(grayImage, topHatImage, grayPlusTopHatImage);
    Core.subtract(grayPlusTopHatImage, blackHatImage, grayPlusTopHatMinusBlackHatImage);
    Imgproc.GaussianBlur(grayPlusTopHatMinusBlackHatImage, blurImage, new Size(5, 5), 1);
    Imgproc.cvtColor(grayPlusTopHatMinusBlackHatImage, tempImage, MORPH_CLOSE);
    return tempImage;
  }


  // Recognize text with Tesseract-OCR from image file
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

  static BufferedImage Mat2BufferedImage(Mat matrix) throws Exception {
    MatOfByte mob = new MatOfByte();
    Imgcodecs.imencode(".jpg", matrix, mob);
    byte ba[] = mob.toArray();

    BufferedImage bi = ImageIO.read(new ByteArrayInputStream(ba));
    return bi;
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


  private void contors(boolean rotation) {
    Imgproc.findContours(threshold, contours, new Mat(), RETR_TREE, Imgproc.CHAIN_APPROX_SIMPLE);
    int i = 0;
    for (MatOfPoint contour : contours) {
      MatOfPoint2f pointsArea = new MatOfPoint2f(contour.toArray());
      RotatedRect rectRot = Imgproc.minAreaRect(pointsArea);
      // validate contour by area
      if ((rectRot.size.area() > 1500) && (rectRot.size.area() < 10000)) {
//      if ((rectRot.size.area() > 5000) && (rectRot.size.area() < 7000)) {
        Point rotated_rect_points[] = new Point[4];
        rectRot.points(rotated_rect_points);
        Rect rect = Imgproc.boundingRect(new MatOfPoint(rotated_rect_points));
        if ((rect.width > rect.height) && (rect.width < 6 * rect.height)) {
          Imgproc.rectangle(source, rect.tl(), rect.br(), red, 3);
          Imgproc.rectangle(threshold, rect.tl(), rect.br(), red, 3);
          licensePlate = new Mat(sourceORG, rect);

          licensePlate = filterPlateImage(licensePlate);


          //toDo if angle in +-OK diapazon => easy recognition, remove rotate from Page7???


          //recognize licence plate
          if (licensePlate != null && !licensePlate.empty()) {
            //rotation
            if (rotation) {
              System.out.println("with rotation");
              int angle = (int) rectRot.angle;

              rotatedPlate1 = rotateImage(licensePlate, angle);
              rotatedPlate2 = rotateImage(licensePlate, -angle);

              Imgcodecs.imwrite(imgPath + "result\\rotated" + i + "A.jpg", rotatedPlate1);
              Imgcodecs.imwrite(imgPath + "result\\rotated" + i + "B.jpg", rotatedPlate2);

//              rotatedPlate1 = Imgcodecs.imread(imgPath + "result\\rotated" + i + "A.jpg");
//              rotatedPlate2 = Imgcodecs.imread(imgPath + "result\\rotated" + i + "B.jpg");


              BufferedImage cuttedAndShearedImg1;
              BufferedImage cuttedAndShearedImg2;

              if ((rotatedPlate1 != null) && (rotatedPlate2 != null)) {
//                System.out.println("222");
                cuttedAndShearedImg1 = cutAndShearRotatedPlate(rotatedPlate1);
                cuttedAndShearedImg2 = cutAndShearRotatedPlate(rotatedPlate2);
                if ((cuttedAndShearedImg1 != null) && (cuttedAndShearedImg2 != null)) {
//                  System.out.println("333");
                  String tempText = "";
                  String tempText1 = recognizeText(cuttedAndShearedImg1);
                  String tempText2 = recognizeText(cuttedAndShearedImg2);

                  if (tempText2.length() > tempText1.length()) {
                    System.out.println("444");
                    tempText = tempText2;
                    licensePlateImg = rotatedPlate1;
                  } else {
                    tempText = tempText1;
                    licensePlateImg = rotatedPlate2;
                  }
                  if (tempText.length() > licenseNumber.length()) {
                    licenseNumber = tempText;
                    Imgproc.rectangle(source, rect.tl(), rect.br(), green, 3);
                    Imgproc.rectangle(threshold, rect.tl(), rect.br(), green, 3);
//                    if (tempText.equals() == temp1) {
//                      licensePlateImg = rotatedPlate1;
//                    } else {
//                      licensePlateImg = rotatedPlate2;
//                    }
                    System.out.println(i + " : " + rectRot.angle);
                  }
                } else {
                  System.out.println("undef");
                }
              }


//              Imgcodecs.imwrite(imgPath + "result\\rotated" + i + "A.jpg", rotatedPlate1);
//              Imgcodecs.imwrite(imgPath + "result\\rotated" + i + "B.jpg", rotatedPlate2);
//
//              temp1 = recognizeText(imgPath + "result\\rotated" + i + "A.jpg");
//              temp2 = recognizeText(imgPath + "result\\rotated" + i + "B.jpg");

            }


          } else {
            System.out.println("without rotation");
//            licensePlate = filterPlateImage(licensePlate);
            Imgcodecs.imwrite(imgPath + "result\\licensePlate" + i + ".jpg", licensePlate);
            String tempText = recognizeText(imgPath + "result\\licensePlate" + i + ".jpg");

            if (tempText.length() > licenseNumber.length()) {
              licensePlateImg = licensePlate;
              licenseNumber = tempText;
              Imgproc.rectangle(source, rect.tl(), rect.br(), green, 3);
              Imgproc.rectangle(threshold, rect.tl(), rect.br(), green, 3);
            }
          }
        }

      }
      i++;
    }

  }

  public void saveImages() {
    Imgcodecs.imwrite(imgPath + "result\\source.jpg", source);
    Imgcodecs.imwrite(imgPath + "result\\sourceORG.jpg", sourceORG);
    Imgcodecs.imwrite(imgPath + "result\\gray.jpg", gray);
    Imgcodecs.imwrite(imgPath + "result\\topHat.jpg", topHat);
    Imgcodecs.imwrite(imgPath + "result\\blackHat.jpg", blackHat);
    Imgcodecs.imwrite(imgPath + "result\\grayPlusTopHat.jpg", grayPlusTopHat);
    Imgcodecs.imwrite(imgPath + "result\\grayPlusTopHatMinusBlackHat.jpg", grayPlusTopHatMinusBlackHat);
    Imgcodecs.imwrite(imgPath + "result\\blur.jpg", blur);
    Imgcodecs.imwrite(imgPath + "result\\threshold.jpg", threshold);
  }

  public BufferedImage cutAndShearRotatedPlate(Mat img) {
    double angle = 0;
    // Cut off plate from horizontal rotated plate image
    Mat copy = new Mat();
    img.copyTo(copy);
//    Mat cuttedP = new Mat();
    List<MatOfPoint> contours = new ArrayList<>();
    Mat threshold = new Mat();
    Mat gray = new Mat();
    Imgproc.cvtColor(copy, gray, Imgproc.COLOR_RGB2GRAY);
    Imgproc.threshold(gray, threshold, 80, 255, Imgproc.THRESH_BINARY_INV);
    Imgcodecs.imwrite(imgPath + "\\test\\thresholedPlate.jpg", threshold);
    Imgproc.findContours(threshold, contours, new Mat(), RETR_TREE, Imgproc.CHAIN_APPROX_SIMPLE);
    System.out.println("AAAA");
    for (MatOfPoint c : contours) {
      System.out.println("BBBB");
      MatOfPoint2f points = new MatOfPoint2f(c.toArray());
      RotatedRect rotatedRect2 = Imgproc.minAreaRect(points);
      int imgArea = (int) threshold.size().area();
      if ((rotatedRect2.size.area() > imgArea * 0.3) && (rotatedRect2.size.area() < imgArea * 0.9)) {
        System.out.println("CCCC");
        angle = rotatedRect2.angle;
        Point rotRectPoints[] = new Point[4];
        rotatedRect2.points(rotRectPoints);
        Rect rect = Imgproc.boundingRect(new MatOfPoint(rotRectPoints));
        System.out.println("1a");
        Imgproc.rectangle(copy, rect.tl(), rect.br(), red, 2);
        Imgcodecs.imwrite(imgPath + "test\\copy.jpg", copy);
        System.out.println("1b");
        System.out.println("*** " + rotatedRect2.angle);
//        cuttedPlate = new Mat(copy, rect);
        cuttedP = new Mat(img, rect);
        System.out.println("1c");
        Imgcodecs.imwrite(imgPath + "test\\cuttedPlate.jpg", cuttedP);
        System.out.println("1d");
      }
    }
    //shear cutted plate with
    BufferedImage buffer = null;
    System.out.println("11111");
    try {
      System.out.println("22222");
      System.out.println("cuttedPlate: " + cuttedPlate == null);
      buffer = Mat2BufferedImage(cuttedPlate);
      AffineTransform tx = new AffineTransform();
      //tx.translate(buffer.getHeight() / 2, buffer.getWidth() / 2);
      tx.shear(angle, 0);
      //tx.shear(-0.4, 0);
      //tx.translate(-buffer.getWidth() / 2, -buffer.getHeight() / 2);
      AffineTransformOp op = new AffineTransformOp(tx, AffineTransformOp.TYPE_BILINEAR);
      //BufferedImage newImage = new BufferedImage(buffer.getHeight(), buffer.getWidth(), BufferedImage.TYPE_INT_ARGB);
      BufferedImage shearedPLate = new BufferedImage(buffer.getWidth(), buffer.getHeight(), buffer.getType());
      op.filter(buffer, shearedPLate);
      File output = new File(imgPath + "test\\buff.jpg");
      ImageIO.write(shearedPLate, "jpg", output);
      System.out.println(recognizeText(shearedPLate));
      //todo extra filter() on plate ???

      return shearedPLate;
    } catch (Exception e) {
      e.printStackTrace();
    }
    return null;
  }


  public void ttt() {
    String inputPath = imgPath + "test\\rotated39A.jpg";
    Mat img = Imgcodecs.imread(inputPath);
    BufferedImage cuttedAndShearedImg = cutAndShearRotatedPlate(img);
    if (cuttedAndShearedImg != null) {
      String text = recognizeText(cuttedAndShearedImg);
    } else {
      System.out.println("undef");
    }
  }


  public void test(Mat sourceImage) {
    Mat grayImage = new Mat();
    Mat tempImage = new Mat();
    Imgcodecs.imwrite(imgPath + "result\\000.jpg", sourceImage);
    Scalar minBlue = new Scalar(0, 0, 0, 0);
    Scalar maxBlue = new Scalar(255, 0, 0, 0);
    Core.inRange(sourceImage, minBlue, maxBlue, tempImage);
    Imgcodecs.imwrite(imgPath + "result\\111.jpg", tempImage);
  }


  // Rotate license plate
  private Mat rotateImage(Mat img, int angle) {
    Imgcodecs.imwrite(imgPath + "result\\img.jpg", img);
    int rotatedAngle = 0;
    if (angle == 0) {
      return img;
    }
    if (angle < 0) {
      rotatedAngle = 90 - Math.abs(angle);
      System.out.println("minus");
    }
    if (angle > 0) {
      rotatedAngle = -angle;
      System.out.println("plus");
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


  public void computeSkew(String inFile, String outputFile) {
    //Load this image in grayscale
    Mat img = Imgcodecs.imread(inFile, Imgcodecs.IMREAD_GRAYSCALE);

    //Binarize it
    //Use adaptive threshold if necessary
    //Imgproc.adaptiveThreshold(img, img, 255, ADAPTIVE_THRESH_MEAN_C, THRESH_BINARY, 15, 40);
    Imgproc.threshold(img, img, 200, 255, THRESH_BINARY);

    //Invert the colors (because objects are represented as white pixels, and the background is represented by black pixels)
    Core.bitwise_not(img, img);
    Mat element = Imgproc.getStructuringElement(Imgproc.MORPH_RECT, new Size(3, 3));

    //We can now perform our erosion, we must declare our rectangle-shaped structuring element and call the erode function
    Imgproc.erode(img, img, element);

    //Find all white pixels
    Mat wLocMat = Mat.zeros(img.size(), img.type());
    Core.findNonZero(img, wLocMat);

    //Create an empty Mat and pass it to the function
    MatOfPoint matOfPoint = new MatOfPoint(wLocMat);

    //Translate MatOfPoint to MatOfPoint2f in order to user at a next step
    MatOfPoint2f mat2f = new MatOfPoint2f();
    matOfPoint.convertTo(mat2f, CvType.CV_32FC2);

    //Get rotated rect of white pixels
    RotatedRect rotatedRect = Imgproc.minAreaRect(mat2f);

    Point[] vertices = new Point[4];
    rotatedRect.points(vertices);
    List<MatOfPoint> boxContours = new ArrayList<>();
    boxContours.add(new MatOfPoint(vertices));
    Imgproc.drawContours(img, boxContours, 0, new Scalar(128, 128, 128), -1);

    double resultAngle = rotatedRect.angle;
    if (rotatedRect.size.width > rotatedRect.size.height) {
      rotatedRect.angle += 90.f;
    }

    //Or
    //rotatedRect.angle = rotatedRect.angle < -45 ? rotatedRect.angle + 90.f : rotatedRect.angle;

    Mat result = deskew(Imgcodecs.imread(inFile), rotatedRect.angle);
    Imgcodecs.imwrite(outputFile, result);

  }

  public Mat deskew(Mat src, double angle) {
    Point center = new Point(src.width() / 2, src.height() / 2);
    Mat rotImage = Imgproc.getRotationMatrix2D(center, angle, 1.0);
    //1.0 means 100 % scale
    Size size = new Size(src.width(), src.height());
    Imgproc.warpAffine(src, src, rotImage, size, Imgproc.INTER_LINEAR + Imgproc.CV_WARP_FILL_OUTLIERS);
    return src;
  }


  private static int skewDetectPixelRotation(Mat mat) {
    int[] projections = null;
    int[] angle_measure = new int[181];

    for (int theta = 0; theta <= 180; theta = theta + 5) {
      projections = new int[mat.rows()];
      for (int i = 0; i < mat.rows(); i++) {
        double[] pixVal;
        for (int j = 0; j < mat.cols(); j++) {
          pixVal = mat.get(i, j);
          if (pixVal[0] == 0)//black pixel
          {
            int new_row = rotate(i, j, theta, mat);
            if (new_row >= 0 && new_row < mat.rows())
              projections[new_row]++;
          }
        }
      }
      Mat tempMat = mat.clone();
      for (int r = 0; r < mat.rows(); r++) {
        DrawProjection(r, projections[r], tempMat);
      }
      //Highgui.imwrite(DEST_PATH+"/out_"+theta+".jpg",tempMat);
      angle_measure[theta] = criterion_func(projections);

    }
    int angle = 0;
    int val = 0;
    for (int i = 0; i < angle_measure.length; i++) {
      if (val < angle_measure[i]) {
        val = angle_measure[i];
        angle = i;
      }
    }
    return angle;
  }

  //Rotation about the center of the image
  private static int rotate(double y1, double x1, int theta, Mat mat) {
    int x0 = mat.cols() / 2;
    int y0 = mat.rows() / 2;

    int new_col = (int) ((x1 - x0) * Math.cos(Math.toRadians(theta)) - (y1 - y0) * Math.sin(Math.toRadians(theta)) + x0);
    int new_row = (int) ((x1 - x0) * Math.sin(Math.toRadians(theta)) + (y1 - y0) * Math.cos(Math.toRadians(theta)) + y0);

    return new_row;

  }

  private static void DrawProjection(int rownum, int projCount, Mat image) {
    final Point pt1 = new Point(0, -1);
    final Point pt2 = new Point();
    pt1.y = rownum;
    pt2.x = projCount;
    pt2.y = rownum;
//    Core.line(image, pt1, pt2, COLOR_GREEN);
  }

  private static int criterion_func(int[] projections) {
    int max = 0;
    //use below code for image rotation
    //for(int i=0;i<projections.length-1;i++)
    //result+=Math.pow((projections[i+1]-projections[i]), 2);
    for (int i = 0; i < projections.length; i++) {
      if (max < projections[i]) {
        max = projections[i];
      }
    }

    return max;
  }

}