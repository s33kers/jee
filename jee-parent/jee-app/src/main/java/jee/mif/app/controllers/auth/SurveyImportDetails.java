package jee.mif.app.controllers.auth;

import jee.mif.app.helper.SurveyViewHelper;
import jee.mif.app.model.SurveyView;
import jee.mif.bl.model.Result;
import jee.mif.bl.security.JeeAuthenticationManager;
import jee.mif.bl.security.model.LoggedUser;
import jee.mif.bl.services.NotificationService;
import jee.mif.bl.services.SurveyImporter;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
@Controller
@RequestMapping("/auth/import-details")
public class SurveyImportDetails {

    @Autowired
    private SurveyImporter surveyImporter;

    @Autowired
    private NotificationService notificationService;

    @Autowired
    private JeeAuthenticationManager authenticationManager;

    @Autowired
    private SurveyViewHelper surveyViewHelper;

    @GetMapping("/{id}")
    public String preview(@PathVariable("id") Long surveyId, ModelMap model) {
        Result<SurveyView> surveyView = surveyViewHelper.findForImportDetails(surveyId);
        if (!surveyView.isSuccess()) {
            return "redirect:/auth";
        }
        model.addAttribute("surveyId", surveyId);
        model.addAttribute("importDetails", surveyView.getResult().getUploadingResults());
        return "/auth/import-details";
    }

    @PostMapping("/upload")
    public String apply(@RequestParam("file") MultipartFile file, @RequestParam(value = "survey-id") Long surveyId, ModelMap model) {
        String filename = file.getOriginalFilename();
        if (filename != null && !filename.endsWith(".xlsx")) {
            model.addAttribute("surveyId", surveyId);

            notificationService.addErrorMessage( "Only .xlsx files are supported!");
            return "/auth/import-details";
        }

        LoggedUser loggedUser = authenticationManager.getLoggedUser();
        try {
            surveyImporter.importXlsx(IOUtils.toByteArray(file.getInputStream()), loggedUser.getId(), surveyId);
        } catch (IOException e) {
            e.printStackTrace();
        }

        notificationService.addInfoMessage("Survey importing is in progress. Soon it will appear near all others.");
        return "redirect:/auth/";
    }


    @PostMapping("/delete-import")
    public String deleteSurvey(@RequestParam(value = "survey-id") Long surveyId) {
        Result result = surveyViewHelper.deleteSurvey(surveyId);
        if (!result.isSuccess()) {
            notificationService.addErrorMessage(result.getAdditionalInfo());
            return "/auth/import-details/" + surveyId;
        } else{
            notificationService.addInfoMessage("Survey deleted successfully");
            return "redirect:/auth";
        }
    }
}
