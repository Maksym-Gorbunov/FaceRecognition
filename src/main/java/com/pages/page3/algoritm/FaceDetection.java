package com.pages.page3.algoritm;

import com.constants.Constants;
import com.pages.page3.ImagePanel;
import org.opencv.core.*;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
import org.opencv.objdetect.CascadeClassifier;

import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.io.File;

//import com.recognition.image.constants.Constants;
//import com.recognition.image.gui.ImagePanel;

//import org.opencv.*;

public class FaceDetection {
  private CascadeClassifier cascadeClassifier;

  public FaceDetection() {
    System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
    this.cascadeClassifier = new CascadeClassifier(Constants.CASCADE_CLASSIFIER);
  }

  public void detectFaces(File file, ImagePanel imagePanel) {
    Mat image = Imgcodecs.imread(file.getAbsolutePath(), Imgcodecs.IMREAD_COLOR);

    MatOfRect faceDetections = new MatOfRect();
    //cascadeClassifier.detectMultiScale(image, faceDetections);
    cascadeClassifier.detectMultiScale(image, faceDetections, 1.2, 3, 10,
            new Size(40, 40), new Size(500, 500));

    // Paint rectangles around recognized faces
    for (Rect rect : faceDetections.toArray()) {
      Imgproc.rectangle(image,
              new Point(rect.x, rect.y),
              new Point(rect.x + rect.width, rect.y + rect.height),
              new Scalar(100, 100, 250), 10);
    }

    BufferedImage bufferedImage = convertMatToImage(image);
    imagePanel.updadeImage(bufferedImage);
  }

  // Matrix image to image
  private BufferedImage convertMatToImage(Mat mat) {
    // case greyscale image
    int type = BufferedImage.TYPE_BYTE_GRAY;
    // case color image
    if (mat.channels() > 1) {
      type = BufferedImage.TYPE_3BYTE_BGR;
    }

    int bufferSize = mat.channels() * mat.cols() * mat.rows();
    byte[] bytes = new byte[bufferSize];
    mat.get(0, 0, bytes);
    BufferedImage image = new BufferedImage(mat.cols(), mat.rows(), type);
    final byte[] targetPixel = ((DataBufferByte) image.getRaster().getDataBuffer()).getData();
    System.arraycopy(bytes, 0, targetPixel, 0, bytes.length);
    return image;
  }
}
