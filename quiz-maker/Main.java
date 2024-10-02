import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import console.Console;
import console.UserInterruptException;
import question.DuplicateQuestionException;
import question.Question;
import question.Questions;
import quiz.Quiz;
import quiz.QuizQuestion;

public class Main {

  static final String QUESTION_FOLDER = "save_files";

  Questions questions;
  Console console;

  public Main() {
    questions = new Questions();
    console = new Console(System.in, System.out);
  }

  public void addQuestions() {
    while (true) {
      try {
        console.clear();
        String question = console.getInput("Enter a question:");
        console.println();
        String answer = console.getInput("Enter the answer:");
        console.println();
        questions.put(question, answer);
        console.println("Question successfully added", Console.GREEN);
        sleep(2000);
      } catch (DuplicateQuestionException e) {
        console.println();
        console.println("Question already exists!", Console.RED);
        sleep(2000);
      } catch (UserInterruptException e) {
        console.println();
        console.println("Going back to menu...");
        sleep(2000);
        return;
      }
    }
  }

  public void loadQuestions() {
    try {
      while (true) {
        console.clear();
        File folder = new File(QUESTION_FOLDER);
        File[] files = folder.listFiles();
        for (int i = 0; i < files.length; ++i) {
          console.println((i + 1) + ": " + files[i].getName());
        }
        console.println();
        String selectedIndex = console.getInput("Select a file by entering a number:");
        try {
          int index = Integer.parseInt(selectedIndex);
          loadQuestions(files[index - 1].getPath());
          return;
        } catch (NumberFormatException e) {
          console.println();
          console.println("Invalid selection!", Console.RED);
          sleep(2000);
        }
      }
    } catch (UserInterruptException e) {
      console.println();
      console.println("Going back to menu...");
      return;
    }
  }

  private void loadQuestions(String filePath) {
    try {
      List<String> lines = Files.readAllLines(Paths.get(filePath));
      questions = new Questions();
      for (int i = 0; i < lines.size(); i += 2) {
        questions.put(lines.get(i), lines.get(i + 1));
      }
      console.println();
      console.println(filePath.substring(filePath.lastIndexOf("/") + 1) + " successfully loaded.", Console.GREEN);
      sleep(2000);
    } catch (IOException e) {
      console.println();
      console.println("Unable to read file: " + filePath, Console.RED);
      sleep(2000);
    }
  }

  public void takeQuiz() {
    try {
      console.clear();
      String input = console.getInput("How many questions? (default: 10):");
      int numQuestions = input.matches("-?\\d+") ? Integer.parseInt(input) :10;
      Quiz quiz = new Quiz(questions, numQuestions);
      for (QuizQuestion question :quiz) {
        console.println();
        console.println("Question " + question.questionNumber() + ":");
        console.println(question.question());
        console.println();
        String attempt = console.getInput("Your answer:");
        console.println();
        question.grade(attempt);
      }
      for (QuizQuestion question : quiz) {
        console.println(
          question.questionNumber() + " : " + 
          (question.isCorrect() ? 
            Console.GREEN + "Correct" + Console.RESET 
            : Console.RED + "Incorrect" + Console.RESET + " the correct answer was: " + question.answer())
        );
      }
      console.println("Score: " + quiz.score().toString());
      console.getInput("Enter 'q' to go back to menu:").equalsIgnoreCase("q");
    } catch (UserInterruptException e) {
      console.println();
      console.println("Going back to menu...");
      sleep(2000);
      return;
    }
  }

  public void browseQuestions() {
    console.clear();
    console.println("unable to browse questions at this time!", Console.RED);
    sleep(2000);
  }

  public void saveQuestions() {
    try {
      File folder = new File(QUESTION_FOLDER);
      File[] files = folder.listFiles();
      List<String> fileNames = Arrays.stream(files)
        .map(File::getName)
        .collect(Collectors.toList());
      String fileName = console.getInput("Enter a file name to save questions to:");
      if (fileNames.contains(fileName)) {
        String response = console.getInput("File, " + fileName + ", already exists. Overwrite? (yes/no)");
        if (!response.equalsIgnoreCase("yes")) {
          console.println("Save aborted");
        } else {
          saveQuestions(fileName);
        }
      } else {
        saveQuestions(fileName);
      }
    } catch (UserInterruptException e) {
      console.println();
      console.println("Going back to menu...");
      sleep(2000);
      return;
    }
  }

  public void saveQuestions(String fileName) {
    try {
      PrintStream fStream = new PrintStream(new File(QUESTION_FOLDER + "/" + fileName));
      for (Question question : questions) {
        fStream.println(question.question());
        fStream.println(question.answer());
      }
      fStream.close();
      console.println("Questions successfully saved to file, " + fileName, Console.GREEN);
      sleep(2000);
    } catch (IOException e) {
      console.println();
      console.println("Unable to save questions to file, " + fileName + "!", Console.RED);
    }
  }

  public void printMenu(String... menuItems) {
    console.println("Menu:");
    for (String menuItem : menuItems) {
      console.println("\t" + menuItem);
    }
    console.println();
  }

  public void sleep(long delay) {
    long start = System.currentTimeMillis();
    while (System.currentTimeMillis() - start < delay);
  }

  public void run() {
    try {
      while (true) {
        console.clear();
        printMenu(
          "add: add questions to the bank from the console",
          "load: load questions from a file",
          "quiz: take a quiz",
          "browse: browse loaded questions",
          "save: save questions to a file"
        );
        String input = console.getInput("Select an option:");
        switch (input.toLowerCase()) {
          case "add":
            addQuestions();
            break;
          case "load":
            loadQuestions();
            break;
          case "quiz":
            takeQuiz();
            break;
          case "browse":
            browseQuestions();
            break;
          case "save":
            saveQuestions();
            break;
          default:
            console.println("Invalid menu option!", Console.RED);
            sleep(2000);
        }
      }
    } catch (UserInterruptException e) {
      console.println();
      console.println("Exiting...");
    }
  }

  public static void main(String[] args) {
    Main program = new Main();
    program.run();
  }

}