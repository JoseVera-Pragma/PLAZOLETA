package com.plazoleta.plazoleta_microservice.infrastructure.configuration.security.adapter;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.util.ReflectionTestUtils;

import javax.crypto.SecretKey;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@TestPropertySource(properties = "jwt.secret=REMOVED_JWT_SECRET")

class JwtTokenAdapterTest {

    private JwtTokenAdapter jwtTokenAdapter;

    @BeforeEach
    void setUp() {
        jwtTokenAdapter = new JwtTokenAdapter();
        ReflectionTestUtils.setField(jwtTokenAdapter, "jwtSecret", "REMOVED_JWT_SECRET");
    }

    private String generateToken(String email, String role, Long userId) {
        SecretKey key = Keys.hmacShaKeyFor(Decoders.BASE64.decode("REMOVED_JWT_SECRET"));
        return Jwts.builder()
                .subject(email)
                .claim("role", role)
                .claim("userId", userId)
                .signWith(key)
                .compact();
    }

    @Test
    void validateToken_validToken_returnsTrue() {
        String token = generateToken("test@example.com", "ROLE_ADMIN", 1L);
        assertTrue(jwtTokenAdapter.validateToken(token));
    }

    @Test
    void validateToken_invalidToken_returnsFalse() {
        String invalidToken = "invalid.token.structure";
        assertFalse(jwtTokenAdapter.validateToken(invalidToken));
    }

    @Test
    void extractClaims_returnsCorrectClaims() {
        String token = generateToken("test@example.com", "ROLE_USER", 5L);
        Claims claims = jwtTokenAdapter.extractClaims(token);

        assertEquals("test@example.com", claims.getSubject());
        assertEquals("ROLE_USER", claims.get("role"));
        assertEquals(5L, claims.get("userId", Long.class));
    }

    @Test
    void extractRole_returnsCorrectRole() {
        String token = generateToken("test@example.com", "ROLE_MODERATOR", 2L);
        assertEquals("ROLE_MODERATOR", jwtTokenAdapter.extractRole(token));
    }

    @Test
    void extractEmail_returnsCorrectEmail() {
        String token = generateToken("me@domain.com", "ROLE_OWNER", 9L);
        assertEquals("me@domain.com", jwtTokenAdapter.extractEmail(token));
    }

    @Test
    void extractUserId_returnsCorrectUserId() {
        String token = generateToken("a@b.com", "ROLE_ADMIN", 77L);
        assertEquals(77L, jwtTokenAdapter.extractUserId(token));
    }
}