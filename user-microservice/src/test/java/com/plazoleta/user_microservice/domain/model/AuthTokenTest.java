package com.plazoleta.user_microservice.domain.model;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class AuthTokenTest {
    @Test
    void testConstructorAndGetters() {
        String token = "abc123";
        String email = "user@example.com";
        String role = "ROLE_ADMIN";
        Long userId = 42L;

        AuthToken authToken = new AuthToken(token, email, role, userId);

        assertEquals(token, authToken.getToken());
        assertEquals("Bearer", authToken.getType());
        assertEquals(email, authToken.getEmail());
        assertEquals(role, authToken.getRole());
        assertEquals(userId, authToken.getUserId());
    }

    @Test
    void testSetters() {
        AuthToken authToken = new AuthToken("initialToken", "initial@example.com", "ROLE_USER", 1L);

        authToken.setToken("newToken");
        authToken.setType("CustomType");
        authToken.setEmail("new@example.com");
        authToken.setRole("ROLE_ADMIN");
        authToken.setUserId(100L);

        assertEquals("newToken", authToken.getToken());
        assertEquals("CustomType", authToken.getType());
        assertEquals("new@example.com", authToken.getEmail());
        assertEquals("ROLE_ADMIN", authToken.getRole());
        assertEquals(100L, authToken.getUserId());
    }
}