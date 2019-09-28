package colorblind;

import com.constants.Constants;
import org.opencv.core.*;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.bytedeco.javacpp.opencv_imgproc.RETR_TREE;

public class ColorblindPlugin {

  public static String path = Constants.imgPath + "colorblind\\";

  public static void main(String[] args) {
    System.loadLibrary(Core.NATIVE_LIBRARY_NAME);

    ColorblindPlugin c = new ColorblindPlugin();

    Mat image = Imgcodecs.imread(Constants.imgPath + "colorblind\\1.jpg");
    //check time
    long startTime = System.nanoTime();
    c.findColorConflict(image);
    long endTime = System.nanoTime();
    long duration = (endTime - startTime);
    System.out.println(duration / 1000000);
  }

  public void findColorConflict(Mat img) {
    boolean conflict = false;
//    File f = new File(imgPath);
//    String fileName = f.getName();
//    String filenameWithoutExt = FilenameUtils.removeExtension(fileName);
//    String ext = FilenameUtils.getExtension(fileName);
    //List<Point> neighboursList = new ArrayList<>();
    Mat monoImg = new Mat(img.rows(), img.cols(), CvType.CV_8UC1, new Scalar(255));

    for (int row = 0; row < img.rows(); row++) {
      for (int col = 0; col < img.cols(); col++) {
        double[] pointGBR = img.get(row, col);
        int pixelColorChannel = getColorChannel(pointGBR);

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
              monoImg.put((int) neighbor.y, (int) neighbor.x, new double[]{0, 0, 0});
              img.put((int) neighbor.y, (int) neighbor.x, new double[]{0, 0, 0});
            }
          }
        }
      }
    }
    if (conflict) {
      Imgcodecs.imwrite(path + "conflicts.jpg", img);
      Imgcodecs.imwrite(path + "mono.jpg", monoImg);
    }

    //draw rects
    List<Rect> rectList = getRectangles(monoImg);
    if ((rectList != null) && (rectList.size() > 0)) {
      for (Rect rect : rectList) {
        Imgproc.rectangle(img, rect.tl(), rect.br(), new Scalar(0, 0, 0), 1);
      }
    }
    Imgcodecs.imwrite(path + "rectangles.jpg", img);
  }


  private List<Rect> getRectangles(Mat monoImg) {
    List<Rect> tempRectList = new ArrayList<>();
    List<MatOfPoint> tempContours = new ArrayList<>();
    Imgproc.findContours(monoImg, tempContours, new Mat(), RETR_TREE, Imgproc.CHAIN_APPROX_SIMPLE);
    for (MatOfPoint c : tempContours) {



      MatOfPoint2f pointsArea = new MatOfPoint2f(c.toArray());
      RotatedRect rotatedRectangle = Imgproc.minAreaRect(pointsArea);
      if (rotatedRectangle.size.area() < 0.5 * monoImg.size().area()) {


        Point rotatedRectPoints[] = new Point[4];
        rotatedRectangle.points(rotatedRectPoints);
        Rect rect = Imgproc.boundingRect(new MatOfPoint(rotatedRectPoints));
        //rect = cutRectIfOutOfImageArea(monoImg, rect);
        // fill small contours with color for easy family grouping
        Imgproc.rectangle(monoImg, rect.tl(), rect.br(), new Scalar(0, 0, 0), -1);

        //Imgproc.fillPoly(monoImg, Arrays.asList(c), new Scalar(0,0,0));
//        MatOfPoint m = new MatOfPoint(rect);
//        Imgproc.fillConvexPoly(monoImg, new MatOfPoint(rotatedRectPoints), new Scalar(0,0,0));

        tempRectList.add(rect);
      }
    }
//    Imgproc.GaussianBlur(monoImg, monoImg, new Size(5, 5), 1);
    Imgcodecs.imwrite(path + "mono1.jpg", monoImg);


    // group rectangles, collect neighbouring small contours in one larger
    if (tempRectList.size() > 0) {
      List<Rect> rectList = new ArrayList<>();
      List<MatOfPoint> contours = new ArrayList<>();
      Imgproc.findContours(monoImg, contours, new Mat(), RETR_TREE, Imgproc.CHAIN_APPROX_SIMPLE);
      for (MatOfPoint c : tempContours) {
        MatOfPoint2f pointsArea = new MatOfPoint2f(c.toArray());
        RotatedRect rotatedRectangle = Imgproc.minAreaRect(pointsArea);
        //if(rotatedRectangle.size.area() < 0.5*monoImg.size().area()){
        Point rotatedRectPoints[] = new Point[4];
        rotatedRectangle.points(rotatedRectPoints);
        Rect rect = Imgproc.boundingRect(new MatOfPoint(rotatedRectPoints));
        //rect = cutRectIfOutOfImageArea(monoImg, rect);
        Imgproc.rectangle(monoImg, rect.tl(), rect.br(), new Scalar(0, 0, 0), 1);
        rectList.add(rect);
        //}
      }
      Imgcodecs.imwrite(path + "mono2.jpg", monoImg);
      return rectList;
    }
    return null;
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
    int channel = 0;
    if ((blue > green) && (blue > red)) {
      channel = 1;
    }
    if ((green > blue) && (green > red)) {
      channel = 2;
    }
    if ((red > blue) && (red > green)) {
      channel = 3;
    }
    return channel;
  }

}
