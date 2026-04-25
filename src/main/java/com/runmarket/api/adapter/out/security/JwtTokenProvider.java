package com.runmarket.api.adapter.out.security;

import com.runmarket.api.domain.model.User;
import com.runmarket.api.domain.port.in.auth.AuthToken;
import com.runmarket.api.domain.port.out.auth.TokenProvider;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;

@Component
public class JwtTokenProvider implements TokenProvider {

    private final SecretKey secretKey;
    private final long expirationMs;

    public JwtTokenProvider(
            @Value("${jwt.secret}") String secret,
            @Value("${jwt.expiration}") long expirationMs) {
        this.secretKey = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
        this.expirationMs = expirationMs;
    }

    @Override
    public AuthToken generateToken(User user) {
        List<String> roles = user.getRoles().stream()
                .map(role -> role.getRoleType().name())
                .toList();

        Date now = new Date();
        Date expiry = new Date(now.getTime() + expirationMs);
        LocalDateTime expiresAt = LocalDateTime.ofInstant(
                Instant.ofEpochMilli(expiry.getTime()), ZoneId.systemDefault());

        String token = Jwts.builder()
                .subject(user.getEmail())
                .claim("roles", roles)
                .issuedAt(now)
                .expiration(expiry)
                .signWith(secretKey)
                .compact();

        return new AuthToken(token, expiresAt);
    }

    @Override
    public boolean validateToken(String token) {
        try {
            Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }

    @Override
    public String getSubject(String token) {
        return Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .getSubject();
    }
}
