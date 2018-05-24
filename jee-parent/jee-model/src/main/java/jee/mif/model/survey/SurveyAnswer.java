package jee.mif.model.survey;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.OrderColumn;
import javax.persistence.Table;
import javax.persistence.Version;
import java.io.Serializable;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by Tadas.
 */

@Entity
@Table(name = "SURVEY_ANSWER")
public class SurveyAnswer implements Serializable {
    private static final long serialVersionUID = -7633613763043449918L;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Version
    @Column(name = "VERSION")
    private Long version;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "SURVEY_ID")
    private Survey survey;

    @Column(name = "COMPLETED")
    private Boolean completed = Boolean.FALSE;

    @Column(name="SAVED_URL")
    private String savedUrl;

    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "SURVEY_ANSWER_ID")
    private Set<QuestionAnswer> questionAnswers = new HashSet<>();

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

    public Survey getSurvey() {
        return survey;
    }

    public void setSurvey(Survey survey) {
        this.survey = survey;
    }

    public Boolean getCompleted() {
        return completed;
    }

    public void setCompleted(Boolean completed) {
        this.completed = completed;
    }

    public String getSavedUrl() {
        return savedUrl;
    }

    public void setSavedUrl(String savedUrl) {
        this.savedUrl = savedUrl;
    }

    public Set<QuestionAnswer> getQuestionAnswers() {
        return questionAnswers;
    }

    public void setQuestionAnswers(Set<QuestionAnswer> questionAnswers) {
        this.questionAnswers = questionAnswers;
    }
}
