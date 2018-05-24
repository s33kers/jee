package jee.mif.bl.repositories.impl;

import jee.mif.bl.repositories.UserRepository;
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
import java.util.List;

/**
 * Created by Tadas.
 */
@Repository
public class UserRepositoryImpl extends SimpleJpaRepository<JeeUser, Long> implements UserRepository {

    @PersistenceContext
    private EntityManager em;

    public UserRepositoryImpl(EntityManager entityManager) {
        super(JpaEntityInformationSupport.getEntityInformation(JeeUser.class, entityManager), entityManager);
    }

    @Override
    public JeeUser findUserByPrincipalEmail(String email) {
        return findOne((userRoot, userQuery, userCb) -> userCb.equal(userCb.upper(userRoot.get(JeeUser_.principalEmail)), email.toUpperCase()));
    }

    @Override
    public boolean isUserByPrincipalEmailExists(String email) {
        return count((userRoot, userQuery, userCb) -> userCb.equal(userCb.upper(userRoot.get(JeeUser_.principalEmail)), email.toUpperCase())) > 0;
    }

    @Override
    public List<JeeUser> findByActivated(Long loggedUserId, boolean activated) {
        return findAll((userRoot, userQuery, userCb) -> userCb.and(userCb.equal(userRoot.get(JeeUser_.activated), activated), userCb.notEqual(userRoot.get(JeeUser_.id), loggedUserId)));
    }

    @Override
    public void delete(Long userId) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaDelete<JeeUser> criteriaDelete = cb.createCriteriaDelete(JeeUser.class);
        Root<JeeUser> root = criteriaDelete.from(JeeUser.class);

        criteriaDelete.where(cb.equal(root.get(JeeUser_.id), userId));

        em.createQuery(criteriaDelete).executeUpdate();
    }
}
