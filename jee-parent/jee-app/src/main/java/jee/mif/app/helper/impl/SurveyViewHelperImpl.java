package jee.mif.app.helper.impl;

import jee.mif.app.helper.SurveyViewHelper;
import jee.mif.app.model.ChooseQuestionOptionView;
import jee.mif.app.model.PageView;
import jee.mif.app.model.QuestionView;
import jee.mif.app.model.SurveyView;
import jee.mif.bl.model.Result;
import jee.mif.bl.services.SurveyService;
import jee.mif.model.survey.BaseQuestion;
import jee.mif.model.survey.ChooseQuestion;
import jee.mif.model.survey.ChooseQuestionOption;
import jee.mif.model.survey.Page;
import jee.mif.model.survey.ScaleQuestion;
import jee.mif.model.survey.Survey;
import jee.mif.model.survey.TextQuestion;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by Tadas.
 */

@Component
public class SurveyViewHelperImpl implements SurveyViewHelper {

    @Autowired
    private SurveyService surveyService;

    @Override
    public Result<SurveyView> find(Long id) {
        return surveyService.getSurvey(id).map(this::buildSurveyViewFull);
    }

    @Override
    public Result<SurveyView> findForImportDetails(Long id) {
        return surveyService.getSurveyForImportDetails(id).map(this::buildSurveyView);
    }

    @Override
    public List<SurveyView> findAll() {
        return surveyService.getAllSurveys().getResult().stream().map(this::buildSurveyView).collect(Collectors.toList());
    }

    @Override
    public Result<SurveyView> loadDetails(String surveyToken) {
        return surveyService.createAnswering(surveyToken).map(this::buildSurveyView);
    }

    @Override
    public Result<SurveyView> findByToken(String urlToken) {
        return surveyService.createAnswering(urlToken).map(this::buildSurveyViewFull);
    }

    private SurveyView buildSurveyView(Survey survey) {
        SurveyView view = new SurveyView();
        view.setId(survey.getId());
        view.setVersion(survey.getVersion());
        view.setCreationDate(survey.getCreationDate());
        view.setResultsPublic(survey.getResultsPublic());
        view.setTitle(survey.getTitle());
        view.setDescription(survey.getDescription());
        view.setValidDate(survey.getValidDate());
        view.setUser(survey.getUser().getPrincipalEmail());
        view.setUploaded(survey.getUploaded());
        view.setDraft(survey.getDraft() == null ? true : survey.getDraft());
        view.setUrlToken(survey.getUrlToken());
        view.setUploadingResults(survey.getUploadingResults());
        view.setUploading(survey.getUploading());
        return view;
    }

    @Override
    public SurveyView buildSurveyViewFull(Survey survey) {
        SurveyView view = buildSurveyView(survey);
        view.setPages(survey.getPages().stream().map(this::buildPageView).collect(Collectors.toList()));
        return view;
    }

    @Override
    public PageView buildPageView(Page page) {
        PageView pageView = new PageView();
        pageView.setId(page.getId());
        pageView.setQuestions(page.getBaseQuestions().stream().map(this::buildQuestionView).collect(Collectors.toList()));
        return pageView;
    }

    @Override
    public Page buildPage(PageView pageView) {
        Page page = new Page();
        page.setBaseQuestions(pageView.getQuestions().stream().map(this::buildQuestion).collect(Collectors.toList()));
        return page;
    }

    @Override
    public Result deleteSurvey(Long surveyId) {
        return surveyService.deleteSurvey(surveyId);
    }

    @Override
    public Result<Void> exportSurvey(Long surveyId, OutputStream outputStream) {
        return surveyService.exportSurvey(surveyId, outputStream);
    }

    private QuestionView buildQuestionView(BaseQuestion question) {
        QuestionView questionView = new QuestionView();
        questionView.setId(question.getId());
        questionView.setQuestion(question.getQuestion());
        questionView.setMandatory(question.getMandatory());
        questionView.setQuestionType(question.getQuestionType());

        switch (question.getQuestionType()) {
            case CHECKBOX:
            case MULTIPLECHOICE: {
                ChooseQuestion chooseQuestion = (ChooseQuestion) question;
                questionView.setChooseQuestionOptions(chooseQuestion.getChooseQuestionOptions().stream().
                        map(this::buildChooseQuestionOptionView).collect(Collectors.toList()));
                break;
            }
            case SCALE: {
                ScaleQuestion scaleQuestion = (ScaleQuestion) question;
                questionView.setMinValue(scaleQuestion.getMinValue());
                questionView.setMaxValue(scaleQuestion.getMaxValue());
                break;
            }
            case TEXT: {
                TextQuestion textQuestion = (TextQuestion) question;
                questionView.setSingleLine(textQuestion.getSingleLine());
                break;
            }
        }

        return questionView;
    }

    private ChooseQuestionOptionView buildChooseQuestionOptionView(ChooseQuestionOption chooseQuestionOption) {
        ChooseQuestionOptionView optionView = new ChooseQuestionOptionView();
        optionView.setId(chooseQuestionOption.getId());
        optionView.setOption(chooseQuestionOption.getOption());
        return optionView;
    }

    private BaseQuestion buildQuestion(QuestionView questionView) {
        switch (questionView.getQuestionType()) {
            case CHECKBOX:
            case MULTIPLECHOICE:
                return buildChooseQuestion(questionView);
            case SCALE:
                return buildScaleQuestion(questionView);
            case TEXT:
                return buildTextQuestion(questionView);
        }

        return null;
    }

    private BaseQuestion buildChooseQuestion(QuestionView questionView) {
        ChooseQuestion question = new ChooseQuestion();
        fillBaseQuestionFields(question, questionView);
        question.setChooseQuestionOptions(questionView.getChooseQuestionOptions().stream().map(this::buildChooseQuestionOption).collect(Collectors.toList()));
        return question;
    }

    private BaseQuestion buildScaleQuestion(QuestionView questionView) {
        ScaleQuestion question = new ScaleQuestion();
        fillBaseQuestionFields(question, questionView);
        question.setMinValue(questionView.getMinValue());
        question.setMaxValue(questionView.getMaxValue());
        return question;
    }

    private BaseQuestion buildTextQuestion(QuestionView questionView) {
        TextQuestion question = new TextQuestion();
        fillBaseQuestionFields(question, questionView);
        question.setSingleLine(question.getSingleLine());
        return question;
    }

    private void fillBaseQuestionFields(BaseQuestion question, QuestionView questionView) {
        question.setId(questionView.getId());
        question.setQuestion(questionView.getQuestion());
        question.setMandatory(questionView.isMandatory());
        question.setQuestionType(questionView.getQuestionType());
    }

    private ChooseQuestionOption buildChooseQuestionOption(ChooseQuestionOptionView optionView) {
        ChooseQuestionOption option = new ChooseQuestionOption();
        option.setId(optionView.getId());
        option.setOption(optionView.getOption());
        return option;
    }
}
