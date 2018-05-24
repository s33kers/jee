package jee.mif.app.controllers.auth;

import jee.mif.app.helper.SurveyViewHelper;
import jee.mif.app.model.SurveyView;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
public class IndexController {

    @Autowired
    private SurveyViewHelper surveyViewHelper;

    @RequestMapping("/auth")
    public String manageSurveys() {
        return "/auth/index";
    }

    @ModelAttribute("allSurveys")
    public List<SurveyView> allSurveys() {
        return surveyViewHelper.findAll();
    }
}
