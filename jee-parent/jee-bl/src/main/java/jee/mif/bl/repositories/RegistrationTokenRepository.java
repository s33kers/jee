package jee.mif.bl.repositories;

import jee.mif.model.user.AuthenticationToken;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * @author Tadas
 */
public interface RegistrationTokenRepository  extends JpaRepository<AuthenticationToken, Long> {

    AuthenticationToken findByToken(String token);

    void deleteAllByUserId (Long userId);
}
