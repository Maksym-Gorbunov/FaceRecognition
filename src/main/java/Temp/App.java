package Temp;

class MyTask extends Thread{
  @Override
  public void run() {
    for (int doc = 1; doc <= 100; doc++) {
      System.out.println("@@ Printing Document #" + doc + " - Printer2");
    }
  }
}

class MyThread implements Runnable{
  @Override
  public void run() {
    for (int doc = 1; doc <= 100; doc++) {
      System.out.println("@@ Printing Document #" + doc + " - Printer3");
    }
  }
}


public class App {

  public static void main(String[] args) {

    //Task1
    System.out.println("==Application Started==");

//    //Task2
//    MyTask task = new MyTask();
////    task.setDaemon(false);
//    task.start();

    // Task2
    Runnable r = new MyThread();
    Thread task = new Thread(r);
//    task.setDaemon(false);
    task.start();

    //Task3
    for (int doc = 1; doc <= 100; doc++) {
      System.out.println("Printing Document #" + doc + " - Printer1");
    }

    //Task4
    System.out.println("==Application Finished==");
  }
}
