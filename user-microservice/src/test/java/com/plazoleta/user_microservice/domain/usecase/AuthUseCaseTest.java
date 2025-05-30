package com.plazoleta.user_microservice.domain.usecase;

import com.plazoleta.user_microservice.domain.exception.UserNotFoundException;
import com.plazoleta.user_microservice.domain.model.*;
import com.plazoleta.user_microservice.domain.spi.IPasswordEncoderPort;
import com.plazoleta.user_microservice.domain.spi.ITokenGeneratorPort;
import com.plazoleta.user_microservice.domain.spi.IUserPersistencePort;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthUseCaseTest {

    @Mock
    private IUserPersistencePort userPersistencePort;

    @Mock
    private IPasswordEncoderPort passwordEncoderPort;

    @Mock
    private ITokenGeneratorPort tokenGeneratorPort;

    @InjectMocks
    private AuthUseCase authUseCase;

    private final String email = "test@example.com";
    private final String password = "password123";
    private final String encodedPassword = "encodedPassword";
    private final Long userId = 1L;
    private final String role = "ROLE_OWNER";
    private final String token = "valid.jwt.token";

    private User buildUser() {
        return new User.Builder()
                .id(userId)
                .firstName("First")
                .lastName("Last")
                .dateOfBirth(LocalDate.of(2000,1,1))
                .identityNumber(new IdentityNumber("1321321321"))
                .phoneNumber(new PhoneNumber("+57545123132"))
                .email(new Email(email))
                .password(encodedPassword)
                .role(new Role(2L, RoleList.ROLE_OWNER, "adf"))
                .build();
    }

    @Test
    void shouldReturnTokenWhenCredentialsAreValid() {
        User user = buildUser();

        when(userPersistencePort.getUserByEmail(new Email(email))).thenReturn(user);
        when(passwordEncoderPort.matches(password, encodedPassword)).thenReturn(true);
        when(tokenGeneratorPort.generateToken(user)).thenReturn(token);

        AuthToken authToken = authUseCase.authenticate(email, password);

        assertEquals(token, authToken.getToken());
        assertEquals(email, authToken.getEmail());
        assertEquals(role, authToken.getRole());
        assertEquals(userId, authToken.getUserId());
    }

    @Test
    void shouldThrowWhenUserIsNotFound() {
        when(userPersistencePort.getUserByEmail(new Email(email))).thenReturn(null);

        UserNotFoundException exception = assertThrows(UserNotFoundException.class,
                () -> authUseCase.authenticate(email, password));

        assertEquals("User not found", exception.getMessage());
    }

    @Test
    void shouldThrowWhenPasswordDoesNotMatch() {
        User user = buildUser();

        when(userPersistencePort.getUserByEmail(new Email(email))).thenReturn(user);
        when(passwordEncoderPort.matches(password, encodedPassword)).thenReturn(false);

        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> authUseCase.authenticate(email, password));

        assertEquals("Invalid credentials", exception.getMessage());
    }

    @Test
    void shouldReturnTrueWhenTokenIsValid() {
        when(tokenGeneratorPort.validateToken(token)).thenReturn(true);

        assertTrue(authUseCase.validateToken(token));
    }

    @Test
    void shouldReturnFalseWhenTokenIsInvalid() {
        when(tokenGeneratorPort.validateToken(token)).thenReturn(false);

        assertFalse(authUseCase.validateToken(token));
    }

    @Test
    void shouldExtractEmailFromToken() {
        when(tokenGeneratorPort.extractEmail(token)).thenReturn(email);

        String result = authUseCase.extractEmailFromToken(token);

        assertEquals(email, result);
    }

    @Test
    void shouldExtractRoleFromToken() {
        when(tokenGeneratorPort.extractRole(token)).thenReturn(role);

        String result = authUseCase.extractRoleFromToken(token);

        assertEquals(role, result);
    }
}
