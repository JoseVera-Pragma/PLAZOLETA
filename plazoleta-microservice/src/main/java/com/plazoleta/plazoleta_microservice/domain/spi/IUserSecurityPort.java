package com.plazoleta.plazoleta_microservice.domain.spi;

import com.plazoleta.plazoleta_microservice.domain.model.User;

public interface IUserSecurityPort {
    User getUserById(Long id);
}
