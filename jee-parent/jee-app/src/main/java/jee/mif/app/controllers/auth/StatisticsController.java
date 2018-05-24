package jee.mif.app.controllers.auth;

import jee.mif.app.helper.SurveyStatisticsHelper;
import jee.mif.app.helper.SurveyViewHelper;
import jee.mif.app.model.SurveyStatisticsView;
import jee.mif.app.model.SurveyView;
import jee.mif.bl.model.Result;
import jee.mif.bl.utils.UrlUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

/**
 * Created by Mindaugas on 2017-05-17.
 */
@Controller
@RequestMapping("/statistics")
public class StatisticsController {

    private static final String ATTRIBUTE_STATS = "stats";
    private static final String ATTRIBUTE_SURVEY = "survey";
    private static final String ATTRIBUTE_PREVIEW_URL = "previewUrl";

    @Autowired
    private SurveyStatisticsHelper statistics;

    @Autowired
    private SurveyViewHelper surveyViewHelper;

    @GetMapping("/{id}")
    public String index(@PathVariable("id") Long surveyId, ModelMap model) {
        List<SurveyStatisticsView> stats = statistics.findBySurveyId(surveyId);
        Result<SurveyView> surveyView = surveyViewHelper.find(surveyId);

        String previewUrl = UrlUtils.SURVEY_PREVIEW + surveyId;

        model.addAttribute(ATTRIBUTE_STATS, stats);
        model.addAttribute(ATTRIBUTE_SURVEY, surveyView.getResult());
        model.addAttribute(ATTRIBUTE_PREVIEW_URL, previewUrl);

        return "statistics";
    }
}
