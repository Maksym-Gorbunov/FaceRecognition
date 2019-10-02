package colorblind;

import org.opencv.core.*;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

import static org.bytedeco.javacpp.opencv_imgproc.RETR_TREE;

public class ColorblindPlugin {

  public static String projectPath = "c:\\java\\FaceRecognition\\";
  public static String imgOutPathForConflicts = projectPath + "data\\img\\colorblind\\conflicts\\";
  public static String imgOutPathForFiltered = projectPath + "data\\img\\colorblind\\filtered\\";
  private int minRectArea = 2500;        // increase this value to ignore small rectangles
  public static boolean logger = true;   // true => save images for easy debugging

  public static void main(String[] args) {
    System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
    //create folders for images
    if (logger) {
      new File(imgOutPathForConflicts).mkdirs();
      new File(imgOutPathForFiltered).mkdirs();
    }
    ColorblindPlugin c = new ColorblindPlugin();
    long startTime = System.nanoTime();

    Mat img = Imgcodecs.imread("c:\\java\\FaceRecognition\\data\\img\\colorblind\\colors.jpg");
    List<Rect> rectList = c.findConflict(img);

    c.getFilteredImage(img, rectList, new Point(150, 100));
    c.getFilteredImage(img, rectList, new Point(600, 100));
    c.getFilteredImage(img, rectList, new Point(300, 600));

    long endTime = System.nanoTime();
    long duration = (endTime - startTime);
    double seconds = (double) duration / 1_000_000_000.0;
    System.out.println("Execution time: " + seconds);
  }


  // Find color conflict (Red/Green) and identify areas as rectangles
  // Mark area, draw and save if 'logger' is true
  private List<Rect> findConflict(Mat img) {
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
    if (conflict) {
      if (logger) {
        Imgcodecs.imwrite(imgOutPathForConflicts + "1.jpg", img);
        Imgcodecs.imwrite(imgOutPathForConflicts + "2.jpg", monoImg);
      }
      List<Rect> rectList = findContours(monoImg);
      if (rectList != null) {
        if (logger) {
          if (rectList != null) {
            for (Rect rect : rectList) {
              Imgproc.rectangle(img, rect.tl(), rect.br(), new Scalar(0, 0, 0), 2);
            }
          }
          Imgcodecs.imwrite(imgOutPathForConflicts + "5.jpg", img);
        }
        System.out.println("Total rectangles: " + rectList.size());
        return rectList;
      }
    }
    System.out.println("Color conflict not found");
    return null;
  }


