package jee.mif.bl.repositories;

import jee.mif.model.survey.SurveyStatistics;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * Created by Tadas.
 */
public interface SurveyStatisticsRepository extends JpaRepository<SurveyStatistics, Long> {

    List<SurveyStatistics> findBySurveyId(long surveyId, long userId);

    List<SurveyStatistics> findBySurveyIdAndIsPublic(long surveyId);
}
