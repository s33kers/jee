package jee.mif.app.controllers.auth;

import jee.mif.app.helper.UserViewHelper;
import jee.mif.app.model.UserView;
import jee.mif.bl.exceptions.VersionNotLatestException;
import jee.mif.bl.model.ActionEnum;
import jee.mif.bl.model.Result;
import jee.mif.bl.security.JeeAuthenticationManager;
import jee.mif.bl.services.NotificationService;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * Created by Tadas.
 */

@Controller
public class PasswordResetController {

    @Autowired
    private JeeAuthenticationManager authenticationManager;
    @Autowired
    private UserViewHelper userViewHelper;
    @Autowired
    private NotificationService notificationService;

    @RequestMapping("/auth/password-reset")
    public String passwordReset(Model model) {
        Result<UserView> user = userViewHelper.getJeeUserView(authenticationManager.getLoggedUser().getId());
        if (!user.isSuccess()) {
            notificationService.addErrorMessage(user.getAdditionalInfo());
        } else {
            model.addAttribute("user", user.getResult());
        }
        return "/auth/password-reset";
    }


    @RequestMapping(value = "/auth/password-reset", method = RequestMethod.POST)
    public String passwordResetApply (Model model, UserView user) throws VersionNotLatestException {
        if (ObjectUtils.notEqual(user.getPassword(), user.getPasswordConfirm())) {
            model.addAttribute("user", user);
            notificationService.addErrorMessage("Passwords don't match");
            return "/auth/password-reset";
        }
        if(ActionEnum.SUCCESS != userViewHelper.updateUserPassword(user)) {
            notificationService.addErrorMessage("Unexpected error happened during password reset");
            return "redirect:/";
        } else {
            notificationService.addInfoMessage("Password updated");
            return "redirect:/auth";
        }
    }


}
