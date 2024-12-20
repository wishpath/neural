package org.sa.structure;

import org.sa.DataPrinter;
import org.sa.PictureFlatter;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class Network {

  public static final String NETWORK_STATE_FILEPATH = "src\\main\\java\\org\\sa\\data\\network-state";
  private List<int[][]> pictures;
  private List<Integer> labels;
  private List<Node> flatPixelNodes;
  private List<Node> intermediateNodes1;
  private List<Node> intermediateNodes2;
  private List<Node> answerNodes;
  List<List<Node>> layers;

  public Network(List<int[][]> pictures, List<Integer> labels) {
    this.pictures = pictures;
    this.labels = labels;
    initialiseNetwork();
  }

  private void initialiseNetwork() {
    flatPixelNodes = PictureFlatter.flattenPicture(pictures.get(0));
    //DataPrinter.printPicture(pictures, labels, 0);
    intermediateNodes1 = initialiseNodeLayer(flatPixelNodes, 16);
    intermediateNodes2 = initialiseNodeLayer(intermediateNodes1, 16);
    answerNodes = initialiseNodeLayer(intermediateNodes2, 10);
    layers = List.of(intermediateNodes1, intermediateNodes2, answerNodes);
  }

  private List<Node> initialiseNodeLayer(List<Node> previousNodes, int size) {
    List<Node> nodes = new ArrayList<>();
    for (int i = 0; i < size; i++) nodes.add(new Node(previousNodes, size));
    return nodes;
  }

  public List<Node> calculateAnswerNodes(int index) {
    flatPixelNodes = PictureFlatter.flattenPicture(pictures.get(index));
    //DataPrinter.printPicture(pictures, labels, index);
    calculateNodeLayer(intermediateNodes1);
    calculateNodeLayer(intermediateNodes2);
    calculateNodeLayer(answerNodes);
    return answerNodes;
  }

  private void calculateNodeLayer(List<Node> thisLayer) {
    for (Node node : thisLayer) node.calculateValue();
  }

  public Double getNetworkScore(int index) {
    calculateAnswerNodes(index);
    int i = 0;
    Double score = 0.0;
    for (Node answerNode : answerNodes) {
      double desiredNodeValue = (i == labels.get(index)) ? 1.0 : 0.0;
      score += getNodeScore(desiredNodeValue, answerNode.getValue());
    }
    return score;
  }

  public Double printSinglePictureScore(int index) {
    calculateAnswerNodes(index);
    DataPrinter.printPicture(index);
    System.out.println("answer should be: " + labels.get(index));
    int i = 0;
    Double score = 0.0;
    for (Node answerNode : answerNodes) {
      double desiredNodeValue = (i == labels.get(index)) ? 1.0 : 0.0;
      System.out.println(i++ + ": " + answerNode.getValue() + ": " + desiredNodeValue);
      score += getNodeScore(desiredNodeValue, answerNode.getValue());
    }
    System.out.println("score:" + score + "\n\n\n");
    return score;
  }

  private double getNodeScore(double desiredNodeValue, double nodeValue) {
    return Math.pow(desiredNodeValue - nodeValue, 2);
  }

  public double getAllPicsScore(int index, int batchSize) {
    //all pictures score:
    Double allPicsScore = 0.0;
    for (int i = index - batchSize; i < index; i++) allPicsScore += getNetworkScore(i);
    //System.out.println("All pictures score: " + allPicsScore);
    return allPicsScore;
  }

  public void iterateLayersManageVariables(int i, int batchSize) {
    final double STEP_COEFFICIENT = 1.1;
    double initialScore = getAllPicsScore(i, batchSize);
    //System.out.println("initialScore: " + initialScore);

    // [layer index]
    // [node index]
    // [bias: stay - 0; increase - 1; decrease - 2]
    // [weight index]
    // [weight: stay - 0; increase - 1; decrease - 2]

    double minScore = getAllPicsScore(i, batchSize);
    MinScoreAddress minScoreAddress = null;
    double tempScore = Double.MAX_VALUE;

    for (int layerIndex = 0; layerIndex < layers.size() - 2 ; layerIndex++) { //last 2 layers no!
      //System.out.println("Layer: " + layerIndex);
      for (int nodeIndex = 0; nodeIndex < layers.get(layerIndex).size(); nodeIndex++) {
        //System.out.println("Node: " + nodeIndex);
        Node node = layers.get(layerIndex).get(nodeIndex);

        node.increaseBias(STEP_COEFFICIENT);
        tempScore = getAllPicsScore(i, batchSize);
        if (tempScore < minScore) {
          minScore = tempScore;
          minScoreAddress = new MinScoreAddress(layerIndex, nodeIndex, Boolean.TRUE);
        }

        node.doubleDecreaseBias(STEP_COEFFICIENT);
        tempScore = getAllPicsScore(i, batchSize);
        if (tempScore < minScore) {
          minScore = tempScore;
          minScoreAddress = new MinScoreAddress(layerIndex, nodeIndex, Boolean.FALSE);
        }

        node.increaseBias(STEP_COEFFICIENT);

        for (int weightIndex = 0; weightIndex < node.getWeightsSize(); weightIndex++) {
          //System.out.println("weight: " + weightIndex);

          node.increaseWeight(weightIndex, STEP_COEFFICIENT);
          tempScore = getAllPicsScore(i, batchSize);
          if (tempScore < minScore) {
            minScore = tempScore;
            minScoreAddress = new MinScoreAddress(layerIndex, nodeIndex, Integer.valueOf(weightIndex), Boolean.TRUE);
          }

          node.doubleDecreaseWeight(weightIndex, STEP_COEFFICIENT);
          tempScore = getAllPicsScore(i, batchSize);
          if (tempScore < minScore) {
            minScore = tempScore;
            minScoreAddress = new MinScoreAddress(layerIndex, nodeIndex, Integer.valueOf(weightIndex), Boolean.FALSE);
          }

          node.increaseWeight(weightIndex, STEP_COEFFICIENT);
        }
      }
    }

    //double afterwardsScore = getAllPicsScore(i, batchSize);
    System.out.println("initialScore: " + initialScore);
//    System.out.println("afterwardsScore:" + afterwardsScore);
//    System.out.println("min score:" + minScore);
    System.out.println("change address: " + minScoreAddress);

    applyMinScoreChanges(minScoreAddress, STEP_COEFFICIENT);
    System.out.println("post application score: " + getAllPicsScore(i, batchSize) + "\n\n");
  }

  private void applyMinScoreChanges(MinScoreAddress minScoreAddress, double stepCoefficient) {
    Node node = layers
        .get(minScoreAddress.getLayerIndex())
        .get(minScoreAddress.getNodeIndex());
    applyBias(node, minScoreAddress, stepCoefficient);
    applyWeight(node, minScoreAddress, stepCoefficient);
  }

  private void applyBias(Node node, MinScoreAddress minScoreAddress, double stepCoefficient) {
    if (minScoreAddress.biasIncreased == null) return;
    if (minScoreAddress.biasIncreased.booleanValue() == true) node.increaseBias(stepCoefficient);
    else node.decreaseBias(stepCoefficient);
  }

  private void applyWeight(Node node, MinScoreAddress minScoreAddress, double stepCoefficient) {
    if (minScoreAddress.weightIncreased == null) return;
    Integer weightIndex = minScoreAddress.getWeightIndex();
    if (minScoreAddress.weightIncreased.booleanValue() == true) node.increaseWeight(weightIndex, stepCoefficient);
    else node.decreaseWeight(weightIndex, stepCoefficient);
  }

  public void saveNetworkState() {
    try (BufferedWriter writer = new BufferedWriter(new FileWriter(NETWORK_STATE_FILEPATH))) {
      for (int layerIndex = 0; layerIndex < layers.size(); layerIndex++) {
        for (int nodeIndex = 0; nodeIndex < layers.get(layerIndex).size(); nodeIndex++) {
          Node node = layers.get(layerIndex).get(nodeIndex);
          writer.write(node.getBiasValue() + "\n");
          for (int weightIndex = 0; weightIndex < node.getWeightsSize(); weightIndex++) {
            writer.write(node.getValueByIndex(weightIndex) + "\n");
          }
        }
      }
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  public void loadNetworkState() {
    try (BufferedReader reader = new BufferedReader(new FileReader(NETWORK_STATE_FILEPATH))) {
      for (int layerIndex = 0; layerIndex < layers.size(); layerIndex++) {
        for (int nodeIndex = 0; nodeIndex < layers.get(layerIndex).size(); nodeIndex++) {
          Node node = layers.get(layerIndex).get(nodeIndex);
          node.setBias(Double.parseDouble(reader.readLine()));
          for (int weightIndex = 0; weightIndex < node.getWeightsSize(); weightIndex++) {
            node.setWeight(weightIndex, Double.parseDouble(reader.readLine()));
          }
        }
      }
    } catch (IOException e) {
      throw new RuntimeException(e);
    } catch (NumberFormatException e) {
      throw new RuntimeException("Invalid data format in the network state file", e);
    }
  }
}
