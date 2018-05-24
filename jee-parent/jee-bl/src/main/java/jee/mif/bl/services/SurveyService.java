package jee.mif.bl.services;

import jee.mif.bl.model.Result;
import jee.mif.model.survey.BaseQuestion;
import jee.mif.model.survey.Page;
import jee.mif.model.survey.Survey;

import java.io.OutputStream;
import java.util.List;

public interface SurveyService {

    Result<Survey> createNew();

    Result<Page> getPage(Long surveyId, int pageNumber);

    Result<Survey> createPage(Long surveyId, int pageNumber);

    Result<Survey> deletePage(Long surveyId, int pageNumber);

    Result<Survey> getSurvey(Long id);

    Result<Survey> getSurveyForImportDetails(Long id);

    Result<Survey> getSurveyForEdit(Long id);

    Result<List<Survey>> getAllSurveys();

    Result<Survey> createAnswering(String uuidSurvey);

    Result<Survey> save(Survey survey);

    Result<Survey> publish(Long id);

    Result deleteSurvey(Long surveyId);

    Result<Void> exportSurvey(Long surveyId, OutputStream outputStream);
}
