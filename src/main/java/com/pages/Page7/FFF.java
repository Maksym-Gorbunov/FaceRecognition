package com.pages.Page7;


import static org.bytedeco.javacpp.opencv_imgproc.*;
import static org.bytedeco.javacpp.opencv_highgui.*;
import static org.bytedeco.javacpp.opencv_core.*;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import javax.swing.JFrame;

import com.constants.Constants;
import net.sourceforge.tess4j.ITessAPI;
import net.sourceforge.tess4j.Tesseract1;
import net.sourceforge.tess4j.TesseractException;

import org.bytedeco.javacpp.Loader;
import org.bytedeco.javacpp.opencv_core;
import org.bytedeco.javacpp.opencv_core.CvMemStorage;
import org.bytedeco.javacpp.opencv_core.CvRect;
import org.bytedeco.javacpp.opencv_core.CvSeq;
import org.bytedeco.javacpp.opencv_core.IplConvKernel;
import org.bytedeco.javacpp.opencv_core.IplImage;
import org.bytedeco.javacv.CanvasFrame;


public class FFF {
  String yeni="",Result="",PlakaMetin="",ters="";
  IplImage grayImg,roiCikis,gray_roi,HarfroiCikis,Harfgray_roi,PlakaAlani;

  public String getYeni() {
    return yeni;
  }

  public void setYeni(String yeni) {
    this.yeni = yeni;
  }

  public String getTers() {
    return ters;
  }

  public void setTers(String ters) {
    this.ters = ters;
  }

