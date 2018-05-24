package jee.mif.app;

import jee.mif.app.helper.SurveyViewHelper;
import jee.mif.bl.security.impl.JeeAuthenticationProvider;
import jee.mif.bl.services.UserService;
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
public class SurveyManagmentTest {

    @Autowired
    private SurveyViewHelper surveyViewHelper;
    @Autowired
    private UserService userService;
    @Autowired
    private JeeAuthenticationProvider jeeAuthenticationProvider;

    @Test
    public void deleteSurveyById() {
        surveyViewHelper.deleteSurvey(62L);
    }
}
