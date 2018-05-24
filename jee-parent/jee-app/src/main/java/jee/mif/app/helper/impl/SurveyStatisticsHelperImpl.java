package jee.mif.app.helper.impl;

import jee.mif.app.helper.SurveyStatisticsHelper;
import jee.mif.app.model.QuestionStatisticsView;
import jee.mif.app.model.SurveyStatisticsView;
import jee.mif.bl.services.SurveyStatisticsService;
import jee.mif.model.survey.QuestionStatistics;
import jee.mif.model.survey.SurveyStatistics;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.TreeSet;
import java.util.stream.Collectors;

/**
 * Created by Tadas.
 */

@Component
public class SurveyStatisticsHelperImpl implements SurveyStatisticsHelper {

    @Autowired
    private SurveyStatisticsService surveyStatisticsService;

    @Override
    public List<SurveyStatisticsView> findBySurveyId(long surveyId) {
        return surveyStatisticsService.findBySurveyId(surveyId).stream().map(this::buildSurveyStatisticsView).collect(Collectors.toList());
    }

    private SurveyStatisticsView buildSurveyStatisticsView (SurveyStatistics surveyStatistics) {
        SurveyStatisticsView view = new SurveyStatisticsView();
        view.setQuestion(surveyStatistics.getQuestion());
        view.setQuestionId(surveyStatistics.getQuestionId());
        view.setAnswerCount(surveyStatistics.getAnswerCount());
        view.setQuestionType(surveyStatistics.getQuestionType());
        view.setSurveyId(surveyStatistics.getSurveyId());
        view.setQuestionStatisticsViewList(surveyStatistics.getQuestionStatistics().stream().map(this::buildQuestionStatisticsView).collect(Collectors.toList()));
        view.setResultsPublic(surveyStatistics.getResultsPublic());
        view.setUserId(surveyStatistics.getUserId());

        calculateAdditionalData(view);
        return view;
    }

    private QuestionStatisticsView buildQuestionStatisticsView(QuestionStatistics questionStatistics) {
        QuestionStatisticsView view = new QuestionStatisticsView();
        view.setAnswer(questionStatistics.getAnswer());
        view.setAnswerCount(questionStatistics.getAnswerCount());
        view.setQuestionId(questionStatistics.getQuestionId());
        return view;
    }


    private void calculateAdditionalData(SurveyStatisticsView view) {
        switch (view.getQuestionType()) {
            case SCALE:
                if(view.getScaleValueArray().length != 0){
                    view.setMode(getMode(view.getScaleValueArray()));
                    view.setMean(round(getMean(view.getScaleValueArray())));
                    view.setMedian(round(getMedian(view.getScaleValueArray())));
                }
                break;
            case CHECKBOX:
            case MULTIPLECHOICE:
                for (QuestionStatisticsView questionStatisticsView : view.getQuestionStatisticsViewList()) {
                    questionStatisticsView.setRate(questionStatisticsView.getAnswerCount()*100/view.getAnswerCount());
                }
                break;
        }

    }

    private static Double round(Double value) {
        BigDecimal bd = new BigDecimal(value);
        bd = bd.setScale(2, RoundingMode.HALF_UP);
        return bd.doubleValue();
    }

    private List<Double> getMode(Double[] scaleValueArray) {
        List<Double> list = new ArrayList<>();
        TreeSet<Double> tree = new TreeSet<>();
        List<Double> modes = new ArrayList<>();

        for (Double aScaleValueArray : scaleValueArray) {
            list.add(aScaleValueArray);
            tree.add(aScaleValueArray);
        }

        int highmark = 0;
        for (Double x: tree) {
            int freq = Collections.frequency(list, x);
            if (freq == highmark) {
                modes.add(x);
            }
            if (freq > highmark) {
                modes.clear();
                modes.add(x);
                highmark = freq;
            }
        }
        return modes;
    }

    private Double getMedian(Double[] scaleValueArray) {
        if (scaleValueArray.length == 0) {
            return Double.NaN;
        }

        int middle = scaleValueArray.length/2;
        if (scaleValueArray.length%2 == 1) {
            return scaleValueArray[middle];
        } else {
            return (scaleValueArray[middle-1] + scaleValueArray[middle]) / 2.0;
        }
    }

    private Double getMean(Double[] scaleValueArray) {
        double sum = 0;
        for (int i = 0; i < scaleValueArray.length; i++) {
            sum += scaleValueArray[i];
        }
        return sum / scaleValueArray.length;
    }
}
