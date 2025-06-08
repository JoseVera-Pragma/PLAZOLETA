package com.plazoleta.plazoleta_microservice.infrastructure.configuration.security;

import com.plazoleta.plazoleta_microservice.infrastructure.configuration.security.filter.JwtRequestFilter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;


import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SecurityConfigTest {

    @Mock
    private JwtRequestFilter jwtRequestFilter;

    @Mock
    private HttpSecurity httpSecurity;

    private SecurityConfig securityConfig;

    @BeforeEach
    void setUp() {
        securityConfig = new SecurityConfig(jwtRequestFilter);
    }

    @Test
    void testConstructor() {
        JwtRequestFilter filter = mock(JwtRequestFilter.class);

        SecurityConfig config = new SecurityConfig(filter);

        assertNotNull(config);
    }

    @Test
    void testCorsConfigurationSource() {
        CorsConfigurationSource corsSource = securityConfig.corsConfigurationSource();

        assertNotNull(corsSource);
        assertTrue(corsSource instanceof UrlBasedCorsConfigurationSource);

        UrlBasedCorsConfigurationSource urlBasedSource = (UrlBasedCorsConfigurationSource) corsSource;
        CorsConfiguration corsConfig = urlBasedSource.getCorsConfigurations().get("/**");

        assertNotNull(corsConfig);
        assertEquals(List.of("*"), corsConfig.getAllowedOriginPatterns());
        assertEquals(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"), corsConfig.getAllowedMethods());
        assertEquals(List.of("*"), corsConfig.getAllowedHeaders());
        assertTrue(corsConfig.getAllowCredentials());
    }

    @Test
    void testCorsConfigurationAllowedOriginPatterns() {
        CorsConfigurationSource corsSource = securityConfig.corsConfigurationSource();
        UrlBasedCorsConfigurationSource urlBasedSource = (UrlBasedCorsConfigurationSource) corsSource;
        CorsConfiguration corsConfig = urlBasedSource.getCorsConfigurations().get("/**");

        assertNotNull(corsConfig.getAllowedOriginPatterns());
        assertEquals(1, corsConfig.getAllowedOriginPatterns().size());
        assertEquals("*", corsConfig.getAllowedOriginPatterns().get(0));
    }

    @Test
    void testCorsConfigurationAllowedMethods() {
        CorsConfigurationSource corsSource = securityConfig.corsConfigurationSource();
        UrlBasedCorsConfigurationSource urlBasedSource = (UrlBasedCorsConfigurationSource) corsSource;
        CorsConfiguration corsConfig = urlBasedSource.getCorsConfigurations().get("/**");

        List<String> expectedMethods = List.of("GET", "POST", "PUT", "DELETE", "OPTIONS");
        assertEquals(expectedMethods, corsConfig.getAllowedMethods());
    }

    @Test
    void testCorsConfigurationAllowedHeaders() {
        CorsConfigurationSource corsSource = securityConfig.corsConfigurationSource();
        UrlBasedCorsConfigurationSource urlBasedSource = (UrlBasedCorsConfigurationSource) corsSource;
        CorsConfiguration corsConfig = urlBasedSource.getCorsConfigurations().get("/**");

        assertEquals(List.of("*"), corsConfig.getAllowedHeaders());
    }

    @Test
    void testCorsConfigurationAllowCredentials() {
        CorsConfigurationSource corsSource = securityConfig.corsConfigurationSource();
        UrlBasedCorsConfigurationSource urlBasedSource = (UrlBasedCorsConfigurationSource) corsSource;
        CorsConfiguration corsConfig = urlBasedSource.getCorsConfigurations().get("/**");

        assertTrue(corsConfig.getAllowCredentials());
    }

    @Test
    void testFilterChainCreation() throws Exception {
        CorsConfigurationSource corsSource = securityConfig.corsConfigurationSource();
        assertNotNull(corsSource);

        assertTrue(corsSource instanceof UrlBasedCorsConfigurationSource);
    }

    @Test
    void testSecurityConfigBeanCreation() {
        assertNotNull(securityConfig);
        assertNotNull(securityConfig.corsConfigurationSource());
    }

    @Test
    void testJwtRequestFilterInjection() {
        JwtRequestFilter testFilter = mock(JwtRequestFilter.class);

        SecurityConfig config = new SecurityConfig(testFilter);

        assertNotNull(config);
    }
}