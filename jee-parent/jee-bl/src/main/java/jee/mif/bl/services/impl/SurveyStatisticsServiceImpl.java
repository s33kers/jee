package jee.mif.bl.services.impl;

import jee.mif.bl.aspect.Monitor;
import jee.mif.bl.repositories.SurveyStatisticsRepository;
import jee.mif.bl.security.JeeAuthenticationManager;
import jee.mif.bl.services.SurveyStatisticsService;
import jee.mif.model.survey.SurveyStatistics;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Created by Tadas.
 */
@Monitor
@Component
public class SurveyStatisticsServiceImpl implements SurveyStatisticsService {

    @Autowired
    private SurveyStatisticsRepository surveyStatisticsRepository;
    @Autowired
    private JeeAuthenticationManager authenticationManager;


    @Override
    public List<SurveyStatistics> findBySurveyId(long surveyId) {
        if (authenticationManager.isUserAuthenticated()) {
            return surveyStatisticsRepository.findBySurveyId(surveyId, authenticationManager.getLoggedUser().getId());
        } else {
            return surveyStatisticsRepository.findBySurveyIdAndIsPublic(surveyId);
        }
    }
}
