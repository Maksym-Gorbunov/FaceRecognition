package com.pages.page6;


import java.awt.image.BufferedImage;

import org.bytedeco.javacpp.opencv_core.IplImage;

import static org.bytedeco.javacpp.helper.opencv_core.CV_RGB;
import static org.bytedeco.javacpp.opencv_core.*;
import static org.bytedeco.javacpp.opencv_imgproc.*;

import org.bytedeco.javacpp.opencv_core;
import org.bytedeco.javacpp.opencv_highgui;
import org.bytedeco.javacpp.opencv_objdetect.CascadeClassifier;
//import org.bytedeco.javacv.CanvasFrame;
import org.bytedeco.javacpp.opencv_core.CvPoint;
import org.bytedeco.javacpp.opencv_core.Mat;
import org.bytedeco.javacpp.opencv_core.Rect;
import org.bytedeco.javacpp.opencv_core.Size;

import com.recognition.software.jdeskew.ImageDeskew;


public class OpenCvDeneme {

  static IplImage roiCikis,sizeImage,gray_image,dondu,cevir;
  //CanvasFrame ilkresim = new CanvasFrame("Resim");
  int plakaX,plakaY,plakaW,plakaH;
  public int getPlakaX() {
    return plakaX;
  }



  public void setPlakaX(int plakaX) {
    this.plakaX = plakaX;
  }



  public int getPlakaY() {
    return plakaY;
  }



  public void setPlakaY(int plakaY) {
    this.plakaY = plakaY;
  }



  public int getPlakaW() {
    return plakaW;
  }



  public void setPlakaW(int plakaW) {
    this.plakaW = plakaW;
  }



  public int getPlakaH() {
    return plakaH;
  }



  public void setPlakaH(int plakaH) {
    this.plakaH = plakaH;
  }




  public static IplImage getDondu() {
    return dondu;
  }



  public static void setDondu(IplImage dondu) {
    OpenCvDeneme.dondu = dondu;
  }



  public static IplImage getCevir() {
    return cevir;
  }



  public static void setCevir(IplImage cevir) {
    OpenCvDeneme.cevir = cevir;
  }



  public static IplImage getRoiCikis() {
    return roiCikis;
  }



  public static void setRoiCikis(IplImage roiCikis) {
    OpenCvDeneme.roiCikis = roiCikis;
  }



  public static IplImage getSizeImage() {
    return sizeImage;
  }



  public static void setSizeImage(IplImage sizeImage) {
    OpenCvDeneme.sizeImage = sizeImage;
  }



  public static IplImage getGray_image() {
    return gray_image;
  }



  public static void setGray_image(IplImage gray_image) {
    OpenCvDeneme.gray_image = gray_image;
  }



