package com.plazoleta.user_microservice.domain.usecase;

import com.plazoleta.user_microservice.domain.api.IAuthServicePort;
import com.plazoleta.user_microservice.domain.exception.UserNotFoundException;
import com.plazoleta.user_microservice.domain.model.AuthToken;
import com.plazoleta.user_microservice.domain.model.User;
import com.plazoleta.user_microservice.domain.spi.IPasswordEncoderPort;
import com.plazoleta.user_microservice.domain.spi.ITokenGeneratorPort;
import com.plazoleta.user_microservice.domain.spi.IUserPersistencePort;

public class AuthUseCase implements IAuthServicePort {

    private final IUserPersistencePort userPersistencePort;
    private final IPasswordEncoderPort passwordEncoderPort;
    private final ITokenGeneratorPort tokenGeneratorPort;

    public AuthUseCase(IUserPersistencePort userPersistencePort,
                       IPasswordEncoderPort passwordEncoderPort,
                       ITokenGeneratorPort tokenGeneratorPort) {
        this.userPersistencePort = userPersistencePort;
        this.passwordEncoderPort = passwordEncoderPort;
        this.tokenGeneratorPort = tokenGeneratorPort;
    }

    @Override
    public AuthToken authenticate(String email, String password) {
        User user = userPersistencePort.getUserByEmail(email).orElseThrow(
                () -> new UserNotFoundException("User not found")
        );

        if (!passwordEncoderPort.matches(password, user.getPassword())) {
            throw new RuntimeException("Invalid credentials");
        }

        String token = tokenGeneratorPort.generateToken(user);
        return new AuthToken(token, user.getEmail(), user.getRole().getName().name(), user.getId());
    }

    @Override
    public boolean validateToken(String token) {
        return tokenGeneratorPort.validateToken(token);
    }

    @Override
    public String extractEmailFromToken(String token) {
        return tokenGeneratorPort.extractEmail(token);
    }

    @Override
    public String extractRoleFromToken(String token) {
        return tokenGeneratorPort.extractRole(token);
    }
}
