package jee.mif.bl.services.model;

import java.time.LocalDate;

public class SurveyHeader {
    private String title;
    private String description;
    private LocalDate validDate;
    private boolean resultsPublic;

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

    public boolean isResultsPublic() {
        return resultsPublic;
    }

    public void setResultsPublic(boolean resultsPublic) {
        this.resultsPublic = resultsPublic;
    }

    @Override
    public String toString() {
        return "SurveyHeader{" +
                "title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", validDate=" + validDate +
                ", resultsPublic=" + resultsPublic +
                '}';
    }
}
