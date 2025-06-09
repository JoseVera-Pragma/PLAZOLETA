package com.plazoleta.traceability_microservice.domain.spi;

import java.util.Optional;

public interface IAuthenticatedUserPort {
    Optional<Long> getCurrentUserId();
}
