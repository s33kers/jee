package jee.mif.app.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Created by Tadas.
 */

@Controller
public class AccessDeniedController {

    @RequestMapping("/access-denied")
    public String denied() {
        return "access-denied";
    }
}
