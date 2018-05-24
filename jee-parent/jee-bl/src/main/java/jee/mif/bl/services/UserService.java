package jee.mif.bl.services;

import jee.mif.bl.exceptions.VersionNotLatestException;
import jee.mif.bl.model.Result;
import jee.mif.model.user.AuthenticationType;
import jee.mif.model.user.JeeUser;

import java.util.List;

/**
 * Created by Tadas.
 */
public interface UserService {

    /**
     * Apply new user which can be registered
     *
     * @param user user with permitted role and email
     * @return applied user<br>
     * <b>ActionEnum.FAILURE</b> - If user with given email exist
     */
    Result<JeeUser> applyNewUser(JeeUser user);

    /**
     * Checks if applying to user is registered by admin and sends email with token to that email
     *
     * @param email authenticating user email
     * @param authenticationType type of authentication<br>
     * <b>ActionEnum.FAILURE</b> - If email doesn't exist or email sending broke up
     */
    Result applyForEmailAuthentication(String email, AuthenticationType authenticationType);

    /**
     * Get user by ID
     * @param id
     * @return user <br>
     * <b>ActionEnum.FAILURE</b> - If user doesn't exist
     */
    Result<JeeUser> getJeeUserById(Long id);

    /**
     * Updates user
     * @param user
     * @return updated user
     */
    Result<JeeUser> updateJeeUser(JeeUser user);

    /**
     * Find all users by their activation status
     *
     * @param loggedUserId logged user id
     * @param activated which users to retrieve
     * @return users without logged one with given status or emptyList if none are found
     */
    Result<List<JeeUser>> getAllJeeUsersByActivationState(Long loggedUserId, boolean activated);

    /**
     * Upadate user's role and block status
     * @param user
     * @return
     */
    Result editUser(JeeUser user) throws VersionNotLatestException;

    /**
     *  Delete user by given ID
     * @param userId
     */
    void deleteUserById(Long userId);
}
