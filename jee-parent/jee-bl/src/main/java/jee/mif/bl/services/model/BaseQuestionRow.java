package jee.mif.bl.services.model;

import jee.mif.model.survey.QuestionType;

public abstract class BaseQuestionRow {

    private Integer number;
    private String question;
    private QuestionType type;
    private Boolean mandatory;

    public BaseQuestionRow(Integer number, String question, QuestionType type, Boolean mandatory) {
        this.number = number;
        this.question = question;
        this.type = type;
        this.mandatory = mandatory;
    }

    public Integer getNumber() {
        return number;
    }

    public String getQuestion() {
        return question;
    }

    public QuestionType getType() {
        return type;
    }

    public Boolean getMandatory() {
        return mandatory;
    }

    @Override
    public String toString() {
        return "BaseQuestionRow{" +
                "\n  number=" + number +
                ",\n  question='" + question + '\'' +
                ",\n  mandatory='" + mandatory + '\'' +
                ",\n  type=" + type +
                "\n}\n";
    }
}
