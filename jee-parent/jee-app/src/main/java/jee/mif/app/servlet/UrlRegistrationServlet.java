package jee.mif.app.servlet;

import jee.mif.bl.security.JeeAuthenticationManager;
import jee.mif.bl.utils.UrlUtils;
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
 * @author Tadas
 */

@Component("urlRegistrationServlet")
public class UrlRegistrationServlet implements HttpRequestHandler {

    @Override
    public void handleRequest(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String path = request.getPathInfo();
        if(path != null) {
            String token = path.substring(1);
            if (StringUtils.isNotBlank(token)) {
                response.sendRedirect(UrlUtils.FINISH_REGISTRATION + token);
            } else {
                response.sendError(HttpServletResponse.SC_BAD_REQUEST);
            }
        } else {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST);
        }
    }
}
