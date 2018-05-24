package jee.mif.bl.repositories.impl;

import jee.mif.bl.repositories.SurveyRepository;
import jee.mif.model.survey.Survey;
import jee.mif.model.survey.Survey_;
import jee.mif.model.user.JeeUser_;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specifications;
import org.springframework.data.jpa.repository.support.JpaEntityInformationSupport;
import org.springframework.data.jpa.repository.support.SimpleJpaRepository;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaDelete;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.List;

/**
 * Created by Tadas.
 */
@Repository
public class SurveyRepositoryImpl extends SimpleJpaRepository<Survey, Long> implements SurveyRepository {

    @PersistenceContext
    private EntityManager em;

    public SurveyRepositoryImpl(EntityManager entityManager) {
        super(JpaEntityInformationSupport.getEntityInformation(Survey.class, entityManager), entityManager);
    }

    @Override
    public List<Survey> findAllByUser(Long userId, boolean isAdmin) {
        Specifications<Survey> specifications = Specifications.where((root, query, cb) -> {
            Predicate hisOwn = cb.equal(root.get(Survey_.user).get(JeeUser_.id), userId);
            Predicate othersPublicAndPublished;
                if (isAdmin) {
                    othersPublicAndPublished = cb.isFalse(root.get(Survey_.draft));
                } else {
                    othersPublicAndPublished = cb.and(cb.isTrue(root.get(Survey_.resultsPublic)), cb.isFalse(root.get(Survey_.draft)));
                }
                return cb.or(hisOwn, othersPublicAndPublished);
        });

        return findAll(specifications, new Sort(new Sort.Order(Sort.Direction.ASC, Survey_.creationDate.getName())));
    }


    @Override
    public Survey findByUrlUuid(String urlUuid) {
        return findOne((root, query, cb) -> cb.equal(root.get(Survey_.urlToken), urlUuid));
    }

    @Override
    public void deleteBySurveyIdAndUserId(Long surveyId, Long userId) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaDelete<Survey> criteriaDelete = cb.createCriteriaDelete(Survey.class);
        Root<Survey> root = criteriaDelete.from(Survey.class);

        criteriaDelete.where(cb.and(cb.equal(root.get(Survey_.id), surveyId), cb.equal(root.get(Survey_.user).get(JeeUser_.id), userId)));

        em.createQuery(criteriaDelete).executeUpdate();
    }

    @Override
    public void delete(Long surveyId) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaDelete<Survey> criteriaDelete = cb.createCriteriaDelete(Survey.class);
        Root<Survey> root = criteriaDelete.from(Survey.class);

        criteriaDelete.where(cb.equal(root.get(Survey_.id), surveyId));

        em.createQuery(criteriaDelete).executeUpdate();
    }

    @Override
    public Survey findSurveyForImportDetails(Long id, Long userId) {
        Specifications<Survey> specifications = Specifications.where((root, query, cb) -> {
            Predicate survey = cb.equal(root.get(Survey_.id), id);
            Predicate hisOwn = cb.equal(root.get(Survey_.user).get(JeeUser_.id), userId);
            Predicate isDraft = cb.isTrue(root.get(Survey_.draft));
            Predicate isUploaded = cb.isTrue(root.get(Survey_.uploaded));
            Predicate isUploading = cb.isFalse(root.get(Survey_.uploading));
            return cb.and(survey, hisOwn, isDraft, isUploaded, isUploading);
        });

        return findOne(specifications);
    }
}
