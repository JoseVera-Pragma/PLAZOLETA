package com.plazoleta.plazoleta_microservice.infrastructure.configuration.security.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CustomUserPrincipalTest {

    @Test
    void testRecordFields() {
        Long id = 1L;
        String email = "test@example.com";
        CustomUserPrincipal principal = new CustomUserPrincipal(id, email);

        assertEquals(id, principal.id());
        assertEquals(email, principal.email());
    }

    @Test
    void testEqualsAndHashCode() {
        CustomUserPrincipal principal1 = new CustomUserPrincipal(1L, "test@example.com");
        CustomUserPrincipal principal2 = new CustomUserPrincipal(1L, "test@example.com");
        CustomUserPrincipal principal3 = new CustomUserPrincipal(2L, "other@example.com");

        assertEquals(principal1, principal2);
        assertEquals(principal1.hashCode(), principal2.hashCode());
        assertNotEquals(principal1, principal3);
    }

    @Test
    void testToString() {
        CustomUserPrincipal principal = new CustomUserPrincipal(1L, "test@example.com");
        String expected = "CustomUserPrincipal[id=1, email=test@example.com]";
        assertEquals(expected, principal.toString());
    }
}