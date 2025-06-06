package com.plazoleta.plazoleta_microservice.domain.spi;

import com.plazoleta.plazoleta_microservice.domain.model.User;

public interface IUserServiceClientPort {
    User findUserById(Long id);
}
