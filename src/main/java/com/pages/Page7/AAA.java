//package com.pages.Page7;
//
//import com.constants.Constants;
//import org.bytedeco.javacpp.opencv_core;
//import org.opencv.core.Core;
//import org.opencv.core.CvType;
//import org.opencv.core.Mat;
//import org.opencv.imgcodecs.Imgcodecs;
//import org.opencv.imgproc.Imgproc;
//import org.bytedeco.javacpp.opencv_core.*;
//import static org.bytedeco.javacpp.opencv_highgui.cvShowImage;
//import static org.bytedeco.javacpp.opencv_imgproc.cvErode;
//import static org.opencv.highgui.HighGui.imshow;
//
//import org.opencv.core.Core;
//import org.opencv.core.CvType;
//import org.opencv.core.Mat;
//import org.opencv.imgcodecs.Imgcodecs;
//i
//
//
//
//
//public class AAA {
//  public static void main(String[] args) {
//    System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
//
//    Mat img = Imgcodecs.imread(Constants.imgPath+"car3.png");
//    Mat gray = new Mat();
//    Mat draw = new Mat();
//    Mat edge = new Mat();
//
//    Imgproc.cvtColor(img, gray, Imgproc.COLOR_BGR2GRAY);
//    Imgproc.Canny(gray, edge, 50, 150, 3, false);
//    edge.convertTo(draw, CvType.CV_8U);
//
//
//
//
//    if(Imgcodecs.imwrite(Constants.imgPath+"result\\gray.png", gray)){
//      System.out.println("Gray saved to gray.png");
//    }
//
//
//    IplImage i = draw;
//
//    //
////    if(Imgcodecs.imwrite(Constants.imgPath+"result\\edge.png", draw)){
////      System.out.println("Edge saved to edge.png");
////    }
//
//
//  }
//
//
//
//  public BufferedImage Mat2BufferedImage(Mat m) {
//    // Fastest code
//    // output can be assigned either to a BufferedImage or to an Image
//
//    int type = BufferedImage.TYPE_BYTE_GRAY;
//    if ( m.channels() > 1 ) {
//      type = BufferedImage.TYPE_3BYTE_BGR;
//    }
//    int bufferSize = m.channels()*m.cols()*m.rows();
//    byte [] b = new byte[bufferSize];
//    m.get(0,0,b); // get all the pixels
//    BufferedImage image = new BufferedImage(m.cols(),m.rows(), type);
//    final byte[] targetPixels = ((DataBufferByte) image.getRaster().getDataBuffer()).getData();
//    System.arraycopy(b, 0, targetPixels, 0, b.length);
//    return image;
//  }
//}
