package jee.mif.model.survey;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Version;
import java.io.Serializable;
import java.util.Set;

/**
 * Created by Tadas.
 */

@Entity
@Table(name = "QUESTION_ANSWER")
public class QuestionAnswer implements Serializable{
    private static final long serialVersionUID = 3182942126088390456L;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Version
    @Column(name = "VERSION")
    private Long version;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "QUESTION_ID")
    private BaseQuestion baseQuestion;

    @Column(name = "ANSWER")
    private String answer;

    @ManyToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinTable(name = "MULTIPLE_ANSWERS", joinColumns = @JoinColumn(name = "QUESTION_ANSWER_ID"), inverseJoinColumns = @JoinColumn(name = "CHOOSE_QUESTION_ANSWER_ID"))
    private Set<ChooseQuestionOption> chooseQuestionOptions;

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

    public BaseQuestion getBaseQuestion() {
        return baseQuestion;
    }

    public void setBaseQuestion(BaseQuestion baseQuestion) {
        this.baseQuestion = baseQuestion;
    }

    public String getAnswer() {
        return answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }

    public Set<ChooseQuestionOption> getChooseQuestionOptions() {
        return chooseQuestionOptions;
    }

    public void setChooseQuestionOptions(Set<ChooseQuestionOption> chooseQuestionOptions) {
        this.chooseQuestionOptions = chooseQuestionOptions;
    }
}
