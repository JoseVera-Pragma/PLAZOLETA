package com.plazoleta.plazoleta_microservice.domain.api;

import com.plazoleta.plazoleta_microservice.domain.model.Dish;

public interface IDishServicePort {
    Dish save(Long ownerId, Dish dish);
    void update(Long ownerId, Dish dish);
    Dish getById(Long id);
}
