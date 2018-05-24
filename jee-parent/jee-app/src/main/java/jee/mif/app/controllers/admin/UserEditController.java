package jee.mif.app.controllers.admin;

import jee.mif.app.helper.UserViewHelper;
import jee.mif.app.model.UserView;
import jee.mif.bl.exceptions.VersionNotLatestException;
import jee.mif.bl.model.Result;
import jee.mif.bl.services.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * Created by Tadas.
 */

@Controller
public class UserEditController {

    @Autowired
    private UserViewHelper userViewHelper;
    @Autowired
    private NotificationService notificationService;

    @GetMapping("/admin/user-edit/{id}")
    public String edit(@PathVariable("id") Long id, ModelMap model) {
        Result<UserView> surveyView = userViewHelper.getJeeUserView(id);
        if (!surveyView.isSuccess()) {
            notificationService.addErrorMessage(surveyView.getAdditionalInfo());
            return "redirect:/admin/users";
        }

        model.addAttribute("user", surveyView.getResult());
        return "/admin/user-edit";
    }

    @RequestMapping(value = "/admin/user-edit/{id}", method = RequestMethod.POST)
    public String edit(@ModelAttribute("user") UserView user) throws VersionNotLatestException {
        Result result = userViewHelper.editUser(user);
        if(!result.isSuccess()) {
            notificationService.addErrorMessage(result.getAdditionalInfo());
            return "/admin/user-edit";
        } else {
            notificationService.addInfoMessage("User details updated");
            return "redirect:/admin/users";
        }
    }

}
