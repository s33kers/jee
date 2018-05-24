package jee.mif.bl.services.model;

import java.util.List;

public class AnswerRow {

    private Integer id;

    private Integer questionNumber;

    private List<String> answers;

    public AnswerRow(Integer id, Integer questionNumber, List<String> answers) {
        this.id = id;
        this.questionNumber = questionNumber;
        this.answers = answers;
    }

    public Integer getId() {
        return id;
    }

    public Integer getQuestionNumber() {
        return questionNumber;
    }

    public List<String> getAnswers() {
        return answers;
    }

    @Override
    public String toString() {
        return "AnswerRow{" +
                "\n  id=" + id +
                ",\n  questionNumber=" + questionNumber +
                ",\n  answers=" + answers +
                "\n}\n";
    }
}
