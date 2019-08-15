package com.gui;

import org.bytedeco.javacpp.opencv_core;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;

import org.bytedeco.javacpp.opencv_core.IplImage;

import static org.bytedeco.javacpp.opencv_core.*;
import static org.bytedeco.javacpp.opencv_imgproc.*;
import static org.bytedeco.javacpp.opencv_highgui.*;
//import com.recognition.image.constants.Constants;

public class ImagePanel extends JPanel {
  private static final long serialVersionUID = 1L;
  private JLabel imageLabel;
  private ImageIcon transformedImageIcon;
  private int width;
  private int height;

  public ImagePanel(int width, int height) {
    this.imageLabel = new JLabel();
    setLayout(new BorderLayout());
    setBorder(BorderFactory.createLineBorder(Color.black));
    add(imageLabel, BorderLayout.CENTER);
    setBackground(Color.ORANGE);
    setPreferredSize(new Dimension(width, height));
    this.width = width;
    this.height = height;
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

  public void loadIplImage(String imgPath) {
    this.transformedImageIcon = new ImageIcon(imgPath);
    Image image = transformedImageIcon.getImage();
    updadeImage(image);
  }

  public void clear() {
    this.removeAll();
    this.validate();
    this.repaint();
//    imageLabel.updateUI();
//    imageLabel = null;
//    transformedImageIcon = null;
//    updadeImage(null);
  }
}
