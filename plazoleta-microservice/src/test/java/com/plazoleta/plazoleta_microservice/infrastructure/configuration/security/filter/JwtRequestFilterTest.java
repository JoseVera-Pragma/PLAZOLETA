package com.plazoleta.plazoleta_microservice.infrastructure.configuration.security.filter;

import com.plazoleta.plazoleta_microservice.infrastructure.configuration.security.adapter.JwtTokenAdapter;
import com.plazoleta.plazoleta_microservice.infrastructure.configuration.security.model.CustomUserPrincipal;
import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.core.context.SecurityContextHolder;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class JwtRequestFilterTest {

    @Mock
    private JwtTokenAdapter jwtTokenAdapter;

    @InjectMocks
    private JwtRequestFilter jwtRequestFilter;

    private MockHttpServletRequest request;
    private MockHttpServletResponse response;
    private FilterChain filterChain;

    @BeforeEach
    void setUp() {
        request = new MockHttpServletRequest();
        response = new MockHttpServletResponse();
        filterChain = mock(FilterChain.class);
        SecurityContextHolder.clearContext();
    }

    @Test
    void doFilter_validToken_setsAuthentication() throws ServletException, IOException {
        String token = "valid.token";
        request.addHeader("Authorization", "Bearer " + token);

        Claims claims = mock(Claims.class);
        when(jwtTokenAdapter.validateToken(token)).thenReturn(true);
        when(jwtTokenAdapter.extractClaims(token)).thenReturn(claims);
        when(claims.get("role", String.class)).thenReturn("ROLE_OWNER");
        when(claims.get("userId", Long.class)).thenReturn(3L);
        when(claims.getSubject()).thenReturn("owner@owner.com");

        jwtRequestFilter.doFilter(request, response, filterChain);

        var authentication = SecurityContextHolder.getContext().getAuthentication();
        assertNotNull(authentication);
        assertTrue(authentication.isAuthenticated());
        assertEquals("owner@owner.com", ((CustomUserPrincipal) authentication.getPrincipal()).email());
        assertEquals(3L, ((CustomUserPrincipal) authentication.getPrincipal()).id());
        assertTrue(authentication.getAuthorities().stream()
                .anyMatch(auth -> auth.getAuthority().equals("ROLE_OWNER")));
        verify(filterChain).doFilter(request, response);
    }

    @Test
    void doFilter_invalidToken_setsUnauthorized() throws ServletException, IOException {
        String token = "invalid.token";
        request.addHeader("Authorization", "Bearer " + token);

        when(jwtTokenAdapter.validateToken(token)).thenReturn(false);

        jwtRequestFilter.doFilter(request, response, filterChain);

        assertEquals(HttpServletResponse.SC_UNAUTHORIZED, response.getStatus());
        verify(filterChain, never()).doFilter(request, response);
    }

    @Test
    void doFilter_noAuthorizationHeader_continuesFilterChain() throws ServletException, IOException {
        jwtRequestFilter.doFilter(request, response, filterChain);

        assertNull(SecurityContextHolder.getContext().getAuthentication());
        verify(filterChain).doFilter(request, response);
    }
}