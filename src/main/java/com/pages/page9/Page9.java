package com.pages.page9;

import com.constants.Constants;
import com.gui.Gui;
import com.pages.Pages;
import com.pages.page7.ImgObject;
import com.pages.page7.Recognizer;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;


// Simble webbcamera
public class Page9 extends JPanel implements Pages {
  private static final long serialVersionUID = 1L;
  private Gui gui;
  private JPanel tab9;
  private JPanel mainPanel = new JPanel();
  private JPanel buttonsPanel = new JPanel();
  private JButton openBtn = new JButton("Open File");
  private JButton playBtn = new JButton("Play");
  private JButton stopBtn = new JButton("Stop");
  private JButton screenshotBtn = new JButton("Screenshot");
  private JButton recognizeBtn = new JButton("Recognize");
  private VideoPanel videoPanel;
  private Graphics graphics;
  private boolean status = false;
  private Color defaultPanelColor;
  private File file;
  private Recognizer recognizer = new Recognizer();
  private String outPath = Constants.videoPath + "screenshots\\";


  public Page9(final Gui gui) {
    this.gui = gui;
    tab9 = gui.getTab9();
    videoPanel = new VideoPanel(Constants.VIDEO_WIDTH, Constants.VIDEO_HEIGHT);
    initComponents();
    addListeners();
  }


  private void addListeners() {

    openBtn.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        JFileChooser fc = new JFileChooser(Constants.videoPath);
        fc.setMultiSelectionEnabled(false);
        if (fc.showOpenDialog(gui) == JFileChooser.APPROVE_OPTION) {
          File f = fc.getSelectedFile();
          if (f != null) {
            file = f;
            playBtn.setEnabled(true);
          }
        }
      }
    });

    playBtn.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        // path manipulations
        clearFolder(outPath);
        String fileNameWithOutExt = FilenameUtils.removeExtension(file.getName());
        String fileOutPath = outPath + fileNameWithOutExt + "\\";
        new File(fileOutPath).mkdirs();

        // auto recognize on play
        videoPanel.play(file, fileOutPath);
        playBtn.setEnabled(false);
        stopBtn.setEnabled(true);
        screenshotBtn.setEnabled(true);
        recognizeBtn.setEnabled(true);
      }
    });

    stopBtn.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        videoPanel.stop();
        playBtn.setEnabled(true);
        stopBtn.setEnabled(false);
        screenshotBtn.setEnabled(false);
        recognizeBtn.setEnabled(false);
      }
    });

    screenshotBtn.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        videoPanel.getScreenshot();
      }
    });

    recognizeBtn.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
//        ImgObject object = new ImgObject(new File(Constants.videoPath+"screenshots\\90.jpg"));
//        File f = new File(Constants.videoPath + "screenshots\\90.jpg");
//        recognizer.recognize(f, 120, 5, 100, 0);


      }
    });
  }


  private void initComponents() {
    mainPanel.add(videoPanel);

    buttonsPanel.add(openBtn);
    buttonsPanel.add(playBtn);
    buttonsPanel.add(stopBtn);
    buttonsPanel.add(screenshotBtn);
    buttonsPanel.add(recognizeBtn);

    tab9.add(mainPanel);
    tab9.add(buttonsPanel);
    mainPanel.setPreferredSize(new Dimension(800, 500));
    buttonsPanel.setPreferredSize(new Dimension(800, 100));
    defaultPanelColor = videoPanel.getBackground();

    playBtn.setEnabled(false);
    stopBtn.setEnabled(false);
    screenshotBtn.setEnabled(false);
    recognizeBtn.setEnabled(false);
  }

  // Clear folder from old files
  public void clearFolder(String path) {
    try {
      FileUtils.deleteDirectory(new File(path));
    } catch (IOException e) {
      e.printStackTrace();
    }
    new File(path).mkdirs();
  }
}
