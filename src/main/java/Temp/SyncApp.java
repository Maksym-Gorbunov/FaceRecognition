package Temp;

// myThread /////////////////////////////////////////////////////////
class MineThread extends Thread{
  Printer printerReference;
  public MineThread(Printer p){
    printerReference = p;
  }
  @Override
  public void run(){
    printerReference.print(1000, "Mine.pdf");
  }
}

// yourThread ///////////////////////////////////////////////////////
class YourThread extends Thread{
  Printer printerReference;
  public YourThread(Printer p){
    printerReference = p;
  }
  @Override
  public void run(){
//    try {
//      Thread.sleep(500);
//    } catch (InterruptedException e) {
//      e.printStackTrace();
//    }
    printerReference.print(1000, "__Your.pdf");
  }
}

class Printer {
  synchronized void print(int totalCopies, String docName){
    for(int i=1;i<=totalCopies;i++){
      System.out.println("Printing #"+i+docName);
    }
  }
}

public class SyncApp {
  public static void main(String[] args) {
    Printer p = new Printer();
    MineThread t1 = new MineThread(p);
    YourThread t2 = new YourThread(p);

    t1.start();
    //join make thread syncronized
    /*
    try {
      t1.join();
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
    */
    t2.start();
  }
}
