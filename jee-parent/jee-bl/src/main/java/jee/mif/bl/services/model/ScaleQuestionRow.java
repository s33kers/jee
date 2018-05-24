package jee.mif.bl.services.model;


import jee.mif.model.survey.QuestionType;

public class ScaleQuestionRow extends BaseQuestionRow {

    private Integer minValue;
    private Integer maxValue;

    public ScaleQuestionRow(Integer number, String question, Boolean mandatory, QuestionType type, Integer minValue, Integer maxValue) {
        super(number, question, type, mandatory);
        this.minValue = minValue;
        this.maxValue = maxValue;
    }

    public Integer getMinValue() {
        return minValue;
    }

    public Integer getMaxValue() {
        return maxValue;
    }

    @Override
    public String toString() {
        return "ScaleQuestionRow{" +
                "\n" + super.toString() +
                "\n  minValue=" + minValue +
                ",\n  maxValue=" + maxValue +
                "\n}\n";
    }
}
