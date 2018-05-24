package jee.mif.bl.services.model;

import jee.mif.model.survey.QuestionType;

import java.util.List;

public class ChooseQuestion extends BaseQuestionRow {

    private List<String> choices;

    public ChooseQuestion(Integer number, String question, Boolean mandatory, QuestionType type, List<String> choices) {
        super(number, question, type, mandatory);
        this.choices = choices;
    }

    public List<String> getChoices() {
        return choices;
    }

    @Override
    public String toString() {
        return "ChooseQuestion{" +
                "\n" + super.toString() +
                "\n  choices=" + choices +
                "\n}\n";
    }
}
