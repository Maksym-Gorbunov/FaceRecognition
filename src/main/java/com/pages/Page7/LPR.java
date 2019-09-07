//package com.pages.Page7;
//
//
//import com.constants.Constants;
//import net.sourceforge.tess4j.Tesseract;
//import net.sourceforge.tess4j.TesseractException;
//import org.opencv.core.*;
//import org.opencv.core.Point;
//import org.opencv.core.Rect;
//import org.opencv.core.RotatedRect;
//import org.opencv.core.Scalar;
//import org.opencv.core.Size;
//import org.opencv.imgcodecs.Imgcodecs;
//import org.opencv.imgproc.Imgproc;
//import static com.constants.Constants.imgPath;
//import static org.opencv.imgproc.Imgproc.*;
//import org.opencv.core.CvType;
//import org.opencv.core.Mat;
//import java.awt.geom.AffineTransform;
//import java.awt.image.AffineTransformOp;
//import java.io.File;
//import java.io.IOException;
//import java.util.ArrayList;
//import java.util.List;
//import java.awt.image.BufferedImage;
//import javax.imageio.ImageIO;
//import java.io.ByteArrayInputStream;
//import org.opencv.core.MatOfByte;
//import java.awt.image.DataBufferByte;
//
//public class LPR {
//  private ImgObject image;
//  private Scalar blue = new Scalar(255, 0, 0, 255);
//  private Scalar green = new Scalar(0, 255, 0, 255);
//  private Scalar red = new Scalar(0, 0, 255, 255);
//  private Scalar randomColor = new Scalar(Math.random() * 255, Math.random() * 255, Math.random() * 255, 0);
//  //  private Mat originalImg;
////  private Mat originalContoursImg;
////  private Mat filteredContoursImg;
//
//
//
//  public void recognize(String path) {
//    System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
////    image = new ImgObject(path);
//    int thresh = 150;
//    Mat filtered = filterImage(image.getOriginal(), thresh);
//    contours(filtered);
//
//    image.saveImages(imgPath+"\\lpr\\");
//    System.out.println("...done...");
//  }
//
//  private void contours(Mat filtered) {
//    Mat rotated = new Mat();
//    Mat rotated1 = new Mat();
//    Mat rotated2 = new Mat();
//    BufferedImage buffPlate1;
//    BufferedImage buffPlate2;
//    List<MatOfPoint> contours = new ArrayList<>();
//    Imgproc.findContours(filtered, contours, new Mat(), RETR_TREE, Imgproc.CHAIN_APPROX_SIMPLE);
//    int i = 0;
//    Mat contourImage = new Mat();
//    for (MatOfPoint contour : contours) {
//      MatOfPoint2f pointsArea = new MatOfPoint2f(contour.toArray());
//      RotatedRect rotatedRectangle = Imgproc.minAreaRect(pointsArea);
//      if ((rotatedRectangle.size.area() > 2000) && (rotatedRectangle.size.area() < 15000)) {
//        Point rotatedRectPoints[] = new Point[4];
//        rotatedRectangle.points(rotatedRectPoints);
//        Rect rect = Imgproc.boundingRect(new MatOfPoint(rotatedRectPoints));
//        if ((rect.width > rect.height) && (rect.width < 6 * rect.height)) {
//          //toDo, check average color in contour, if white >50%
//          Imgproc.rectangle(image.getOriginal(), rect.tl(), rect.br(), red, 3);
//          Imgproc.rectangle(image.getFiltered(), rect.tl(), rect.br(), red, 3);
////          contourImage = new Mat(filtered, rect);
//          contourImage = new Mat(filtered, rect);
//          Imgcodecs.imwrite(imgPath + "lpr\\" + i + ".jpg", contourImage);
//          //toDo rotate and transform, get angle from rect, work with original or filtered???
////          int angle = (int) rotatedRectangle.angle;
////          rotated1 = rotateImage(contourImage, angle);
////          rotated2 = rotateImage(contourImage, -angle);
////          Imgcodecs.imwrite(imgPath + "lpr\\rotated" + i + "A.jpg", rotated1);
////          Imgcodecs.imwrite(imgPath + "lpr\\rotated" + i + "B.jpg", rotated2);
//
////          buffPlate1 = cutAndShearRotatedPlate(rotated1, i, 'A');
////          buffPlate2 = cutAndShearRotatedPlate(rotated2, i, 'B');
//
//          //toDo experiment with buff
////          extraFilter(buffPlate1, i, 'A');
////          extraFilter(buffPlate2, i, 'B');
//
//
////          String tempText1 = recognizeText(buffPlate1);
////          String tempText2 = recognizeText(buffPlate2);
////
////          System.out.println(i + "A: " + tempText1);
////          System.out.println(i + "B: " + tempText2);
//
//        }
//      }
//      i++;
//    }
//  }
//
//  //toDo invert black and white, mayby extra contours filtering???
//  private Mat extraFilter(BufferedImage bufferedImage, int i, char c) {
//    if (bufferedImage == null) {
//      return null;
//    }
//    Mat img = bufferedImage2Mat(bufferedImage);
//    List<MatOfPoint> contours = new ArrayList<>();
//    Imgproc.findContours(img, contours, new Mat(), RETR_TREE, Imgproc.CHAIN_APPROX_SIMPLE);
//    Mat contourImage = new Mat();
//    img.copyTo(contourImage);
//    for (MatOfPoint contour : contours) {
//      MatOfPoint2f pointsArea = new MatOfPoint2f(contour.toArray());
//      RotatedRect rotatedRectangle = Imgproc.minAreaRect(pointsArea);
//      if (rotatedRectangle.size.area() > img.size().area() * 0.6) {
//        Point rotatedRectPoints[] = new Point[4];
//        rotatedRectangle.points(rotatedRectPoints);
//        Rect rect = Imgproc.boundingRect(new MatOfPoint(rotatedRectPoints));
//        Imgproc.rectangle(img, rect.tl(), rect.br(), red, 3);
////        Imgproc.rectangle(filteredContoursImg, rect.tl(), rect.br(), red, 3);
//        contourImage = new Mat(img, rect);
//        Imgcodecs.imwrite(imgPath + "lpr\\extraC" + i + c + ".jpg", img);
//        Imgcodecs.imwrite(imgPath + "lpr\\extraContour" + i + c + ".jpg", contourImage);
//      }
//    }
//
//
//    Mat inverted = new Mat();
//    Core.bitwise_not(contourImage, inverted);
////    Core.bitwise_not(img, inverted);
//    return inverted;
//  }
//
//
//
//
//  public BufferedImage cutAndShearRotatedPlate(Mat img, int i, char c) {
//    double angle = 0;
//    // Cut off plate from horizontal rotated plate image
//    BufferedImage shearedPLate;
//    Mat copy = new Mat();
//    img.copyTo(copy);
//    Mat cuttedPlate = new Mat();
//    List<MatOfPoint> contours = new ArrayList<>();
//    Imgproc.findContours(copy, contours, new Mat(), RETR_TREE, Imgproc.CHAIN_APPROX_SIMPLE);
//    for (MatOfPoint contour : contours) {
//      MatOfPoint2f points = new MatOfPoint2f(contour.toArray());
//      RotatedRect rotatedRect2 = Imgproc.minAreaRect(points);
//      double imgArea = copy.size().area();
//      double rotArea = rotatedRect2.size.area();
//      if ((rotArea > imgArea * 0.3) && (rotArea < imgArea * 0.9)) {
//        angle = rotatedRect2.angle;
//        Point rotRectPoints[] = new Point[4];
//        rotatedRect2.points(rotRectPoints);
//        Rect rect = Imgproc.boundingRect(new MatOfPoint(rotRectPoints));
//        Imgproc.rectangle(img, rect.tl(), rect.br(), red, 2);
//        cuttedPlate = new Mat(img, rect);
//        Imgcodecs.imwrite(imgPath + "lpr\\contourPlate" + i + c + ".jpg", img);
//        Imgcodecs.imwrite(imgPath + "lpr\\copy" + i + c + ".jpg", copy);
//
////        toDO experiment
//
////        cuttedPlate = extraFilter(cuttedPlate, i, c);
//
//        Imgcodecs.imwrite(imgPath + "lpr\\cuttedPlate" + i + c + ".jpg", cuttedPlate);
//
//        shearedPLate = shearImage(cuttedPlate, angle);
//        if (shearedPLate != null) {
//          File output = new File(imgPath + "lpr\\buff" + i + c + ".jpg");
//          try {
//            ImageIO.write(shearedPLate, "jpg", output);
//          } catch (IOException e) {
//            e.printStackTrace();
//          }
//        }
//        return shearedPLate;
//      }
//    }
//    return null;
//  }
//
//  //shear cutted plate with rotated rectangle angle
//  private BufferedImage shearImage(Mat cuttedPlate, double angle) {
//    BufferedImage buffer = null;
//    try {
//      buffer = Mat2BufferedImage(cuttedPlate);
//      AffineTransform tx = new AffineTransform();
//      //tx.translate(buffer.getHeight() / 2, buffer.getWidth() / 2);
//      tx.shear(angle, 0);
//      //tx.shear(-0.4, 0);
//      //tx.translate(-buffer.getWidth() / 2, -buffer.getHeight() / 2);
//      AffineTransformOp op = new AffineTransformOp(tx, AffineTransformOp.TYPE_BILINEAR);
//      BufferedImage shearedPLate = new BufferedImage(buffer.getWidth(), buffer.getHeight(), buffer.getType());
//      op.filter(buffer, shearedPLate);
//      //todo extra filter() on plate ???
//      return shearedPLate;
//    } catch (Exception e) {
//      e.printStackTrace();
//    }
//    return null;
//  }
//
//
//
//
//
//
//
//
//}
