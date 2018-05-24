package jee.mif.app.controllers.auth;

import jee.mif.bl.security.JeeAuthenticationManager;
import jee.mif.bl.security.model.LoggedUser;
import jee.mif.bl.services.NotificationService;
import jee.mif.bl.services.SurveyImporter;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Controller
@RequestMapping("/auth/import-survey")
public class ImportSurveyController {

    @Autowired
    private SurveyImporter surveyImporter;

    @Autowired
    private NotificationService notificationService;

    @Autowired
    private JeeAuthenticationManager authenticationManager;

    @GetMapping
    public String importSurvey(ModelMap model) {
        return "/auth/import-survey";
    }

    @PostMapping
    public String apply(@RequestParam("file") MultipartFile file, ModelMap model) {
        String filename = file.getOriginalFilename();
        if (filename != null && !filename.endsWith(".xlsx")) {
            notificationService.addErrorMessage( "Only .xlsx files are supported!");
            return "/auth/import-survey";
        }

        LoggedUser loggedUser = authenticationManager.getLoggedUser();
        try {
            surveyImporter.importXlsx(IOUtils.toByteArray(file.getInputStream()), loggedUser.getId(), null);
        } catch (IOException e) {
            e.printStackTrace();
        }
        notificationService.addInfoMessage("Survey importing is in progress. Soon it will appear near all others.");
        return "redirect:/auth/";
    }
}
