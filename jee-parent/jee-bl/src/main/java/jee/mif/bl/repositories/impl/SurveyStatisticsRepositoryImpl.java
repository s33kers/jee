package jee.mif.bl.repositories.impl;

import jee.mif.bl.repositories.SurveyStatisticsRepository;
import jee.mif.model.survey.SurveyStatistics;
import jee.mif.model.survey.SurveyStatistics_;
import org.springframework.data.jpa.repository.support.JpaEntityInformationSupport;
import org.springframework.data.jpa.repository.support.SimpleJpaRepository;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;

/**
 * Created by Tadas.
 */
@Repository
public class SurveyStatisticsRepositoryImpl extends SimpleJpaRepository<SurveyStatistics, Long> implements SurveyStatisticsRepository {


    @PersistenceContext
    private EntityManager em;

    public SurveyStatisticsRepositoryImpl(EntityManager entityManager) {
        super(JpaEntityInformationSupport.getEntityInformation(SurveyStatistics.class, entityManager), entityManager);
    }

    @Override
    public List<SurveyStatistics> findBySurveyId(long surveyId, long userId) {
        return findAll((root, query, cb) -> cb.and(cb.equal(root.get(SurveyStatistics_.surveyId), surveyId),
                cb.or(cb.isTrue(root.get(SurveyStatistics_.resultsPublic)), cb.equal(root.get(SurveyStatistics_.userId), userId))));
    }

    @Override
    public List<SurveyStatistics> findBySurveyIdAndIsPublic(long surveyId) {
        return findAll((root, query, cb) -> cb.and(cb.equal(root.get(SurveyStatistics_.surveyId), surveyId), cb.isTrue(root.get(SurveyStatistics_.resultsPublic))));
    }
}
