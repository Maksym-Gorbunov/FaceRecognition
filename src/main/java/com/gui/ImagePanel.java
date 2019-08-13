 package com.gui;

 import javax.swing.*;
 import java.awt.*;
 import java.io.File;

//import com.recognition.image.constants.Constants;

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
 }
