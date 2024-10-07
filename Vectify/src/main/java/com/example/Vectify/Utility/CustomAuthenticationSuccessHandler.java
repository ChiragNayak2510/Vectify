package com.example.Vectify.Utility;

import jakarta.servlet.http.Cookie;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.Map;

@Component
public class CustomAuthenticationSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    @Autowired
    private JwtUtility jwtUtility;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException {
        System.out.println("Successfully logged in");

        // Retrieve provider and identifier from OAuth2AuthenticationToken
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

        // Generate JWT token using JwtUtility
        String jwtToken = jwtUtility.generateToken(Map.of("provider", provider), identifier);
        System.out.println("Successfully logged in");
        // Add JWT token to HttpOnly cookie
        Cookie jwtCookie = new Cookie("jwt", jwtToken);
        jwtCookie.setHttpOnly(true);
        jwtCookie.setMaxAge((int) jwtUtility.getJwtExpiration() / 1000); // Set expiration
        response.addCookie(jwtCookie);
        // Redirect to success URL
        super.onAuthenticationSuccess(request, response, authentication);
    }
}
