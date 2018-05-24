package jee.mif.app;

import jee.mif.app.helper.SurveyAnsweringHelper;
import jee.mif.app.model.ChooseQuestionOptionView;
import jee.mif.app.model.PageView;
import jee.mif.app.model.QuestionAnswerView;
import jee.mif.app.model.QuestionView;
import jee.mif.app.model.SurveyAnswerView;
import jee.mif.bl.model.Result;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.junit4.SpringRunner;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Created by Tadas.
 */

@RunWith(SpringRunner.class)
@SpringBootTest
@Transactional(Transactional.TxType.REQUIRES_NEW)
@Rollback(false)
public class SurveyAnsweringTest {

    @Autowired
    private SurveyAnsweringHelper surveyAnsweringHelper;

    private SurveyAnswerView createSurveyAnswer() {
        SurveyAnswerView surveyAnswerView = surveyAnsweringHelper.createAnswering("f7df42e9-9653-429b-93e8-723604911ef9").getResult();

        for (PageView pageView : surveyAnswerView.getSurveyView().getPages()) {
            for (QuestionView questionView : pageView.getQuestions()) {
                QuestionAnswerView questionAnswerView = new QuestionAnswerView();
                switch (questionView.getQuestionType()) {
                    case TEXT:
                        questionAnswerView.setAnswer(LocalDateTime.now().toString());
                        break;
                    case SCALE:
                        questionAnswerView.setAnswer(String.valueOf(ThreadLocalRandom.current().nextDouble(questionView.getMinValue().doubleValue(), questionView.getMaxValue().doubleValue())));
                        break;
                    case CHECKBOX:
                        List<ChooseQuestionOptionView> optionViews = questionView.getChooseQuestionOptions();
                        break;
                    case MULTIPLECHOICE:
                        List<ChooseQuestionOptionView> optionViews2 = questionView.getChooseQuestionOptions();
                        Set<ChooseQuestionOptionView> multipleAnswers = new HashSet<>();
                        for(int i = 0; i < optionViews2.size(); i++) {
                            if (ThreadLocalRandom.current().nextDouble( 1, 3) < 2) {
                                multipleAnswers.add(optionViews2.get(i));
                            }
                        }
                        multipleAnswers.add(optionViews2.get(0));
                        break;
                    default:
                        Assert.fail();
                }
                questionView.setAnswerView(questionAnswerView);
            }
        }

        return surveyAnswerView;
    }

    @Test
    public void finishSurveyAnswering() {
        SurveyAnswerView surveyAnswerView = createSurveyAnswer();
        Result<SurveyAnswerView> result = surveyAnsweringHelper.finishAnswering(surveyAnswerView);
        Assert.assertTrue(result.isSuccess());
    }

    @Test
    public void saveSurveyAnswering() {
        SurveyAnswerView surveyAnswerView = createSurveyAnswer();
        Result<SurveyAnswerView> result = surveyAnsweringHelper.saveAnswering(surveyAnswerView, "tadas235@gmail.com");
        Assert.assertTrue(result.isSuccess());
    }

}
