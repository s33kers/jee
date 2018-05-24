package jee.mif.model.survey;

import javax.persistence.Column;
import javax.persistence.Entity;

/**
 * Created by Tadas.
 */

@Entity
public class TextQuestion extends BaseQuestion {
    private static final long serialVersionUID = -7680260476866477625L;

    @Column(name = "SINGLE_LINE")
    private Boolean singleLine = Boolean.FALSE;

    public Boolean getSingleLine() {
        return singleLine;
    }

    public void setSingleLine(Boolean singleLine) {
        this.singleLine = singleLine;
    }
}
