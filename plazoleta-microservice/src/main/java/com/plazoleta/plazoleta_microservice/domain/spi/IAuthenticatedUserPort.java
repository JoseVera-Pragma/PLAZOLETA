package com.plazoleta.plazoleta_microservice.domain.spi;

import java.util.Optional;

public interface IAuthenticatedUserPort {
    Optional<Long> getCurrentUserId();
}
