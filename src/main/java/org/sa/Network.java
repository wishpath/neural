package org.sa;

import org.sa.other.DataPrinter;
import org.sa.other.MinScoreAddress;
import org.sa.other.PictureFlatter;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class Network {

  public static final String NETWORK_STATE_FILEPATH = "src\\main\\java\\org\\sa\\data\\network-state";
  public static final double STEP_COEFFICIENT = 1.1;
  private List<Node> flatPixelNodes;
  private List<Node> intermediateNodes1;
  private List<Node> intermediateNodes2;
  private List<Node> answerNodes;
  private List<List<Node>> layers;

  public Network() {
    initialiseNetwork();
  }

  private void initialiseNetwork() {
    flatPixelNodes = PictureFlatter.flattenPicture(0);
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
    flatPixelNodes = PictureFlatter.flattenPicture(index);
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
      double desiredNodeValue = (i == Data.LABELS.get(index)) ? 1.0 : 0.0;
      score += getNodeScore(desiredNodeValue, answerNode.getValue());
    }
    return score;
  }

  public Double printSinglePictureScore(int index) {
    calculateAnswerNodes(index);
    DataPrinter.printLabeledPicture(index);
    System.out.println("answer should be: " + Data.LABELS.get(index));
    int i = 0;
    Double score = 0.0;
    for (Node answerNode : answerNodes) {
      double desiredNodeValue = (i == Data.LABELS.get(index)) ? 1.0 : 0.0;
      System.out.println(i++ + ": " + answerNode.getValue() + ": " + desiredNodeValue);
      score += getNodeScore(desiredNodeValue, answerNode.getValue());
    }
    System.out.println("score:" + score + "\n\n\n");
    return score;
  }

  private double getNodeScore(double desiredNodeValue, double nodeValue) {
    return Math.pow(desiredNodeValue - nodeValue, 2);
  }

  private double getAllPicsScore(int index) {
    Double allPicsScore = 0.0;
    for (int i = index - Main.BATCH_SIZE; i < index; i++) allPicsScore += getNetworkScore(i);
    return allPicsScore;
  }

  public void iterateLayersManageParameters(int pictureIndex) {

    double initialScore = getAllPicsScore(pictureIndex);
    MinScoreAddress minScoreAddress = null;
    double tempScore = Double.MAX_VALUE;

    for (int layerIndex = 0; layerIndex < layers.size(); layerIndex++) {
      for (int nodeIndex = 0; nodeIndex < layers.get(layerIndex).size(); nodeIndex++) {
        Node node = layers.get(layerIndex).get(nodeIndex);
        if (nodeIndex == 5 || nodeIndex == 7) continue;
        minScoreAddress = getMinScoreAddressChangingBiases(pictureIndex, node, minScoreAddress, layerIndex, nodeIndex);
        for (int weightIndex = 0; weightIndex < node.getWeightsSize(); weightIndex++) {
          minScoreAddress = getMinScoreAddressChangingWeights(pictureIndex, node, weightIndex, minScoreAddress, layerIndex, nodeIndex);
        }
      }
    }
    applyMinScoreChanges(minScoreAddress);
    System.out.println("initialScore: " + initialScore);
    System.out.println("change address: " + minScoreAddress);
    System.out.println("post application score: " + getAllPicsScore(pictureIndex) + "\n\n");
  }

  private MinScoreAddress getMinScoreAddressChangingWeights(int i, Node node, int weightIndex,
                                                            MinScoreAddress minScoreAddress,
                                                            int layerIndex, int nodeIndex) {
    double tempScore;
    node.increaseWeight(weightIndex);
    tempScore = getAllPicsScore(i);
    if (tempScore < minScoreAddress.getMinScoreValue())
      minScoreAddress = new MinScoreAddress(layerIndex, nodeIndex, Integer.valueOf(weightIndex), Boolean.TRUE, tempScore);
    node.doubleDecreaseWeight(weightIndex);
    tempScore = getAllPicsScore(i);
    if (tempScore < minScoreAddress.getMinScoreValue())
      minScoreAddress = new MinScoreAddress(layerIndex, nodeIndex, Integer.valueOf(weightIndex), Boolean.FALSE, tempScore);
    node.increaseWeight(weightIndex);
    return minScoreAddress;
  }

  private MinScoreAddress getMinScoreAddressChangingBiases(int pictureIndex, Node node, MinScoreAddress minScoreAddress,
                                                           int layerIndex, int nodeIndex) {
    double tempScore;
    node.increaseBias();
    tempScore = getAllPicsScore(pictureIndex);
    if (minScoreAddress == null || tempScore < minScoreAddress.getMinScoreValue())
      minScoreAddress = new MinScoreAddress(layerIndex, nodeIndex, Boolean.TRUE, tempScore);
    node.doubleDecreaseBias();
    tempScore = getAllPicsScore(pictureIndex);
    if (tempScore < minScoreAddress.getMinScoreValue())
      minScoreAddress = new MinScoreAddress(layerIndex, nodeIndex, Boolean.FALSE, tempScore);
    node.increaseBias();
    return minScoreAddress;
  }

  private void applyMinScoreChanges(MinScoreAddress minScoreAddress) {
    Node node = layers
        .get(minScoreAddress.getLayerIndex())
        .get(minScoreAddress.getNodeIndex());
    applyBias(node, minScoreAddress);
    applyWeight(node, minScoreAddress);
  }

  private void applyBias(Node node, MinScoreAddress minScoreAddress) {
    if (minScoreAddress.getBiasIncreased() == null) return;
    if (minScoreAddress.getBiasIncreased().booleanValue() == true) node.increaseBias();
    else node.decreaseBias();
  }

  private void applyWeight(Node node, MinScoreAddress minScoreAddress) {
    if (minScoreAddress.getWeightIncreased() == null) return;
    Integer weightIndex = minScoreAddress.getWeightIndex();
    if (minScoreAddress.getWeightIncreased().booleanValue() == true) node.increaseWeight(weightIndex);
    else node.decreaseWeight(weightIndex);
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
