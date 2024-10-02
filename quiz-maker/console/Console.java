package console;
import java.io.InputStream;
import java.io.PrintStream;
import java.util.Scanner;

public class Console {

  public static final String CLEAR_SCREEN = "\033[H\033[2J";
  public static final String MOVE_CURSOR_UP = "\033[A";
  public static final String RESET = "\033[0m";
  public static final String GREEN = "\033[32m";
  public static final String RED = "\033[31m";

  InputStream in;
  PrintStream out;
  Scanner scanner;
  
  public Console(InputStream in, PrintStream out) {
    this.in = in;
    this.out = out;
    scanner = new Scanner(in);
  }

  public void println(String message) {
    out.println(message);
  }

  public void println(String message, String color) {
    out.println(color + message + RESET);
  }

  public void println() {
    out.println();
  }

  public String getInput(String prompt) {
    out.println(prompt);
    String input = scanner.nextLine().trim();
    if (input.trim().equals("q")) {
      throw new UserInterruptException();
    }
    return input;
  }

  public void clear() {
    out.print(CLEAR_SCREEN);
    out.flush();
  }

}
