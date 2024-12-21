package org.sa.other;

import org.sa.Data;

import java.util.Random;
import java.util.stream.IntStream;

public class DataPrinter {

  public static final int GREY_VALUE = 128;
  public static final String PRINT_WHITE = "#";
  public static final String PRINT_BLACK = ".";

  public static void printRandomPictures() {
    System.out.println("Random Samples:");
    IntStream.range(0, 5).forEach(i -> {
      int index = new Random().nextInt(Data.PICTURES.size());
      printLabeledPicture(index);
    });
  }

  private static void printPicture(int index) {
    for (int[] row : Data.PICTURES.get(index)) {
      for (int pixelBwValue : row) {
        System.out.print(pixelBwValue > GREY_VALUE ? PRINT_WHITE : PRINT_BLACK);
      }
      System.out.println();
    }
  }


  public static void printLabeledPicture(int index) {
    System.out.println("\nLabel: " + Data.LABELS.get(index));
    printPicture(index);
  }
}
