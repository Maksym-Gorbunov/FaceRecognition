package com.pages.Page7;

import java.io.File;
import java.net.URI;

public class ImgFile extends File {


  private String licenseNumber = "- - -";

  public ImgFile(String pathname) {
    super(pathname);
  }

  @Override
  public String toString() {
    return super.getName();
  }

  public String getLicenseNumber() {
    return licenseNumber;
  }

  public void setLicenseNumber(String licenseNumber) {
    this.licenseNumber = licenseNumber;
  }
}
