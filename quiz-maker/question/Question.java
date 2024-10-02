package question;

public class Question {
  
  private String question;
  private String answer;

  public Question(String question, String answer) {
    this.question = question;
    this.answer = answer;
  }

  public String question() {
    return question;
  }

  public String answer() {
    return answer;
  }
  
  @Override
  public boolean equals(Object obj) {
    if (obj == this) {
      return true;
    }
    if (obj == null || getClass() != obj.getClass()) {
      return false;
    }
    Question other = (Question) obj;
    return (question != null && question.equals(other.question));
  }

}