  // Find rectangles with color conflict area from Mat image
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
        Point center = new Point(rect.tl().x + (rect.width / 2), rect.tl().y + (rect.height / 2));
        int radius = 0;
        if (rect.width > rect.height) {
          radius = rect.width;
        }
        if (rect.height > rect.width) {
          radius = rect.height;
        }
        Imgproc.circle(monoImg, center, radius, new Scalar(0, 0, 0), -1);
      }
    }
    if (logger) {
      Imgcodecs.imwrite(imgOutPathForConflicts + "3.jpg", monoImg);
    }
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
      rectList = removeInnerRects(rectList);
      if (logger) {
        Imgcodecs.imwrite(imgOutPathForConflicts + "4.jpg", monoImg);
      }
      return rectList;
    }
    return null;
  }


  // Get all neighbour pixels to current point
  // 8 neighbours usual in middle
  // 5 neighbours from side pixels
  // 3 neighbours from corner pixels
  //   N   N   N
  //   N  'p'  N
  //   N   N   N
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


  // Get color channel from BGR values (channels 'Blue'=1, 'Green'=2, 'Red'=3, 'undefined'=0)
  private int getColorChannel(double[] bgr) {
    double blue = bgr[0];
    double green = bgr[1];
    double red = bgr[2];
    int coefficient = 20; // color coefficient difference, experiment value
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


  // Remove inner rects from rect
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


  ///////////////////////////////////////// COLORBLIND FILTER (Simulator) ////////////////////////////////////////////

  // Get filtered mini image from rectangle
  // , run after 'findConflict()', else rectList is empty
  // Mouse click => new Point => if(point match one of rectangles) => create mini filtered image and return it
  // if 'logger' is true => save all filtered mini images and one main image with clicked filtered mini image
  private Mat getFilteredImage(Mat img, List<Rect> rectList, Point point) {
    if (rectList != null) {
      Mat imgCopy = new Mat();
      img.copyTo(imgCopy);
      int i = 1;
      for (Rect rect : rectList) {
        if (rect.contains(point)) {
          Mat mask = new Mat(img, rect);
          Mat maskFiltered = colorblindFilter(mask);
          if (logger) {
            Mat filteredImg = new Mat();
            img.copyTo(filteredImg);
            maskFiltered.copyTo(filteredImg
                    .rowRange((int) rect.tl().y, (int) (rect.tl().y + rect.height))
                    .colRange((int) rect.tl().x, (int) rect.tl().x + rect.width));
            Imgproc.circle(imgCopy, point, 10, new Scalar(0, 0, 0), -1);
            Imgproc.circle(filteredImg, point, 10, new Scalar(0, 0, 0), -1);
            Imgproc.rectangle(filteredImg, rect.tl(), rect.br(), new Scalar(255, 0, 0), 2);
            Imgcodecs.imwrite(imgOutPathForFiltered + i + "_originalWithClick.jpg", imgCopy);
            Imgcodecs.imwrite(imgOutPathForFiltered + i + "_selectedConflictImg.jpg", filteredImg);
            Imgcodecs.imwrite(imgOutPathForFiltered + i + "_selectedConflictMini.jpg", maskFiltered);
          }
          System.out.println("Point ("+(int)point.x+","+(int)point.y+") - click");
          return maskFiltered;
        }
        i++;
      }
      if (logger) {
        System.out.println("Point ("+(int)point.x+","+(int)point.y+") - miss");
        Imgproc.circle(imgCopy, point, 10, new Scalar(0, 0, 0), -1);
        Imgcodecs.imwrite(imgOutPathForFiltered + i + "_originalWithClick.jpg", imgCopy);
      }
    }
    return null;
  }


  private Mat colorblindFilter(Mat img) {
    Mat filtered = new Mat();
    img.copyTo(filtered);
    int redTotal = 0;
    int greenTotal = 0;

    double redChannelValue1 = 0;
    double redChannelValue2 = 0;
    double redChannelValue3 = 0;
    double greenChannelValue1 = 0;
    double greenChannelValue2 = 0;
    double greenChannelValue3 = 0;

    for (int row = 0; row < img.rows(); row++) {
      for (int col = 0; col < img.cols(); col++) {
        double[] pixelBGR = img.get(row, col);
        int pixelColorChannel = getColorChannel(pixelBGR);
        if (pixelColorChannel == 3) {
          redChannelValue1 += img.get(row, col)[0];
          redChannelValue2 += img.get(row, col)[1];
          redChannelValue3 += img.get(row, col)[2];
          redTotal++;
        }
        if (pixelColorChannel == 2) {
          greenChannelValue1 += img.get(row, col)[0];
          greenChannelValue2 += img.get(row, col)[1];
          greenChannelValue3 += img.get(row, col)[2];
          greenTotal++;
        }
      }
    }
    double redAvarageColor1 = redChannelValue1 / redTotal;
    double redAvarageColor2 = redChannelValue2 / redTotal;
    double redAvarageColor3 = redChannelValue3 / redTotal;
    double greenAvarageColor1 = greenChannelValue1 / greenTotal;
    double greenAvarageColor2 = greenChannelValue2 / greenTotal;
    double greenAvarageColor3 = greenChannelValue3 / greenTotal;
    for (int row = 0; row < img.rows(); row++) {
      for (int col = 0; col < img.cols(); col++) {
        double[] pixelBGR = img.get(row, col);
        int pixelColorChannel = getColorChannel(pixelBGR);
        if (redTotal > greenTotal) {
          if (pixelColorChannel == 2) {
            filtered.put(row, col, new double[]{redAvarageColor1, redAvarageColor2, redAvarageColor3});
            greenTotal++;
          }
        }
        if (greenTotal > redTotal) {
          if (pixelColorChannel == 3) {
            filtered.put(row, col, new double[]{greenAvarageColor1, greenAvarageColor2, greenAvarageColor3});
            greenTotal++;
          }
        }
      }
    }
    return filtered;
  }

}
