 package com.pages.page3;

import com.constants.Constants;
//import com.recognition.image.constants.Constants;

import javax.swing.*;
import java.awt.*;
import java.io.File;

 public class ImagePanel extends JPanel{
   private static final long serialVersionUID = 1L;
   private JLabel imageLabel;
   private ImageIcon transformedImageIcon;

   public ImagePanel(){
     this.imageLabel = new JLabel();
     setLayout(new BorderLayout());
 //    setBorder(BorderFactory.createEmptyBorder(Constants.IMAGE_LABEL_BORDER, Constants.IMAGE_LABEL_BORDER,
 //            Constants.IMAGE_LABEL_BORDER, Constants.IMAGE_LABEL_BORDER));
     setBorder(BorderFactory.createLineBorder(Color.black));
     add(imageLabel, BorderLayout.CENTER);
     setBackground(Color.ORANGE);
 //    setSize(700, 200);
   }

   public void updadeImage(final Image image) {
     imageLabel.setIcon(new ImageIcon(scaleImage(image)));
   }

   private Image scaleImage(Image image) {
     return image.getScaledInstance(Constants.VIDEO_WIDTH, Constants.VIDEO_HEIGHT, Image.SCALE_SMOOTH);
   }

   public void loadImage(File file) {
     this.transformedImageIcon = new ImageIcon(file.getAbsolutePath());
     Image image = transformedImageIcon.getImage();
     updadeImage(image);
   }
 }
