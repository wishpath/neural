package org.sa;

import org.sa.structure.Node;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class PictureFlatter {
  public static final double PIXEL_MAX_VALUE_AKA_WHITE = 255.0;

  public static List<Node> flattenPicture(int[][] picture) {
    return Arrays.stream(picture)
        .flatMapToInt(Arrays::stream)
        .mapToDouble(pixel -> pixel / PIXEL_MAX_VALUE_AKA_WHITE)
        .boxed()
        .map(value -> new Node(value))
        .collect(Collectors.toList());
  }
}
