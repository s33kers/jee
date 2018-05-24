package jee.mif.bl.services.impl;

import jee.mif.bl.aspect.Monitor;
import jee.mif.bl.model.ActionEnum;
import jee.mif.bl.model.Result;
import jee.mif.bl.repositories.SurveyRepository;
import jee.mif.bl.security.JeeAuthenticationManager;
import jee.mif.bl.security.model.LoggedUser;
import jee.mif.bl.services.SurveyExportService;
import jee.mif.bl.services.SurveyService;
import jee.mif.bl.services.UserService;
import jee.mif.model.survey.Page;
import jee.mif.model.survey.Survey;
import jee.mif.model.user.JeeUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.OutputStream;
import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Monitor
@Component
@Transactional
public class SurveyServiceImpl implements SurveyService {

    private static final String ERROR_SURVEY_NOT_FOUND = "Survey does not exist or you do not have permission to access it";
    private static final String ERROR_SURVEY_NOT_EDITABLE = "Survey is not editable";
    private static final String ERROR_SURVEY_ALREADY_PUBLISHED = "Survey is already published";
    private static final String ERROR_PAGE_OUT_OF_BOUNDS = "Page number is out of bounds";
    private static final String ERROR_SURVEY_NO_LONGER_VALID_TO_FILL = "Survey is no longer valid to fill";

    @Autowired
    private SurveyRepository surveyRepository;

    @Autowired
    private JeeAuthenticationManager authenticationManager;

    @Autowired
    private UserService userService;

    @Autowired
    private SurveyExportService surveyExportService;

    @Override
    public Result<Survey> createNew() {
        LoggedUser loggedUser = authenticationManager.getLoggedUser();

        Result<JeeUser> user = userService.getJeeUserById(loggedUser.getId());
        if(!user.isSuccess()) {
            return new Result<>(ActionEnum.FAILURE, user.getAdditionalInfo());
        }

        Survey survey = new Survey();
        survey.setCreationDate(LocalDateTime.now());
        survey.setUser(user.getResult());
        survey.getPages().add(new Page());

        Survey savedSurvey = surveyRepository.save(survey);
        return new Result<>(savedSurvey, ActionEnum.SUCCESS);
    }

    @Override
    public Result<Page> getPage(Long surveyId, int pageNumber) {
        Result<Survey> surveyResult = getSurvey(surveyId);
        if (!surveyResult.isSuccess()) {
            return new Result<>(ActionEnum.FAILURE, surveyResult.getAdditionalInfo());
        }

        Survey survey = surveyResult.getResult();
        if (pageNumber < 1 || pageNumber > survey.getPages().size() + 1) {
            return new Result<>(ActionEnum.FAILURE, ERROR_PAGE_OUT_OF_BOUNDS);
        }

        List<Page> pages = survey.getPages();
        Page page = pages.get(pageNumber - 1);
        return new Result<>(page, ActionEnum.SUCCESS);
    }

    @Override
    public Result<Survey> createPage(Long surveyId, int pageNumber) {
        Result<Survey> surveyResult = getSurvey(surveyId);
        if (!surveyResult.isSuccess()) {
            return new Result<>(ActionEnum.FAILURE, surveyResult.getAdditionalInfo());
        }

        Survey survey = surveyResult.getResult();
        if (pageNumber < 1 || pageNumber > survey.getPages().size() + 1) {
            return new Result<>(ActionEnum.FAILURE, ERROR_PAGE_OUT_OF_BOUNDS);
        }

        Page createdPage = new Page();

        List<Page> pages = survey.getPages();
        pages.add(pageNumber - 1, createdPage);
        survey.setPages(pages);

        Survey savedSurvey = surveyRepository.save(survey);
        return new Result<>(savedSurvey, ActionEnum.SUCCESS);
    }

    @Override
    public Result<Survey> deletePage(Long surveyId, int pageNumber) {
        Result<Survey> surveyResult = getSurvey(surveyId);
        if (!surveyResult.isSuccess()) {
            return new Result<>(ActionEnum.FAILURE, surveyResult.getAdditionalInfo());
        }

        Survey survey = surveyResult.getResult();
        if (pageNumber < 1 || pageNumber > survey.getPages().size()) {
            return new Result<>(ActionEnum.FAILURE, ERROR_PAGE_OUT_OF_BOUNDS);
        }

        List<Page> pages = survey.getPages();
        pages.remove(pageNumber - 1);
        survey.setPages(pages);

        Survey savedSurvey = surveyRepository.save(survey);
        return new Result<>(savedSurvey, ActionEnum.SUCCESS);
    }

