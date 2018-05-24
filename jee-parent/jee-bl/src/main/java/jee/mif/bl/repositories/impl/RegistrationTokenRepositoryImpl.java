package jee.mif.bl.repositories.impl;

import jee.mif.bl.repositories.RegistrationTokenRepository;
import jee.mif.model.user.AuthenticationToken;
import jee.mif.model.user.AuthenticationToken_;
import jee.mif.model.user.AuthenticationType;
import jee.mif.model.user.JeeUser;
import jee.mif.model.user.JeeUser_;
import org.springframework.data.jpa.repository.support.JpaEntityInformationSupport;
import org.springframework.data.jpa.repository.support.SimpleJpaRepository;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaDelete;
import javax.persistence.criteria.Root;

/**
 * Created by Tadas.
 */
@Repository
public class RegistrationTokenRepositoryImpl extends SimpleJpaRepository<AuthenticationToken, Long> implements RegistrationTokenRepository {

    @PersistenceContext
    private EntityManager em;

    public RegistrationTokenRepositoryImpl(EntityManager entityManager) {
        super(JpaEntityInformationSupport.getEntityInformation(AuthenticationToken.class, entityManager), entityManager);
    }

    @Override
    public AuthenticationToken findByToken(String token) {
        return findOne((tokenRoot, tokenQuery, tokenCb) -> tokenCb.equal(tokenRoot.get(AuthenticationToken_.token), token));
    }

    @Override
    public void deleteAllByUserId (Long userId) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaDelete<AuthenticationToken> criteriaDelete = cb.createCriteriaDelete(AuthenticationToken.class);
        Root<AuthenticationToken> root = criteriaDelete.from(AuthenticationToken.class);

        criteriaDelete.where(cb.equal(root.get(AuthenticationToken_.user).get(JeeUser_.id), userId));

        em.createQuery(criteriaDelete).executeUpdate();
    }
}
