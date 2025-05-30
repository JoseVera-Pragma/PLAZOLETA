package com.plazoleta.user_microservice.infrastructure.configuration.security.adapter;

import com.plazoleta.user_microservice.domain.model.User;
import com.plazoleta.user_microservice.domain.spi.ITokenGeneratorPort;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Service;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import java.security.NoSuchAlgorithmException;
import java.util.*;

@Service
public class JwtTokenAdapter implements ITokenGeneratorPort {

    @Value("${jwt.secret}")
    private String jwtSecret;

    @Value("${jwt.expiration}")
    private long jwtExpirationInMs;

    private static final String TOKEN_PREFIX = "Bearer ";

    public JwtTokenAdapter() throws NoSuchAlgorithmException {
        KeyGenerator keyGen = KeyGenerator.getInstance("HmacSHA256");
        SecretKey sk = keyGen.generateKey();
        jwtSecret = Base64.getEncoder().encodeToString(sk.getEncoded());
    }

    private SecretKey getSignInKey() {
        byte[] keyBytes = Decoders.BASE64.decode(jwtSecret);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    @Override
    public String generateToken(User user) {
        Date expiryDate = new Date(System.currentTimeMillis() + jwtExpirationInMs);

        Map<String, Object> claims = new HashMap<>();
        claims.put("role", user.getRole().getName());
        claims.put("userId", user.getId());
        claims.put("name", user.getFirstName());

        return Jwts.builder()
                .claims(claims)
                .subject(user.getEmail().getValue())
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(expiryDate)
                .signWith(getSignInKey())
                .compact();
    }

    @Override
    public boolean validateToken(String token) {
        try {
            Jwts.parser()
                    .verifyWith(getSignInKey())
                    .build()
                    .parseSignedClaims(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            System.err.println("Token validation failed: " + e.getMessage());
            return false;
        }
    }

    @Override
    public String extractEmail(String token) {
        Claims claims = extractClaims(token);
        return claims.getSubject();
    }

    @Override
    public String extractRole(String token) {
        Claims claims = extractClaims(token);
        return claims.get("role", String.class);
    }

    @Override
    public Long extractUserId(String token) {
        Claims claims = extractClaims(token);
        return claims.get("userId", Long.class);
    }

    private Claims extractClaims(String token) {
        return Jwts.parser()
                .verifyWith(getSignInKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    public Collection<? extends GrantedAuthority> extractAuthorities(String token) {
        String role = extractRole(token);
        return Collections.singletonList(new SimpleGrantedAuthority(role));
    }
}