package org.sa.other;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class AppStopChecker {
  private static final String STOPPER_FILEPATH = "src\\main\\java\\org\\sa\\parameter\\stopper";

  public static boolean shouldStopApp() {
    try (BufferedReader reader = new BufferedReader(new FileReader(STOPPER_FILEPATH))) {
      String content = reader.readLine();
      boolean shouldStop = "stop".equalsIgnoreCase(content != null ? content.trim() : "");
      return shouldStop;
    } catch (IOException e) {
      return false;
    }
  }
}