    @Override
    public Result<Survey> getSurvey(Long id) {
        Survey survey = surveyRepository.findOne(id);
        if (survey == null) {
            return new Result<>(ActionEnum.FAILURE, ERROR_SURVEY_NOT_FOUND);
        }
        return new Result<>(survey, ActionEnum.SUCCESS);
    }

    @Override
    public Result<Survey> getSurveyForImportDetails(Long id) {
        LoggedUser loggedUser = authenticationManager.getLoggedUser();

        Survey survey = surveyRepository.findSurveyForImportDetails(id, loggedUser.getId());
        if (survey == null) {
            return new Result<>(ActionEnum.FAILURE, ERROR_SURVEY_NOT_FOUND);
        }
        return new Result<>(survey, ActionEnum.SUCCESS);
    }

    @Override
    public Result<Survey> getSurveyForEdit(Long id) {
        Result<Survey> survey = getSurvey(id);
        if (!survey.isSuccess()) {
            return new Result<>(ActionEnum.FAILURE, survey.getAdditionalInfo());
        }

        if (survey.getResult().getDraft()) {
            return survey;
        } else {
            return new Result<>(ActionEnum.FAILURE, ERROR_SURVEY_NOT_EDITABLE);
        }
    }

    @Override
    public Result<List<Survey>> getAllSurveys() {
        List<Survey> surveys = surveyRepository.findAllByUser(authenticationManager.getLoggedUser().getId(), authenticationManager.isAuthenticatedAdmin());
        return new Result<>(surveys, ActionEnum.SUCCESS);
    }

    @Override
    public Result<Survey> createAnswering(String surveyToken) {
        Survey survey = surveyRepository.findByUrlUuid(surveyToken);
        if (survey == null) {
            return new Result<>(ActionEnum.FAILURE, ERROR_SURVEY_NOT_FOUND);
        }

        if (survey.getValidDate() != null && LocalDateTime.now().isAfter(survey.getValidDate())) {
            return new Result<>(ActionEnum.FAILURE, ERROR_SURVEY_NO_LONGER_VALID_TO_FILL);
        }

        return new Result<>(survey, ActionEnum.SUCCESS);
    }

    @Override
    public Result<Survey> save(Survey survey) {
        Survey savedSurvey = surveyRepository.save(survey);
        return new Result<>(savedSurvey, ActionEnum.SUCCESS);
    }

    @Override
    public Result<Survey> publish(Long id) {
        Result<Survey> surveyResult = getSurvey(id);
        if (!surveyResult.isSuccess()) {
            return new Result<>(ActionEnum.FAILURE, surveyResult.getAdditionalInfo());
        }

        Survey survey = surveyResult.getResult();
        if (!survey.getDraft()) {
            return new Result<>(ActionEnum.FAILURE, ERROR_SURVEY_ALREADY_PUBLISHED);
        }

        survey.setCreationDate(LocalDateTime.now());
        survey.setUrlToken(UUID.randomUUID().toString());
        survey.setDraft(false);

        return save(survey);
    }

    @Override
    public Result deleteSurvey(Long surveyId) {
        if (authenticationManager.isAuthenticatedAdmin()) {
            surveyRepository.delete(surveyId);
            return new Result(ActionEnum.SUCCESS);
        } else if (authenticationManager.isUserAuthenticated()) {
            LoggedUser loggedUser = authenticationManager.getLoggedUser();
            surveyRepository.deleteBySurveyIdAndUserId(surveyId, loggedUser.getId());
            return new Result(ActionEnum.SUCCESS);
        }

        return new Result(ActionEnum.FAILURE, "Survey doesn't exist or you have no permissions to delete");
    }

    @Override
    public Result<Void> exportSurvey(Long surveyId, OutputStream outputStream) {
        Result<Survey> survey = getSurvey(surveyId);
        if (!survey.isSuccess()) {
            return new Result<>(ActionEnum.FAILURE, survey.getAdditionalInfo());
        }
        return surveyExportService.exportXlsx(survey.getResult(), outputStream);
    }
}
