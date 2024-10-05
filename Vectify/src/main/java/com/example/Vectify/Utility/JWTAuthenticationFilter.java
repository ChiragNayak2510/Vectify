package com.example.Vectify.Utility;

import com.example.Vectify.Entity.UserEntity;
import com.example.Vectify.Service.UserService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class JWTAuthenticationFilter extends OncePerRequestFilter {

    @Autowired
    private JwtUtility jwtUtility;

    @Autowired
    private UserService userService;

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull FilterChain filterChain) throws ServletException, IOException {
        final String authHeader = request.getHeader("Authorization");
        final String jwt;
        final String identifier;
        final String provider;

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        jwt = authHeader.substring(7);

        // Extract username (GitHub) or email (Google) from JWT token
        identifier = jwtUtility.extractUsername(jwt);  // Extracts username or email based on your logic
        provider = jwtUtility.extractProvider(jwt);     // Extracts the provider from claims

        if (identifier != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            // Load UserEntity based on the provider (either by email or username)
            UserEntity userEntity;
            if ("github".equals(provider)) {
                userEntity = (UserEntity) this.userService.getUserByUsername(identifier).orElse(null); // Load by GitHub username
            } else if ("google".equals(provider)) {
                userEntity = (UserEntity) this.userService.getUserByEmail(identifier).orElse(null); // Load by Google email
            } else {
                filterChain.doFilter(request, response);
                return;
            }

            // Validate JWT token with UserEntity
            if (jwtUtility.isValidToken(jwt, userEntity)) {
                UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                        userEntity, null
                );
                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));


                SecurityContextHolder.getContext().setAuthentication(authToken);
            }
        }

        // Continue the filter chain
        filterChain.doFilter(request, response);
    }
}
