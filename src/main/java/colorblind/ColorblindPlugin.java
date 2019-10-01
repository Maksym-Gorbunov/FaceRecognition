package colorblind;

import com.constants.Constants;
import org.opencv.core.*;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.bytedeco.javacpp.opencv_imgproc.COLOR_BGR2HSV;
import static org.bytedeco.javacpp.opencv_imgproc.RETR_TREE;

public class ColorblindPlugin {

  public static String path = Constants.imgPath + "colorblind\\";
  private Scalar white = new Scalar(255, 255, 255);
  private double[] whitePixel = new double[]{255, 255, 255};
  private Scalar black = new Scalar(0, 0, 0);
  private Scalar blue = new Scalar(255, 0, 0);
  private boolean logger = false;

  public static void main(String[] args) throws IOException {
    System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
    ColorblindPlugin colorblindPlugin = new ColorblindPlugin();
    Mat image = Imgcodecs.imread(Constants.imgPath + "colorblind\\colors.jpg");
    colorblindPlugin.findColorConflict(image);
  }


  private Mat colorblindFilter3(Mat img) {
    Mat filtered = new Mat();
    img.copyTo(filtered);
    int redTotal = 0;
    int greenTotal = 0;
    double redChannelValue = 0;
    double greenChannelValue = 0;
    for (int row = 0; row < img.rows(); row++) {
      for (int col = 0; col < img.cols(); col++) {
        double[] pixelBGR = img.get(row, col);
        int pixelColorChannel = getColorChannel(pixelBGR);
        if (pixelColorChannel == 3) {
          redChannelValue += img.get(row, col)[2];
          redTotal++;
        }
        if (pixelColorChannel == 2) {
          greenChannelValue += img.get(row, col)[1];
          greenTotal++;
        }
      }
    }
    double redAvarageColor = redChannelValue / redTotal;
    double greenAvarageColor = greenChannelValue / greenTotal;
    for (int row = 0; row < img.rows(); row++) {
      for (int col = 0; col < img.cols(); col++) {
        double[] pixelBGR = img.get(row, col);
        int pixelColorChannel = getColorChannel(pixelBGR);
        if (redTotal > greenTotal) {
          if (pixelColorChannel == 2) {
            filtered.put(row, col, new double[]{0, 0, redAvarageColor});
            greenTotal++;
          }
        }
        if (greenTotal > redTotal) {
          if (pixelColorChannel == 3) {
            filtered.put(row, col, new double[]{0, greenAvarageColor, 0});
            greenTotal++;
          }
        }
      }
    }
    return filtered;
  }


  // Simulate colorblind filter
  private Mat colorblindFilter2(Mat img) {
    Mat filtered = new Mat();
    img.copyTo(filtered);
    double blue = 0;
    double green = 0;
    double red = 0;
    for (int row = 0; row < img.rows(); row++) {
      for (int col = 0; col < img.cols(); col++) {
        double[] pixelBGR = img.get(row, col);
        int pixelColorChannel = getColorChannel(pixelBGR);
        if (pixelColorChannel == 3) {
          Point point = new Point(col, row);
          List<Point> neighbours = getNeighborPixels(img, point);
          int mainColorChannel = getMainColor(neighbours, img);
          if (mainColorChannel == 2) {
            filtered.put(row, col, new double[]{0, 255, 0});
          }
          if (mainColorChannel == 3) {
            filtered.put(row, col, new double[]{0, 0, 255});
          }
        }
        if (pixelColorChannel == 2) {
          Point point = new Point(col, row);
          List<Point> neighbours = getNeighborPixels(img, point);
          int mainColorChannel = getMainColor(neighbours, img);
          if (mainColorChannel == 2) {
            filtered.put(row, col, new double[]{0, 255, 0});
          }
          if (mainColorChannel == 3) {
            filtered.put(row, col, new double[]{0, 0, 255});
          }
        }
      }
    }
    return filtered;
  }

  // Check if exist 'red/green' color conflict
  private boolean isConflict(Mat img, List<Point> neighbours) {
    for (Point p : neighbours) {
      int channel = getColorChannel(img.get((int) p.y, (int) p.x));
      if (channel == 2) {
        return true;
      }
    }
    return false;
  }


  // Get main color from neighbours
  private char getMainColor(List<Point> neighbours, Mat img) {
    int redTotal = 1;
    int greenTotal = 0;
    for (Point p : neighbours) {
      if (getColorChannel(img.get((int) p.y, (int) p.x)) == 2) {  // if pixel green
        greenTotal++;
        System.out.println("-------");
      }
      if (getColorChannel(img.get((int) p.y, (int) p.x)) == 3) {  //if pixel red
        redTotal++;
      }
    }
    System.out.println(greenTotal + " : " + redTotal);
    if (greenTotal > redTotal) {
      return 2;
    }
    if (redTotal > greenTotal) {
      return 3;
    }
    return 1;
  }


