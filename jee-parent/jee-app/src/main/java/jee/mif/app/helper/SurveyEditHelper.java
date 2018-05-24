package jee.mif.app.helper;

import jee.mif.app.model.PageEditView;
import jee.mif.app.model.PageView;
import jee.mif.app.model.SurveyView;
import jee.mif.bl.exceptions.VersionNotLatestException;
import jee.mif.bl.model.Result;

public interface SurveyEditHelper {

    Result<SurveyView> createNew();

    Result<SurveyView> getSurveyForEdit(Long surveyId);

    Result<SurveyView> createPage(Long surveyId, int pageNumber);

    Result<SurveyView> savePageAndSurveyDetails(Long surveyId, int pageNumber, PageEditView pageEditView) throws VersionNotLatestException;

    Result<SurveyView> deletePage(Long surveyId, int pageNumber);

    Result<PageView> getPage(Long surveyId, int pageNumber);

    Result<SurveyView> publish(Long surveyId);

    Result<SurveyView> validateBeforePublish(SurveyView surveyView);

    Result<SurveyView> canUserDeleteSurvey(SurveyView surveyView);
}
