package jee.mif.app.controllers;

import jee.mif.app.helper.UserViewHelper;
import jee.mif.app.model.UserView;
import jee.mif.bl.model.ActionEnum;
import jee.mif.bl.security.JeeAuthenticationManager;
import jee.mif.bl.services.NotificationService;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * Created by Tadas.
 */
@Controller
public class FinishRegistrationController {

    @Autowired
    private JeeAuthenticationManager authenticationManager;
    @Autowired
    private UserViewHelper userViewHelper;
    @Autowired
    private NotificationService notificationService;

    @RequestMapping("/finish-registration/{token}")
    public String register(Model model) {
        if (authenticationManager.isUserAuthenticated()) {
            return "redirect:/auth";
        }
        model.addAttribute("user", new UserView());
        return "finish-registration";
    }

    @RequestMapping(value = "/finish-registration/{token}", method = RequestMethod.POST)
    public String apply(UserView user, @PathVariable String token, Model model) {

        if (StringUtils.isBlank(user.getPassword()) || StringUtils.isBlank(user.getPasswordConfirm())) {
            notificationService.addErrorMessage("One of password field is blank");
            model.addAttribute("user", user);
            return "finish-registration";
        }

        if (ObjectUtils.notEqual(user.getPassword(), user.getPasswordConfirm())) {
            notificationService.addErrorMessage("Passwords don't match");
            model.addAttribute("user", user);
            return "finish-registration";
        }

        if (ActionEnum.SUCCESS != userViewHelper.isRegistrationTokenValid(token)) {
            if (!authenticationManager.isUserAuthenticated()) {
                notificationService.addErrorMessage("Registration token no more valid");
            }
            return "redirect:/";
        }

        if (ActionEnum.SUCCESS != userViewHelper.finishUserRegistration(user)) {
            notificationService.addErrorMessage("Unexpected error happened during registration");
            model.addAttribute("user", user);
            return "finish-registration";
        } else {
            return "redirect:/";
        }
    }

}

