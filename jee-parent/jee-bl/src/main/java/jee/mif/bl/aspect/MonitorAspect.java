package jee.mif.bl.aspect;

import jee.mif.bl.properties.AdditionalInfoProperties;
import jee.mif.bl.security.JeeAuthenticationManager;
import jee.mif.bl.security.model.LoggedUser;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Created by Tadas.
 */

@Aspect
@Component
public class MonitorAspect {

   private final Logger logger = LoggerFactory.getLogger("jee.mif.bl.services");

    @Autowired
    private JeeAuthenticationManager jeeAuthenticationManager;
    @Autowired
    private AdditionalInfoProperties properties;

    @Before(value = "@within(jee.mif.bl.aspect.Monitor) || @annotation(jee.mif.bl.aspect.Monitor)")
    public void auditMethod (JoinPoint jp) throws Throwable {
        if (properties.isMonitoringEnabled() && jeeAuthenticationManager.isUserAuthenticated()) {
            LoggedUser user = jeeAuthenticationManager.getLoggedUser();
            logger.info(user.getPrincipalEmail() + " role: " + user.getRoleType().name() + " method: " + jp.getSignature().getDeclaringTypeName() + "." +jp.getSignature().getName());
        }
    }
}