  public void plakaBul (IplImage im) throws InterruptedException{

    CascadeClassifier plakaBulucu = new CascadeClassifier("xml/eu.xml");





    IplConvKernel morfo = IplConvKernel.create(3, 3, 1, 1, CV_SHAPE_RECT,null);
    // im = org.bytedeco.javacpp.opencv_highgui.cvLoadImage("c:/users/fevzi/desktop/Plaka/snapshots/tr/a (49).jpg");
    //CanvasFrame  ilkResim = new CanvasFrame("İlk Resm");
    IplImage im2 =IplImage.create(im.width(),im.height(),im.depth(),im.nChannels());
    cvCopy(im, im2);
    IplImage sizeImage2 = IplImage.create(1024,768, im2.depth(),im2.nChannels());
    cvResize(im2, sizeImage2);

    sizeImage = IplImage.create(1024,768, im.depth(),im.nChannels());
    gray_image = IplImage.create(sizeImage.width(),sizeImage.height(), IPL_DEPTH_8U, 1);
    //cevir=Cevir(im);
    cvResize(im, sizeImage);
    cvCvtColor(sizeImage, gray_image, CV_BGR2GRAY);
    cvSmooth(gray_image, gray_image, CV_GAUSSIAN, 3,3, 1, 1);
    cvEqualizeHist(gray_image, gray_image);
    //cvThreshold(gray_image, gray_image, 125, 255, CV_THRESH_BINARY);
    //cvSmooth(gray_image, gray_image, CV_GAUSSIAN, 7, 7, 1, 1);
    //cvDilate(gray_image, gray_image, morfo, 2);
    //cvErode(gray_image, gray_image, morfo, 1);
    //cvDilate(gray_image, gray_image, null, 1);
    //cvErode(gray_image, gray_image, null, 1);
    //cvAdaptiveThreshold(gray_image, gray_image, 255, CV_ADAPTIVE_THRESH_MEAN_C, CV_THRESH_BINARY, 11, 3);
    //cvThreshold(gray_image, gray_image, 15, 255, CV_THRESH_BINARY);
    cvMorphologyEx(gray_image, gray_image,null, morfo, CV_MOP_OPEN,1);
    cvMorphologyEx(gray_image, gray_image,null, morfo, CV_MOP_CLOSE,1);

    //cvCanny(gray_image, gray_image, 5.1, 1);
    Mat resimGiris = new Mat(gray_image, true);
    Rect detectGiris = new Rect();

    //plakaBulucu.detectMultiScale(resimGiris, detectGiris,1.1, 1,1, new Size(20, 20), new Size(300, 300));
    plakaBulucu.detectMultiScale(resimGiris, detectGiris);

   /* cvRectangle(
     sizeImage2,
     cvPoint(detectGiris.x(), detectGiris.y()),
     cvPoint(detectGiris.x() + detectGiris.width(),
       detectGiris.y() + detectGiris.height()),
     cvScalar(0, 255, 0, 0), 2, 8, 0);
     */


    setPlakaX(detectGiris.asCvRect().x());
    setPlakaY(detectGiris.asCvRect().y());
    setPlakaH(detectGiris.asCvRect().height());
    setPlakaW(detectGiris.asCvRect().width());
    opencv_core.cvSetImageROI(sizeImage2, cvRect(detectGiris.asCvRect().x()-10, detectGiris.asCvRect().y(),detectGiris.asCvRect().width()+10, detectGiris.asCvRect().height()));
    roiCikis = IplImage.create(cvGetSize(sizeImage2), sizeImage2.depth(), sizeImage2.nChannels());
    opencv_core.cvCopy(sizeImage2, roiCikis);
    //  cevir=Cevir(roiCikis);

    opencv_core.cvResetImageROI(sizeImage2);




    // ilkresim.showImage(sizeImage2);
  }



  public static opencv_core.IplImage Cevir(opencv_core.IplImage image) {
    BufferedImage img = image.getBufferedImage();
    ImageDeskew cevirAci = new ImageDeskew(img);
    double aci = cevirAci.getSkewAngle();
    opencv_core.IplImage copy = cvCloneImage(image);
    opencv_core.IplImage rotatedImage = cvCreateImage(cvGetSize(copy), copy.depth(), copy.nChannels());
    //Define Rotational Matrix
    opencv_core.CvMat mapMatrix = cvCreateMat(2, 3, CV_32FC1);

    //Define Mid Point
    opencv_core.CvPoint2D32f centerPoint = new opencv_core.CvPoint2D32f();
    centerPoint.x(copy.width() / 2);
    centerPoint.y(copy.height() / 2);

    //Get Rotational Matrix
    cv2DRotationMatrix(centerPoint, aci, 1.0, mapMatrix);

    //Rotate the Image
    cvWarpAffine(copy, rotatedImage, mapMatrix, CV_INTER_CUBIC + CV_WARP_FILL_OUTLIERS, cvScalarAll(255));
    cvReleaseImage(copy);
    cvReleaseMat(mapMatrix);

    return rotatedImage;
  }
}
