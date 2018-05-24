package jee.mif.bl.services;

import jee.mif.model.survey.SurveyStatistics;

import java.util.List;

/**
 * Created by Tadas.
 */
public interface SurveyStatisticsService {

    List<SurveyStatistics> findBySurveyId(long surveyId);
}
