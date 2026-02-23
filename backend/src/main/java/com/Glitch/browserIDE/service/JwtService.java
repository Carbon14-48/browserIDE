package com.Glitch.browserIDE.service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtParser;
import io.jsonwebtoken.Jwts;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import io.jsonwebtoken.security.Keys;

@Service
public class JwtService {

    @Value("${jwt.secret}")
    private String secretKey;

    @Value("${jwt.expiration}")
    private long jwtExpiration;

    @Value("${jwt.refresh.expiration}")
    private long refreshExpiration;

    private SecretKey getSigningKey() {
        byte[] keyBytes = secretKey.getBytes(StandardCharsets.UTF_8);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    private Claims extractAllClaims(String token) {
        JwtParser parser = Jwts.parser().verifyWith(getSigningKey()).build();
        return parser.parseSignedClaims(token).getPayload();
    }

    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    public Long extractUserId(String token) {
        String subject = extractClaim(token, Claims::getSubject);
        return Long.parseLong(subject);
    }

    public String extractEmail(String token) {
        return extractClaim(token, claims -> claims.get("email", String.class));
    }

    private Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    private boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    public String generateAccessToken(Long userId, String email) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("email", email);
        claims.put("type", "access");

        long currentTimeMillis = System.currentTimeMillis();

        return Jwts.builder()
                .claims(claims)
                .subject(userId.toString())
                .issuedAt(new Date(currentTimeMillis))
                .expiration(new Date(currentTimeMillis + jwtExpiration))
                .signWith(getSigningKey())
                .compact();
    }

    public String generateRefreshToken(Long userId) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("type", "refresh");

        long currentTimeMillis = System.currentTimeMillis();

        return Jwts.builder()
                .claims(claims)
                .subject(userId.toString())
                .issuedAt(new Date(currentTimeMillis))
                .expiration(new Date(currentTimeMillis + refreshExpiration))
                .signWith(getSigningKey())
                .compact();
    }

    //// Acces Token
    public boolean isTokenValid(String token, Long userId) {
        final Long tokenUserId = extractUserId(token);
        return tokenUserId.equals(userId) && !isTokenExpired(token);
    }

    public Date getRefreshTokenExpiration() {
        return new Date(System.currentTimeMillis() + refreshExpiration);
    }
}