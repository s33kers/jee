package jee.mif.model.survey;

import org.hibernate.annotations.Immutable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;

/**
 * Created by Tadas.
 */

@Entity
@Table(name = "QUESTION_STATISTICS")
@Immutable
public class QuestionStatistics implements Serializable{
    private static final long serialVersionUID = -1600143753611330844L;

    @Id
    @Column(name = "ANSWER")
    private String answer;

    @Column(name = "ANSWER_COUNT")
    private long answerCount;

    @Column(name = "QUESTION_ID")
    private Long questionId;

    public String getAnswer() {
        return answer;
    }

    public long getAnswerCount() {
        return answerCount;
    }

    public long getQuestionId() {
        return questionId;
    }
}
