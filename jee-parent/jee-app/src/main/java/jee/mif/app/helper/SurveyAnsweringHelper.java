package jee.mif.app.helper;

import jee.mif.app.model.SurveyAnswerView;
import jee.mif.bl.model.Result;

/**
 * Created by Tadas.
 */
public interface SurveyAnsweringHelper {

    Result<SurveyAnswerView> createAnswering(String uuidSurvey);

    Result<SurveyAnswerView> continueAnswering(String answerToken);

    Result<SurveyAnswerView> finishAnswering(SurveyAnswerView surveyAnswerView);

    Result<SurveyAnswerView> saveAnswering(SurveyAnswerView surveyAnswerView, String email);
}
