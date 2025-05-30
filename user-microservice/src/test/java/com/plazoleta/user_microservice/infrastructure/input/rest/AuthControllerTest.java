package com.plazoleta.user_microservice.infrastructure.input.rest;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.plazoleta.user_microservice.application.dto.request.LoginRequestDto;
import com.plazoleta.user_microservice.application.dto.response.AuthResponseDto;
import com.plazoleta.user_microservice.application.mapper.IAuthRequestMapper;
import com.plazoleta.user_microservice.domain.api.IAuthServicePort;
import com.plazoleta.user_microservice.domain.model.AuthToken;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class AuthControllerTest {

    @Mock
    private IAuthServicePort authServicePort;

    @Mock
    private IAuthRequestMapper authRequestMapper;

    @InjectMocks
    private AuthController authController;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(authController).build();
        objectMapper = new ObjectMapper();
    }

    @Test
    void testLogin_WithValidCredentials_ShouldReturnAuthResponse() throws Exception {
        LoginRequestDto loginRequest = new LoginRequestDto();
        loginRequest.setEmail("test@example.com");
        loginRequest.setPassword("password123");

        AuthToken authToken = new AuthToken("jwt-token-123", "ADMIN", "ADMIN", 1l);
        AuthResponseDto expectedResponse = new AuthResponseDto("jwt-token-123", "jwt", "ADMIN", "ADMIN", 1l);

        when(authServicePort.authenticate("test@example.com", "password123"))
                .thenReturn(authToken);
        when(authRequestMapper.toAuthResponse(authToken))
                .thenReturn(expectedResponse);

        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andDo(print())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.accessToken").value("jwt-token-123"))
                .andExpect(jsonPath("$.role").value("ADMIN"));

        verify(authServicePort).authenticate("test@example.com", "password123");
        verify(authRequestMapper).toAuthResponse(authToken);
    }

    @Test
    void testLogin_WithInvalidRequestFormat_ShouldReturnBadRequest() throws Exception {
        String invalidJson = "{ invalid json }";

        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidJson))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(authServicePort);
        verifyNoInteractions(authRequestMapper);
    }

    @Test
    void testLogin_WithMissingEmail_ShouldReturnBadRequest() throws Exception {
        LoginRequestDto loginRequest = new LoginRequestDto();
        loginRequest.setPassword("password123");

        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(authServicePort);
        verifyNoInteractions(authRequestMapper);
    }

    @Test
    void testLogin_WithMissingPassword_ShouldReturnBadRequest() throws Exception {
        LoginRequestDto loginRequest = new LoginRequestDto();
        loginRequest.setEmail("test@example.com");

        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(authServicePort);
        verifyNoInteractions(authRequestMapper);
    }

    @Test
    void testValidateToken_WithValidToken_ShouldReturnTrue() throws Exception {
        String validToken = "valid-jwt-token";
        when(authServicePort.validateToken(validToken)).thenReturn(true);

        mockMvc.perform(post("/auth/validate")
                        .param("token", validToken))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().string("true"));

        verify(authServicePort).validateToken(validToken);
    }

    @Test
    void testValidateToken_WithInvalidToken_ShouldReturnFalse() throws Exception {
        String invalidToken = "invalid-jwt-token";
        when(authServicePort.validateToken(invalidToken)).thenReturn(false);

        mockMvc.perform(post("/auth/validate")
                        .param("token", invalidToken))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().string("false"));

        verify(authServicePort).validateToken(invalidToken);
    }

    @Test
    void testValidateToken_WithMissingToken_ShouldReturnBadRequest() throws Exception {
        mockMvc.perform(post("/auth/validate"))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(authServicePort);
    }

    @Test
    void testValidateToken_WithEmptyToken_ShouldCallService() throws Exception {
        String emptyToken = "";
        when(authServicePort.validateToken(emptyToken)).thenReturn(false);

        mockMvc.perform(post("/auth/validate")
                        .param("token", emptyToken))
                .andExpect(status().isOk())
                .andExpect(content().string("false"));

        verify(authServicePort).validateToken(emptyToken);
    }
}