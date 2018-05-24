package jee.mif.app;

import jee.mif.bl.utils.UrlUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.servlet.ServletListenerRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.annotation.web.configurers.FormLoginConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.security.core.session.SessionRegistryImpl;
import org.springframework.security.web.session.HttpSessionEventPublisher;

/**
 * Created by Tadas.
 */
@Configuration
@EnableWebSecurity
public class SpringSecurityConfiguration extends WebSecurityConfigurerAdapter {

    @Autowired
    private AuthenticationProvider authenticationProvider;

    @Autowired
    public void configureGlobalSecurity(AuthenticationManagerBuilder auth) throws Exception {
        auth.authenticationProvider(authenticationProvider);
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED)
                .sessionFixation()
                .migrateSession()
                .maximumSessions(100)
                .expiredUrl(UrlUtils.LOGIN)
                .sessionRegistry(sessionRegistry());
        http.authorizeRequests().antMatchers(UrlUtils.PERMIT_ALL_URLS).permitAll();
        http.authorizeRequests().antMatchers(UrlUtils.AUTH_END_POINT + "**").hasAnyRole("USER", "ADMIN");
        http.authorizeRequests().antMatchers(UrlUtils.ADMIN_END_POINT + "**").hasRole("ADMIN");
        http.authorizeRequests().and().logout().logoutUrl(UrlUtils.LOGOUT).logoutSuccessUrl(UrlUtils.LOGIN);

        FormLoginConfigurer<HttpSecurity> formLogin = http.formLogin();
        formLogin.loginPage(UrlUtils.LOGIN);
        formLogin.usernameParameter("email");
        formLogin.passwordParameter("password");
        formLogin.defaultSuccessUrl(UrlUtils.AUTH_END_POINT).failureUrl(UrlUtils.LOGIN);

        http.exceptionHandling().accessDeniedPage(UrlUtils.ACCESS_DENIED);
        http.headers().frameOptions().disable();
        http.csrf().disable();
    }

    @Bean
    public SessionRegistry sessionRegistry() {
        return new SessionRegistryImpl();
    }

    @Bean
    public static ServletListenerRegistrationBean httpSessionEventPublisher() {
        return new ServletListenerRegistrationBean(new HttpSessionEventPublisher());
    }
}
