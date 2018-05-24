package jee.mif.bl.security;

import jee.mif.bl.security.model.LoggedUser;
import jee.mif.model.user.AuthenticationType;
import jee.mif.model.user.JeeUser;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.core.Authentication;

/**
 * @author Tadas
 */
public interface JeeAuthenticationManager {

    Authentication getAuthentication();

    LoggedUser getLoggedUser();

    void authenticate(String token, AuthenticationType authenticationType) throws AuthenticationCredentialsNotFoundException;

    Authentication getAuthentication(String password, String login);

    String hashPassword(String password);

    boolean isPasswordCorrect(String password, Long currentUserId);

    boolean isUserAuthenticated();

    boolean isAuthenticatedAdmin();

}
