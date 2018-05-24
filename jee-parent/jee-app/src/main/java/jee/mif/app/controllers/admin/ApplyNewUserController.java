package jee.mif.app.controllers.admin;

import jee.mif.app.helper.UserViewHelper;
import jee.mif.app.model.UserView;
import jee.mif.bl.model.ActionEnum;
import jee.mif.bl.services.NotificationService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * Created by Tadas.
 */

@Controller
public class ApplyNewUserController  {

    @Autowired
    private UserViewHelper userViewHelper;
    @Autowired
    private NotificationService notificationService;

    private UserView userView;

    @ModelAttribute("user")
    public UserView user(){
        if (userView == null) {
            userView = new UserView();
        }
        return userView;
    }

    @RequestMapping("/admin/apply-new-user")
    public String register() {
        return "/admin/apply-new-user";
    }

    @RequestMapping(value = "/admin/apply-new-user", method = RequestMethod.POST)
    public String apply (@ModelAttribute("user") UserView user) {
        if (StringUtils.isBlank(user.getEmail())) {
            notificationService.addErrorMessage("Email field blank");
            return "/admin/apply-new-user";
        }

        if(ActionEnum.SUCCESS != userViewHelper.applyNewUser(user)) {
            notificationService.addErrorMessage("Unexpected error happened during email registration");
            return "/admin/apply-new-user";
        } else {
            notificationService.addInfoMessage("Email registered");
            return "redirect:/admin/users";
        }
    }
}
