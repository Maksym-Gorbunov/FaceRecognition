 package com.gui;

 import org.bytedeco.javacpp.opencv_core;

 import javax.imageio.ImageIO;
 import javax.swing.*;
 import java.awt.*;
 import java.io.ByteArrayInputStream;
 import java.io.File;
 import java.awt.image.BufferedImage;

//import com.recognition.image.constants.Constants;
 import org.opencv.core.Mat;
 import org.opencv.core.MatOfByte;
 import org.opencv.imgcodecs.Imgcodecs;

 public class ImagePanel extends JPanel{
   private static final long serialVersionUID = 1L;
   private JLabel imageLabel;
   private ImageIcon transformedImageIcon;
   private int width;
   private int height;

   public ImagePanel(int width, int height){
     this.imageLabel = new JLabel();
     setLayout(new BorderLayout());
     setBorder(BorderFactory.createLineBorder(Color.black));
     add(imageLabel, BorderLayout.CENTER);
     setBackground(Color.ORANGE);
     setPreferredSize(new Dimension(width,height));
     this.width = width;
     this.height =height;
   }

   public void updadeImage(final Image image) {
     imageLabel.setIcon(new ImageIcon(scaleImage(image)));
   }

   private Image scaleImage(Image image) {
     return image.getScaledInstance(width, height, Image.SCALE_SMOOTH);
//     return image.getScaledInstance(Constants.VIDEO_WIDTH, Constants.VIDEO_HEIGHT, Image.SCALE_SMOOTH);
   }

   public void loadImage(File file) {
     this.transformedImageIcon = new ImageIcon(file.getAbsolutePath());
     Image image = transformedImageIcon.getImage();
     updadeImage(image);
   }

   public void clear() {
     imageLabel.setIcon(null);
   }

   // load image to ImagePanel without extra savers
   public void loadIplImage(opencv_core.IplImage filteredImage) {
    BufferedImage img1 = filteredImage.getBufferedImage();
     ImageIcon icon = new ImageIcon(img1);
     Image image = icon.getImage();
     updadeImage(image);
   }

   static BufferedImage Mat2BufferedImage(Mat matrix)throws Exception {
     if(matrix == null){
       return null;
     }
     MatOfByte mob=new MatOfByte();
     Imgcodecs.imencode(".jpg", matrix, mob);
     byte ba[]=mob.toArray();
     BufferedImage bi= ImageIO.read(new ByteArrayInputStream(ba));
     return bi;
   }

   public void loadImage(Mat img) {
     if((img == null)){
       return;
     }
     BufferedImage buffImg = null;
     try {
       buffImg = Mat2BufferedImage(img);
     } catch (Exception e) {
       e.printStackTrace();
     }
     ImageIcon icon = new ImageIcon(buffImg);
     Image image = icon.getImage();
     updadeImage(image);
   }
 }
