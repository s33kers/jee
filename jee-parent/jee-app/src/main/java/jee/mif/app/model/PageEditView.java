package jee.mif.app.model;

import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;
/**
 * Created by Tadas.
 */

public class PageEditView extends PageView implements Serializable {
    private static final long serialVersionUID = -4746285453499598088L;

    private Long version;
    private LocalDateTime creationDate;
    private String title;
    private String description;
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate validDate;
    private boolean resultsPublic;

    public Long getVersion() {
        return version;
    }

    public void setVersion(Long version) {
        this.version = version;
    }

    public LocalDateTime getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(LocalDateTime creationDate) {
        this.creationDate = creationDate;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public LocalDate getValidDate() {
        return validDate;
    }

    public void setValidDate(LocalDate validDate) {
        this.validDate = validDate;
    }

    public boolean getResultsPublic(){
        return resultsPublic;
    }

    public void setResultsPublic(boolean resultsPublic){
        this.resultsPublic = resultsPublic;
    }

    public boolean isResultsPublic() {
        return resultsPublic;
    }

    @Override
    public String toString() {
        return "PageEditView{" +
                "super=" + super.toString() +
                ", creationDate=" + creationDate +
                ", title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", validDate=" + validDate +
                ", resultsPublic=" + resultsPublic +
                '}';
    }
}
