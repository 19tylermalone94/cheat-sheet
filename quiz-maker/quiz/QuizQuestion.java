package quiz;

import question.Question;

public class QuizQuestion extends Question {

  private boolean isCorrect;
  private int questionNumber;

  public QuizQuestion(Question q, int questionNumber) {
    super(q.question(), q.answer());
    this.questionNumber = questionNumber;
  }

  public void grade(String answer) {
    isCorrect = answer.equalsIgnoreCase(this.answer());
  }

  public int questionNumber() {
    return questionNumber;
  }

  public boolean isCorrect() {
    return isCorrect;
  }

}
