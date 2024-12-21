package org.sa;

import org.sa.other.AppStopChecker;

import java.io.IOException;

public class Main {

  private static final int ITERATION_CHECKPOINT_INTERVAL = 10;
  public static int BATCH_SIZE = 10;


  public static void main(String[] args) throws IOException {
    Network network = new Network();
    network.loadNetworkState();
    printExamplesOfEachDigitAndCalculateScore(network);
    iterateNetworkImprovements(network);
  }

  private static void iterateNetworkImprovements(Network network) {
    network.loadNetworkState();
    for (int i = BATCH_SIZE; i < Data.PICTURES.size(); i += BATCH_SIZE) {
      network.iterateLayersManageParameters(i);
      if (doCheckpointActions(network, i)) break;
    }
  }

  private static boolean doCheckpointActions(Network network, int i) {
    if (i % (ITERATION_CHECKPOINT_INTERVAL * BATCH_SIZE) == 0) {
      network.saveNetworkState();
      if (AppStopChecker.shouldStopApp()) {
        System.out.println("app stopped gracefully");
        return true;
      }
    }
    return false;
  }

  private static void printExamplesOfEachDigitAndCalculateScore(Network network) {
    double scoreForTen = 0.0;
    for (int digit = 0; digit < 10; digit++) {
      int indexOfDigit = 0;
      for (; indexOfDigit < Data.LABELS.size(); indexOfDigit++) {
        if (Data.LABELS.get(indexOfDigit) == digit) break;
      }
      scoreForTen += network.printSinglePictureScore(indexOfDigit);
    }
    System.out.println("Average: " + scoreForTen / 10);
  }
}