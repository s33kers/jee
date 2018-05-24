package jee.mif.app.helper.impl;

import jee.mif.app.helper.SurveyEditHelper;
import jee.mif.app.helper.SurveyViewHelper;
import jee.mif.app.model.PageEditView;
import jee.mif.app.model.PageView;
import jee.mif.app.model.QuestionView;
import jee.mif.app.model.SurveyView;
import jee.mif.bl.exceptions.VersionNotLatestException;
import jee.mif.bl.model.ActionEnum;
import jee.mif.bl.model.Result;
import jee.mif.bl.security.JeeAuthenticationManager;
import jee.mif.bl.security.model.LoggedUser;
import jee.mif.bl.services.SurveyService;
import jee.mif.model.survey.Page;
import jee.mif.model.survey.Survey;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Component
public class SurveyEditHelperImpl implements SurveyEditHelper {

    private static final String DATE_INVALID = "Valid until date can not be earlier than today";
    private static final String TITLE_REQUIRED = "Survey title is required";
    private static final String NO_QUESTIONS = "There are no questions to publish";
    private static final String EMPTY_PAGES = "There are one or more empty pages left";
    private static final String EMPTY_QUESTIONS = "There are one or more empty questions left";
    private static final String EMPTY_OPTIONS = "There is checkbox or multiple choice question without answer options";
    private static final String SCALE_QUESTION = "There is scale question without minimum or maximum value";
    private static final String SCALE_MIN_MAX_VALUE = "One of scale questions minimum value are equal or greater than maximum value";

    @Autowired
    private SurveyService surveyService;

    @Autowired
    private SurveyViewHelper surveyViewHelper;

    @Autowired
    private JeeAuthenticationManager jeeAuthenticationManager;

    @Override
    public Result<SurveyView> createNew() {
        return surveyService.createNew().map(surveyViewHelper::buildSurveyViewFull);
    }

    @Override
    public Result<SurveyView> getSurveyForEdit(Long surveyId) {
        return surveyService.getSurveyForEdit(surveyId).map(surveyViewHelper::buildSurveyViewFull);
    }

    @Override
    public Result<SurveyView> createPage(Long surveyId, int pageNumber) {
        return surveyService.createPage(surveyId, pageNumber).map(surveyViewHelper::buildSurveyViewFull);
    }

    @Override
    public Result<SurveyView> savePageAndSurveyDetails(Long surveyId, int pageNumber, PageEditView pageEditView) throws VersionNotLatestException {
        Result<Survey> surveyResult = surveyService.getSurvey(surveyId);
        if (!surveyResult.isSuccess()) {
            return new Result<>(ActionEnum.FAILURE, surveyResult.getAdditionalInfo());
        }
        Survey survey = surveyResult.getResult();
        if (survey.getVersion() > pageEditView.getVersion()) {
            throw new VersionNotLatestException();
        }
        survey.setTitle(pageEditView.getTitle());
        survey.setDescription(pageEditView.getDescription());
        survey.setResultsPublic(pageEditView.getResultsPublic());
        survey.setValidDate(pageEditView.getValidDate() != null ? pageEditView.getValidDate().atStartOfDay() : null);

        List<Page> pages = survey.getPages();
        if (pages.size() > 0) {
            pages.set(pageNumber - 1, surveyViewHelper.buildPage(pageEditView));
            survey.setPages(pages);
        }

        return surveyService.save(survey).map(surveyViewHelper::buildSurveyViewFull);
    }

    @Override
    public Result<SurveyView> deletePage(Long surveyId, int pageNumber) {
        return surveyService.deletePage(surveyId, pageNumber).map(surveyViewHelper::buildSurveyViewFull);
    }

    @Override
    public Result<PageView> getPage(Long surveyId, int pageNumber) {
        return surveyService.getPage(surveyId, pageNumber).map(surveyViewHelper::buildPageView);
    }

    @Override
    public Result<SurveyView> publish(Long surveyId) {
        return surveyService.publish(surveyId).map(surveyViewHelper::buildSurveyViewFull);
    }

    @Override
    public Result validateBeforePublish(SurveyView survey) {
        if(StringUtils.isBlank(survey.getTitle())){
            return new Result(ActionEnum.FAILURE, TITLE_REQUIRED);
        }

        if(survey.getValidDate() != null && survey.getValidDate().isBefore(LocalDateTime.now())){
            return new Result(ActionEnum.FAILURE, DATE_INVALID);
        }

        if(survey.getPages() == null || survey.getPages().isEmpty()){
            return new Result(ActionEnum.FAILURE, NO_QUESTIONS);
        }

        if(areAnyPagesBlank(survey.getPages())){
            return new Result(ActionEnum.FAILURE, EMPTY_PAGES);
        }

        String errorText = areAnyQuestionsBlank(survey.getPages());
        if(errorText != null){
            return new Result(ActionEnum.FAILURE, errorText);
        }

        return new Result(ActionEnum.SUCCESS);
    }

    @Override
    public Result canUserDeleteSurvey(SurveyView surveyView){
        LoggedUser user = jeeAuthenticationManager.getLoggedUser();
        ActionEnum canDelete = ActionEnum.FAILURE;
        if(jeeAuthenticationManager.isAuthenticatedAdmin() ||
                (user != null && user.getPrincipalEmail().equals(surveyView.getUser()))){
            canDelete = ActionEnum.SUCCESS;
        }
        return new Result(canDelete);
    }

    private boolean areAnyPagesBlank(List<PageView> pageViews){
        for (PageView pageView : pageViews) {
            if(pageView.getQuestions() == null || pageView.getQuestions().isEmpty()) {
                return true;
            }
        }
        return false;
    }

    private String areAnyQuestionsBlank(List<PageView> pageViews) {
        for (PageView pageView : pageViews) {
            for (QuestionView questionView : pageView.getQuestions()) {
                if (StringUtils.isBlank(questionView.getQuestion())) {
                    return EMPTY_QUESTIONS;
                }
                switch (questionView.getQuestionType()) {
                    case MULTIPLECHOICE:
                    case CHECKBOX:
                        if (questionView.getChooseQuestionOptions() == null || questionView.getChooseQuestionOptions().isEmpty()) {
                            return EMPTY_OPTIONS;
                        }
                        break;
                    case SCALE:
                        if (questionView.getMinValue() == null || questionView.getMaxValue() == null) {
                            return SCALE_QUESTION;
                        }
                        if (questionView.getMinValue().compareTo(questionView.getMaxValue()) >= 0) {
                            return SCALE_MIN_MAX_VALUE;
                        }
                        break;
                }
            }
        }
        return null;
    }
}
