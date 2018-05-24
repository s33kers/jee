package jee.mif.bl.repositories.impl;

import jee.mif.bl.repositories.QuestionAnswerRepository;
import jee.mif.model.survey.QuestionAnswer;
import org.springframework.data.jpa.repository.support.JpaEntityInformationSupport;
import org.springframework.data.jpa.repository.support.SimpleJpaRepository;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 * Created by Tadas.
 */
@Repository
public class QuestionAnswerRepositoryImpl extends SimpleJpaRepository<QuestionAnswer, Long> implements QuestionAnswerRepository {

    @PersistenceContext
    private EntityManager em;

    public QuestionAnswerRepositoryImpl(EntityManager entityManager) {
        super(JpaEntityInformationSupport.getEntityInformation(QuestionAnswer.class, entityManager), entityManager);
    }
}
