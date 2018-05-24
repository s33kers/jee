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
import javax.persistence.OrderColumn;
import javax.persistence.Table;
import javax.persistence.Version;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Tadas.
 */

@Entity
@Table(name = "PAGE")
public class Page implements Serializable {
    private static final long serialVersionUID = -8782961955093637756L;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Version
    @Column(name = "VERSION")
    private Long version;

    @OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @OrderColumn(name = "QUESTION_NUMBER")
    @JoinColumn(name = "PAGE_ID")
    private List<BaseQuestion> baseQuestions = new ArrayList<>();

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

    public List<BaseQuestion> getBaseQuestions() {
        return baseQuestions;
    }

    public void setBaseQuestions(List<BaseQuestion> baseQuestions) {
        this.baseQuestions = baseQuestions;
    }
}
