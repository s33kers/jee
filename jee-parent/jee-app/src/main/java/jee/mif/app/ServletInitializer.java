package jee.mif.app;

import jee.mif.bl.utils.UrlUtils;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.boot.web.support.SpringBootServletInitializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.context.support.HttpRequestHandlerServlet;

/**
 * @author Tadas
 */
@Configuration
@EnableAutoConfiguration
@ComponentScan({ "jee.mif.app" })
public class ServletInitializer extends SpringBootServletInitializer {

    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
        setRegisterErrorPageFilter(false);
        return application.sources(JeeApplication.class);
    }

    @Bean
    public ServletRegistrationBean urlRegistrationServletBean() {
        ServletRegistrationBean statusServlet = new ServletRegistrationBean(new HttpRequestHandlerServlet(), UrlUtils.NEW_USER_REGISTRATION + "*");
        statusServlet.setName("urlRegistrationServlet");
        return statusServlet;
    }

    @Bean
    public ServletRegistrationBean urlPasswordResetServletBean() {
        ServletRegistrationBean statusServlet = new ServletRegistrationBean(new HttpRequestHandlerServlet(), UrlUtils.PASSWORD_RESET + "*");
        statusServlet.setName("urlPasswordResetServlet");
        return statusServlet;
    }

}