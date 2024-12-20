package org.sa.structure;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Node {
  public static final double COEFFICIENT_FOR_INITIALISATION = 0.05;
  private List<Node> previousNodesLayer;
  private List<Double> weights;
  private double bias;

  private double value;

  public Node(List<Node> previousNodesLayer, int thisLayerSize) {
    this.previousNodesLayer = previousNodesLayer;
    Double averageWeights = COEFFICIENT_FOR_INITIALISATION * thisLayerSize / previousNodesLayer.size();
    this.weights = initialiseWeights(averageWeights);
    this.bias = getBias();
    calculateValue();
  }

  public void printBias() {
    System.out.println("bias:" + bias);
  }

  public void calculateValue() {
    value = getSigmoid(getSum() + bias);
  }

  public Node(double value) {
    this.value = value;
  }

  private List<Double> initialiseWeights(Double averageWeights) {
    Random random = new Random();
    List<Double> weights = new ArrayList<>();
    for (int i = 0; i < previousNodesLayer.size(); i++) {
      // weight will have a variation of ±50% of averageWeights.
      // For example, if averageWeights = 0.3, weights will range approximately from 0.15 to 0.45.
      double randomWeight = averageWeights + (random.nextDouble() - 0.5) * 1.0 * averageWeights;
      if (randomWeight < 0) throw new RuntimeException("weight should be >= 0");
      weights.add(randomWeight);
    }
    return weights;
  }

  private double getBias() {
    Random random = new Random();
    // bias will have a variation of ±0.2 from the initial value of -0.2.
    // For example, if bias = -0.2, it will range approximately from -0.4 to 0.
    return -0.2 + (random.nextDouble() - 0.5) * 0.4;
  }

  private Double getSum() {
    Double sum = 0.0;
    for (int i = 0; i < previousNodesLayer.size(); i++) {
      sum += previousNodesLayer.get(i).getValue() * weights.get(i);
    }
    return sum;
  }

  public static double getSigmoid(Double weightedSum) {
    return 1 / (1 + Math.exp(-weightedSum));
  }

  public double getValue() {
    return value;
  }

  public void increaseBias(double stepCoefficient) {
    bias *= stepCoefficient;
  }

  public void decreaseBias(double stepCoefficient) {
    bias /= stepCoefficient;
  }

  public void doubleDecreaseBias(double stepCoefficient) {
    bias = (bias / stepCoefficient) / stepCoefficient;
  }

  public int getWeightsSize() {
    return weights.size();
  }

  public void increaseWeight(int i, double stepCoefficient) {
    double increasedWeight = weights.get(i) * stepCoefficient;
    weights.set(i, increasedWeight);
  }

  public void decreaseWeight(Integer weightIndex, double stepCoefficient) {
    double decreasedWeight = weights.get(weightIndex) / stepCoefficient;
    weights.set(weightIndex, decreasedWeight);
  }

  public void doubleDecreaseWeight(int i, double stepCoefficient) {
    double decreasedWeight = (weights.get(i) / stepCoefficient) / stepCoefficient;
    weights.set(i, decreasedWeight);
  }

  public double getValueByIndex(int weightIndex) {
    return weights.get(weightIndex);
  }

  public double getBiasValue() {
    return bias;
  }

  public void setBias(double newBias) {
    bias = newBias;
  }

  public void setWeight(int weightIndex, double newWeight) {
    weights.set(weightIndex, newWeight);
  }
}
