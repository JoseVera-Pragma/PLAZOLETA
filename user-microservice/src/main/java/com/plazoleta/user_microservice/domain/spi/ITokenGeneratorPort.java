package com.plazoleta.user_microservice.domain.spi;

import com.plazoleta.user_microservice.domain.model.User;

public interface ITokenGeneratorPort {
    String generateToken(User user);
    boolean validateToken(String token);
    String extractEmail(String token);
    String extractRole(String token);
    Long extractUserId(String token);
}
