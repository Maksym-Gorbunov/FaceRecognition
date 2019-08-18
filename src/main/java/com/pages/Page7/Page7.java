package com.pages.Page7;

import com.gui.Gui;
import com.pages.Pages;

import javax.swing.*;

public class Page7 extends JPanel implements Pages {

  private JFrame gui;
  private JPanel tab7;


  public Page7(Gui gui){
    this.gui = gui;
    tab7 = gui.getTab7();
    initComponents();
    initButtons();
    addListeners();

  }





}
