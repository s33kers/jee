package jee.mif.app.controllers.auth;

import jee.mif.app.helper.UserViewHelper;
import jee.mif.app.model.UserView;
import jee.mif.bl.exceptions.VersionNotLatestException;
import jee.mif.bl.model.ActionEnum;
import jee.mif.bl.model.Result;
import jee.mif.bl.security.JeeAuthenticationManager;
import jee.mif.bl.services.NotificationService;
import jee.mif.model.user.JeeUser;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Created by Mindaugas on 2017-04-12.
 */
@Controller
public class AccountController {

    @Autowired
    private JeeAuthenticationManager authenticationManager;
    @Autowired
    private UserViewHelper userViewHelper;
    @Autowired
    private NotificationService notificationService;

    @RequestMapping("/auth/account")
    public String index(Model model) {
        Result<UserView> user = userViewHelper.getJeeUserView(authenticationManager.getLoggedUser().getId());
        if (!user.isSuccess()) {
            notificationService.addErrorMessage(user.getAdditionalInfo());
        } else {
            model.addAttribute("user", user.getResult());
        }
        return "/auth/account";
    }

    @PostMapping("/auth/account/changeName")
    public String changeName(Model model, @ModelAttribute("user") UserView user) throws VersionNotLatestException {
        if(StringUtils.isBlank(user.getName()) || StringUtils.isBlank(user.getSurname())){
            notificationService.addErrorMessage("Name or Surname field is blank");
            model.addAttribute("user", user);
            return "/auth/account";
        }

        if(ActionEnum.SUCCESS != userViewHelper.updateUserNameSurname(user)) {
            notificationService.addErrorMessage("Unexpected error happened during name and surname update");
            model.addAttribute("user", user);
            return "/auth/account";
        } else {
            notificationService.addInfoMessage("Name and surname updated");
            return "redirect:/auth/account";
        }
    }

    @PostMapping("/auth/account/changePassword")
    public String changePassword(Model model, UserView user) throws VersionNotLatestException {
        if (StringUtils.isBlank(user.getPassword()) || StringUtils.isBlank(user.getPasswordConfirm())) {
            notificationService.addErrorMessage("One of password field is blank");
            model.addAttribute("user", user);
            return "/auth/account";
        }

        if (ObjectUtils.notEqual(user.getPassword(), user.getPasswordConfirm())) {
            notificationService.addErrorMessage("New passwords don't match");
            model.addAttribute("user", user);
            return "/auth/account";
        }

        if(!authenticationManager.isPasswordCorrect(user.getOldPassword(), authenticationManager.getLoggedUser().getId())){
            notificationService.addErrorMessage("Old passwords don't match");
            model.addAttribute("user", user);
            return "/auth/account";
        }

        if(ActionEnum.SUCCESS != userViewHelper.updateUserPassword(user)) {
            notificationService.addErrorMessage("Unexpected error happened during password update");
            model.addAttribute("user", user);
            return "/auth/account";
        } else {
            notificationService.addInfoMessage("Password updated");
            return "redirect:/logout/";
        }
    }

}
