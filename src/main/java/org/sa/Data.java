package org.sa;

import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Data {


  public static final int DATA_LOAD_LIMITATION_SIZE = 10000;
  public static final int PICTURE_VERIFIER = 2051;
  public static final int LABEL_VERIFIER = 2049;
  private static final String PICTURES_FILEPATH = "src\\main\\java\\org\\sa\\data\\t10k-images.idx3-ubyte";
  private static final String LABELS_FILEPATH = "src\\main\\java\\org\\sa\\data\\t10k-labels.idx1-ubyte";
  public static final List<int[][]> PICTURES = readPictures();
  public static final List<Integer> LABELS = readLabels();

  private static List<int[][]> readPictures() {
    DataInputStream imageStream = getStream(PICTURES_FILEPATH);
    if (getInt(imageStream) != PICTURE_VERIFIER) throw new IllegalArgumentException();
    int imagesCount = getInt(imageStream);
    if (imagesCount < DATA_LOAD_LIMITATION_SIZE) throw new RuntimeException("imagesCount < DATA_LOAD_LIMITATION_SIZE should be true");
    System.out.println("images size: " + imagesCount);
    int rows = getInt(imageStream);
    System.out.println("image rows: " + rows);
    int columns = getInt(imageStream);
    System.out.println("image columns: " + columns);
    System.out.println("columns * rows: " + columns * rows);
    System.out.println("data load limitation size: " + DATA_LOAD_LIMITATION_SIZE);

    List<int[][]> images = new ArrayList<>(DATA_LOAD_LIMITATION_SIZE);
    for (int i = 0; i < DATA_LOAD_LIMITATION_SIZE; i++) {
      int[][] image = new int[rows][columns];
      for (int r = 0; r < rows; r++) {
        for (int c = 0; c < columns; c++) {
          image[r][c] = getUnsignedByte(imageStream);
        }
      }
      images.add(image);
    }
    return images;
  }

  private static List<Integer> readLabels(){
    DataInputStream labelStream = getStream(LABELS_FILEPATH);
    if (getInt(labelStream) != LABEL_VERIFIER) throw new IllegalArgumentException();
    int labelsSize = getInt(labelStream);
    System.out.println("labels size: " + labelsSize);
    List<Integer> labels = new ArrayList<>(DATA_LOAD_LIMITATION_SIZE);
    for (int i = 0; i < DATA_LOAD_LIMITATION_SIZE; i++) labels.add(getUnsignedByte(labelStream));
    return labels;
  }

  private static DataInputStream getStream(String filepath){
    try {
      return new DataInputStream(new FileInputStream(filepath));
    } catch (FileNotFoundException e) {
      throw new RuntimeException(e);
    }
  }

  private static int getInt(DataInputStream imageStream) {
    try {
      return imageStream.readInt();
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  private static int getUnsignedByte(DataInputStream imageStream){
    try {
      return imageStream.readUnsignedByte();
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }
}

