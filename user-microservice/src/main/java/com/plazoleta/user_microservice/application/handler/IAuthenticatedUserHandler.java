package com.plazoleta.user_microservice.application.handler;

import java.util.Optional;

public interface IAuthenticatedUserHandler {
    Optional<Long> getCurrentUserId();
    Optional<String> getCurrentUserRole();
}
