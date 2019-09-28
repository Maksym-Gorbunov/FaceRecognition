package temp;

public class dd {
  public static void main(String[] args) {
    long startTime = System.nanoTime();
    //stringTest();     //910ms
    //charTest();       //375
    intTest();
    long endTime = System.nanoTime();
    long duration = (endTime - startTime);
    System.out.println(duration/1000000);


  }


  public static void intTest(){
    for(int i=0;i<10000;i++){
      int s = 3;
      System.out.println(s+i);
    }
  }

  public static void stringTest(){
    for(int i=0;i<10000;i++){
      String s = new String("Red");
      System.out.println(s+i);
    }
  }

  public static void charTest(){
    for(int i=0;i<10000;i++){
      char s = 'r';
      System.out.println(s+i);
    }
  }
}
