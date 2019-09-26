package temp;

import com.constants.Constants;
import com.pages.page9.Page9;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

import java.util.ArrayList;
import java.util.List;

public class ColorBlind {

  public static String path = Constants.imgPath + "colorblind\\";

  public static void main(String[] args) {
    System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
    ColorBlind c = new ColorBlind();
    Mat src = Imgcodecs.imread(path + "color.jpg");
//
//    Mat src1 = Imgcodecs.imread(path + "1.png");
//    Mat gray = new Mat();
//    Imgproc.cvtColor(src1, gray, Imgproc.COLOR_RGB2GRAY);
//    Imgcodecs.imwrite(path + "gray.jpg", gray);

    c.findColor(src);
  }

  private void findColor(Mat img) {
    Mat matrix = new Mat();
    img.copyTo(matrix);

    for (int row = 0; row < matrix.rows(); row++) {
      for (int col = 0; col < matrix.cols(); col++) {
        double[] pixelGBR = matrix.get(row,col);

        if(getColorName(pixelGBR).equals("Red")){
          Point point = new Point(col, row);
          List<Point> neighborhoods = getNeighborhoods(matrix,point);
          for(Point n : neighborhoods){
            double[] neighborBGR = matrix.get(n.y, n.x);
            String neighborName = getColorName(neighborBGR);
          }



          matrix.put(row, col, new double[]{0, 0, 0});
        }
      }
    }

//    Imgcodecs.imwrite(Constants.imgPath + "colorblind\\result1.jpg", matrix);
  }

  private List<Point> getNeighborhoods(Mat src, Point point) {
    List<Point> points = new ArrayList<>();
      if(point.y != 0){
        Point top = new Point(point.x, point.y-1);
        points.add(top);
      }
      if(point.y != src.height()-1){
        Point bottom = new Point(point.x, point.y+1);
        points.add(bottom);
      }
      if(point.x != 0) {
        Point left = new Point(point.x - 1, point.y);
        points.add(left);
      }
      if(point.x != src.width()-1) {
        Point right = new Point(point.x + 1, point.y);
        points.add(right);
      }
    return points;
  }


  private void temp(Mat image) {
//    Mat imageCopy = new Mat();
//    image.copyTo(imageCopy);
//    Mat gray = new Mat();
//    Imgproc.cvtColor(image, gray, Imgproc.COLOR_RGB2GRAY);
//    Imgcodecs.imwrite(path + "gray.jpg", gray);

    //Imgproc.rectangle(frame, Page9.rect.tl(), Page9.rect.br(), blue, 3);
    //Imgproc.putText(cuttedRotatedPlate, String.valueOf((int)c.getAngle()), new Point(10, 30), 1, 1, red, 1);
//    double[] point1 = image.get(30,30);
//    for(double field: point1){
//      System.out.println(field);
//    }
    double[] blue = {255, 0, 0};
    double[] green = {0, 255, 0};
    double[] red = {0, 0, 255};

    Point pointA = new Point(10, 10);
    Point pointB = new Point(100, 10);
    Point pointC = new Point(240, 10);
    Point pointD = new Point(340, 10);

//    image.put((int) pointA.x, (int) pointA.y, blue);
//    image.put((int) pointB.x, (int) pointB.y, blue);
    Imgproc.circle(image, pointA, 10, new Scalar(0, 0, 0), 2);
    Imgproc.circle(image, pointB, 10, new Scalar(0, 0, 0), 2);
    Imgproc.circle(image, pointC, 10, new Scalar(0, 0, 0), 2);
    Imgproc.circle(image, pointD, 10, new Scalar(0, 0, 0), 2);

    double[] a = image.get((int) pointA.y, (int) pointA.x);
    double[] b = image.get((int) pointB.y, (int) pointB.x);
    double[] c = image.get((int) pointC.y, (int) pointC.x);
    double[] d = image.get((int) pointD.y, (int) pointD.x);

    String aColorName = getColorName(a);
    String bColorName = getColorName(b);
    String cColorName = getColorName(c);
    String dColorName = getColorName(d);

    System.out.println("A [" + a[0] + ", " + a[1] + ", " + a[2] + "] " + aColorName);
    System.out.println("B [" + b[0] + ", " + b[1] + ", " + b[2] + "] " + bColorName);
    System.out.println("C [" + c[0] + ", " + c[1] + ", " + c[2] + "] " + cColorName);
    System.out.println("D [" + d[0] + ", " + d[1] + ", " + d[2] + "] " + dColorName);
    System.out.println("_________________________________");


    Imgproc.putText(image, aColorName, new Point(pointA.x - 10, pointA.y + 20), 1, 0.8, new Scalar(0, 0, 0), 1);
    Imgproc.putText(image, bColorName, new Point(pointB.x - 10, pointB.y + 20), 1, 0.8, new Scalar(0, 0, 0), 1);
    Imgproc.putText(image, cColorName, new Point(pointC.x - 10, pointC.y + 20), 1, 0.8, new Scalar(0, 0, 0), 1);
    Imgproc.putText(image, dColorName, new Point(pointD.x - 10, pointD.y + 20), 1, 0.8, new Scalar(0, 0, 0), 1);

    Imgcodecs.imwrite(path + "colorCopy.jpg", image);
  }

  // Get color name from BGR values
  private String getColorName(double[] bgr) {
    double blue = bgr[0];
    double green = bgr[1];
    double red = bgr[2];

    String name = "";

    if ((blue > green) && (blue > red)) {
      name = "Blue";
    }
    if ((green > blue) && (green > red)) {
      name = "Green";
    }
    if ((red > blue) && (red > green)) {
      name = "Red";
    }
    return name;
  }


}


