package com.tanishka.ecommerce.serviceimpl;

import java.nio.charset.StandardCharsets;
import java.util.Date;

import javax.crypto.SecretKey;

import org.springframework.stereotype.Service;

import com.tanishka.ecommerce.config.JwtConfig;

import io.jsonwebtoken.*;

import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;

@Service
public class JwtServiceImpl {

    private final JwtConfig jwtConfig;

    private static final long ACCESS_TOKEN_VALIDITY = 1000 * 60 * 15; // 15 min
    private static final long REFRESH_TOKEN_VALIDITY = 1000 * 60 * 60 * 24; // 1 day

    public JwtServiceImpl(JwtConfig jwtConfig) {
        this.jwtConfig = jwtConfig;
    }

    private SecretKey getSignInKey() {
        byte[] keyBytes = jwtConfig.getSecret().getBytes(StandardCharsets.UTF_8);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    @PostConstruct
    public void logKey() {
        System.out.println("Loaded JWT secret: " + jwtConfig.getSecret());
    }

    public String generateAccessToken(String email, String role) {
        return generateToken(email, role, ACCESS_TOKEN_VALIDITY);
    }

    public String generateRefreshToken(String email, String role) {
        return generateToken(email, role, REFRESH_TOKEN_VALIDITY);
    }

    private String generateToken(String username, String role, long expirationMillis) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + expirationMillis);

        JwtBuilder builder = Jwts.builder()
                .setSubject(username)
                .setIssuedAt(now)
                .setExpiration(expiryDate)
                .signWith(getSignInKey(), SignatureAlgorithm.HS256);

        if (role != null) {
            builder.claim("role", role);
        }

        return builder.compact();
    }

    public String extractUsername(String token) {
        return getClaims(token).getSubject();
    }

    public boolean isTokenValid(String token) {
        try {
            Claims claims = getClaims(token);
            return claims.getExpiration().after(new Date());
        } catch (JwtException | IllegalArgumentException e) {
            System.err.println("Token validation error: " + e.getMessage());
            return false;
        }
    }

    public Claims getClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSignInKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }
}
