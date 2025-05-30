package com.plazoleta.user_microservice.infrastructure.configuration.security.filter;

import com.plazoleta.user_microservice.infrastructure.configuration.security.adapter.JwtTokenAdapter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetails;

import java.util.Collection;
import java.util.List;


import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class JwtAuthenticationFilterTest {

    @Mock
    private JwtTokenAdapter jwtProvider;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private FilterChain filterChain;

    @InjectMocks
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    private static final String VALID_TOKEN = "valid.jwt.token";
    private static final String INVALID_TOKEN = "invalid.token";
    private static final String USER_EMAIL = "user@example.com";

    @BeforeEach
    void setUp() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void shouldContinueChainWhenNoAuthHeader() throws Exception {
        when(request.getHeader("Authorization")).thenReturn(null);

        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        verify(filterChain).doFilter(request, response);
        assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
        verifyNoInteractions(jwtProvider);
    }

    @Test
    void shouldContinueWhenHeaderDoesNotStartWithBearer() throws Exception {
        when(request.getHeader("Authorization")).thenReturn("Basic xyz123");

        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        verify(filterChain).doFilter(request, response);
        assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
        verifyNoInteractions(jwtProvider);
    }

    @Test
    void shouldContinueWhenTokenIsEmpty() throws Exception {
        when(request.getHeader("Authorization")).thenReturn("Bearer ");

        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        verify(filterChain).doFilter(request, response);
        assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
        verifyNoInteractions(jwtProvider);
    }

    @Test
    void shouldAuthenticateSuccessfullyWithValidToken() throws Exception {
        when(request.getHeader("Authorization")).thenReturn("Bearer " + VALID_TOKEN);
        when(jwtProvider.extractEmail(VALID_TOKEN)).thenReturn(USER_EMAIL);
        when(jwtProvider.validateToken(VALID_TOKEN)).thenReturn(true);

        Collection<? extends GrantedAuthority> authorities = List.of(new SimpleGrantedAuthority("ROLE_USER"));
        when(jwtProvider.extractAuthorities(VALID_TOKEN)).thenReturn((Collection) authorities);
        when(request.getRemoteAddr()).thenReturn("127.0.0.1");

        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        verify(filterChain).doFilter(request, response);

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        assertThat(auth).isNotNull();
        assertThat(auth.getName()).isEqualTo(USER_EMAIL);
        assertThat(auth.getAuthorities()).containsExactlyElementsOf((Collection) authorities);
        assertThat(auth).isInstanceOf(UsernamePasswordAuthenticationToken.class);
    }

    @Test
    void shouldNotAuthenticateWhenTokenIsInvalid() throws Exception {
        when(request.getHeader("Authorization")).thenReturn("Bearer " + INVALID_TOKEN);
        when(jwtProvider.extractEmail(INVALID_TOKEN)).thenReturn(USER_EMAIL);
        when(jwtProvider.validateToken(INVALID_TOKEN)).thenReturn(false);

        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        verify(filterChain).doFilter(request, response);
        assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
        verify(jwtProvider, never()).extractAuthorities(any());
    }

    @Test
    void shouldNotAuthenticateWhenEmailIsNull() throws Exception {
        when(request.getHeader("Authorization")).thenReturn("Bearer " + VALID_TOKEN);
        when(jwtProvider.extractEmail(VALID_TOKEN)).thenReturn(null);

        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        verify(filterChain).doFilter(request, response);
        assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
        verify(jwtProvider, never()).validateToken(any());
        verify(jwtProvider, never()).extractAuthorities(any());
    }

    @Test
    void shouldNotOverrideExistingAuthentication() throws Exception {
        Authentication existingAuth = new UsernamePasswordAuthenticationToken(
                "existing@user.com", null, List.of(new SimpleGrantedAuthority("ROLE_ADMIN"))
        );
        SecurityContextHolder.getContext().setAuthentication(existingAuth);

        when(request.getHeader("Authorization")).thenReturn("Bearer " + VALID_TOKEN);
        when(jwtProvider.extractEmail(VALID_TOKEN)).thenReturn(USER_EMAIL);

        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        verify(filterChain).doFilter(request, response);
        assertThat(SecurityContextHolder.getContext().getAuthentication()).isEqualTo(existingAuth);
        verify(jwtProvider, never()).validateToken(any());
    }

    @Test
    void shouldHandleExceptionsWithoutBreakingChain() throws Exception {
        when(request.getHeader("Authorization")).thenReturn("Bearer " + VALID_TOKEN);
        when(jwtProvider.extractEmail(VALID_TOKEN)).thenThrow(new RuntimeException("Token parsing error"));

        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        verify(filterChain).doFilter(request, response);
        assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
    }

    @Test
    void shouldSetWebAuthenticationDetailsCorrectly() throws Exception {
        when(request.getHeader("Authorization")).thenReturn("Bearer " + VALID_TOKEN);
        when(jwtProvider.extractEmail(VALID_TOKEN)).thenReturn(USER_EMAIL);
        when(jwtProvider.validateToken(VALID_TOKEN)).thenReturn(true);
        when(jwtProvider.extractAuthorities(VALID_TOKEN)).thenReturn(List.of());
        when(request.getRemoteAddr()).thenReturn("192.168.1.1");
        when(request.getSession(false)).thenReturn(null);

        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        assertThat(auth.getDetails()).isInstanceOf(WebAuthenticationDetails.class);
    }

    @Test
    void shouldExtractTokenCorrectlyFromHeader() throws Exception {
        String complexToken = "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.test.signature";
        when(request.getHeader("Authorization")).thenReturn("Bearer " + complexToken);
        when(jwtProvider.extractEmail(complexToken)).thenReturn(USER_EMAIL);
        when(jwtProvider.validateToken(complexToken)).thenReturn(true);
        when(jwtProvider.extractAuthorities(complexToken)).thenReturn(List.of());

        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        verify(jwtProvider).extractEmail(complexToken);
        verify(jwtProvider).validateToken(complexToken);
    }
}