package jee.mif.app.model;

import java.io.Serializable;

/**
 * Created by Tadas.
 */
public class QuestionStatisticsView implements Serializable {
    private static final long serialVersionUID = -4964037488077703671L;

    private String answer;
    private long answerCount;
    private Long questionId;
    private Long rate;

    public String getAnswer() {
        return answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }

    public long getAnswerCount() {
        return answerCount;
    }

    public void setAnswerCount(long answerCount) {
        this.answerCount = answerCount;
    }

    public long getQuestionId() {
        return questionId;
    }

    public void setQuestionId(long questionId) {
        this.questionId = questionId;
    }

    public Long getRate() {
        return rate;
    }

    public void setRate(Long rate) {
        this.rate = rate;
    }
}
