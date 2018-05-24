package jee.mif.app.servlet;

import jee.mif.bl.security.JeeAuthenticationManager;
import jee.mif.bl.services.NotificationService;
import jee.mif.bl.utils.UrlUtils;
import jee.mif.model.user.AuthenticationType;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.stereotype.Component;
import org.springframework.web.HttpRequestHandler;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Created by Tadas.
 */

@Component("urlPasswordResetServlet")
public class UrlPasswordResetServlet implements HttpRequestHandler {

    @Autowired
    private JeeAuthenticationManager jeeAuthenticationManager;
    @Autowired
    private NotificationService notificationService;

    @Override
    public void handleRequest(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        if (jeeAuthenticationManager.isUserAuthenticated()) {
            response.sendRedirect(UrlUtils.AUTH_END_POINT);
            return;
        }
        String path = request.getPathInfo();
        if(path != null) {
            String token = path.substring(1);
            if (StringUtils.isNotBlank(token)) {
                try {
                    jeeAuthenticationManager.authenticate(token, AuthenticationType.PASSWORD_RESET);
                    response.sendRedirect(UrlUtils.AUTH_PASSWORD_RESET);
                } catch (AuthenticationCredentialsNotFoundException e) {
                    notificationService.addErrorMessage(e.getMessage());
                    response.sendRedirect(UrlUtils.LOGIN);
                }

            } else {
                response.sendError(HttpServletResponse.SC_BAD_REQUEST);
            }
        } else {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST);
        }
    }
}
