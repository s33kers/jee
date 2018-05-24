package jee.mif.bl.repositories;

import jee.mif.model.survey.SurveyAnswer;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Created by Tadas.
 */

public interface SurveyAnswerRepository extends JpaRepository<SurveyAnswer, Long> {

    SurveyAnswer findByAnswerToken(String answerUuid);
}
