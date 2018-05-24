package jee.mif.bl.repositories;

import jee.mif.model.survey.Survey;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * Created by Tadas.
 */
public interface SurveyRepository extends JpaRepository<Survey, Long> {

    List<Survey> findAllByUser(Long userId, boolean isAdmin);

    Survey findByUrlUuid(String urlUuid);

    void deleteBySurveyIdAndUserId(Long surveyId, Long userId);

    Survey findSurveyForImportDetails(Long id, Long userId);
}
