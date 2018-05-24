package jee.mif.bl.services.impl;

import jee.mif.bl.aspect.Monitor;
import jee.mif.bl.exceptions.VersionNotLatestException;
import jee.mif.bl.model.ActionEnum;
import jee.mif.bl.model.Result;
import jee.mif.bl.properties.AdditionalInfoProperties;
import jee.mif.bl.repositories.RegistrationTokenRepository;
import jee.mif.bl.repositories.UserRepository;
import jee.mif.bl.security.model.LoggedUser;
import jee.mif.bl.services.EmailSender;
import jee.mif.bl.services.UserService;
import jee.mif.bl.utils.UrlUtils;
import jee.mif.model.user.AuthenticationToken;
import jee.mif.model.user.AuthenticationType;
import jee.mif.model.user.JeeUser;
import jee.mif.model.user.RoleType;
import org.apache.commons.lang3.BooleanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.session.SessionInformation;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.stereotype.Component;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import javax.mail.MessagingException;
import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

/**
 * Created by Tadas.
 */

@Monitor
@Transactional
@Component
public class UserServiceImpl implements UserService {

    private static final String EMAIL_SUBJECT = "User token authentication";

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private RegistrationTokenRepository registrationTokenRepository;
    @Autowired
    private EmailSender emailSender;
    @Autowired
    private AdditionalInfoProperties properties;
    @Autowired
    private SessionRegistry sessionRegistry;
    @Autowired
    private TemplateEngine templateEngine;

    @Override
    public Result<JeeUser> applyNewUser(JeeUser user) {
        Objects.requireNonNull(user, "Parameter 'user' is mandatory!");
        user.setRole(RoleType.ROLE_USER);
        Result<JeeUser> result;
        if (!userRepository.isUserByPrincipalEmailExists(user.getPrincipalEmail())) {
            result = new Result<>(userRepository.save(user), ActionEnum.SUCCESS);
        } else {
            result = new Result<>(ActionEnum.FAILURE, "This email has been used");
        }

        return result;
    }

    @Override
    public Result applyForEmailAuthentication(String email, AuthenticationType type) {
        Objects.requireNonNull(email, "Parameter 'email' is mandatory!");
        Objects.requireNonNull(type, "Parameter 'type' is mandatory!");

        JeeUser user = userRepository.findUserByPrincipalEmail(email);
        if (user != null && BooleanUtils.isFalse(user.getBlocked())) {
            if (BooleanUtils.isTrue(user.getActivated()) && type == AuthenticationType.REGISTRATION) {
                return new Result(ActionEnum.FAILURE, "User already activated");
            }
            if (BooleanUtils.isFalse(user.getActivated()) && type == AuthenticationType.PASSWORD_RESET) {
                return new Result(ActionEnum.FAILURE, "User must be activated first");
            }
            registrationTokenRepository.deleteAllByUserId(user.getId());
            AuthenticationToken authenticationToken = new AuthenticationToken();
            authenticationToken.setUser(user);
            authenticationToken.setCreationDate(LocalDateTime.now());
            authenticationToken.setToken(UUID.randomUUID().toString());
            authenticationToken.setTokenType(type);
            registrationTokenRepository.save(authenticationToken);

            try {
                String urlToSend = AuthenticationType.PASSWORD_RESET == type ? UrlUtils.PASSWORD_RESET : UrlUtils.NEW_USER_REGISTRATION;
                Context context = new Context();
                context.setVariable("url", properties.getServerUrl() + urlToSend + authenticationToken.getToken());
                emailSender.sendMail(user.getPrincipalEmail(), EMAIL_SUBJECT, templateEngine.process(type.getEmailTemplateName(), context));
            } catch (MessagingException e) {
                return new Result(ActionEnum.FAILURE, e.getMessage());
            }
            return new Result(ActionEnum.SUCCESS);
        }
        return new Result(ActionEnum.FAILURE, "User doesn't exist");
    }

    @Override
    public Result<JeeUser> getJeeUserById(Long id) {
        Objects.requireNonNull(id, "Parameter 'id' is mandatory!");
        JeeUser user = userRepository.findOne(id);
        if (user != null) {
            return new Result<>(user, ActionEnum.SUCCESS);
        } else {
            return new Result<>(ActionEnum.FAILURE, "User doesn't exist");
        }
    }

    @Override
    public Result<JeeUser> updateJeeUser(JeeUser user) {
        return new Result<>(userRepository.save(user), ActionEnum.SUCCESS);
    }

    @Override
    public Result<List<JeeUser>> getAllJeeUsersByActivationState(Long loggedUserId, boolean activated) {
        return new Result<>(userRepository.findByActivated(loggedUserId, activated), ActionEnum.SUCCESS);
    }

    @Override
    public Result editUser(JeeUser user) throws VersionNotLatestException {
        Result<JeeUser> result = getJeeUserById(user.getId());
        if (ActionEnum.FAILURE == result.getState()) {
            return result;
        }
        JeeUser userEntity = result.getResult();
        if (userEntity.getVersion() > user.getVersion()) {
            throw new VersionNotLatestException();
        }

        userEntity.setBlocked(user.getBlocked());
        userEntity.setRole(user.getRole());
        updateJeeUser(userEntity);

        sessionRegistry.getAllPrincipals();
        if (BooleanUtils.isTrue(user.getBlocked())) {
            LoggedUser userToBlock = sessionRegistry.getAllPrincipals().stream().map(LoggedUser.class::cast).filter(p -> user.getPrincipalEmail().equals(p.getPrincipalEmail())).findAny().orElse(null);
            if (userToBlock != null) {
                for (SessionInformation sessionInformation : sessionRegistry.getAllSessions(userToBlock, false)) {
                    sessionInformation.expireNow();
                }
            }
        }
        return new Result(ActionEnum.SUCCESS);
    }

    @Override
    public void deleteUserById(Long userId) {
        userRepository.delete(userId);
    }
}
