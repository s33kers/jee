package jee.mif.bl.services;

import jee.mif.bl.model.Result;
import jee.mif.model.survey.QuestionAnswer;
import jee.mif.model.survey.SurveyAnswer;

import java.util.Set;

/**
 * Created by Tadas.
 */
public interface SurveyAnswerService {

    Result<SurveyAnswer> getById(Long surveyAnswerId);

    Result<SurveyAnswer> continueAnswering(String surveyAnswer);

    void deleteById(Long id);

    void saveAnswers(SurveyAnswer surveyAnswer);

    Result saveSurveyAnswersAndSendEmail(SurveyAnswer surveyAnswer, String email);
}
