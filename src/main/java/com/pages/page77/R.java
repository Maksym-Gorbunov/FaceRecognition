package com.pages.page77;

import com.constants.Constants;
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

public class R {
  public static String screenshotPath = Constants.imgPath + "222\\";
  private Scalar blue = new Scalar(255, 0, 0, 255);
  private Scalar green = new Scalar(0, 255, 0, 255);
  private Scalar red = new Scalar(0, 0, 255, 255);
  private Scalar gray = new Scalar(20, 20, 20, 255);
  private Scalar randomColor = new Scalar(Math.random() * 255, Math.random() * 255, Math.random() * 255, 0);
  private boolean logger = true;

  public static void main(String[] args) {
    System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
    R r = new R();
//    Mat image = Imgcodecs.imread(Constants.imgPath + "cars\\111\\-30.jpg");
//    Mat image2 = Imgcodecs.imread(Constants.imgPath + "cars\\111\\-60.jpg");
    Mat image = Imgcodecs.imread(Constants.imgPath + "cars\\regnums\\KKZ061.jpg");
//    Mat image = Imgcodecs.imread(Constants.imgPath + "cars\\regnums\\COS799.jpg");
    r.lpr(image, 100);
//    r.lpr(image2, 200);
  }

  // Convert BufferedImage to Mat
  public static Mat bufferedImageToMat(BufferedImage bi) {
    Mat mat = new Mat(bi.getHeight(), bi.getWidth(), CvType.CV_8UC1);
    byte[] data = ((DataBufferByte) bi.getRaster().getDataBuffer()).getData();
    mat.put(0, 0, data);
    return mat;
  }

  /////////////////////////////////////////////////////////////////////////////////////
  public Screenshot lpr(Mat image, int thresh) {
    System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
    Screenshot screenshot = new Screenshot(image);
    findContouts(screenshot, thresh);
    if (screenshot.getContours().size() > 0) {
      for(Contour c : screenshot.getContours()){
        String contourPath = screenshotPath+c.getIndex()+"\\";
        new File(contourPath).mkdirs();

        Mat rotatedPlate = rotateImage(c.getPlateOriginal(), c.getRotatedRect());
        c.setPlateRotated(rotatedPlate);
        //toDo no need c.plateGray
        Mat cuttedRotatedPlate = cutPlateFromRotatedPlate(rotatedPlate, c.getRotatedRect());
        Imgproc.putText(cuttedRotatedPlate, String.valueOf((int)c.getAngle()), new Point(10, 30), 1, 1, red, 1);
        c.setPlateRotatedCutted(cuttedRotatedPlate);

        Mat shearedPlate = shearImage(cuttedRotatedPlate, c.getRotatedRect());
        c.setPlateSheared(shearedPlate);
      }
    } else {
      System.out.println("Contours not found");
    }
    clearFolder(screenshotPath);
    screenshot.saveImages(screenshotPath);
    return screenshot;
  }

  //shear cutted plate with rotated rectangle angle or slider
  public Mat shearImage(Mat cuttedPlate, RotatedRect rotatedRect) {
    Mat grayImg = new Mat();
    Imgproc.cvtColor(cuttedPlate, grayImg, Imgproc.COLOR_RGB2GRAY);
    double x = 0;

      if (rotatedRect.size.width > rotatedRect.size.height) {
        //System.out.println("plus");
        x = 0.2;
      } else {
        //System.out.println("minus");
        x = -0.45;
      }
    System.out.println("angle="+(int)rotatedRect.angle+", x="+x);
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

  // Convert Mat to BufferedImage
  private BufferedImage mat2BufferedImage(Mat matrix) throws Exception {
    MatOfByte mob = new MatOfByte();
    Imgcodecs.imencode(".jpg", matrix, mob);
    byte ba[] = mob.toArray();
    BufferedImage bi = ImageIO.read(new ByteArrayInputStream(ba));
    return bi;
  }

  // Filter image
  private Mat filterImage(Mat gray, int thresh, int blur) {
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


  // Try to get contours from image
  private void findContouts(Screenshot screenshot, int thresh) {
    screenshot.setFilteredImg(filterImage(screenshot.getGrayImg(), thresh, 5));
    Mat filteredImg = screenshot.getFilteredImg();
    List<MatOfPoint> contours = new ArrayList<>();
    Imgproc.findContours(filteredImg, contours, new Mat(), RETR_TREE, Imgproc.CHAIN_APPROX_SIMPLE);
    double imgArea = filteredImg.size().area();
    Mat colorCopy = new Mat();
    Mat filteredCopy = new Mat();
    int i=1;
    for (MatOfPoint c : contours) {
      MatOfPoint2f pointsArea = new MatOfPoint2f(c.toArray());
      RotatedRect rotatedRectangle = Imgproc.minAreaRect(pointsArea);
      double rotRectArea = rotatedRectangle.size.area();
      if ((rotRectArea > 0.01 * imgArea) && (rotRectArea < 0.4 * imgArea)) {
        Point rotatedRectPoints[] = new Point[4];
        rotatedRectangle.points(rotatedRectPoints);
        Rect rect = Imgproc.boundingRect(new MatOfPoint(rotatedRectPoints));
        rect = cutRectIfOutOfImageArea(filteredImg, rect);
        if ((rect.width > rect.height) && (rect.width < 6 * rect.height)) {
          //toDo, check average color in contour, if white >50%
          Contour contour = new Contour(c,i, rotatedRectangle, rect);
          screenshot.addContour(contour);
          if(colorCopy.empty()){
            screenshot.getOriginalImg().copyTo(colorCopy);
            screenshot.getFilteredImg().copyTo(filteredCopy);
          }
          Imgproc.putText(colorCopy, String.valueOf(i), new Point(rect.tl().x+10, rect.tl().y+30), 2, 1, red, 1);
          Imgproc.rectangle(colorCopy, rect.tl(), rect.br(), red, 2);
          Imgproc.rectangle(filteredCopy, rect.tl(), rect.br(), red, 2);

          Mat plateOriginal = new Mat(screenshot.getOriginalImg(), rect);
          Mat plateGray = new Mat();
          Imgproc.cvtColor(plateOriginal, plateGray, Imgproc.COLOR_RGB2GRAY);
          contour.setPlateOriginal(plateOriginal);
          contour.setPlateGray(plateGray);

          i++;
        }
      }
    }
    if(contours.size() > 0){
      screenshot.setOriginalContoursImg(colorCopy);
      screenshot.setFilteredContoursImg(filteredCopy);
    }
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
}
