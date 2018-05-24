package jee.mif.bl.services.impl;

import jee.mif.bl.aspect.Monitor;
import jee.mif.bl.model.ActionEnum;
import jee.mif.bl.model.Result;
import jee.mif.bl.properties.AdditionalInfoProperties;
import jee.mif.bl.repositories.QuestionAnswerRepository;
import jee.mif.bl.repositories.SurveyAnswerRepository;
import jee.mif.bl.services.EmailSender;
import jee.mif.bl.services.SurveyAnswerService;
import jee.mif.bl.utils.UrlUtils;
import jee.mif.model.survey.QuestionAnswer;
import jee.mif.model.survey.SurveyAnswer;
import org.apache.commons.lang3.BooleanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import javax.mail.MessagingException;
import javax.transaction.Transactional;
import java.util.Set;

/**
 * Created by Tadas.
 */
@Monitor
@Component
@Transactional
public class SurveyAnswerServiceImpl implements SurveyAnswerService {

    private static final String EMAIL_SUBJECT = "Saved survey";
    private static final String EMAIL_TAMPLATE_NAME = "email-save-survey";

    @Autowired
    private SurveyAnswerRepository surveyAnswerRepository;
    @Autowired
    private QuestionAnswerRepository questionAnswerRepository;
    @Autowired
    private EmailSender emailSender;
    @Autowired
    private AdditionalInfoProperties properties;
    @Autowired
    private TemplateEngine templateEngine;

    @Override
    public Result<SurveyAnswer> getById(Long surveyAnswerId) {
        return null;
    }

    @Override
    public Result<SurveyAnswer> continueAnswering(String answerToken) {
        SurveyAnswer survey = surveyAnswerRepository.findByAnswerToken(answerToken);
        if (survey == null) {
            return new Result<>(ActionEnum.FAILURE, "No survey answering has been found");
        } else if (BooleanUtils.isTrue(survey.getCompleted())) {
            return new Result<>(ActionEnum.FAILURE, "Survey is already completed");
        }

        return new Result<>(survey, ActionEnum.SUCCESS);
    }

    @Override
    public void deleteById(Long id) {
        surveyAnswerRepository.delete(id);
    }

    @Override
    public void saveAnswers(SurveyAnswer surveyAnswer) {
        surveyAnswerRepository.save(surveyAnswer);
    }

    @Override
    public Result saveSurveyAnswersAndSendEmail(SurveyAnswer surveyAnswer, String email) {
        surveyAnswerRepository.save(surveyAnswer);
        try {
            Context context = new Context();
            context.setVariable("url", properties.getServerUrl() + UrlUtils.SURVEY_CONTINUE_ANSWERING + surveyAnswer.getSavedUrl());
            emailSender.sendMail(email, EMAIL_SUBJECT, templateEngine.process(EMAIL_TAMPLATE_NAME, context));
        } catch (MessagingException e) {
            return new Result(ActionEnum.FAILURE, e.getMessage());
        }

        return new Result<>(surveyAnswer, ActionEnum.SUCCESS);
    }
}
