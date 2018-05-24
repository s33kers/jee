package jee.mif.bl.security.impl;

import jee.mif.bl.repositories.RegistrationTokenRepository;
import jee.mif.bl.repositories.UserRepository;
import jee.mif.bl.security.JeeAuthenticationManager;
import jee.mif.bl.security.model.LoggedUser;
import jee.mif.bl.services.NotificationService;
import jee.mif.bl.services.UserService;
import jee.mif.model.user.AuthenticationToken;
import jee.mif.model.user.AuthenticationType;
import jee.mif.model.user.JeeUser;
import jee.mif.model.user.RoleType;
import org.apache.commons.lang3.BooleanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationToken;
import org.springframework.stereotype.Component;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * @author Tadas
 */

@Transactional
@Component("jeeAuthenticationManager")
public class JeeAuthenticationManagerImpl implements JeeAuthenticationManager {

    @Autowired
    private RegistrationTokenRepository registrationTokenRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private NotificationService notificationService;

    @Autowired
    private UserService userService;

    @Override
    public Authentication getAuthentication() {
        SecurityContext securityContext = SecurityContextHolder.getContext();
        if (securityContext != null) {
            return securityContext.getAuthentication();
        }
        return null;
    }

    @Override
    public LoggedUser getLoggedUser() {
        if (getAuthentication() != null && getAuthentication().getPrincipal() instanceof LoggedUser) {
            return (LoggedUser) getAuthentication().getPrincipal();
        }
        return null;
    }

    @Override
    public boolean isUserAuthenticated() {
        return getAuthentication() != null && getAuthentication().getPrincipal() instanceof LoggedUser;
    }

    @Override
    public boolean isAuthenticatedAdmin() {
        LoggedUser loggedUser = getLoggedUser();
        return loggedUser != null && loggedUser.getRoleType() == RoleType.ROLE_ADMIN;
    }

    @Override
    public void authenticate(String token, AuthenticationType authenticationType) throws AuthenticationCredentialsNotFoundException {
        AuthenticationToken authenticationToken = registrationTokenRepository.findByToken(token);
        if (authenticationToken != null && authenticationType == authenticationToken.getTokenType()) {
            JeeUser userToBeLoggedIn  = authenticationToken.getUser();
            if (BooleanUtils.isFalse(userToBeLoggedIn.getBlocked()) && authenticationToken.getCreationDate().isBefore(LocalDateTime.now().plusDays(2))) {
                LoggedUser loggedUser = mapLoggedUser(userToBeLoggedIn);
                SecurityContextHolder.getContext().setAuthentication(new PreAuthenticatedAuthenticationToken(loggedUser, "", mapGrantedAuthority(authenticationToken.getUser())));
            } else {
                throw new AuthenticationCredentialsNotFoundException("Authentication expired or user is blocked");
            }
            registrationTokenRepository.delete(authenticationToken);
        } else {
            throw new AuthenticationCredentialsNotFoundException("Authentication token not found");
        }
    }

    @Override
    public Authentication getAuthentication(String password, String login) {
        JeeUser jeeUser = userRepository.findUserByPrincipalEmail(login);

        if (jeeUser != null && BooleanUtils.isFalse(jeeUser.getBlocked()) && passwordEncoder.matches(password, jeeUser.getHashedPassword())) {
            return new PreAuthenticatedAuthenticationToken(mapLoggedUser(jeeUser), password, mapGrantedAuthority(jeeUser));
        } else {
            notificationService.addErrorMessage("Bad or non existing credentials.");
        }
        return null;
    }

    @Override
    public String hashPassword(String password) {
        return passwordEncoder.encode(password);
    }

    @Override
    public boolean isPasswordCorrect(String password, Long currentUserId) {
        JeeUser currentUser = userService.getJeeUserById(currentUserId).getResult();
        return passwordEncoder.matches(password, currentUser.getHashedPassword());
    }

    private LoggedUser mapLoggedUser (JeeUser user) {
        LoggedUser loggedUser = new LoggedUser();
        loggedUser.setId(user.getId());
        loggedUser.setRoleType(user.getRole());
        loggedUser.setPrincipalEmail(user.getPrincipalEmail());
        loggedUser.setName(user.getName());
        loggedUser.setSurname(user.getSurname());
        return loggedUser;
    }

    private Set<GrantedAuthority> mapGrantedAuthority (JeeUser user) {
        return new HashSet<>(Arrays.asList(new SimpleGrantedAuthority(user.getRole().name())));
    }
}
