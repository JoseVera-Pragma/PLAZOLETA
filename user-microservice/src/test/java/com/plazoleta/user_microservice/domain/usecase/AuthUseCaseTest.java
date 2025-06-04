package com.plazoleta.user_microservice.domain.usecase;

import com.plazoleta.user_microservice.domain.exception.UserNotFoundException;
import com.plazoleta.user_microservice.domain.model.*;
import com.plazoleta.user_microservice.domain.spi.IPasswordEncoderPort;
import com.plazoleta.user_microservice.domain.spi.ITokenGeneratorPort;
import com.plazoleta.user_microservice.domain.spi.IUserPersistencePort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AuthUseCaseTest {

    private IUserPersistencePort userPersistencePort;
    private IPasswordEncoderPort passwordEncoderPort;
    private ITokenGeneratorPort tokenGeneratorPort;
    private AuthUseCase authUseCase;

    @BeforeEach
    void setUp() {
        userPersistencePort = mock(IUserPersistencePort.class);
        passwordEncoderPort = mock(IPasswordEncoderPort.class);
        tokenGeneratorPort = mock(ITokenGeneratorPort.class);
        authUseCase = new AuthUseCase(userPersistencePort, passwordEncoderPort, tokenGeneratorPort);
    }

    private User mockUser() {
        return User.builder()
                .id(1L)
                .firstName("Jose")
                .lastName("Vera")
                .identityNumber("123456789")
                .phoneNumber("3000000000")
                .dateOfBirth(LocalDate.of(1990, 1, 1))
                .email("test@example.com")
                .password("encodedPassword")
                .role(new Role(1L, RoleList.ROLE_EMPLOYED,"ROLE_EMPLOYED"))
                .build();
    }

    @Test
    void authenticate_shouldReturnToken_whenCredentialsAreValid() {
        User user = mockUser();
        String rawPassword = "1234";
        String token = "valid.jwt.token";

        when(userPersistencePort.getUserByEmail(user.getEmail())).thenReturn(Optional.of(user));
        when(passwordEncoderPort.matches(rawPassword, user.getPassword())).thenReturn(true);
        when(tokenGeneratorPort.generateToken(user)).thenReturn(token);

        AuthToken result = authUseCase.authenticate(user.getEmail(), rawPassword);

        assertEquals(token, result.getToken());
        assertEquals(user.getEmail(), result.getEmail());
        assertEquals(user.getRole().getName().name(), result.getRole());
        assertEquals(user.getId(), result.getUserId());
    }

    @Test
    void authenticate_shouldThrowUserNotFoundException_whenEmailNotFound() {
        String email = "notfound@example.com";

        when(userPersistencePort.getUserByEmail(email)).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> authUseCase.authenticate(email, "password"));
    }

    @Test
    void authenticate_shouldThrowRuntimeException_whenPasswordDoesNotMatch() {
        User user = mockUser();
        String wrongPassword = "wrong";

        when(userPersistencePort.getUserByEmail(user.getEmail())).thenReturn(Optional.of(user));
        when(passwordEncoderPort.matches(wrongPassword, user.getPassword())).thenReturn(false);

        assertThrows(RuntimeException.class, () -> authUseCase.authenticate(user.getEmail(), wrongPassword));
    }

    @Test
    void validateToken_shouldDelegateToTokenGenerator() {
        String token = "token123";

        when(tokenGeneratorPort.validateToken(token)).thenReturn(true);

        assertTrue(authUseCase.validateToken(token));
        verify(tokenGeneratorPort).validateToken(token);
    }

    @Test
    void extractEmailFromToken_shouldDelegateToTokenGenerator() {
        String token = "token123";
        String expectedEmail = "test@example.com";

        when(tokenGeneratorPort.extractEmail(token)).thenReturn(expectedEmail);

        assertEquals(expectedEmail, authUseCase.extractEmailFromToken(token));
    }

    @Test
    void extractRoleFromToken_shouldDelegateToTokenGenerator() {
        String token = "token123";
        String expectedRole = "ROLE_OWNER";

        when(tokenGeneratorPort.extractRole(token)).thenReturn(expectedRole);

        assertEquals(expectedRole, authUseCase.extractRoleFromToken(token));
    }
}