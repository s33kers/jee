package jee.mif.model.survey;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.OrderColumn;
import javax.persistence.Table;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Tadas.
 */

@Entity
@Table(name = "CHOOSE_QUESTION")
public class ChooseQuestion extends BaseQuestion {
    private static final long serialVersionUID = 3444964884572837625L;

    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @OrderColumn(name = "ANSWER_NUMBER")
    @JoinColumn(name = "CHOOSE_QUESTION_ID")
    private List<ChooseQuestionOption> chooseQuestionOptions = new ArrayList<>();

    public List<ChooseQuestionOption> getChooseQuestionOptions() {
        return chooseQuestionOptions;
    }

    public void setChooseQuestionOptions(List<ChooseQuestionOption> chooseQuestionOptions) {
        this.chooseQuestionOptions = chooseQuestionOptions;
    }
}
