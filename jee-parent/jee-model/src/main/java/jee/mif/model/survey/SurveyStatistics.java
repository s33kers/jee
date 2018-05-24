package jee.mif.model.survey;

import org.hibernate.annotations.Immutable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import java.io.Serializable;
import java.util.List;

/**
 * Created by Tadas.
 */
@Entity
@Table(name = "SURVEY_STATISTICS")
@Immutable
public class SurveyStatistics implements Serializable {
    private static final long serialVersionUID = 6136344083426043611L;

    @Id
    @Column(name = "QUESTION_ID")
    private Long questionId;

    @Column(name = "SURVEY_ID")
    private Long surveyId;

    @Column(name = "QUESTION_TYPE")
    @Enumerated(EnumType.STRING)
    private QuestionType questionType;

    @Column(name = "QUESTION")
    private String question;

    @OneToMany(fetch = FetchType.EAGER, mappedBy = "questionId")
    private List<QuestionStatistics> questionStatistics;

    @Column(name = "PUBLIC_RESULTS")
    private Boolean resultsPublic;

    @Column(name = "USER_ID")
    private Long userId;

    @Column(name = "ANSWER_COUNT")
    private Long answerCount;

    public Long getSurveyId() {
        return surveyId;
    }

    public QuestionType getQuestionType() {
        return questionType;
    }

    public String getQuestion() {
        return question;
    }

    public Long getQuestionId() {
        return questionId;
    }

    public List<QuestionStatistics> getQuestionStatistics() {
        return questionStatistics;
    }

    public Boolean getResultsPublic() {
        return resultsPublic;
    }

    public Long getUserId() {
        return userId;
    }

    public Long getAnswerCount() {
        return answerCount;
    }
}
