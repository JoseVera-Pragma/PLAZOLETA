package com.plazoleta.plazoleta_microservice.infrastructure.configuration.security;

import com.plazoleta.plazoleta_microservice.infrastructure.configuration.security.filter.JwtRequestFilter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.io.IOException;


import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@SpringBootTest
@AutoConfigureMockMvc
class SecurityConfigTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private JwtRequestFilter jwtRequestFilter;

    @BeforeEach
    void setUp() throws ServletException, IOException {
        Mockito.doNothing().when(jwtRequestFilter).doFilter(Mockito.any(), Mockito.any(), Mockito.any());
    }

    @Test
    void whenAccessingPublicEndpoint_thenOk() throws Exception {
        mockMvc.perform(get("/swagger-ui/index.html")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    void whenAccessingProtectedEndpointWithoutToken_thenUnauthorized() throws Exception {
        Mockito.doAnswer(invocation -> {
            invocation.getArgument(2, FilterChain.class).doFilter(
                    invocation.getArgument(0), invocation.getArgument(1));
            return null;
        }).when(jwtRequestFilter).doFilter(Mockito.any(), Mockito.any(), Mockito.any());

        mockMvc.perform(get("/categories")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void whenAccessingProtectedEndpointWithToken_thenOk() throws Exception {
        Mockito.doAnswer(invocation -> {
            invocation.getArgument(2, FilterChain.class).doFilter(
                    invocation.getArgument(0), invocation.getArgument(1));
            return null;
        }).when(jwtRequestFilter).doFilter(Mockito.any(), Mockito.any(), Mockito.any());

        mockMvc.perform(get("/categories")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer fake-token")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized());
    }
}