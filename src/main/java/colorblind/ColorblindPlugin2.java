package colorblind;

import org.opencv.core.*;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

import java.util.ArrayList;
import java.util.List;

import static org.bytedeco.javacpp.opencv_imgproc.RETR_TREE;

public class ColorblindPlugin2 {

  public static String imgPath = "c:\\java\\FaceRecognition\\data\\img\\colorblind2\\";
  private int minRectArea = 2500;   // increase this value to ignore small rectangles
  private boolean logger = false;   // true -> save images to 'imgPath' for easy debugging

  public static void main(String[] args) {
    System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
    ColorblindPlugin2 c = new ColorblindPlugin2();
    Mat img = Imgcodecs.imread(imgPath + "colors.jpg");
    //Mat img = Imgcodecs.imread(imgPath + "1a.jpg");
    long startTime = System.nanoTime();
    c.findConflict(img);
    long endTime = System.nanoTime();
    long duration = (endTime - startTime);
    double seconds = (double) duration / 1_000_000_000.0;
    System.out.println("Execution time: " + seconds);
  }


  private void findConflict(Mat img) {
    Mat monoImg = new Mat(img.rows(), img.cols(), CvType.CV_8UC1, new Scalar(255));
    boolean conflict = false;
    for (int row = 0; row < img.rows(); row++) {
      for (int col = 0; col < img.cols(); col++) {
        double[] pixelBGR = img.get(row, col);
        int pixelColorChannel = getColorChannel(pixelBGR);
        //if pixel in red color family
        if (pixelColorChannel == 3) {
          Point point = new Point(col, row);
          List<Point> neighbours = getNeighborPixels(img, point);
          for (Point neighbor : neighbours) {
            double[] neighborBGR = img.get((int) neighbor.y, (int) neighbor.x);
            int neighbourColorChannel = getColorChannel(neighborBGR);
            // match 'red/green' conflict (pixel is red, neighbour pixel is green)
            if (neighbourColorChannel == 2) {
              conflict = true;
              monoImg.put((int) point.y, (int) point.x, new double[]{0, 0, 0});
              monoImg.put((int) neighbor.y, (int) neighbor.x, new double[]{0, 0, 0});
            }
          }
        }
      }
    }
    Imgcodecs.imwrite(imgPath + "1.jpg", img);
    Imgcodecs.imwrite(imgPath + "2.jpg", monoImg);
    if (conflict) {
      List<Rect> rectList = findContours(monoImg);
      if(rectList!=null){
        for(Rect rect: rectList){
          Imgproc.rectangle(img, rect.tl(), rect.br(), new Scalar(255, 0, 0), 2);
        }
      }
      Imgcodecs.imwrite(imgPath + "6.jpg", img);
    } else {
      System.out.println("Color conflict not found");
    }
  }

  private List<Rect> findContours(Mat monoImg) {
    List<Rect> rectList = new ArrayList<>();
    List<MatOfPoint> contours = new ArrayList<>();

    Imgproc.findContours(monoImg, contours, new Mat(), RETR_TREE, Imgproc.CHAIN_APPROX_SIMPLE);
    for (MatOfPoint c : contours) {
      MatOfPoint2f pointsArea = new MatOfPoint2f(c.toArray());
      RotatedRect rotatedRectangle = Imgproc.minAreaRect(pointsArea);
      if ((rotatedRectangle.size.area() < 0.5 * monoImg.size().area())) {
        Point rotatedRectPoints[] = new Point[4];
        rotatedRectangle.points(rotatedRectPoints);
        Rect rect = Imgproc.boundingRect(new MatOfPoint(rotatedRectPoints));
        Imgproc.rectangle(monoImg, rect.tl(), rect.br(), new Scalar(0, 0, 0), -1);
        //rectList.add(rect);
      }
    }
    Imgcodecs.imwrite(imgPath + "3.jpg", monoImg);

    Imgproc.findContours(monoImg, contours, new Mat(), RETR_TREE, Imgproc.CHAIN_APPROX_SIMPLE);
    for (MatOfPoint c : contours) {
      MatOfPoint2f pointsArea = new MatOfPoint2f(c.toArray());
      RotatedRect rotatedRectangle = Imgproc.minAreaRect(pointsArea);
      if ((rotatedRectangle.size.area() < 0.5 * monoImg.size().area())) {
        Point rotatedRectPoints[] = new Point[4];
        rotatedRectangle.points(rotatedRectPoints);
        Rect rect = Imgproc.boundingRect(new MatOfPoint(rotatedRectPoints));
        Imgproc.rectangle(monoImg, rect.tl(), rect.br(), new Scalar(0, 0, 0), -1);
        //rectList.add(rect);
      }
    }
    Imgcodecs.imwrite(imgPath + "4.jpg", monoImg);

    Imgproc.findContours(monoImg, contours, new Mat(), RETR_TREE, Imgproc.CHAIN_APPROX_SIMPLE);
    for (MatOfPoint c : contours) {
      MatOfPoint2f pointsArea = new MatOfPoint2f(c.toArray());
      RotatedRect rotatedRectangle = Imgproc.minAreaRect(pointsArea);
      if ((rotatedRectangle.size.area() < 0.5 * monoImg.size().area())) {
        Point rotatedRectPoints[] = new Point[4];
        rotatedRectangle.points(rotatedRectPoints);
        Rect rect = Imgproc.boundingRect(new MatOfPoint(rotatedRectPoints));
        Imgproc.rectangle(monoImg, rect.tl(), rect.br(), new Scalar(0, 0, 0), -1);
        rectList.add(rect);
      }
    }
    if (rectList.size() != 0) {
      //System.out.println(rectList.size());
      rectList = removeInnerRects(rectList);
      //System.out.println(rectList.size());
      Imgcodecs.imwrite(imgPath + "5.jpg", monoImg);
      return rectList;
    }
    return null;
  }


