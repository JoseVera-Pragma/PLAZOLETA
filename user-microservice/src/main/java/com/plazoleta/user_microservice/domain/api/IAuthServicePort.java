package com.plazoleta.user_microservice.domain.api;

import com.plazoleta.user_microservice.domain.model.AuthToken;

public interface IAuthServicePort {
    AuthToken authenticate(String email, String password);
    boolean validateToken(String token);
    String extractEmailFromToken(String token);
    String extractRoleFromToken(String token);
}
