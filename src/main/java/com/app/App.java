package com.app;

import com.gui.Gui;
import org.opencv.core.Core;
import javax.swing.*;
import java.util.Map;

public class App {
  public static void main(String[] args) {
    System.loadLibrary(Core.NATIVE_LIBRARY_NAME);

    try {
      UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
    } catch (ClassNotFoundException | InstantiationException
            | UnsupportedLookAndFeelException | IllegalAccessException e) {
      e.printStackTrace();
    }

    SwingUtilities.invokeLater(new Runnable() {
      @Override
      public void run() {
        new Gui();
      }
    });
  }

//  public static void pause(){
//    this.wait();
//  }

//  public static String getMainClassName() {
//    for (final Map.Entry<String, String> entry : System.getenv().entrySet())
//      if (entry.getKey().startsWith("App")) // like JAVA_MAIN_CLASS_13328
//        return entry.getValue();
//    throw new IllegalStateException("Cannot determine main class.");
//  }
}
