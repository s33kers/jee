package jee.mif.app.helper.impl;

import jee.mif.app.helper.UserViewHelper;
import jee.mif.app.model.UserView;
import jee.mif.bl.exceptions.VersionNotLatestException;
import jee.mif.bl.model.ActionEnum;
import jee.mif.bl.model.Result;
import jee.mif.bl.security.JeeAuthenticationManager;
import jee.mif.bl.security.model.LoggedUser;
import jee.mif.bl.services.UserService;
import jee.mif.model.user.AuthenticationType;
import jee.mif.model.user.JeeUser;
import org.apache.commons.lang3.BooleanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.stereotype.Component;

import javax.transaction.Transactional;
import java.util.List;

/**
 * Created by Tadas.
 */

@Component
public class UserViewHelperImpl implements UserViewHelper {

    @Autowired
    private UserService userService;
    @Autowired
    private JeeAuthenticationManager authenticationManager;

    @Override
    public ActionEnum applyNewUser(UserView user) {
        return userService.applyNewUser(buildUserEntity(user)).getState();
    }

    public ActionEnum applyForEmailAuthentication(String email, AuthenticationType type) {
        return userService.applyForEmailAuthentication(email, type).getState();
    }

    @Override
    @Transactional
    public ActionEnum finishUserRegistration(UserView userView) {
        LoggedUser loggedUser = authenticationManager.getLoggedUser();
        Result<JeeUser> result = userService.getJeeUserById(loggedUser.getId());
        if (ActionEnum.FAILURE == result.getState()) {
            return ActionEnum.FAILURE;
        }
        JeeUser user = result.getResult();
        user.setName(userView.getName());
        user.setSurname(userView.getSurname());
        user.setHashedPassword(authenticationManager.hashPassword(userView.getPassword()));
        user.setActivated(true);
        return userService.updateJeeUser(user).getState();
    }

    @Override
    public ActionEnum isRegistrationTokenValid(String token) {
        try {
            authenticationManager.authenticate(token, AuthenticationType.REGISTRATION);
        } catch (AuthenticationCredentialsNotFoundException e) {
            return ActionEnum.FAILURE;
        }

        if (authenticationManager.getLoggedUser() == null) {
            return ActionEnum.FAILURE;
        } else {
            return ActionEnum.SUCCESS;
        }
    }

    @Override
    @Transactional
    public ActionEnum updateUserPassword(UserView userView) throws VersionNotLatestException {
        LoggedUser loggedUser = authenticationManager.getLoggedUser();
        Result<JeeUser> result = userService.getJeeUserById(loggedUser.getId());
        if (ActionEnum.FAILURE == result.getState()) {
            return ActionEnum.FAILURE;
        }
        JeeUser user = result.getResult();
        if (user.getVersion() > userView.getVersion()) {
            throw new VersionNotLatestException();
        }
        user.setHashedPassword(authenticationManager.hashPassword(userView.getPassword()));
        return userService.updateJeeUser(user).getState();
    }

    @Override
    public ActionEnum updateUserNameSurname(UserView userView) throws VersionNotLatestException {
        LoggedUser loggedUser = authenticationManager.getLoggedUser();
        Result<JeeUser> result = userService.getJeeUserById(loggedUser.getId());
        if (ActionEnum.FAILURE == result.getState()) {
            return ActionEnum.FAILURE;
        }
        JeeUser user = result.getResult();
        if (user.getVersion() > userView.getVersion()) {
            throw new VersionNotLatestException();
        }
        user.setName(userView.getName());
        user.setSurname(userView.getSurname());
        return userService.updateJeeUser(user).getState();
    }

    @Override
    public Result editUser(UserView userView) throws VersionNotLatestException {
        return userService.editUser(buildUserEntity(userView));
    }

    @Override
    public List<JeeUser> getAllJeeUsersByActivationState(boolean activated) {
        return userService.getAllJeeUsersByActivationState(authenticationManager.getLoggedUser().getId(), activated).getResult();
    }

    @Override
    public Result<UserView> getJeeUserView(long id) {
        return userService.getJeeUserById(id).map(this::buildUserView);
    }

    @Override
    public JeeUser getJeeUser(long id) {
        return userService.getJeeUserById(id).getResult();
    }

    @Override
    public void deleteUserById(long id) {
        userService.deleteUserById(id);
    }

    private JeeUser buildUserEntity (UserView view) {
        JeeUser user = new JeeUser();
        user.setId(view.getId());
        user.setVersion(view.getVersion());
        user.setPrincipalEmail(view.getEmail());
        user.setName(view.getName());
        user.setSurname(view.getSurname());
        user.setRole(view.getRoleType());
        user.setBlocked(view.isBlocked());
        user.setActivated(view.isActivated());
        return user;
    }

    private UserView buildUserView(JeeUser user) {
        UserView view = new UserView();
        view.setId(user.getId());
        view.setVersion(user.getVersion());
        view.setEmail(user.getPrincipalEmail());
        view.setName(user.getName());
        view.setSurname(user.getSurname());
        view.setRoleType(user.getRole());
        view.setBlocked(BooleanUtils.isTrue(user.getBlocked()));
        view.setActivated(BooleanUtils.isTrue(user.getActivated()));
        return view;
    }
}
