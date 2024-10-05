package com.example.Vectify.Utility;

import com.example.Vectify.Utility.JwtUtility;
import jakarta.servlet.http.Cookie;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;

@Component
public class CustomAuthenticationSuccessHandler implements AuthenticationSuccessHandler {

    @Autowired
    private JwtUtility jwtUtility;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException {
        OAuth2AuthenticationToken oauthToken = (OAuth2AuthenticationToken) authentication;
        String provider = oauthToken.getAuthorizedClientRegistrationId();
        String identifier;

        if ("github".equals(provider)) {
            identifier = oauthToken.getPrincipal().getAttribute("login");
        } else if ("google".equals(provider)) {
            identifier = oauthToken.getPrincipal().getAttribute("email");
        } else {
            throw new IllegalStateException("Unsupported provider: " + provider);
        }

        String jwtToken = jwtUtility.generateToken(Map.of("provider", provider), identifier);

        Cookie jwtCookie = new Cookie("jwt", jwtToken);
        jwtCookie.setHttpOnly(true);
        jwtCookie.setMaxAge((int) jwtUtility.getJwtExpiration() / 1000);
        response.addCookie(jwtCookie);

        response.sendRedirect("/");
    }
}
