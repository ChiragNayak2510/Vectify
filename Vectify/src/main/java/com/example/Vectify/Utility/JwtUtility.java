package com.example.Vectify.Utility;

import com.example.Vectify.Entity.UserEntity;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.Map;
import java.util.function.Function;

@Component
public class JwtUtility {

    private final SecretKey secretKey;
    @Getter
    private final long jwtExpiration;

    @Autowired
    public JwtUtility(@Value("${JWT_SECRET_KEY}") String secret,
                      @Value("${JWT_EXPIRATION}") long jwtExpiration) {
        this.secretKey = Keys.hmacShaKeyFor(secret.getBytes());
        this.jwtExpiration = jwtExpiration;
    }

    // Generate a JWT token
    public String generateToken(Map<String, Object> claims, String subject) {
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(subject)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + jwtExpiration))
                .signWith(secretKey)
                .compact();
    }
    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    public String extractProvider(String token) {
        return extractClaim(token, claims -> claims.get("provider", String.class));
    }

    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    public boolean isValidToken(String token, UserEntity userEntity) {
        final String extractedIdentifier = extractUsername(token);
        boolean isValid = false;

        if (userEntity != null) {
            isValid = ("github".equals(userEntity.getUserType()) && extractedIdentifier.equals(userEntity.getUsername())) ||
                    ("google".equals(userEntity.getUserType()) && extractedIdentifier.equals(userEntity.getEmail())) &&
                            !isTokenExpired(token);
        }
        return isValid;
    }

    private boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    public Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    private Claims extractAllClaims(String token){
        return Jwts
                .parser()
                .setSigningKey(secretKey)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }
}
