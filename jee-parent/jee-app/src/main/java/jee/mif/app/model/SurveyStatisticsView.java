package jee.mif.app.model;

import jee.mif.model.survey.QuestionType;

import java.io.Serializable;
import java.util.List;
import java.util.Objects;
import java.util.function.Supplier;
import java.util.stream.Stream;

/**
 * Created by Tadas.
 */
public class SurveyStatisticsView implements Serializable {
    private static final long serialVersionUID = 2692870177887171070L;

    private Long surveyId;
    private QuestionType questionType;
    private String question;
    private Long questionId;
    private Long answerCount;
    private List<QuestionStatisticsView> questionStatisticsViewList;
    private Double[] scaleValueArray;
    private boolean resultsPublic;
    private Long userId;
    private List<Double> mode;
    private Double median;
    private Double mean;

    public Long getSurveyId() {
        return surveyId;
    }

    public void setSurveyId(Long surveyId) {
        this.surveyId = surveyId;
    }

    public QuestionType getQuestionType() {
        return questionType;
    }

    public void setQuestionType(QuestionType questionType) {
        this.questionType = questionType;
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public Long getQuestionId() {
        return questionId;
    }

    public void setQuestionId(Long questionId) {
        this.questionId = questionId;
    }

    public Long getAnswerCount() {
        return answerCount;
    }

    public void setAnswerCount(Long answerCount) {
        this.answerCount = answerCount;
    }

    public List<QuestionStatisticsView> getQuestionStatisticsViewList() {
        return questionStatisticsViewList;
    }

    public void setQuestionStatisticsViewList(List<QuestionStatisticsView> questionStatisticsViewList) {
        this.questionStatisticsViewList = questionStatisticsViewList;
    }

    public boolean isResultsPublic() {
        return resultsPublic;
    }

    public void setResultsPublic(boolean resultsPublic) {
        this.resultsPublic = resultsPublic;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public List<Double> getMode() {
        return mode;
    }

    public void setMode(List<Double> mode) {
        this.mode = mode;
    }

    public Double getMedian() {
        return median;
    }

    public void setMedian(Double median) {
        this.median = median;
    }

    public Double getMean() {
        return mean;
    }

    public void setMean(Double mean) {
        this.mean = mean;
    }

    public Double[] getScaleValueArray() {
        if (scaleValueArray == null && questionType == QuestionType.SCALE) {
            scaleValueArray = new Double[questionStatisticsViewList.size()];
            scaleValueArray = questionStatisticsViewList.stream()
                    .flatMap(q -> Stream.generate(() -> Double.parseDouble(q.getAnswer())).limit(q.getAnswerCount()))
                    .sorted(Double::compareTo)
                    .toArray(Double[]::new);
        }
        return scaleValueArray;
    }
}
