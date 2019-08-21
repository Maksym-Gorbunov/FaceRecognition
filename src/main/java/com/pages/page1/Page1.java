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
  private JPanel btnPanel = new JPanel();
  private JButton startBtn = new JButton("Start");
  private JButton pauseBtn = new JButton("Pause");
  private JButton momentBtn = new JButton("Moment");
  private JButton testBtn = new JButton("Test");
  private JPanel webcamPanel = new JPanel();
  private Graphics graphics;
  private boolean status = false;
  private Color defaultPanelColor;
  private Moment moment;
  private boolean showMoment = false;

  public Page1(final Gui gui) {
    this.gui = gui;
    tab1 = gui.getTab1();
    initComponents();
    addListeners();
  }

  private void initComponents() {

    ///////////////////////////////////////////////////////////////
    mainPanel.setBackground(Color.blue);
    btnPanel.setBackground(Color.green);
    ///////////////////////////////////////////////////////////////

    webcamPanel.setPreferredSize(new Dimension(Constants.VIDEO_WIDTH, Constants.VIDEO_HEIGHT));
    mainPanel.add(webcamPanel);

    btnPanel.add(startBtn);
    btnPanel.add(pauseBtn);
    btnPanel.add(testBtn);
    btnPanel.add(momentBtn);
    momentBtn.setEnabled(false);

    tab1.add(mainPanel);
    tab1.add(btnPanel);
    mainPanel.setPreferredSize(new Dimension(800, 500));
    btnPanel.setPreferredSize(new Dimension(800, 100));
    defaultPanelColor = webcamPanel.getBackground();
    pauseBtn.setEnabled(false);
  }

  private void addListeners() {

    startBtn.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        System.out.println("Start...");
        webSource = new VideoCapture(0);
        myThread = new DaemonThread();
        Thread t = new Thread(myThread);
        t.setDaemon(true);
        myThread.runnable = true;
        t.start();
        startBtn.setEnabled(false);
        pauseBtn.setEnabled(true);
        testBtn.setEnabled(false);
        momentBtn.setEnabled(false);
        gui.getTabs().setEnabled(false);
      }
    });

    pauseBtn.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        System.out.println("Pause...");
        myThread.runnable = false;
        pauseBtn.setEnabled(false);
        startBtn.setEnabled(true);
        momentBtn.setEnabled(true);
        webSource.release();
        testBtn.setEnabled(true);
        gui.getTabs().setEnabled(true);
      }
    });

    momentBtn.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        showMoment = !showMoment;
        if (showMoment) {
          startBtn.setEnabled(false);
          moment = new Moment(webcamPanel);
          Thread t = new Thread(moment);
          t.setDaemon(true);
          moment.runnable = true;
          t.start();
        } else {
          moment.runnable = false;
          startBtn.setEnabled(true);
        }
      }
    });

    testBtn.addActionListener(new ActionListener() {
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
