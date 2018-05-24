package jee.mif.app.controllers;

import jee.mif.app.helper.SurveyViewHelper;
import jee.mif.app.model.SurveyView;
import jee.mif.bl.model.Result;
import jee.mif.bl.properties.AdditionalInfoProperties;
import jee.mif.bl.services.NotificationService;
import jee.mif.bl.utils.UrlUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Created by Tadas.
 */

@Controller
public class SurveyDetailsController {

    private static final String ATTRIBUTE_SURVEY = "survey";
    private static final String ATTRIBUTE_URL = "surveyUrl";
    @Autowired
    private SurveyViewHelper surveyAnsweringHelper;
    @Autowired
    private NotificationService notificationService;
    @Autowired
    private AdditionalInfoProperties properties;

    @RequestMapping("/survey-details/{surveyToken}")
    public String surveyDetails(@PathVariable String surveyToken, Model model) {
        Result<SurveyView> answersView = surveyAnsweringHelper.loadDetails(surveyToken);
        if (!answersView.isSuccess()) {
            notificationService.addErrorMessage(answersView.getAdditionalInfo());
            return "error";
        }
        model.addAttribute(ATTRIBUTE_SURVEY, answersView.getResult());
        model.addAttribute(ATTRIBUTE_URL, properties.getServerUrl() + UrlUtils.SURVEY_DETAILS + answersView.getResult().getUrlToken());
        return "survey-details";
    }
}
