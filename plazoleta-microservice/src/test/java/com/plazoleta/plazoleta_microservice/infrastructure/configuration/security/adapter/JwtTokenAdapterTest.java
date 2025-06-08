package com.plazoleta.plazoleta_microservice.infrastructure.configuration.security.adapter;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import javax.crypto.SecretKey;

import java.util.Base64;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class JwtTokenAdapterTest {

    private JwtTokenAdapter jwtTokenAdapter;
    private String testSecret;
    private SecretKey testKey;

    @BeforeEach
    void setUp() {
        jwtTokenAdapter = new JwtTokenAdapter();

        testKey = Keys.secretKeyFor(io.jsonwebtoken.SignatureAlgorithm.HS256);
        testSecret = Base64.getEncoder().encodeToString(testKey.getEncoded());

        ReflectionTestUtils.setField(jwtTokenAdapter, "jwtSecret", testSecret);
    }

    @Test
    void validateToken_ValidToken_ReturnsTrue() {
        String validToken = createTestToken("test@email.com", "ADMIN", 1L);

        boolean isValid = jwtTokenAdapter.validateToken(validToken);

        assertTrue(isValid);
    }

    @Test
    void validateToken_InvalidToken_ReturnsFalse() {
        String invalidToken = "invalid.token.here";

        boolean isValid = jwtTokenAdapter.validateToken(invalidToken);

        assertFalse(isValid);
    }

    @Test
    void validateToken_ExpiredToken_ReturnsFalse() {
        String expiredToken = createExpiredTestToken("test@email.com", "USER", 1L);

        boolean isValid = jwtTokenAdapter.validateToken(expiredToken);

        assertFalse(isValid);
    }

    @Test
    void validateToken_NullToken_ReturnsFalse() {
        boolean isValid = jwtTokenAdapter.validateToken(null);

        assertFalse(isValid);
    }

    @Test
    void validateToken_EmptyToken_ReturnsFalse() {
        boolean isValid = jwtTokenAdapter.validateToken("");

        assertFalse(isValid);
    }

    @Test
    void extractClaims_ValidToken_ReturnsClaims() {
        String email = "test@email.com";
        String role = "ADMIN";
        Long userId = 123L;
        String validToken = createTestToken(email, role, userId);

        Claims claims = jwtTokenAdapter.extractClaims(validToken);

        assertNotNull(claims);
        assertEquals(email, claims.getSubject());
        assertEquals(role, claims.get("role", String.class));
        assertEquals(userId, claims.get("userId", Long.class));
    }

    @Test
    void extractRole_ValidToken_ReturnsRole() {
        String expectedRole = "CUSTOMER";
        String token = createTestToken("test@email.com", expectedRole, 1L);

        String actualRole = jwtTokenAdapter.extractRole(token);

        assertEquals(expectedRole, actualRole);
    }

    @Test
    void extractEmail_ValidToken_ReturnsEmail() {
        String expectedEmail = "user@example.com";
        String token = createTestToken(expectedEmail, "USER", 1L);

        String actualEmail = jwtTokenAdapter.extractEmail(token);

        assertEquals(expectedEmail, actualEmail);
    }

    @Test
    void extractUserId_ValidToken_ReturnsUserId() {
        Long expectedUserId = 456L;
        String token = createTestToken("test@email.com", "ADMIN", expectedUserId);

        Long actualUserId = jwtTokenAdapter.extractUserId(token);

        assertEquals(expectedUserId, actualUserId);
    }

    @Test
    void extractClaims_InvalidToken_ThrowsException() {
        String invalidToken = "invalid.token.here";

        assertThrows(Exception.class, () -> {
            jwtTokenAdapter.extractClaims(invalidToken);
        });
    }

    @Test
    void extractRole_TokenWithoutRole_ReturnsNull() {
        String tokenWithoutRole = createTokenWithoutRole("test@email.com", 1L);

        String role = jwtTokenAdapter.extractRole(tokenWithoutRole);

        assertNull(role);
    }

    @Test
    void extractUserId_TokenWithoutUserId_ReturnsNull() {
        String tokenWithoutUserId = createTokenWithoutUserId("test@email.com", "USER");

        Long userId = jwtTokenAdapter.extractUserId(tokenWithoutUserId);

        assertNull(userId);
    }

    private String createTestToken(String email, String role, Long userId) {
        return Jwts.builder()
                .subject(email)
                .claim("role", role)
                .claim("userId", userId)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + 86400000))
                .signWith(testKey)
                .compact();
    }

    private String createExpiredTestToken(String email, String role, Long userId) {
        return Jwts.builder()
                .subject(email)
                .claim("role", role)
                .claim("userId", userId)
                .issuedAt(new Date(System.currentTimeMillis() - 86400000))
                .expiration(new Date(System.currentTimeMillis() - 3600000))
                .signWith(testKey)
                .compact();
    }

    private String createTokenWithoutRole(String email, Long userId) {
        return Jwts.builder()
                .subject(email)
                .claim("userId", userId)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + 86400000))
                .signWith(testKey)
                .compact();
    }

    private String createTokenWithoutUserId(String email, String role) {
        return Jwts.builder()
                .subject(email)
                .claim("role", role)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + 86400000))
                .signWith(testKey)
                .compact();
    }
}