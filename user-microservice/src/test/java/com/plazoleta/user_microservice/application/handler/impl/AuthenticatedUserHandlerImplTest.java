package com.plazoleta.user_microservice.application.handler.impl;


import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthenticatedUserHandlerImplTest {

    @InjectMocks
    private AuthenticatedUserHandlerImpl authenticatedUserHandler;

    @Mock
    private SecurityContext securityContext;

    @Mock
    private Authentication authentication;

    @Test
    void getCurrentUserId_WhenAuthenticationIsNull_ShouldReturnEmpty() {
        try (MockedStatic<SecurityContextHolder> mockedHolder = mockStatic(SecurityContextHolder.class)) {
            mockedHolder.when(SecurityContextHolder::getContext).thenReturn(securityContext);
            when(securityContext.getAuthentication()).thenReturn(null);

            Optional<Long> result = authenticatedUserHandler.getCurrentUserId();

            assertTrue(result.isEmpty());
        }
    }

    @Test
    void getCurrentUserId_WhenDetailsIsNull_ShouldReturnEmpty() {
        try (MockedStatic<SecurityContextHolder> mockedHolder = mockStatic(SecurityContextHolder.class)) {
            mockedHolder.when(SecurityContextHolder::getContext).thenReturn(securityContext);
            when(securityContext.getAuthentication()).thenReturn(authentication);
            when(authentication.getDetails()).thenReturn(null);

            Optional<Long> result = authenticatedUserHandler.getCurrentUserId();

            assertTrue(result.isEmpty());
        }
    }

    @Test
    void getCurrentUserId_WhenDetailsIsNotMap_ShouldReturnEmpty() {
        try (MockedStatic<SecurityContextHolder> mockedHolder = mockStatic(SecurityContextHolder.class)) {
            mockedHolder.when(SecurityContextHolder::getContext).thenReturn(securityContext);
            when(securityContext.getAuthentication()).thenReturn(authentication);
            when(authentication.getDetails()).thenReturn("not a map");

            Optional<Long> result = authenticatedUserHandler.getCurrentUserId();

            assertTrue(result.isEmpty());
        }
    }

    @Test
    void getCurrentUserId_WhenUserIdIsNull_ShouldReturnEmpty() {
        try (MockedStatic<SecurityContextHolder> mockedHolder = mockStatic(SecurityContextHolder.class)) {
            Map<String, Object> detailsMap = new HashMap<>();
            detailsMap.put("userId", null);

            mockedHolder.when(SecurityContextHolder::getContext).thenReturn(securityContext);
            when(securityContext.getAuthentication()).thenReturn(authentication);
            when(authentication.getDetails()).thenReturn(detailsMap);

            Optional<Long> result = authenticatedUserHandler.getCurrentUserId();

            assertTrue(result.isEmpty());
        }
    }

    @Test
    void getCurrentUserId_WhenUserIdExists_ShouldReturnUserId() {
        try (MockedStatic<SecurityContextHolder> mockedHolder = mockStatic(SecurityContextHolder.class)) {
            Map<String, Object> detailsMap = new HashMap<>();
            detailsMap.put("userId", "12345");

            mockedHolder.when(SecurityContextHolder::getContext).thenReturn(securityContext);
            when(securityContext.getAuthentication()).thenReturn(authentication);
            when(authentication.getDetails()).thenReturn(detailsMap);

            Optional<Long> result = authenticatedUserHandler.getCurrentUserId();

            assertTrue(result.isPresent());
            assertEquals(12345L, result.get());
        }
    }

    @Test
    void getCurrentUserId_WhenUserIdIsInteger_ShouldReturnUserId() {
        try (MockedStatic<SecurityContextHolder> mockedHolder = mockStatic(SecurityContextHolder.class)) {
            Map<String, Object> detailsMap = new HashMap<>();
            detailsMap.put("userId", 67890);

            mockedHolder.when(SecurityContextHolder::getContext).thenReturn(securityContext);
            when(securityContext.getAuthentication()).thenReturn(authentication);
            when(authentication.getDetails()).thenReturn(detailsMap);

            Optional<Long> result = authenticatedUserHandler.getCurrentUserId();

            assertTrue(result.isPresent());
            assertEquals(67890L, result.get());
        }
    }

    @Test
    void getCurrentUserId_WhenUserIdIsNotNumeric_ShouldThrowNumberFormatException() {
        try (MockedStatic<SecurityContextHolder> mockedHolder = mockStatic(SecurityContextHolder.class)) {
            Map<String, Object> detailsMap = new HashMap<>();
            detailsMap.put("userId", "not-a-number");

            mockedHolder.when(SecurityContextHolder::getContext).thenReturn(securityContext);
            when(securityContext.getAuthentication()).thenReturn(authentication);
            when(authentication.getDetails()).thenReturn(detailsMap);

            assertThrows(NumberFormatException.class, () -> {
                authenticatedUserHandler.getCurrentUserId();
            });
        }
    }

    @Test
    void getCurrentUserRole_WhenAuthenticationIsNull_ShouldReturnEmpty() {
        try (MockedStatic<SecurityContextHolder> mockedHolder = mockStatic(SecurityContextHolder.class)) {
            mockedHolder.when(SecurityContextHolder::getContext).thenReturn(securityContext);
            when(securityContext.getAuthentication()).thenReturn(null);

            Optional<String> result = authenticatedUserHandler.getCurrentUserRole();

            assertTrue(result.isEmpty());
        }
    }

    @Test
    void getCurrentUserRole_WhenAuthoritiesIsNull_ShouldReturnEmpty() {
        try (MockedStatic<SecurityContextHolder> mockedHolder = mockStatic(SecurityContextHolder.class)) {
            mockedHolder.when(SecurityContextHolder::getContext).thenReturn(securityContext);
            when(securityContext.getAuthentication()).thenReturn(authentication);
            when(authentication.getAuthorities()).thenReturn(null);

            Optional<String> result = authenticatedUserHandler.getCurrentUserRole();

            assertTrue(result.isEmpty());
        }
    }

    @Test
    void getCurrentUserRole_WhenAuthoritiesIsEmpty_ShouldReturnEmpty() {
        try (MockedStatic<SecurityContextHolder> mockedHolder = mockStatic(SecurityContextHolder.class)) {
            Collection<? extends GrantedAuthority> emptyAuthorities = Collections.emptyList();

            mockedHolder.when(SecurityContextHolder::getContext).thenReturn(securityContext);
            when(securityContext.getAuthentication()).thenReturn(authentication);
            when(authentication.getAuthorities()).thenReturn((Collection) emptyAuthorities);

            Optional<String> result = authenticatedUserHandler.getCurrentUserRole();

            assertTrue(result.isEmpty());
        }
    }

    @Test
    void getCurrentUserRole_WhenAuthoritiesExist_ShouldReturnFirstAuthority() {
        // Mocks manuales
        SecurityContext securityContext = mock(SecurityContext.class);
        Authentication authentication = mock(Authentication.class);
        Collection<GrantedAuthority> authorities = List.of(new SimpleGrantedAuthority("ROLE_ADMIN"));

        try (MockedStatic<SecurityContextHolder> mockedHolder = mockStatic(SecurityContextHolder.class)) {
            mockedHolder.when(SecurityContextHolder::getContext).thenReturn(securityContext);
            when(securityContext.getAuthentication()).thenReturn(authentication);
            when(authentication.getAuthorities()).thenReturn((Collection) authorities);

            AuthenticatedUserHandlerImpl authenticatedUserHandler = new AuthenticatedUserHandlerImpl();
            Optional<String> result = authenticatedUserHandler.getCurrentUserRole();

            assertTrue(result.isPresent());
            assertEquals("ROLE_ADMIN", result.get());
        }
    }

    @Test
    void getCurrentUserRole_WhenSingleAuthorityExists_ShouldReturnThatAuthority() {
        try (MockedStatic<SecurityContextHolder> mockedHolder = mockStatic(SecurityContextHolder.class)) {
            Collection<GrantedAuthority> authorities = Collections.singletonList(
                    new SimpleGrantedAuthority("ROLE_USER")
            );

            mockedHolder.when(SecurityContextHolder::getContext).thenReturn(securityContext);
            when(securityContext.getAuthentication()).thenReturn(authentication);
            when(authentication.getAuthorities()).thenReturn((Collection) authorities);

            Optional<String> result = authenticatedUserHandler.getCurrentUserRole();

            assertTrue(result.isPresent());
            assertEquals("ROLE_USER", result.get());
        }
    }

    @Test
    void bothMethods_WhenAuthenticationIsCompletelyNull_ShouldReturnEmpty() {
        try (MockedStatic<SecurityContextHolder> mockedHolder = mockStatic(SecurityContextHolder.class)) {
            mockedHolder.when(SecurityContextHolder::getContext).thenReturn(securityContext);
            when(securityContext.getAuthentication()).thenReturn(null);

            Optional<Long> userIdResult = authenticatedUserHandler.getCurrentUserId();
            Optional<String> userRoleResult = authenticatedUserHandler.getCurrentUserRole();

            assertTrue(userIdResult.isEmpty());
            assertTrue(userRoleResult.isEmpty());
        }
    }
}