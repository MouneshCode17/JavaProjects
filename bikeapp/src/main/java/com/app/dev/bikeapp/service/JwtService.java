package com.app.dev.bikeapp.service;

import java.nio.charset.StandardCharsets;
import java.util.Date;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.app.dev.bikeapp.entity.User;

import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

@Service
public class JwtService{
    
    private final SecretKey signingKey;

    public JwtService(@Value("${jwt.secret}")String secret){

        this.signingKey = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    }
    
    public String generateToken(User user) {
    return Jwts.builder()
            .subject(user.getId().toString())
            .claim("role", user.getRole().name())
            .issuedAt(new Date())
            .expiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60))
            .signWith(signingKey)
            .compact();
    }
    
    public String extractUserId(String token) {
    return Jwts.parser()
            .verifyWith(signingKey)
            .build()
            .parseSignedClaims(token)
            .getPayload()
            .getSubject();
}
    
public boolean isTokenValid(String token) {
    try {
        Jwts.parser()
                .verifyWith(signingKey)
                .build()
                .parseSignedClaims(token);

        return true;
    } catch (JwtException | IllegalArgumentException e) {
        return false;
    }
}
}