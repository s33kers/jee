package jee.mif.app.model;

import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Tadas.
 */

public class SurveyView implements Serializable {
    private static final long serialVersionUID = -8365111485335841821L;

    private Long id;
    private Long version;
    private LocalDateTime creationDate;
    private String urlToken;
    private String title;
    private String description;
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDateTime validDate;
    private String user;
    private boolean resultsPublic;
    private boolean uploaded;
    private boolean draft = true;
    private List<PageView> pages = new ArrayList<>();
    private String uploadingResults;
    private boolean uploading;

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

    public LocalDateTime getValidDate() {
        return validDate;
    }

    public void setValidDate(LocalDateTime validDate) {
        this.validDate = validDate;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public boolean isResultsPublic() {
        return resultsPublic;
    }

    public void setResultsPublic(boolean resultsPublic) {
        this.resultsPublic = resultsPublic;
    }

    public boolean isUploaded() {
        return uploaded;
    }

    public void setUploaded(boolean uploaded) {
        this.uploaded = uploaded;
    }

    public boolean isDraft() {
        return draft;
    }

    public void setDraft(boolean draft) {
        this.draft = draft;
    }

    public List<PageView> getPages() {
        return pages;
    }

    public void setPages(List<PageView> pages) {
        this.pages = pages;
    }

    public String getUploadingResults() {
        return uploadingResults;
    }

    public void setUploadingResults(String uploadingResults) {
        this.uploadingResults = uploadingResults;
    }

    public boolean isUploading() {
        return uploading;
    }

    public void setUploading(boolean uploading) {
        this.uploading = uploading;
    }
}
