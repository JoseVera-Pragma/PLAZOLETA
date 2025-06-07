package com.plazoleta.plazoleta_microservice.infrastructure.out.feing;

import feign.RequestTemplate;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Collection;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class FeignConfigTest {

    private FeignConfig feignConfig;

    @BeforeEach
    void setUp() {
        feignConfig = new FeignConfig();
    }

    @Test
    void shouldAddAuthorizationHeaderWhenTokenIsValid() {
        String token = "ey123456789";
        Authentication authentication = mock(Authentication.class);
        when(authentication.getCredentials()).thenReturn(token);

        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(authentication);

        SecurityContextHolder.setContext(securityContext);

        RequestTemplate requestTemplate = new RequestTemplate();

        feignConfig.authRequestInterceptor().apply(requestTemplate);

        Collection<String> authHeader = requestTemplate.headers().get("Authorization");
        assertNotNull(authHeader);
        assertTrue(authHeader.contains("Bearer " + token));
    }

    @Test
    void shouldNotAddHeaderWhenAuthenticationIsNull() {
        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(null);

        SecurityContextHolder.setContext(securityContext);

        RequestTemplate requestTemplate = new RequestTemplate();

        feignConfig.authRequestInterceptor().apply(requestTemplate);

        assertFalse(requestTemplate.headers().containsKey("Authorization"));
    }


    @Test
    void shouldNotThrowOrAddHeaderWhenSecurityContextIsNull() {
        try (MockedStatic<SecurityContextHolder> mockedStatic = mockStatic(SecurityContextHolder.class)) {
            mockedStatic.when(SecurityContextHolder::getContext).thenReturn(null);

            RequestTemplate requestTemplate = new RequestTemplate();

            feignConfig.authRequestInterceptor().apply(requestTemplate);

            assertFalse(requestTemplate.headers().containsKey("Authorization"));
        }
    }

    @Test
    void shouldNotAddHeaderWhenTokenIsInvalid() {
        Authentication authentication = mock(Authentication.class);
        when(authentication.getCredentials()).thenReturn("invalidToken");

        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(authentication);

        SecurityContextHolder.setContext(securityContext);

        RequestTemplate requestTemplate = new RequestTemplate();

        feignConfig.authRequestInterceptor().apply(requestTemplate);

        assertFalse(requestTemplate.headers().containsKey("Authorization"));
    }

    @Test
    void shouldNotAddHeaderWhenSecurityContextIsEmpty() {

        RequestTemplate requestTemplate = new RequestTemplate();

        feignConfig.authRequestInterceptor().apply(requestTemplate);

        assertFalse(requestTemplate.headers().containsKey("Authorization"));
    }

    @Test
    void shouldNotAddHeaderWhenCredentialsAreNotString() {
        Authentication authentication = mock(Authentication.class);
        when(authentication.getCredentials()).thenReturn(12345);

        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(authentication);

        SecurityContextHolder.setContext(securityContext);

        RequestTemplate requestTemplate = new RequestTemplate();

        feignConfig.authRequestInterceptor().apply(requestTemplate);

        assertFalse(requestTemplate.headers().containsKey("Authorization"));
    }

    @AfterEach
    void clearSecurityContext() {
        SecurityContextHolder.clearContext();
    }
}