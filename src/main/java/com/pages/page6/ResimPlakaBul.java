package com.pages.page6;


import net.sourceforge.tess4j.ITessAPI;
import net.sourceforge.tess4j.Tesseract1;
import net.sourceforge.tess4j.TesseractException;
import org.bytedeco.javacpp.opencv_core.IplImage;
import org.bytedeco.javacpp.opencv_core.IplConvKernel;
import org.bytedeco.javacv.CanvasFrame;
import org.opencv.core.Core;

import static org.bytedeco.javacpp.opencv_highgui.*;
import static org.bytedeco.javacpp.opencv_imgproc.*;
import static org.bytedeco.javacpp.opencv_core.*;
public class ResimPlakaBul {


  public static void main(String[] args) {
    System.loadLibrary(Core.NATIVE_LIBRARY_NAME);

    IplConvKernel morfo = IplConvKernel.create(3, 3,1, 1, CV_SHAPE_RECT,null);

    IplImage resim =cvLoadImage("c:/masaustu/Plaka/snapshots/tr/a (37).jpg");
    // IplImage plaka = cvLoadImage(arg0)
    CanvasFrame frame = new CanvasFrame("Resim");
    CanvasFrame frame1 = new CanvasFrame("Plaka Alanı");
    frame.showImage(resim);
    OpenCvDeneme plakabul = new OpenCvDeneme();
    try {
      // plakabul.plakaBul(plakabul.Cevir(resim));
      plakabul.plakaBul(resim);
    } catch (InterruptedException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
    IplImage roiCikis =(plakabul.getRoiCikis().clone());
    cvSaveImage("plaka.jpg", roiCikis);
    //SaveImage("plaka.jpg", plakabul.Cevir(roiCikis));;
    ///cvFlip(roiCikis,roiCikis,90);
    frame1.showImage(roiCikis);


  }


}