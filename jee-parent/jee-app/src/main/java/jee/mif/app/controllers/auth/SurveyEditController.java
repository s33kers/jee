package jee.mif.app.controllers.auth;

import jee.mif.app.helper.SurveyEditHelper;
import jee.mif.app.helper.SurveyViewHelper;
import jee.mif.app.model.PageEditView;
import jee.mif.app.model.PageView;
import jee.mif.app.model.SurveyView;
import jee.mif.bl.exceptions.VersionNotLatestException;
import jee.mif.bl.model.Result;
import jee.mif.bl.properties.AdditionalInfoProperties;
import jee.mif.bl.security.JeeAuthenticationManager;
import jee.mif.bl.security.model.LoggedUser;
import jee.mif.bl.services.NotificationService;
import jee.mif.bl.utils.UrlUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.time.LocalDateTime;
import java.util.List;

@Controller
@RequestMapping("/auth/survey-edit")
public class SurveyEditController {

    private static final String MESSAGE_SUCCESSFULLY_PUBLISHED = "Survey was successfully published";

    private static final String ATTRIBUTE_SURVEY = "survey";
    private static final String ATTRIBUTE_TOTAL_PAGES = "totalPages";
    private static final String ATTRIBUTE_CURRENT_PAGE_NUMBER = "currentPageNumber";
    private static final String ATTRIBUTE_CURRENT_PAGE = "currentPage";
    private static final String ATTRIBUTE_URL = "url";
    private static final String ATTRIBUTE_CAN_DELETE = "canDelete";
    private static final String SURVEY_DELETED_SUCCESSFULLY = "Survey deleted successfully";

    private static final String REQUEST_PARAM_PAGE = "page";

    @Autowired
    private SurveyEditHelper surveyEditHelper;

    @Autowired
    private SurveyViewHelper surveyViewHelper;

    @Autowired
    private NotificationService notificationService;

    @Autowired
    private AdditionalInfoProperties properties;

    @GetMapping("/new")
    public String createNew() {
        Result<SurveyView> surveyView = surveyEditHelper.createNew();
        if (!surveyView.isSuccess()) {
            notificationService.addErrorMessage(surveyView.getAdditionalInfo());
            return "redirect:/";
        }

        return "redirect:/auth/survey-edit/" + surveyView.getResult().getId();
    }

    @GetMapping("/preview/{id}")
    public String preview(@PathVariable("id") Long surveyId, @RequestParam(value = "page", required = false, defaultValue = "1") int pageNumber, ModelMap model) {
        Result<SurveyView> surveyView = surveyViewHelper.find(surveyId);
        if(!surveyView.isSuccess())
            return "redirect:/auth";

        int totalPages = surveyView.getResult().getPages().size();

        PageView currentPage = null;
        if (totalPages != 0){
            Result<PageView> pageViewResult = surveyEditHelper.getPage(surveyId, pageNumber);
            if(pageViewResult.isSuccess())
                currentPage = pageViewResult.getResult();
            else {
                notificationService.addErrorMessage(pageViewResult.getAdditionalInfo());
                return "redirect:/auth/survey-edit/" + surveyId;
            }
        } else if (pageNumber != 1) {
            return "redirect:/auth/survey-edit/" + surveyId;
        }

        String url = properties.getServerUrl() + UrlUtils.SURVEY_DETAILS + surveyView.getResult().getUrlToken();

        SurveyView survey = surveyView.getResult();
        Boolean canDelete = surveyEditHelper.canUserDeleteSurvey(survey).isSuccess();

        model.addAttribute(ATTRIBUTE_SURVEY, survey);
        model.addAttribute(ATTRIBUTE_TOTAL_PAGES, totalPages);
        model.addAttribute(ATTRIBUTE_CURRENT_PAGE_NUMBER, pageNumber);
        model.addAttribute(ATTRIBUTE_CURRENT_PAGE, currentPage);
        model.addAttribute(ATTRIBUTE_URL, url);
        model.addAttribute(ATTRIBUTE_CAN_DELETE, canDelete);

        return "/auth/survey-edit";
    }

    @GetMapping("/{id}")
    public String edit(@PathVariable("id") Long surveyId, @RequestParam(value = "page", required = false, defaultValue = "1") int pageNumber, ModelMap model) {
        Result<SurveyView> surveyView = surveyEditHelper.getSurveyForEdit(surveyId);
        if (!surveyView.isSuccess()) {
            notificationService.addErrorMessage(surveyView.getAdditionalInfo());
            return "redirect:/auth";
        }

        int totalPages = surveyView.getResult().getPages().size();

        PageView currentPage = null;
        if (totalPages != 0) {
           Result<PageView> surveyViewResult = surveyEditHelper.getPage(surveyId, pageNumber);
           if (surveyViewResult.isSuccess()) {
               currentPage = surveyViewResult.getResult();
           } else {
               notificationService.addErrorMessage(surveyViewResult.getAdditionalInfo());
               return "redirect:/auth/survey-edit/" + surveyId;
           }
        } else if (pageNumber != 1) {
            return "redirect:/auth/survey-edit/" + surveyId;
        }


        SurveyView survey = surveyView.getResult();
        Boolean canDelete = surveyEditHelper.canUserDeleteSurvey(survey).isSuccess();

        model.addAttribute(ATTRIBUTE_SURVEY, survey);
        model.addAttribute(ATTRIBUTE_TOTAL_PAGES, totalPages);
        model.addAttribute(ATTRIBUTE_CURRENT_PAGE_NUMBER, pageNumber);
        model.addAttribute(ATTRIBUTE_CURRENT_PAGE, currentPage);
        model.addAttribute(ATTRIBUTE_CAN_DELETE, canDelete);
        return "/auth/survey-edit";
    }

