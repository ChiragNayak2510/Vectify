package com.example.Vectify.Service;

import com.example.Vectify.Entity.UserEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2UserAuthority;
import org.springframework.stereotype.Service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;
import java.util.Map;

@Service
public class OAuthUserService implements OAuth2UserService<OAuth2UserRequest, OAuth2User> {

    private static final Logger logger = LoggerFactory.getLogger(OAuthUserService.class);

    @Autowired
    private UserService userService;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2UserService<OAuth2UserRequest, OAuth2User> delegate = new DefaultOAuth2UserService();
        OAuth2User oAuth2User = delegate.loadUser(userRequest);

        Map<String, Object> attributes = oAuth2User.getAttributes();
        String username = null;
        String email = null;
        String userType = userRequest.getClientRegistration().getRegistrationId();

        if ("google".equals(userType)) {
            username = (String) attributes.get("name");
            email = (String) attributes.get("email");
        } else if ("github".equals(userType)) {
            username = (String) attributes.get("login");
            email = (String) attributes.get("email");
        }

        System.out.println(username+" "+email);
        if ("google".equals(userType) && (email == null || email.isEmpty())) {
            logger.error("Email is mandatory for Google users");
            throw new OAuth2AuthenticationException("Email is required for Google users.");
        } else if ("github".equals(userType) && (username == null || username.isEmpty())) {
            logger.error("Username is mandatory for GitHub users");
            throw new OAuth2AuthenticationException("Username is required for GitHub users.");
        }

        UserEntity user = new UserEntity();
        user.setUsername(username);
        user.setEmail(email);
        user.setUserType(userType);
        logger.info("User email: {}", user.getEmail());
        logger.info("User type: {}", user.getUserType());

        userService.addOrUpdateUser(user);

        return new DefaultOAuth2User(
                Collections.singleton(new OAuth2UserAuthority(attributes)),
                attributes,
                "github".equals(userRequest.getClientRegistration().getRegistrationId()) ? "login" : "email"
        );
    }
}
