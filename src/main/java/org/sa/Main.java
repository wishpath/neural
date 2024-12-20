package org.sa;

import org.apache.commons.lang3.tuple.Pair;
import org.sa.structure.Network;

import java.io.IOException;
import java.util.List;

public class Main {


  public static final int ITERATION_CHECKPOINT_INTERVAL = 5;
  public static List<int[][]> PICTURES;
  public static List<Integer> LABELS;

  public static void main(String[] args) throws IOException {

    Pair<List<int[][]>, List<Integer>> data = new DataLoader().loadData();
    PICTURES = data.getLeft();
    LABELS = data.getRight();
    System.out.println("\nData initialised!\n");
    //DataPrinter.printDataSamples(data);

    //initialise network
    Network network = new Network(PICTURES, LABELS);
    network.loadNetworkState();

    printEachAndCalculateAverage(network);

    iterateImprovements(network);
  }

  private static void iterateImprovements(Network network) {
    network.loadNetworkState();
    int batchSize = 25;
    for (int i = batchSize; i < PICTURES.size(); i += batchSize) {
      network.iterateLayersManageVariables(i, batchSize);
      network.saveNetworkState();
      if (i % (ITERATION_CHECKPOINT_INTERVAL * batchSize) == 0) {
        if (AppStopChecker.shouldStopApp()) {
          System.out.println("app stopped gracefully");
          break;
        }
      }
    }
  }

  private static void printEachAndCalculateAverage(Network network) {
    double scoreForTen = 0.0;
    System.out.println("0");
    scoreForTen += network.printSinglePictureScore(3);//
    System.out.println("1");
    scoreForTen += network.printSinglePictureScore(2);//
    System.out.println("2");
    scoreForTen += network.printSinglePictureScore(1);//
    System.out.println("3");
    scoreForTen += network.printSinglePictureScore(30);
    System.out.println("4");
    scoreForTen += network.printSinglePictureScore(4);//
    System.out.println("5");
    scoreForTen += network.printSinglePictureScore(15);//
    System.out.println("6");
    scoreForTen += network.printSinglePictureScore(11);//
    System.out.println("7");
    scoreForTen += network.printSinglePictureScore(0);//
    System.out.println("8");
    scoreForTen += network.printSinglePictureScore(61);//
    System.out.println("9");
    scoreForTen += network.printSinglePictureScore(12);//
    System.out.println("Average: " + scoreForTen / 10);
  }
}