package jee.mif.app.controllers;

import jee.mif.app.helper.SurveyAnsweringHelper;
import jee.mif.app.model.SurveyAnswerView;
import jee.mif.bl.model.Result;
import jee.mif.bl.services.NotificationService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * Created by Tadas.
 */

@Controller
public class SurveyAnsweringController {

    private static final String ATTRIBUTE_SURVEY = "survey";
    private static final String ATTRIBUTE_TOTAL_PAGES = "totalPages";
    private static final String SURVEY_SUCCESSFULLY_SUBMITED = "Survey successfully submitted";
    private static final String SURVEY_SUCCESSFULLY_SAVED = "Survey saved successfully. Email will be sent shortly.";
    private static final String EMPTY_EMAIL = "Please enter your email";

    @Autowired
    private SurveyAnsweringHelper surveyAnsweringHelper;
    @Autowired
    private NotificationService notificationService;

    @RequestMapping(value = "/survey-answering/{surveyToken}", method = RequestMethod.GET)
    public String startAnswering(@PathVariable String surveyToken, Model model) {
        Result<SurveyAnswerView> answersView = surveyAnsweringHelper.createAnswering(surveyToken);

        if (!answersView.isSuccess()) {
            notificationService.addErrorMessage(answersView.getAdditionalInfo());
            return "error";
        }

        model.addAttribute(ATTRIBUTE_SURVEY, answersView.getResult());
        model.addAttribute(ATTRIBUTE_TOTAL_PAGES, answersView.getResult().getSurveyView().getPages().size());

        return "survey-answering";
    }

    @RequestMapping(value = "/survey-continue-answering/{answerToken}", method = RequestMethod.GET)
    public String continueAnswering(@PathVariable String answerToken, Model model) {
        Result<SurveyAnswerView> answersView = surveyAnsweringHelper.continueAnswering(answerToken);
        if (!answersView.isSuccess()) {
            notificationService.addErrorMessage(answersView.getAdditionalInfo());
            return "error";
        }

        model.addAttribute(ATTRIBUTE_SURVEY, answersView.getResult());
        model.addAttribute(ATTRIBUTE_TOTAL_PAGES, answersView.getResult().getSurveyView().getPages().size());

        return "survey-answering";
    }

    @RequestMapping(value = {"/survey-answering/*", "/survey-continue-answering/*"}, method = RequestMethod.POST)
    public String finishAnswering(@RequestParam String action, @RequestParam String email, @ModelAttribute SurveyAnswerView survey, Model model) {
        Result<SurveyAnswerView> answersView = null;
        if ("submit".equals(action)) {
            answersView = surveyAnsweringHelper.finishAnswering(survey);
        } else if ("save".equals(action)) {
            if (StringUtils.isBlank(email)) {
                notificationService.addErrorMessage(EMPTY_EMAIL);
                model.addAttribute(ATTRIBUTE_SURVEY, survey);
                model.addAttribute(ATTRIBUTE_TOTAL_PAGES, survey.getSurveyView().getPages().size());
                return "survey-answering";
            }

            answersView = surveyAnsweringHelper.saveAnswering(survey, email);
        }
        if (!answersView.isSuccess()) {
            notificationService.addErrorMessage(answersView.getAdditionalInfo());
            model.addAttribute(ATTRIBUTE_SURVEY, survey);
            model.addAttribute(ATTRIBUTE_TOTAL_PAGES, survey.getSurveyView().getPages().size());
            return "survey-answering";
        } else {
            notificationService.addInfoMessage("submit".equals(action) ? SURVEY_SUCCESSFULLY_SUBMITED : SURVEY_SUCCESSFULLY_SAVED);
            return "redirect:/survey-details/" + survey.getSurveyView().getUrlToken();
        }
    }
}
