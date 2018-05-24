package jee.mif.app.helper;

import jee.mif.app.model.PageView;
import jee.mif.app.model.SurveyView;
import jee.mif.bl.model.Result;
import jee.mif.model.survey.Page;
import jee.mif.model.survey.Survey;

import java.io.OutputStream;
import java.util.List;

/**
 * Created by Tadas.
 */
public interface SurveyViewHelper {

    Result<SurveyView> find(Long id);

    Result<SurveyView> findForImportDetails(Long id);

    Result<SurveyView> findByToken(String urlToken);

    List<SurveyView> findAll();

    Result<SurveyView> loadDetails(String surveyToken);

    SurveyView buildSurveyViewFull(Survey survey);

    PageView buildPageView(Page page);

    Page buildPage(PageView pageView);

    Result deleteSurvey(Long surveyId);

    Result<Void> exportSurvey(Long surveyId, OutputStream outputStream);

}
