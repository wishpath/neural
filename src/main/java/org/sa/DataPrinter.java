package org.sa;

import org.apache.commons.lang3.tuple.Pair;

import java.util.List;
import java.util.Random;
import java.util.stream.IntStream;

public class DataPrinter {

  public static final int GREY_VALUE = 128;
  public static final String PRINT_WHITE = "#";
  public static final String PRINT_BLACK = ".";

  public static void printDataSamples(Pair<List<int[][]>, List<Integer>> data) {
    List<int[][]> pictures = data.getLeft();
    List<Integer> labels = data.getRight();
    System.out.println("Random Samples:");
    IntStream.range(0, 5).forEach(i -> {
      int index = new Random().nextInt(pictures.size());
      System.out.println("\nLabel: " + labels.get(index));
      printImage(pictures.get(index));
    });
  }

  private static void printImage(int[][] image) {
    for (int[] row : image) {
      for (int pixelBwValue : row) {
        System.out.print(pixelBwValue > GREY_VALUE ? PRINT_WHITE : PRINT_BLACK);
      }
      System.out.println();
    }
  }

  public static void printPicture(List<int[][]> pictures, List<Integer> labels, int i) {
    System.out.println("\nLabel: " + labels.get(i));
    printImage(pictures.get(i));
  }

  public static void printPicture(int index) {
    printPicture(Main.PICTURES, Main.LABELS, index);
  }
}