  public Mat filterImage(Mat img, int thresh, int blur) {
    //Mat grayImg = new Mat();
//    gray.copyTo(grayImg);
    Mat topHatImg = new Mat();
    Mat blackHatImg = new Mat();
    Mat grayPlusTopHatImg = new Mat();
    Mat grayPlusTopHatMinusBlackHatImg = new Mat();
    Mat blurImg = new Mat();
    Mat thresholdImg = new Mat();
    Mat kernel = new Mat(new Size(3, 3), CvType.CV_8U, new Scalar(255));
    //Imgproc.cvtColor(img, grayImg, Imgproc.COLOR_RGB2GRAY);
    Imgproc.morphologyEx(img, topHatImg, Imgproc.MORPH_TOPHAT, kernel);
    Imgproc.morphologyEx(img, blackHatImg, Imgproc.MORPH_BLACKHAT, kernel);
    Core.add(img, topHatImg, grayPlusTopHatImg);
    Core.subtract(grayPlusTopHatImg, blackHatImg, grayPlusTopHatMinusBlackHatImg);
    Imgproc.GaussianBlur(grayPlusTopHatMinusBlackHatImg, blurImg, new Size(blur, blur), 10);
    Imgproc.threshold(blurImg, thresholdImg, thresh, 255, Imgproc.THRESH_BINARY_INV);
    return thresholdImg;
  }


  private List<Point> getNeighborPixels(Mat src, Point point) {
    List<Point> points = new ArrayList<>();
    if (point.y != 0) {
      Point top = new Point(point.x, point.y - 1);
      points.add(top);
      if (point.x != 0) {
        Point tl = new Point(point.x - 1, point.y - 1);
        points.add(tl);
      }
      if (point.x != src.width() - 1) {
        Point tr = new Point(point.x + 1, point.y - 1);
        points.add(tr);
      }
    }
    if (point.y != src.height() - 1) {
      Point bottom = new Point(point.x, point.y + 1);
      points.add(bottom);
      if (point.x != 0) {
        Point bl = new Point(point.x - 1, point.y + 1);
        points.add(bl);
      }
      if (point.x != src.width() - 1) {
        Point br = new Point(point.x + 1, point.y + 1);
        points.add(br);
      }
    }
    if (point.x != 0) {
      Point left = new Point(point.x - 1, point.y);
      points.add(left);
    }
    if (point.x != src.width() - 1) {
      Point right = new Point(point.x + 1, point.y);
      points.add(right);
    }
    return points;
  }


  // Get color name from BGR values (channels 'Blue'=1, 'Green'=2, 'Red'=3, 'undefined'=0)
  private int getColorChannel(double[] bgr) {
    double blue = bgr[0];
    double green = bgr[1];
    double red = bgr[2];
    int coefficient = 20;
    int channel = 0;
    if ((blue > green + coefficient) && (blue > red + coefficient)) {
      channel = 1;
    }
    if ((green > blue + coefficient) && (green > red + coefficient)) {
      channel = 2;
    }
    if ((red > blue + coefficient) && (red > green + coefficient)) {
      channel = 3;
    }
    return channel;
  }


  // Remove inner rects from rects
  private List<Rect> removeInnerRects(List<Rect> rectList) {
    List<Rect> cleanedRects = new ArrayList<>();
    for (Rect rect : rectList) {
      boolean inside = false;
      for (Rect r : rectList) {
        if ((r.contains(rect.tl())) && (r.contains(rect.br()))) {
          inside = true;
        }
      }
      if (!inside && rect.area() > minRectArea) {
        cleanedRects.add(rect);
      }
    }
    return cleanedRects;
  }

}