  public FFF()throws InterruptedException{
    CanvasFrame frame1 = new CanvasFrame("İlk plaka");
    CanvasFrame frame2 = new CanvasFrame("Kernarı Temiz plaka");
    CanvasFrame frame3 = new CanvasFrame("OCR alanı plaka");
    CanvasFrame frame4 = new CanvasFrame("Bulunan Char plaka");
    CanvasFrame frame5 = new CanvasFrame("Filter plaka");
    int kernelSize =3;
    int KernelAnchorOffset=1;
    IplConvKernel morfo2=cvCreateStructuringElementEx(kernelSize, kernelSize, KernelAnchorOffset, KernelAnchorOffset, CV_SHAPE_RECT);
    IplConvKernel morfo = IplConvKernel.create(3, 3,1, 1, CV_SHAPE_RECT,null);
//    IplImage plaka = cvLoadImage(Constants.imgPath+"\\car002.jpg");
//    IplImage plaka = cvLoadImage(Constants.imgPath+"\\car3.png");
//    IplImage plaka = cvLoadImage("plaka.jpg");
    IplImage img = cvLoadImage("plaka.jpg");
    frame1.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    frame1.showImage(img);

    PlakaAlani = IplImage.create(300, 70, img.depth(), img.nChannels());
    grayImg = IplImage.create(PlakaAlani.width(), PlakaAlani.height(),IPL_DEPTH_8U, 1);
    cvResize(img, PlakaAlani);
    cvCvtColor(PlakaAlani, grayImg, CV_BGR2GRAY);
    cvAdaptiveThreshold(grayImg, grayImg, 255, CV_ADAPTIVE_THRESH_MEAN_C, CV_THRESH_BINARY, 15, 5);
    cvSmooth(grayImg, grayImg, CV_MEDIAN, 7, 7, 1, 1);
    cvMorphologyEx(grayImg, grayImg,null, morfo, CV_MOP_OPEN,1);
    cvSmooth(grayImg, grayImg, CV_MEDIAN, 5, 5, 1, 1);
    cvEqualizeHist(grayImg, grayImg);

    frame5.showImage(grayImg);

    CvMemStorage storage = CvMemStorage.create();
    CvSeq contours = new CvSeq();
    CvSeq ptr = new CvSeq();


    cvFindContours(grayImg, storage, contours,
            Loader.sizeof(opencv_core.CvContour.class), CV_RETR_EXTERNAL,
            CV_CHAIN_APPROX_SIMPLE, cvPoint(0, 0));

    CvRect boundbox = null;
    int i = 0;
    ArrayList<CvRect> HarfAlani = new ArrayList<CvRect>();
    ArrayList<CvRect> YaziAlani = new ArrayList<CvRect>();
    for (ptr = contours; ptr != null; ptr = ptr.h_next()) {
      boundbox = cvBoundingRect(ptr, 0);

      if (boundbox.width() * boundbox.height() >5000 && boundbox.width() * boundbox.height() < 20000) {
        YaziAlani.add(i,boundbox);
        i=i+1;

      }
    }
    opencv_core.cvClearMemStorage(storage);
    contours = null;
    ptr = null;

    CvMemStorage Harfmem = opencv_core.CvMemStorage.create();
    CvSeq Harfcontours = new opencv_core.CvSeq();
    CvSeq Harfptr = new opencv_core.CvSeq();

    try {
      opencv_core.cvSetImageROI(PlakaAlani,cvRect(YaziAlani.get(0).x(), YaziAlani.get(0).y(),
              YaziAlani.get(0).width(),
              YaziAlani.get(0).height()));

      roiCikis = IplImage.create(cvGetSize(PlakaAlani),PlakaAlani.depth(), PlakaAlani.nChannels());
      cvCopy(PlakaAlani, roiCikis);
      cvResetImageROI(PlakaAlani);
      gray_roi = IplImage.create(roiCikis.width(),roiCikis.height(), IPL_DEPTH_8U, 1);
      cvCvtColor(roiCikis, gray_roi, CV_BGR2GRAY);
      cvAdaptiveThreshold(gray_roi, gray_roi, 255, CV_ADAPTIVE_THRESH_MEAN_C,CV_THRESH_BINARY, 15, 5);
      cvSmooth(gray_roi, gray_roi, CV_MEDIAN, 7, 7,1,1);
      cvMorphologyEx(gray_roi, gray_roi,null, morfo2, CV_MOP_OPEN,1);
      cvSmooth(gray_roi, gray_roi, CV_MEDIAN, 5, 5, 1, 1);
      cvEqualizeHist(gray_roi, gray_roi);
      cvCanny(gray_roi, gray_roi, 200, 200);
      frame2.showImage(gray_roi);
      cvFindContours(gray_roi, Harfmem, Harfcontours,
              Loader.sizeof(opencv_core.CvContour.class), CV_RETR_TREE,
              CV_CHAIN_APPROX_SIMPLE, cvPoint(0, 0));

        CvRect Harfboundbox =null;
      int j = 0;
      for (Harfptr = Harfcontours; Harfptr != null; Harfptr = Harfptr.h_next()) {
        Harfboundbox = cvBoundingRect(Harfptr, 0);
        if ((Harfboundbox.height() > 20)&&(Harfboundbox.width()>12) && Harfboundbox.width() * Harfboundbox.height() < 3000){
          HarfAlani.add(j,Harfboundbox);
          j=j+1;
          cvRectangle(
                  PlakaAlani,
                  cvPoint(Harfboundbox.x(), Harfboundbox.y()),
                  cvPoint(Harfboundbox.x() + Harfboundbox.width(),
                          Harfboundbox.y() + Harfboundbox.height()),
                  cvScalar(225, 0, 0, 0), 2, 8, 0);

        }
      }
      frame4.showImage(PlakaAlani);
      int height =0;
      for(int h=0;h<HarfAlani.size();h++){
        height +=HarfAlani.get(h).height();
      }
      if(HarfAlani.size()>0){
        int ortalama =height/HarfAlani.size();
        for(int h=0;h<HarfAlani.size();h++){
          if(HarfAlani.get(h).height()<ortalama-1){
            HarfAlani.remove(h);
          }
        }
      }
      opencv_core.cvResetImageROI(gray_roi);
      opencv_core.cvResetImageROI(PlakaAlani);
      Collections.sort(HarfAlani, Sirala);
      frame3.showImage(roiCikis);
      for(int h=0;h<HarfAlani.size();h++){

        opencv_core.cvSetImageROI(roiCikis,cvRect(HarfAlani.get(h).x()-3, HarfAlani.get(h).y()-3,
                HarfAlani.get(h).width()+3,
                HarfAlani.get(h).height()+6));
        HarfroiCikis = IplImage.create(cvGetSize(roiCikis),roiCikis.depth(), roiCikis.nChannels());
        cvCopy(roiCikis, HarfroiCikis);
        cvResetImageROI(roiCikis);
        Harfgray_roi = IplImage.create(HarfroiCikis.width(),HarfroiCikis.height(), IPL_DEPTH_8U, 1);
        cvCvtColor(HarfroiCikis, Harfgray_roi, CV_BGR2GRAY);
        cvAdaptiveThreshold(Harfgray_roi, Harfgray_roi, 255,CV_ADAPTIVE_THRESH_MEAN_C, CV_THRESH_BINARY, 15, 5);
        cvSmooth(gray_roi, gray_roi, CV_MEDIAN, 7, 7,3,3);
        int zero = cvCountNonZero(Harfgray_roi);
        //System.out.println(zero);
        if(zero<600){
          cvDilate(Harfgray_roi, Harfgray_roi,morfo2,1);
          // cvThreshold(Harfgray_roi, Harfgray_roi, 0, 255, CV_THRESH_BINARY  | CV_THRESH_OTSU);
          cvErode(Harfgray_roi, Harfgray_roi,morfo2,1);
          //System.out.println("Girdi..");

        }
        cvMorphologyEx(Harfgray_roi, Harfgray_roi, null, morfo2, CV_MOP_CLOSE, 1);
        cvSaveImage("oooo.jpg", Harfgray_roi);
//        cvSaveImage("harfler/"+h+"_harf.jpg", Harfgray_roi);
        // System.setProperty("jna.library.path","c:/javacv-bin/Tess4J/");

        String TESS_DATA = Constants.projectPath+"\\lib\\tesseract-OCR\\";
        Tesseract1 instance = new Tesseract1();
        instance.setDatapath(TESS_DATA);
//        instance.setLanguage("leu");
        instance.setPageSegMode(ITessAPI.TessPageSegMode.PSM_SINGLE_CHAR);
        BufferedImage harfGrayBufferedImage =Harfgray_roi.getBufferedImage();
        try {
          Result = instance.doOCR(harfGrayBufferedImage);
          PlakaMetin += Result;
          System.out.println("Bulunan Textler: " + Result);
        } catch (TesseractException e) {
          System.err.println("Tesseract hatasi alindi."+ e.getMessage());
        }

      }
    }catch(Exception e){
      System.out.println(e);

    }

    try{
      yeni = PlakaMetin.replaceAll("\n", "");
    }catch(Exception e){
      System.out.println("yazacak bir şey bulunamadı...");
      yeni="";
    }
    PlakaMetin="";
    opencv_core.cvClearMemStorage(Harfmem);
    Harfcontours = null;
    Harfptr = null;
    System.out.println(yeni);
    //JOptionPane.showMessageDialog(null, yeni);
  }

  public static Comparator<CvRect> Sirala = new Comparator<CvRect>() {

    public int compare(CvRect a,CvRect b){
      int satirNo1 = a.x();
      int satirNo2 = b.x();

      return satirNo1 - satirNo2; //A'dan z ye
    }};
  public static void main(String args[]) throws InterruptedException{
    new FFF();
  }
}

