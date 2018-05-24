package jee.mif.app.model;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by Tadas.
 */
public class SurveyAnswerView implements Serializable {
    private static final long serialVersionUID = 2863790445231753374L;

    private Long id;
    private Long version;
    private SurveyView surveyView;
    private boolean completed;
    private String savedToken;
    private Set<QuestionAnswerView> questionAnswerViewList = new HashSet<>();

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getVersion() {
        return version;
    }

    public void setVersion(Long version) {
        this.version = version;
    }

    public SurveyView getSurveyView() {
        return surveyView;
    }

    public void setSurveyView(SurveyView surveyView) {
        this.surveyView = surveyView;
    }

    public boolean isCompleted() {
        return completed;
    }

    public void setCompleted(boolean completed) {
        this.completed = completed;
    }

    public String getSavedToken() {
        return savedToken;
    }

    public void setSavedToken(String savedToken) {
        this.savedToken = savedToken;
    }

    public Set<QuestionAnswerView> getQuestionAnswerViewList() {
        return questionAnswerViewList;
    }

    public void setQuestionAnswerViewList(Set<QuestionAnswerView> questionAnswerViewList) {
        this.questionAnswerViewList = questionAnswerViewList;
    }
}
