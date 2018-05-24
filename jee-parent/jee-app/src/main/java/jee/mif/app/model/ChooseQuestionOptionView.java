package jee.mif.app.model;

import java.io.Serializable;

/**
 * Created by Tadas.
 */
public class ChooseQuestionOptionView implements Serializable {
    private static final long serialVersionUID = -473558428111285547L;

    private Long id;
    private String option;
    private boolean checked;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getOption() {
        return option;
    }

    public void setOption(String option) {
        this.option = option;
    }

    public boolean getChecked() {
        return checked;
    }

    public void setChecked(boolean checked) {
        this.checked = checked;
    }
    @Override
    public String toString() {
        return "ChooseQuestionOptionView{" +
                "id=" + id +
                ", option='" + option + '\'' +
                '}';
    }
}
