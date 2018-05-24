package jee.mif.app.helper.impl;

import jee.mif.app.helper.SurveyAnsweringHelper;
import jee.mif.app.helper.SurveyViewHelper;
import jee.mif.app.model.ChooseQuestionOptionView;
import jee.mif.app.model.PageView;
import jee.mif.app.model.QuestionAnswerView;
import jee.mif.app.model.QuestionView;
import jee.mif.app.model.SurveyAnswerView;
import jee.mif.app.model.SurveyView;
import jee.mif.bl.model.ActionEnum;
import jee.mif.bl.model.Result;
import jee.mif.bl.services.SurveyAnswerService;
import jee.mif.bl.services.SurveyService;
import jee.mif.model.survey.BaseQuestion;
import jee.mif.model.survey.ChooseQuestionOption;
import jee.mif.model.survey.QuestionAnswer;
import jee.mif.model.survey.QuestionType;
import jee.mif.model.survey.SurveyAnswer;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.StringJoiner;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Created by Tadas.
 */
@Component
public class SurveyAnswerViewHelperImpl implements SurveyAnsweringHelper {

    @Autowired
    private SurveyAnswerService surveyAnswerService;
    @Autowired
    private SurveyService surveyService;
    @Autowired
    private SurveyViewHelper surveyViewHelper;
    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public Result<SurveyAnswerView> createAnswering(String surveyToken) {
        Result<SurveyView> survey = surveyViewHelper.findByToken(surveyToken);
        if (!survey.isSuccess()) {
            return new Result<>(ActionEnum.FAILURE, survey.getAdditionalInfo());
        }

        SurveyAnswerView answerView = new SurveyAnswerView();
        answerView.setSurveyView(survey.getResult());

        return new Result<>(answerView, ActionEnum.SUCCESS);
    }

    @Override
    @Transactional
    public Result<SurveyAnswerView> continueAnswering(String answerToken) {
        Result<SurveyAnswer> surveyAnswerResult = surveyAnswerService.continueAnswering(answerToken);
        if (!surveyAnswerResult.isSuccess()) {
            return new Result<>(ActionEnum.FAILURE, surveyAnswerResult.getAdditionalInfo());
        }

        SurveyAnswer surveyAnswer = surveyAnswerResult.getResult();
        SurveyAnswerView surveyAnswerView = buildSurveyAnswerView(surveyAnswer);
        for (PageView pageView : surveyAnswerView.getSurveyView().getPages()) {
            for (QuestionView questionView : pageView.getQuestions()) {
                QuestionAnswer questionAnswer = surveyAnswer.getQuestionAnswers().stream().filter(q -> q.getBaseQuestion().getId().equals(questionView.getId())).findFirst().orElse(null);
                if (questionAnswer != null) {
                    questionView.setAnswerView(buildQuestionAnswerView(questionAnswer));
                    questionView.setChooseQuestionOptions(setCheckedChooseQuestionOptions(questionView.getChooseQuestionOptions(), questionAnswer.getChooseQuestionOptions()));
                }
            }
        }
        return new Result<>(surveyAnswerView, ActionEnum.SUCCESS);
    }

    @Override
    @Transactional
    public Result<SurveyAnswerView> finishAnswering(SurveyAnswerView surveyAnswerView) {
        Result<SurveyAnswerView> result = isAnswersValid(surveyAnswerView);
        if (!result.isSuccess()) {
            return result;
        }

        SurveyAnswer surveyAnswer = new SurveyAnswer();
        surveyAnswer.setId(surveyAnswerView.getId());
        surveyAnswer.setVersion(surveyAnswerView.getVersion());
        surveyAnswer.setCompleted(true);
        surveyAnswer.setSavedUrl(null);
        surveyAnswer.setQuestionAnswers(buildAllQuestions(surveyAnswerView));
        surveyAnswer.setSurvey(surveyService.getSurvey(surveyAnswerView.getSurveyView().getId()).getResult());

        surveyAnswerService.saveAnswers(surveyAnswer);

        return new Result<>(ActionEnum.SUCCESS);
    }

    @Override
    public Result<SurveyAnswerView> saveAnswering(SurveyAnswerView surveyAnswerView, String email) {
        SurveyAnswer surveyAnswer = new SurveyAnswer();
        surveyAnswer.setId(surveyAnswerView.getId());
        surveyAnswer.setVersion(surveyAnswerView.getVersion());
        surveyAnswer.setCompleted(false);
        surveyAnswer.setSavedUrl(StringUtils.defaultIfBlank(surveyAnswerView.getSavedToken(), UUID.randomUUID().toString()));
        surveyAnswer.setQuestionAnswers(buildAllQuestions(surveyAnswerView));
        surveyAnswer.setSurvey(surveyService.getSurvey(surveyAnswerView.getSurveyView().getId()).getResult());

        Result savedEntity = surveyAnswerService.saveSurveyAnswersAndSendEmail(surveyAnswer, email);
        if (savedEntity.isSuccess()) {
            return new Result<>(ActionEnum.SUCCESS);
        } else {
            return new Result<>(ActionEnum.FAILURE, savedEntity.getAdditionalInfo());
        }
    }

