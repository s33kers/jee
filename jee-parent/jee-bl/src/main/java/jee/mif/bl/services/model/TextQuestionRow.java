package jee.mif.bl.services.model;

import jee.mif.model.survey.QuestionType;

public class TextQuestionRow extends BaseQuestionRow {

    public TextQuestionRow(Integer number, String question, Boolean mandatory, QuestionType type) {
        super(number, question, type, mandatory);
    }

    @Override
    public String toString() {
        return "TextQuestionRow{" +
                "\n" + super.toString() +
                "\n}";
    }
}
