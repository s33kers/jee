package jee.mif.app.controllers.admin;

import jee.mif.app.helper.UserViewHelper;
import jee.mif.app.model.UserView;
import jee.mif.bl.model.ActionEnum;
import jee.mif.model.user.JeeUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

/**
 * Created by Tadas.
 */

@Controller
public class UsersController {

    @Autowired
    private UserViewHelper userViewHelper;

    @ModelAttribute("activatedUsers")
    public List<JeeUser> activatedUsers(){
        return userViewHelper.getAllJeeUsersByActivationState(true);
    }

    @ModelAttribute("notActivatedUsers")
    public List<JeeUser> notActivatedUsers(){
        return userViewHelper.getAllJeeUsersByActivationState(false);
    }

    @RequestMapping("/admin/users")
    public String index() {
        return "/admin/users";
    }

    @PostMapping("/admin/deleteNotActivatedUser")
    public String deleteNotActivatedUser(UserView user){
        userViewHelper.deleteUserById(user.getId());
        return "redirect:/admin/users#notregistered";
    }
}