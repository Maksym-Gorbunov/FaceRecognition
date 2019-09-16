package com.pages.page10;

import com.constants.Constants;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.MatOfByte;
import org.opencv.core.Scalar;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
import org.opencv.videoio.VideoCapture;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.io.ByteArrayInputStream;
import java.io.File;

import static org.bytedeco.javacpp.opencv_core.IplImage;


public class WebcamPanel extends JPanel {
  private static final long serialVersionUID = 1L;
  private JLabel imageLabel;
  private ImageIcon transformedImageIcon;
  private int width;
  private int height;
  private BufferedImage bufferedImage;
  private DaemonThread myThread = null;
  private int count = 0;
  private VideoCapture webSource = null;
  private Mat frame = new Mat();
  private MatOfByte mem = new MatOfByte();
  private Graphics graphics;
  private Mat bg = new Mat();

  // Constructor
  public WebcamPanel(int width, int height) {
    System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
    this.imageLabel = new JLabel();
    setLayout(new BorderLayout());
    setBorder(BorderFactory.createLineBorder(Color.black));
    add(imageLabel, BorderLayout.CENTER);
    setBackground(Color.ORANGE);
    setPreferredSize(new Dimension(width, height));
    this.width = width;
    this.height = height;
  }

//  public Mat getBg() {
//    return bg;
//  }

  // Convert Mat to BufferedImage
  static BufferedImage Mat2BufferedImage(Mat matrix) throws Exception {
    if (matrix == null) {
      return null;
    }
    MatOfByte mob = new MatOfByte();
    Imgcodecs.imencode(".jpg", matrix, mob);
    byte ba[] = mob.toArray();

    BufferedImage bi = ImageIO.read(new ByteArrayInputStream(ba));
    return bi;
  }

  public void setBg() {
    System.out.println("***");
    frame.copyTo(bg);
  }
  ///////////////////////////// class DaemonThread end //////////////////////////////////////

  // Start showing video from webbcam
  public void start() {
    System.out.println("Start...");
    webSource = new VideoCapture(0);
    myThread = new DaemonThread();
    Thread t = new Thread(myThread);
    t.setDaemon(true);
    myThread.runnable = true;
    t.start();
  }

  // Pause video from webcam
  public void stop() {
    System.out.println("Pause...");
    clear();
    setBackground(Color.ORANGE);
    myThread.runnable = false;
    webSource.release();

  }

  // Update image
  public void updadeImage(final Image image) {
    imageLabel.setIcon(new ImageIcon(scaleImage(image)));
  }

  // Scale image
  private Image scaleImage(Image image) {
    return image.getScaledInstance(width, height, Image.SCALE_SMOOTH);
  }

  // Load image from file
  public void loadImage(File file) {
    this.transformedImageIcon = new ImageIcon(file.getAbsolutePath());
    Image image = transformedImageIcon.getImage();
    updadeImage(image);
  }

  // Clear panel
  public void clear() {
    imageLabel.setIcon(null);
  }

  // load image to ImagePanel without extra savers
  public void loadImage(IplImage filteredImage) {
    BufferedImage img1 = filteredImage.getBufferedImage();
    ImageIcon icon = new ImageIcon(img1);
    Image image = icon.getImage();
    updadeImage(image);
  }

  // Load image from Mat
  public void loadImage(Mat filteredImage) {
    if (filteredImage == null) {
      return;
    }
    BufferedImage img1 = null;
    try {
      img1 = Mat2BufferedImage(filteredImage);
    } catch (Exception e) {
      e.printStackTrace();
    }
    ImageIcon icon = new ImageIcon(img1);
    Image image = icon.getImage();
    updadeImage(image);
  }

  // Matrix image BlueGreenRed
  public boolean convertMatToImage(Mat matBGR) {
    int width = matBGR.width();
    int height = matBGR.height();
    int channels = matBGR.channels();
    byte[] sourcePixels = new byte[width * height * channels];
    matBGR.get(0, 0, sourcePixels);
    bufferedImage = new BufferedImage(width, height, BufferedImage.TYPE_3BYTE_BGR);
    final byte[] targetPixel = ((DataBufferByte) bufferedImage.getRaster().getDataBuffer()).getData();
    System.arraycopy(sourcePixels, 0, targetPixel, 0, sourcePixels.length);
    return true;
  }

  // Paint component
  public void paintComponent(Graphics g) {
    super.paintComponent(g);
    if (this.bufferedImage == null) {
      return;
    }
    g.drawImage(this.bufferedImage, 10, 10, this.bufferedImage.getWidth(), this.bufferedImage.getHeight(), null);
  }

  ///////////////////////////// class DaemonThread start //////////////////////////////////////
  class DaemonThread implements Runnable {

    protected volatile boolean runnable = false;


    @Override
    public void run() {
      synchronized (this) {
        while (runnable) {
          if (webSource.grab()) {
            try {
              webSource.retrieve(frame);

//              if(count == 200){
              if (count % 5 == 0) {
                Recognizer recognizer = new Recognizer(frame, count, bg);
                System.out.println(count);
                recognizer.recognize();
              }
              if ((Page10.rect != null) && (Page10.rect.area() != 0)) {
                if (Page10.rect.height < 0.3 * frame.height()) {
                  Imgproc.rectangle(frame, Page10.rect.tl(), Page10.rect.br(), new Scalar(0, 0, 255, 255), 2);
                } else {
                  Imgproc.rectangle(frame, Page10.rect.tl(), Page10.rect.br(), new Scalar(0, 255, 0, 255), 2);
                }
              }

              Imgcodecs.imencode(".bmp", frame, mem);
              BufferedImage buff = ImageIO.read(new ByteArrayInputStream(mem.toArray()));
              graphics = WebcamPanel.this.getGraphics();
              if (graphics.drawImage(buff, 0, 0, Constants.VIDEO_WIDTH, Constants.VIDEO_HEIGHT, 0, 0, buff.getWidth(), buff.getHeight(), null))
                if (runnable == false) {
                  System.out.println("Going to wait()");
                  this.wait();
                }
              count++;
            } catch (Exception e) {
              System.out.println("Error");
            }
          }
        }
      }
    }
  }
}