    private List<ChooseQuestionOptionView> setCheckedChooseQuestionOptions(List<ChooseQuestionOptionView> chooseQuestionOptions, Set<ChooseQuestionOption> answerChooseQuestionOptions) {
        for (ChooseQuestionOptionView o : chooseQuestionOptions){
            if(answerChooseQuestionOptions.stream().anyMatch(p -> p.getOption().equals(o.getOption()))){
                o.setChecked(true);
            }
        }
        return chooseQuestionOptions;
    }

    private Set<QuestionAnswer> buildAllQuestions(SurveyAnswerView surveyAnswerView) {
        Set<QuestionAnswer> answers = new HashSet<>();
        for (PageView pageView : surveyAnswerView.getSurveyView().getPages()) {
            for (QuestionView questionView : pageView.getQuestions()) {
                QuestionAnswer questionAnswer = buildQuestionAnswer(questionView);
                if (questionAnswer != null) {
                    answers.add(questionAnswer);
                }
            }
        }
        return answers;
    }

    private Result<SurveyAnswerView> isAnswersValid(SurveyAnswerView surveyAnswerView) {
        StringJoiner joiner = new StringJoiner(",");
        for (PageView pageView : surveyAnswerView.getSurveyView().getPages()) {
            for (QuestionView questionView : pageView.getQuestions()) {
                if (questionView.isMandatory()) {
                    if ((questionView.getQuestionType() != QuestionType.CHECKBOX && !questionView.getAnswerView().isQuestionAnswered())
                            || (questionView.getQuestionType() == QuestionType.CHECKBOX && questionView.getChooseQuestionOptions().stream().filter(ChooseQuestionOptionView::getChecked).count() == 0)) {
                        joiner.add("\"" + questionView.getQuestion() + "\"");
                    }
                }
            }
        }
        if (joiner.length() == 0) {
            return new Result<>(ActionEnum.SUCCESS);
        } else {
            return new Result<>(ActionEnum.FAILURE, "You haven't answered to these questions: " + joiner.toString());
        }
    }

    private SurveyAnswerView buildSurveyAnswerView(SurveyAnswer result) {
        SurveyAnswerView surveyAnswerView = new SurveyAnswerView();
        surveyAnswerView.setId(result.getId());
        surveyAnswerView.setVersion(result.getVersion());
        surveyAnswerView.setSavedToken(result.getSavedUrl());
        surveyAnswerView.setCompleted(result.getCompleted());
        surveyAnswerView.setSurveyView(surveyViewHelper.buildSurveyViewFull(result.getSurvey()));
        return surveyAnswerView;
    }

    private QuestionAnswer buildQuestionAnswer(QuestionView questionView) {
        QuestionAnswerView questionAnswerView = questionView.getAnswerView();
        if (questionAnswerView == null) {
            return null;
        }

        QuestionAnswer questionAnswer;
        if (questionAnswerView.getId() != null) {
            questionAnswer = entityManager.getReference(QuestionAnswer.class, questionAnswerView.getId());
        } else {
            questionAnswer = new QuestionAnswer();
        }

        questionAnswer.setVersion(questionAnswer.getVersion());
        questionAnswer.setBaseQuestion(entityManager.getReference(BaseQuestion.class, questionView.getId()));

        switch (questionView.getQuestionType()) {
            case MULTIPLECHOICE:
                questionAnswer.setChooseQuestionOptions(questionView.getChooseQuestionOptions().stream()
                        .filter(p -> p.getOption().equals(questionAnswerView.getSelected()))
                        .map(this::buildChooseQuestionAnswer)
                        .collect(Collectors.toSet()));
                break;
            case CHECKBOX:
                questionAnswer.setChooseQuestionOptions(questionView.getChooseQuestionOptions().stream()
                        .filter(ChooseQuestionOptionView::getChecked)
                        .map(this::buildChooseQuestionAnswer)
                        .collect(Collectors.toSet()));
                break;
            case TEXT:
            case SCALE:
                if (StringUtils.isNotBlank(questionAnswerView.getAnswer())) {
                    questionAnswer.setAnswer(questionAnswerView.getAnswer());
                }
                break;
        }

        return questionAnswer;
    }

    private QuestionAnswerView buildQuestionAnswerView(QuestionAnswer questionAnswer) {
        QuestionAnswerView questionAnswerView = new QuestionAnswerView();
        questionAnswerView.setId(questionAnswer.getId());
        questionAnswerView.setAnswer(questionAnswer.getAnswer());
        ChooseQuestionOption selected = questionAnswer.getChooseQuestionOptions().stream().findFirst().orElse(null);
        if(selected != null && StringUtils.isBlank(questionAnswer.getAnswer()))
            questionAnswerView.setSelected(selected.getOption());
        return questionAnswerView;
    }

    private ChooseQuestionOption buildChooseQuestionAnswer(ChooseQuestionOptionView chooseQuestionOptionView) {
        return entityManager.getReference(ChooseQuestionOption.class, chooseQuestionOptionView.getId());
    }
}
