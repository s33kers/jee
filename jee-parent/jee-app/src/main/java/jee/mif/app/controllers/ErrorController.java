package jee.mif.app.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Created by Tadas.
 */
@Controller
public class ErrorController implements org.springframework.boot.autoconfigure.web.ErrorController {

    private static final String PATH = "/error";

    @RequestMapping(value = PATH)
    public String error(Model model, HttpServletRequest request, HttpServletResponse response) {
        model.addAttribute("status", response.getStatus());
        return PATH;
    }

    @Override
    public String getErrorPath() {
        return PATH;
    }
}
