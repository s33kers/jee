package jee.mif.bl.repositories.impl;

import jee.mif.bl.repositories.SurveyAnswerRepository;
import jee.mif.model.survey.Survey;
import jee.mif.model.survey.SurveyAnswer;
import jee.mif.model.survey.SurveyAnswer_;
import jee.mif.model.survey.Survey_;
import org.springframework.data.jpa.repository.support.JpaEntityInformationSupport;
import org.springframework.data.jpa.repository.support.SimpleJpaRepository;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaDelete;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

/**
 * Created by Tadas.
 */
@Repository
public class SurveyAnswerRepositoryImpl extends SimpleJpaRepository<SurveyAnswer, Long> implements SurveyAnswerRepository {

    @PersistenceContext
    private EntityManager em;

    public SurveyAnswerRepositoryImpl(EntityManager entityManager) {
        super(JpaEntityInformationSupport.getEntityInformation(SurveyAnswer.class, entityManager), entityManager);
    }

    @Override
    public SurveyAnswer findByAnswerToken(String answerToken) {
        return findOne((root, query, cb) -> cb.equal(root.get(SurveyAnswer_.savedUrl), answerToken));
    }

    @Override
    public void delete(Long userId) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaDelete<SurveyAnswer> criteriaDelete = cb.createCriteriaDelete(SurveyAnswer.class);
        Root<SurveyAnswer> root = criteriaDelete.from(SurveyAnswer.class);

        criteriaDelete.where(cb.equal(root.get(SurveyAnswer_.id), userId));

        em.createQuery(criteriaDelete).executeUpdate();
    }
}
