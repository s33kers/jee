package jee.mif.bl.services.model;

import jee.mif.model.user.JeeUser;

import java.time.LocalDateTime;

public class SurveyImportParams {

    private LocalDateTime creationDate;
    private String urlToken;
    private JeeUser user;
    private boolean uploaded;
    private boolean draft;

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

    public JeeUser getUser() {
        return user;
    }

    public void setUser(JeeUser user) {
        this.user = user;
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
}