    @PostMapping("/delete-page")
    public String deletePage(@RequestParam(value = "survey-id") Long surveyId, @RequestParam(value = "page") int pageNumber, RedirectAttributes redirectAttributes) {
        Result<SurveyView> surveyView = surveyEditHelper.deletePage(surveyId, pageNumber);
        if (!surveyView.isSuccess()) {
            notificationService.addErrorMessage(surveyView.getAdditionalInfo());
        } else if (pageNumber > 1) {
            redirectAttributes.addAttribute(REQUEST_PARAM_PAGE, pageNumber - 1);
        }
        return "redirect:/auth/survey-edit/" + surveyId;
    }

    @PostMapping("/add-initial-page")
    public String addInitialPage(@RequestParam(value = "survey-id") Long surveyId, RedirectAttributes redirectAttributes) {
        Result<SurveyView> surveyView = surveyEditHelper.createPage(surveyId, 1);
        if (!surveyView.isSuccess()) {
            notificationService.addErrorMessage(surveyView.getAdditionalInfo());
        }
        return "redirect:/auth/survey-edit/" + surveyId;
    }

    @PostMapping("/add-page-left")
    public String addPageLeft(@RequestParam(value = "survey-id") Long surveyId, @RequestParam(value = "page") int pageNumber, RedirectAttributes redirectAttributes) {
        Result<SurveyView> surveyView = surveyEditHelper.createPage(surveyId, pageNumber);
        if (surveyView.isSuccess()) {
            redirectAttributes.addAttribute(REQUEST_PARAM_PAGE, pageNumber);
        } else {
            notificationService.addErrorMessage(surveyView.getAdditionalInfo());
        }
        return "redirect:/auth/survey-edit/" + surveyId;
    }

    @PostMapping("/add-page-right")
    public String addPageRight(@RequestParam(value = "survey-id") Long surveyId, @RequestParam(value = "page") int pageNumber, RedirectAttributes redirectAttributes) {
        Result<SurveyView> surveyView = surveyEditHelper.createPage(surveyId, pageNumber + 1);
        if (surveyView.isSuccess()) {
            redirectAttributes.addAttribute(REQUEST_PARAM_PAGE, pageNumber + 1);
        } else {
            notificationService.addErrorMessage(surveyView.getAdditionalInfo());
        }
        return "redirect:/auth/survey-edit/" + surveyId;
    }

    @PostMapping("/save")
    public String savePage(@RequestParam(value = "survey-id") Long surveyId,
                           @RequestParam(value = "page") int pageNumber,
                           @ModelAttribute PageEditView pageEditView,
                           RedirectAttributes redirectAttributes) throws VersionNotLatestException {
        System.out.println("Saving: " + pageEditView);
        Result<SurveyView> surveyView = surveyEditHelper.savePageAndSurveyDetails(surveyId, pageNumber, pageEditView);
        if (surveyView.isSuccess()) {
            redirectAttributes.addAttribute(REQUEST_PARAM_PAGE, pageNumber);
        } else {
            notificationService.addErrorMessage(surveyView.getAdditionalInfo());
        }
        return "redirect:/auth/survey-edit/" + surveyId;
    }

    @PostMapping("/publish")
    public String publish(@RequestParam(value = "survey-id") Long surveyId) {
        //For now survey with only saved changes
        Result<SurveyView> surveyView = surveyEditHelper.getSurveyForEdit(surveyId);
        if(!surveyView.isSuccess()){
            notificationService.addErrorMessage(surveyView.getAdditionalInfo());
            return "redirect:/";
        }

        Result validation = surveyEditHelper.validateBeforePublish(surveyView.getResult());
        if (!validation.isSuccess()) {
            notificationService.addErrorMessage(validation.getAdditionalInfo());
            return "redirect:/auth/survey-edit/" + surveyId;
        }

        Result<SurveyView> surveyViewPublish = surveyEditHelper.publish(surveyId);
        if (!surveyViewPublish.isSuccess()) {
            notificationService.addErrorMessage(surveyViewPublish.getAdditionalInfo());
        } else {
            notificationService.addInfoMessage(MESSAGE_SUCCESSFULLY_PUBLISHED);
        }

        return "redirect:/auth/survey-edit/preview/" + surveyId;
    }

    @GetMapping("/export/survey-{survey-id}.xlsx")
    public ResponseEntity<Resource> getFile(@PathVariable("survey-id") Long surveyId) {
        ByteArrayOutputStream exportedFile = new ByteArrayOutputStream();
        Result<Void> result = surveyViewHelper.exportSurvey(surveyId, exportedFile);
        if (!result.isSuccess()) {
            throw new RuntimeException(result.getAdditionalInfo());
        }

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                .body(new InputStreamResource(new ByteArrayInputStream(exportedFile.toByteArray())));

    }

    @PostMapping("/delete-survey")
    public String deleteSurvey(@RequestParam(value = "survey-id") Long surveyId) {
        Result result = surveyViewHelper.deleteSurvey(surveyId);
        if (!result.isSuccess()) {
            notificationService.addErrorMessage(result.getAdditionalInfo());
            return "/auth/survey-edit/preview/" + surveyId;
        } else{
            notificationService.addInfoMessage(SURVEY_DELETED_SUCCESSFULLY);
            return "redirect:/auth";
        }
    }
}