package com.pages.page9;

import com.constants.Constants;
import net.sourceforge.tess4j.Tesseract;
import net.sourceforge.tess4j.TesseractException;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfByte;
import org.opencv.imgcodecs.Imgcodecs;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.io.ByteArrayInputStream;
import java.io.File;

public class TextRecognizer {
  public static String TESS_DATA = Constants.projectPath + "\\lib\\tesseract-OCR\\";

  public static String recognizeText(String imgPath) {
    if ((imgPath == null) || (imgPath.equals(""))) {
      return "";
    }
    Tesseract tesseract = new Tesseract();
    tesseract.setDatapath(TESS_DATA);
    try {
      String result = "";
      result = tesseract.doOCR(new File(imgPath));
      result = result.replaceAll("[^A-Z0-9]", "");
      return result;
    } catch (TesseractException e) {
      e.printStackTrace();
    }
    return "";
  }

  public static String recognizeText(Mat img) {
    if (img == null) {
      return "";
    }
    BufferedImage bufferedImage = null;
    Tesseract tesseract = new Tesseract();
    String TESS_DATA = Constants.projectPath + "\\lib\\tesseract-OCR\\";
    tesseract.setDatapath(TESS_DATA);
    try {
      String result = "";
      bufferedImage = Mat2BufferedImage(img);
      result = tesseract.doOCR(bufferedImage);
      result = result.replaceAll("[^A-Z0-9]", "");
      return result;
    } catch (TesseractException e) {
      e.printStackTrace();
    } catch (Exception e) {
      e.printStackTrace();
    }
    return "";
  }

  public static String recognizeText(BufferedImage bufferedImage) {
    if (bufferedImage == null) {
      return "";
    }
    Tesseract tesseract = new Tesseract();
    tesseract.setDatapath(TESS_DATA);
    try {
      String result = "";
      result = tesseract.doOCR(bufferedImage);
      result = result.replaceAll("[^A-Z0-9]", "");
      return result;
    } catch (TesseractException e) {
      e.printStackTrace();
    } catch (Exception e) {
      e.printStackTrace();
    }
    return "";
  }

  // Convert Mat to BufferedImage
  public static BufferedImage Mat2BufferedImage(Mat matrix) throws Exception {
    MatOfByte mob = new MatOfByte();
    Imgcodecs.imencode(".jpg", matrix, mob);
    byte ba[] = mob.toArray();
    BufferedImage bi = ImageIO.read(new ByteArrayInputStream(ba));
    return bi;
  }

  // Convert BufferedImage to Mat
  public static Mat bufferedImage2Mat(BufferedImage bi) {
    Mat mat = new Mat(bi.getHeight(), bi.getWidth(), CvType.CV_8UC1);
    //Mat mat = new Mat(bi.getHeight(), bi.getWidth(), CvType.CV_8UC3);
    byte[] data = ((DataBufferByte) bi.getRaster().getDataBuffer()).getData();
    mat.put(0, 0, data);
    return mat;
  }

}
