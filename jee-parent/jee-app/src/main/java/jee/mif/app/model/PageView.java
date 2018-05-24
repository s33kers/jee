package jee.mif.app.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Tadas.
 */

public class PageView implements Serializable {
    private static final long serialVersionUID = -4746285453499598088L;

    private Long id;
    private List<QuestionView> questions = new ArrayList<>();

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public List<QuestionView> getQuestions() {
        return questions;
    }

    public void setQuestions(List<QuestionView> questions) {
        this.questions = questions;
    }

    @Override
    public String toString() {
        return "PageView{" +
                "id=" + id +
                ", questions=" + questions +
                '}';
    }
}
