package jee.mif.app.model;

import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Tadas.
 */
public class QuestionAnswerView {

    private Long id;
    private Long version;
    private String answer;
    private String selected;
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

    public String getAnswer() {
        return answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }

    public String getSelected() {
        return selected;
    }

    public void setSelected(String selected) {
        this.selected = selected;
    }

    public boolean isQuestionAnswered() {
        return StringUtils.isNotBlank(answer) || StringUtils.isNotBlank(selected);
    }
}
