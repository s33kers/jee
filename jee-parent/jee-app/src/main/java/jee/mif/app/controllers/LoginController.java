package jee.mif.app.controllers;

import jee.mif.bl.security.JeeAuthenticationManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * Created by Tadas.
 */

@Controller
public class LoginController {

    @Autowired
    private JeeAuthenticationManager authenticationManager;

    @RequestMapping("/")
    public String login() {
        if (authenticationManager.isUserAuthenticated()) {
            return "redirect:/auth";
        }
        return "login";
    }

    @RequestMapping(value = "/", method = RequestMethod.POST)
    public String getUserLogin(){
        return "redirect:/auth/";
    }
}
