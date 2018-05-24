package jee.mif.app.helper;

import jee.mif.app.model.UserView;
import jee.mif.bl.exceptions.VersionNotLatestException;
import jee.mif.bl.model.ActionEnum;
import jee.mif.bl.model.Result;
import jee.mif.model.user.AuthenticationType;
import jee.mif.model.user.JeeUser;

import java.util.List;

/**
 * Created by Tadas.
 */
public interface UserViewHelper {

    ActionEnum applyNewUser(UserView user);

    ActionEnum applyForEmailAuthentication (String email, AuthenticationType type);

    ActionEnum finishUserRegistration(UserView user);

    ActionEnum isRegistrationTokenValid(String token);

    ActionEnum updateUserPassword(UserView  user) throws VersionNotLatestException;

    ActionEnum updateUserNameSurname(UserView user) throws VersionNotLatestException;

    Result editUser(UserView userView) throws VersionNotLatestException;

    List<JeeUser> getAllJeeUsersByActivationState(boolean activated);

    Result<UserView> getJeeUserView(long id);

    JeeUser getJeeUser(long id);

    void deleteUserById(long id);
}
