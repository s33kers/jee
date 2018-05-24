package jee.mif.app.controllers;

import jee.mif.app.helper.UserViewHelper;
import jee.mif.bl.model.ActionEnum;
import jee.mif.bl.security.JeeAuthenticationManager;
import jee.mif.bl.services.NotificationService;
import jee.mif.model.user.AuthenticationType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * Created by Tadas.
 */

@Controller
public class ApplyAuthenticationController {

    @Autowired
    private JeeAuthenticationManager authenticationManager;
    @Autowired
    private UserViewHelper  userViewHelper;
    @Autowired
    private NotificationService notificationService;

    private AuthenticationType type;

    @ModelAttribute("submitLabel")
    public String submitLabel() {
        return this.type == AuthenticationType.PASSWORD_RESET ? "Reset password" : "Register";
    }

    @RequestMapping("/apply-authentication/{type}")
    public String login(@PathVariable String type, Model model) {
        if (authenticationManager.isUserAuthenticated()) {
            return "redirect:/auth";
        }
        this.type = AuthenticationType.valueOf(type);
        if(this.type == AuthenticationType.PASSWORD_RESET)
            model.addAttribute("submitLabel", "Reset password");
        else
            model.addAttribute("submitLabel", "Register");
        return "apply-authentication";
    }

    @PostMapping(value = "/apply-authentication/")
    public String apply (@RequestParam String email) {
        if (ActionEnum.SUCCESS != userViewHelper.applyForEmailAuthentication(email, type)) {
            notificationService.addErrorMessage("Email doesn't exist");
            return "/apply-authentication";
        } else {
            notificationService.addInfoMessage("Email will be sent shortly.");
            return "redirect:/";
        }
    }
}
