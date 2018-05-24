package jee.mif.app;

import jee.mif.app.helper.SurveyStatisticsHelper;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.junit4.SpringRunner;

import javax.transaction.Transactional;

/**
 * Created by Tadas.
 */

@RunWith(SpringRunner.class)
@SpringBootTest
@Transactional(Transactional.TxType.REQUIRES_NEW)
@Rollback(false)
public class SurveyStatisticsTest {

    @Autowired
    private SurveyStatisticsHelper surveyStatisticsHelper;

    @Test
    public void findStatistics() {
        Assert.assertTrue(surveyStatisticsHelper.findBySurveyId(62).isEmpty());//non public must be empty
        Assert.assertFalse(surveyStatisticsHelper.findBySurveyId(7).isEmpty());//public must be with results
    }
}
