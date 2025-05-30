package com.plazoleta.plazoleta_microservice.application.handler.impl;

import com.plazoleta.plazoleta_microservice.domain.exception.restaurant.UserNotFoundException;
import com.plazoleta.plazoleta_microservice.infrastructure.configuration.security.model.CustomUserPrincipal;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Collection;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class AuthenticatedUserHandlerImplTest {

    private final AuthenticatedUserHandlerImpl authenticatedUserHandler = new AuthenticatedUserHandlerImpl();

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void getCurrentUserId_shouldReturnUserId_whenAuthenticated() {
        Long expectedId = 3L;
        CustomUserPrincipal principal = new CustomUserPrincipal(expectedId, "owner@owner.com");

        Collection<GrantedAuthority> authorities = List.of(new SimpleGrantedAuthority("ROLE_ADMIN"));
        UsernamePasswordAuthenticationToken auth =
                new UsernamePasswordAuthenticationToken(principal, null, authorities);

        SecurityContextHolder.getContext().setAuthentication(auth);

        Long actualId = authenticatedUserHandler.getCurrentUserId();

        assertEquals(expectedId, actualId);
    }

    @Test
    void getCurrentUserId_shouldThrowException_whenNoAuthentication() {
        SecurityContextHolder.clearContext();

        UserNotFoundException exception = assertThrows(UserNotFoundException.class, () -> {
            authenticatedUserHandler.getCurrentUserId();
        });

        assertEquals("The authenticated user ID could not be obtained", exception.getMessage());
    }
}