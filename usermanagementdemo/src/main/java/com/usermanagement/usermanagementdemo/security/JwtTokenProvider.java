package com.usermanagement.usermanagementdemo.security;

import io.jsonwebtoken.*;

import java.nio.charset.StandardCharsets;

import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Component
public class JwtTokenProvider {

    private Key jwtSecret;
    private long JwtExpirationInMs = 36000000;

    @PostConstruct
    public void init() {
        // Generate a secure key for HS512
        this.jwtSecret = Keys.secretKeyFor(SignatureAlgorithm.HS512);
    }

    public String genrateToken(String username) {
        Map<String, Object> claims = new HashMap<>();
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(username)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + JwtExpirationInMs))
                .signWith(SignatureAlgorithm.HS512, jwtSecret)
                .compact();
    }

    public String getUsernameFromJWT(String token) {
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(jwtSecret)
                .build()
                .parseClaimsJws(token)
                .getBody();

        return claims.getSubject();
    }

    public boolean validateToken(String token) {
        try {
            Jwts.parser().setSigningKey(jwtSecret).parseClaimsJws(token);
            return true;
        } catch (SignatureException ex) {
            throw new RuntimeException("Invalid JWT Signature");
        } catch (MalformedJwtException ex) {
            throw new RuntimeException("Invalid JWT Token");
        } catch (ExpiredJwtException ex) {
            throw new RuntimeException("Expired JWT Token");
        } catch (UnsupportedJwtException ex) {
            throw new RuntimeException("Unsupported JWT Token");
        } catch (IllegalArgumentException ex) {
            throw new RuntimeException("JWT Claims string is empty.");
        }
    }
}
