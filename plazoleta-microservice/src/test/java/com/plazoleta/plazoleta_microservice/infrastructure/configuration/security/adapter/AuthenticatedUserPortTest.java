package com.plazoleta.plazoleta_microservice.infrastructure.configuration.security.adapter;

import com.plazoleta.plazoleta_microservice.infrastructure.configuration.security.model.CustomUserPrincipal;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class AuthenticatedUserPortTest {

    private final AuthenticatedUserPort authenticatedUserPort = new AuthenticatedUserPort();

    @AfterEach
    void clearSecurityContext() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void testGetCurrentUserId_whenPrincipalIsCustomUserPrincipal_shouldReturnId() {
        Long expectedId = 42L;
        CustomUserPrincipal principal = new CustomUserPrincipal(expectedId, "test@example.com");

        Authentication authentication = mock(Authentication.class);
        when(authentication.getPrincipal()).thenReturn(principal);

        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(authentication);

        SecurityContextHolder.setContext(securityContext);

        Optional<Long> userId = authenticatedUserPort.getCurrentUserId();

        assertTrue(userId.isPresent());
        assertEquals(expectedId, userId.get());
    }

    @Test
    void testGetCurrentUserId_whenNoAuthentication_shouldReturnEmpty() {
        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(null);

        SecurityContextHolder.setContext(securityContext);

        Optional<Long> userId = authenticatedUserPort.getCurrentUserId();

        assertTrue(userId.isEmpty());
    }

    @Test
    void testGetCurrentUserId_whenPrincipalIsNotCustomUserPrincipal_shouldReturnEmpty() {
        Authentication authentication = mock(Authentication.class);
        when(authentication.getPrincipal()).thenReturn("someOtherUser");

        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(authentication);

        SecurityContextHolder.setContext(securityContext);

        Optional<Long> userId = authenticatedUserPort.getCurrentUserId();

        assertTrue(userId.isEmpty());
    }
}