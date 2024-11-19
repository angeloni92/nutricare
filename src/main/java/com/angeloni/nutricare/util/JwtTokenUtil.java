package com.angeloni.nutricare.util;

import java.util.Base64;
import java.util.Date;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;

@Component
public class JwtTokenUtil {

    @Value("${jwt.secret}")
    private String secretKey;
    
    private SecretKey getSigningKey() {
        byte[] keyBytes = Base64.getDecoder().decode(secretKey);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    // Method to generate JWT Token
    public String generateToken(String username) {
        Date now = new Date();
        Date expirationTime = new Date(now.getTime() + 3600000); // Expiration of 1 hour (3600000 ms)

        return Jwts.builder()
                .setSubject(username)
                .setIssuedAt(now) // Issued date of the token
                .setExpiration(expirationTime) // Expiration date
                .signWith(SignatureAlgorithm.HS256, getSigningKey())
                .compact();
    }

    // Method to extract Jwt details
    public Claims extractClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build() // Build the parser
                .parseClaimsJws(token) // Parse the JWT and extract the claims
                .getBody();
    }

    // Method to extract username's token
    public String extractUsername(String token) {
        return extractClaims(token).getSubject();
    }

    // Method to extract userId's token
    public String extractUserId(String token) {
        return extractClaims(token).get("user_id", String.class);
    }

    // Method to check if token is expired
    public boolean isTokenExpired(String token) {
        return extractClaims(token).getExpiration().before(new Date());
    }

    // Method to validate token
    public boolean validateToken(String token, String username) {
        return (username.equals(extractUsername(token)) && !isTokenExpired(token));
    }
}
