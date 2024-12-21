package org.sa.other;

public class MinScoreAddress {
  int layerIndex;
  int nodeIndex;
  Boolean biasIncreased = null;

  Integer weightIndex = null;
  Boolean weightIncreased = null;

  double minScoreValue;

  public MinScoreAddress (int layerIndex, int nodeIndex, Boolean biasIncreased, double minScoreValue) {
    this.layerIndex = layerIndex;
    this.nodeIndex = nodeIndex;
    this.biasIncreased = biasIncreased;
    this.minScoreValue = minScoreValue;
  }

  public MinScoreAddress (int layerIndex, int nodeIndex, Integer weightIndex, Boolean weightIncreased, double minScoreValue) {
    this.layerIndex = layerIndex;
    this.nodeIndex = nodeIndex;
    this.weightIndex = weightIndex;
    this.weightIncreased = weightIncreased;
    this.minScoreValue = minScoreValue;
  }

  public int getLayerIndex() {
    return layerIndex;
  }

  public int getNodeIndex() {
    return nodeIndex;
  }

  public Boolean getBiasIncreased() {
    return biasIncreased;
  }

  public Integer getWeightIndex() {
    return weightIndex;
  }

  public Boolean getWeightIncreased() {
    return weightIncreased;
  }

  @Override
  public String toString() {
    return "MinScoreAddress{" +
        "layerIndex=" + layerIndex +
        ", nodeIndex=" + nodeIndex +
        ", biasIncreased=" + biasIncreased +
        ", weightIndex=" + weightIndex +
        ", weightIncreased=" + weightIncreased +
        '}';
  }

  public double getMinScoreValue() {
    return minScoreValue;
  }
}
