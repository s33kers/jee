package jee.mif.app.helper;

import jee.mif.app.model.SurveyStatisticsView;

import java.util.List;

/**
 * Created by Tadas.
 */
public interface SurveyStatisticsHelper {
    List<SurveyStatisticsView> findBySurveyId(long surveyId);
}
