package jee.mif.model.survey;

import jee.mif.model.user.JeeUser;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OrderColumn;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.persistence.Version;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Tadas.
 */

@Entity
@Table(name = "SURVEY")
public class Survey implements Serializable {
    private static final long serialVersionUID = 8671589438190987845L;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Version
    @Column(name = "VERSION")
    private Long version;

    @Column(name = "CREATION_DATE")
    private LocalDateTime creationDate;

    @Column(name = "VALID_DATE")
    private LocalDateTime validDate;

    @Column(name = "SURVEY_TOKEN")
    private String urlToken;

    @Column(name = "TITLE")
    private String title;

    @Column(name = "DESCRIPTION")
    private String description;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "USER_ID")
    private JeeUser user;

    @Column(name = "PUBLIC_RESULTS")
    private Boolean resultsPublic = Boolean.FALSE;

    @Column(name = "UPLOADED")
    private Boolean uploaded = Boolean.FALSE;

    @Column(name = "UPLOADING")
    private Boolean uploading = Boolean.FALSE;

    @Column(name = "UPLOADING_RESULTS", length = 10000)
    private String uploadingResults;

    @Column(name = "DRAFT")
    private Boolean draft = Boolean.TRUE;

    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @OrderColumn(name = "PAGE_NUMBER")
    @JoinColumn(name = "SURVEY_ID")
    private List<Page> pages = new ArrayList<>();

    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "SURVEY_ID")
    private List<SurveyAnswer> surveyAnswers = new ArrayList<>();

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

    public LocalDateTime getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(LocalDateTime creationDate) {
        this.creationDate = creationDate;
    }

    public LocalDateTime getValidDate() {
        return validDate;
    }

    public void setValidDate(LocalDateTime validDate) {
        this.validDate = validDate;
    }

    public String getUrlToken() {
        return urlToken;
    }

    public void setUrlToken(String urlToken) {
        this.urlToken = urlToken;
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

    public JeeUser getUser() {
        return user;
    }

    public void setUser(JeeUser user) {
        this.user = user;
    }

    public Boolean getResultsPublic() {
        return resultsPublic;
    }

    public void setResultsPublic(Boolean resultsPublic) {
        this.resultsPublic = resultsPublic;
    }

    public Boolean getUploaded() {
        return uploaded;
    }

    public void setUploaded(Boolean uploaded) {
        this.uploaded = uploaded;
    }

    public Boolean getUploading() {
        return uploading;
    }

    public void setUploading(Boolean uploading) {
        this.uploading = uploading;
    }

    public String getUploadingResults() {
        return uploadingResults;
    }

    public void setUploadingResults(String uploadingResults) {
        this.uploadingResults = uploadingResults;
    }

    public Boolean getDraft() {
        return draft;
    }

    public void setDraft(Boolean draft) {
        this.draft = draft;
    }

    public List<Page> getPages() {
        return pages;
    }

    public void setPages(List<Page> pages) {
        this.pages = pages;
    }

    public List<SurveyAnswer> getSurveyAnswers() {
        return surveyAnswers;
    }

    public void setSurveyAnswers(List<SurveyAnswer> surveyAnswers) {
        this.surveyAnswers = surveyAnswers;
    }
}
