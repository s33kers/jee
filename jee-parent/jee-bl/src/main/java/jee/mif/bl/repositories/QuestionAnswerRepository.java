package jee.mif.bl.repositories;

import jee.mif.model.survey.QuestionAnswer;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Created by Tadas.
 */
public interface QuestionAnswerRepository  extends JpaRepository<QuestionAnswer, Long> {
}
