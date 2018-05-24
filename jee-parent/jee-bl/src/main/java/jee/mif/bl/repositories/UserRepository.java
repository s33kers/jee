package jee.mif.bl.repositories;

import jee.mif.model.user.JeeUser;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * Created by Tadas.
 */
public interface UserRepository extends JpaRepository<JeeUser, Long> {

    JeeUser findUserByPrincipalEmail (String email);

    boolean isUserByPrincipalEmailExists(String email);

    List<JeeUser> findByActivated(Long loggedUserId, boolean activated);
}

