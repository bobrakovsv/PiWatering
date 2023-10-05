package ru.bserg.watering.config;

import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;

import java.util.ArrayList;

/**
 * Примитивная basic-аутентификация
 */
@Component
public class AuthProvider implements AuthenticationProvider {

    @Override
    public Authentication authenticate(Authentication auth) throws AuthenticationException {
        String name = auth.getName();
        String password = auth.getCredentials().toString();

        // Для basic-аутентификации запросов
        if ("wtr".equals(name) && "pw".equals(password)) {
            return new UsernamePasswordAuthenticationToken(name, password, null);
        }
        ArrayList<GrantedAuthority> gal = new ArrayList<>();
        return new UsernamePasswordAuthenticationToken(name.toUpperCase(), password, gal);
    }


    @Override
    public boolean supports(Class<?> authentication) {
        return authentication.equals(
                UsernamePasswordAuthenticationToken.class);
    }

}

