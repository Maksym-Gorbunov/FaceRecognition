package temp;

import java.util.ArrayList;
import java.util.List;

public class dd {
  public static void main(String[] args) {
    List<String> list = new ArrayList<>();
    list.add("1");
    list.add("2");
    list.add("3");
    list.add("4");
    list.add("5");
    list.add("6");
    for (int i = 0; i < list.size(); i++) {
      for (int j = i + 1; j < list.size(); j++) {
        System.out.println(String.valueOf(i) + j);
      }
    }


  }


}
