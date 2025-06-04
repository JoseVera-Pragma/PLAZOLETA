package com.plazoleta.user_microservice.domain.spi;

import com.plazoleta.user_microservice.domain.model.Role;

import java.util.Optional;

public interface IAuthenticatedUserPort {
    Optional<Long> getCurrentUserId();
    Role getCurrentUserRole();
}
