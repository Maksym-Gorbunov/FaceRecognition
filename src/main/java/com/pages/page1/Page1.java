package com.pages.page1;

import com.constants.Constants;
import com.gui.Gui;
import com.pages.Pages;
import org.opencv.core.Mat;
import org.opencv.core.MatOfByte;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.videoio.VideoCapture;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;


// Simble webbcamera
public class Page1 extends JPanel implements Pages {
  private static final long serialVersionUID = 1L;
  private Gui gui;
  private JPanel tab1;
  private JPanel mainPanel = new JPanel();
  private JPanel buttonsPanel = new JPanel();
  private JButton startButton = new JButton("Start");
  private JButton pauseButton = new JButton("Pause");
  private JButton testButton = new JButton("Test");
  private JPanel webcamPanel = new JPanel();
  private Graphics graphics;
  private boolean status = false;
  private Color defaultPanelColor;

  public Page1(final Gui gui) {
    this.gui = gui;
    tab1 = gui.getTab1();
    initComponents();
    addListeners();
  }

  private void initComponents() {

    ///////////////////////////////////////////////////////////////
    mainPanel.setBackground(Color.blue);
    buttonsPanel.setBackground(Color.green);
    ///////////////////////////////////////////////////////////////

    webcamPanel.setPreferredSize(new Dimension(Constants.VIDEO_WIDTH, Constants.VIDEO_HEIGHT));
    mainPanel.add(webcamPanel);

    buttonsPanel.add(startButton);
    buttonsPanel.add(pauseButton);
    buttonsPanel.add(testButton);

    tab1.add(mainPanel);
    tab1.add(buttonsPanel);
    mainPanel.setPreferredSize(new Dimension(800,500));
    buttonsPanel.setPreferredSize(new Dimension(800,100));
    defaultPanelColor = webcamPanel.getBackground();
    pauseButton.setEnabled(false);
  }

  private void addListeners() {
    startButton.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        System.out.println("Start...");
        webSource = new VideoCapture(0);
        myThread = new DaemonThread();
        Thread t = new Thread(myThread);
        t.setDaemon(true);
        myThread.runnable = true;
        t.start();
        startButton.setEnabled(false);
        pauseButton.setEnabled(true);
        testButton.setEnabled(false);
        gui.getTabs().setEnabled(false);
      }
    });

    pauseButton.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        System.out.println("Pause...");
        myThread.runnable = false;
        pauseButton.setEnabled(false);
        startButton.setEnabled(true);
        webSource.release();
        testButton.setEnabled(true);
        gui.getTabs().setEnabled(true);
      }
    });

    testButton.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        status = !status;
        if (status) {
          webcamPanel.setBackground(Color.ORANGE);
        } else {
          webcamPanel.setBackground(defaultPanelColor);
        }
        JOptionPane.showMessageDialog(null, "Test...", "Test title", JOptionPane.QUESTION_MESSAGE);
      }
    });

  }

  // definitions
  private DaemonThread myThread = null;
  int count = 0;
  VideoCapture webSource = null;
  Mat frame = new Mat();
  MatOfByte mem = new MatOfByte();


  // class DeamonThread
  class DaemonThread implements Runnable {
    protected volatile boolean runnable = false;

    @Override
    public void run() {
      synchronized (this) {
        while (runnable) {
          if (webSource.grab()) {
            try {
              webSource.retrieve(frame);
              Imgcodecs.imencode(".bmp", frame, mem);
              BufferedImage buff = ImageIO.read(new ByteArrayInputStream(mem.toArray()));
              graphics = webcamPanel.getGraphics();
              if (graphics.drawImage(buff, 0, 0, Constants.VIDEO_WIDTH, Constants.VIDEO_HEIGHT, 0, 0, buff.getWidth(), buff.getHeight(), null))
                if (runnable == false) {
                  System.out.println("Going to wait()");
                  this.wait();
                }
            } catch (Exception e) {
              System.out.println("Error");
            }
          }
        }
      }
    }
  }
}
