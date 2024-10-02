package quiz;
import java.util.ArrayList;

import question.Questions;

public class Quiz extends ArrayList<QuizQuestion> {
  
  public Quiz(Questions questions, int numQuestions) {
    for (int q = 0; q < numQuestions; ++q) {
      this.add(new QuizQuestion(questions.randomQuestion(), q + 1));
    }
  }

  public Double score() {
    return (double) this.stream()
      .filter(QuizQuestion::isCorrect)
      .count() / this.size() * 100;
  }

}
