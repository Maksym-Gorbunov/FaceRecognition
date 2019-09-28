package temp;

import com.constants.Constants;
import com.gargoylesoftware.htmlunit.BrowserVersion;
import com.gargoylesoftware.htmlunit.WebClient;
import org.apache.commons.io.FilenameUtils;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.imgcodecs.Imgcodecs;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.htmlunit.HtmlUnitDriver;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.io.InputStream;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class HtmlUnitDriverTest {

  public static String downloadsPath = Constants.imgPath + "colorblind\\downloads\\";
  public static String resultsPath = Constants.imgPath + "colorblind\\results\\";


  public static void main(String[] args) throws Exception {
    HtmlUnitDriver driver = new HtmlUnitDriver(BrowserVersion.CHROME);
    driver.get("https://www.seleniumhq.org/");
    System.out.println("Title: " + driver.getTitle());
    //WebClient
    WebClient webClient = (WebClient) get(driver, "webClient");
    System.out.println("Browser is Chrome : " + webClient.getBrowserVersion().isChrome());
    System.out.println("Browser version : " + webClient.getBrowserVersion());
    temp1(driver);
    //temp2(driver);
    driver.quit();
  }


  private static void temp1(HtmlUnitDriver driver) {
    System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
    ColorBlind colorBlind = new ColorBlind();
    System.out.println(driver.getTitle());
    //System.out.println(driver.getPageSource());
    List<WebElement> imgList = driver.findElements(By.tagName("img"));
    System.out.println(imgList.size());
    //System.out.println(imgList.get(1).getClass().getSimpleName());

    //download images from url
    if (imgList.size() > 0) {
      for (int i = 0; i < imgList.size(); i++) {
        String src = imgList.get(i).getAttribute("src");
        if ((!src.equals("")) && (src != null)) {
          System.out.println(src);
          downloadImages(src, colorBlind);
        }
      }
    }


    //read files and recognize conflict
    try (Stream<Path> walk = Files.walk(Paths.get(downloadsPath))) {
      List<String> result = walk.filter(Files::isRegularFile)
              .map(x -> x.toString()).collect(Collectors.toList());
      for(String imgPath : result){
        colorBlind.findColorConflict(imgPath, resultsPath);
      }
    } catch (IOException e) {
      e.printStackTrace();
    }


  }

  private static void downloadImages(String src, ColorBlind colorBlind) {
    File f = new File(src);
    String fileName = f.getName();
    String ext = FilenameUtils.getExtension(fileName);
    try {
      if(ext.equals("png") || (ext.equals("jpg") || (ext.equals("jpeg")))){

        InputStream inputStream = new URL(src).openStream();
        Files.copy(inputStream, Paths.get(downloadsPath + fileName), StandardCopyOption.REPLACE_EXISTING);
      }
    } catch (IOException e) {
      e.printStackTrace();
    }
  }


  //Get browser version
  private static Object get(Object object, String field) throws Exception {
    Field f = object.getClass().getDeclaredField(field);
    f.setAccessible(true);
    return f.get(object);
  }

  public class A {
    public String name = "max";
  }
}