  // find color conflict ('Red/Green') on Mat image(BGR-format)
  public void findColorConflict(Mat img) {
    boolean conflict = false;
    Mat monoImg = new Mat(img.rows(), img.cols(), CvType.CV_8UC1, new Scalar(0));
    Mat tempImg = null;
    if (logger) {
      tempImg = new Mat();
      img.copyTo(tempImg);
    }
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
            // match 'red/green' conflict
            if (neighbourColorChannel == 2) {
              conflict = true;
              monoImg.put((int) neighbor.y, (int) neighbor.x, whitePixel);
              if (logger) {
                tempImg.put((int) neighbor.y, (int) neighbor.x, whitePixel);
              }
            }
          }
        }
      }
    }
    if (conflict) {
      if (logger) {
        Imgcodecs.imwrite(path + "conflicts.jpg", img);
        Imgcodecs.imwrite(path + "mono.jpg", monoImg);
      }
      //draw rects
      List<Rect> rectList = getRectangles(monoImg, img);
      System.out.println(rectList.size());
      int i = 0;
      if ((rectList != null) && (rectList.size() > 0)) {
        for (Rect rect : rectList) {
          Mat mask = new Mat(img, rect);
          Mat maskFiltered = colorblindFilter3(mask);
          Imgcodecs.imwrite(path + "result\\" + i + "_mask.jpg", mask);
          Imgcodecs.imwrite(path + "result\\" + i + "_mask_filtered.jpg", maskFiltered);
          Imgproc.rectangle(img, rect.tl(), rect.br(), new Scalar(0, 0, 0), 2);
          i++;
        }
        Imgcodecs.imwrite(path + "result\\rectangles.jpg", img);
      }
    } else {
      System.out.println("Color conflict not found");
    }
  }


  // Cut off contours rectangle if out off image area, fix bug of OpenCV library
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


  // Get all pointer neighbours from matrix image
  //   n  n  n
  //   n 'c' n
  //   n  n  n
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
    int coefficient = 0;
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


  // Convert BufferedImage to Mat
  public static Mat bufferedImageToMat(BufferedImage bi) {
    Mat mat = new Mat(bi.getHeight(), bi.getWidth(), CvType.CV_8SC(4));
//    Mat mat = new Mat(bi.getHeight(), bi.getWidth(), CvType.CV_8UC3);
    byte[] data = ((DataBufferByte) bi.getRaster().getDataBuffer()).getData();
    mat.put(0, 0, data);
    return mat;
  }


  private List<Rect> getRectangles(Mat monoImg, Mat img) {
    Mat copy = new Mat();
    img.copyTo(copy);
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
        if (rect.area() > 0) {
          Imgproc.rectangle(monoImg, rect.tl(), rect.br(), white, -1);
          Imgproc.rectangle(copy, rect.tl(), rect.br(), black, 2);
          rectList.add(rect);
        }
      }
    }
    if (logger) {
      Imgcodecs.imwrite(path + "mono_rects1.jpg", monoImg);
      Imgcodecs.imwrite(path + "color_rects1.jpg", copy);
    }
    if (rectList.size() > 0) {
      List<Rect> result = groupRectangles(monoImg);
      return result;
    }
    return null;
  }

  // Group mini rects groups in larger rects, combine intersecting bounding rectangles
  private List<Rect> groupRectangles(Mat monoImg) {
    List<Rect> rectList = new ArrayList<>();
    List<MatOfPoint> contours = new ArrayList<>();
    Imgproc.GaussianBlur(monoImg, monoImg, new Size(5, 5), 1, 1);
    Imgproc.findContours(monoImg, contours, new Mat(), RETR_TREE, Imgproc.CHAIN_APPROX_SIMPLE);
    for (MatOfPoint c : contours) {
      MatOfPoint2f pointsArea = new MatOfPoint2f(c.toArray());
      RotatedRect rotatedRectangle = Imgproc.minAreaRect(pointsArea);
      Point rotatedRectPoints[] = new Point[4];
      rotatedRectangle.points(rotatedRectPoints);
      Rect rect = Imgproc.boundingRect(new MatOfPoint(rotatedRectPoints));
      Imgproc.rectangle(monoImg, rect.tl(), rect.br(), white, -1);
      rect = cutRectIfOutOfImageArea(monoImg, rect);
      rectList.add(rect);
    }
    rectList = removeInnerRects(rectList);
    if (contours.size() > 0) {
      if (logger) {
        Imgcodecs.imwrite(path + "mono_rects2.jpg", monoImg);
      }
      return rectList;
    }
    return null;
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
      if (!inside && rect.area() > 500) {
        cleanedRects.add(rect);
      }
    }
    return cleanedRects;
  }
}
