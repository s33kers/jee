package jee.mif.app.model;

import jee.mif.model.survey.QuestionType;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Tadas.
 */
public class QuestionView implements Serializable{
    private static final long serialVersionUID = 3018662000356787967L;

    private Long id;
    private String question;
    private boolean mandatory;
    private QuestionType questionType;
    private List<ChooseQuestionOptionView> chooseQuestionOptions = new ArrayList<>();
    private BigDecimal minValue;
    private BigDecimal maxValue;
    private boolean singleLine;
    private QuestionAnswerView answerView;
    private String placeholder;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public boolean isMandatory() {
        return mandatory;
    }

    public void setMandatory(boolean mandatory) {
        this.mandatory = mandatory;
    }

    public QuestionType getQuestionType() {
        return questionType;
    }

    public void setQuestionType(QuestionType questionType) {
        this.questionType = questionType;
    }

    public List<ChooseQuestionOptionView> getChooseQuestionOptions() {
        return chooseQuestionOptions;
    }

    public void setChooseQuestionOptions(List<ChooseQuestionOptionView> chooseQuestionOptions) {
        this.chooseQuestionOptions = chooseQuestionOptions;
    }

    public BigDecimal getMinValue() {
        return minValue;
    }

    public void setMinValue(BigDecimal minValue) {
        this.minValue = minValue;
    }

    public BigDecimal getMaxValue() {
        return maxValue;
    }

    public void setMaxValue(BigDecimal maxValue) {
        this.maxValue = maxValue;
    }

    public boolean isSingleLine() {
        return singleLine;
    }

    public void setSingleLine(boolean singleLine) {
        this.singleLine = singleLine;
    }

    public QuestionAnswerView getAnswerView() {
        return answerView;
    }

    public void setAnswerView(QuestionAnswerView answerView) {
        this.answerView = answerView;
    }

    public String getPlaceholder() {
        return placeholder;
    }

    public void setPlaceholder(String placeholder) {
        this.placeholder = placeholder;
    }

    @Override
    public String toString() {
        return "QuestionView{" +
                "id=" + id +
                ", question='" + question + '\'' +
                ", mandatory=" + mandatory +
                ", questionType=" + questionType +
                ", chooseQuestionOptions=" + chooseQuestionOptions +
                ", minValue=" + minValue +
                ", maxValue=" + maxValue +
                ", singleLine=" + singleLine +
                ", answerView=" + answerView +
                ", placeholder='" + placeholder + '\'' +
                '}';
    }
}
