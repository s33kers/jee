package jee.mif.bl.security.impl;

import jee.mif.bl.security.JeeAuthenticationManager;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Component;

/**
 * Created by Tadas.
 */

@Component("jeeAuthenticationProvider")
public class JeeAuthenticationProvider implements AuthenticationProvider {

    @Autowired
    private JeeAuthenticationManager jeeAuthenticationManager;

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        if (!supports(authentication.getClass())) {
            return null;
        }
        String password = StringUtils.trim(authentication.getCredentials().toString());
        String login = StringUtils.trim(authentication.getName());
        return jeeAuthenticationManager.getAuthentication(password, login);
    }


    @Override
    public boolean supports(Class<?> authentication) {
        return authentication.equals(UsernamePasswordAuthenticationToken.class);
    }
}
