package question;

import java.util.ArrayList;

public class Questions extends ArrayList<Question> {
  
  public Question randomQuestion() {
    return this.get((int)Math.floor(this.size() * Math.random()));
  }

  public void put(String question, String answer) {
    if (!this.contains(new Question(question, answer))) {
      this.add(new Question(question, answer));
    } else {
      throw new DuplicateQuestionException();
    }
  }

}
