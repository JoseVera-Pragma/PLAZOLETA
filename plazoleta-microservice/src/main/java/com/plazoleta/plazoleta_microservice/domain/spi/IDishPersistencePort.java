package com.plazoleta.plazoleta_microservice.domain.spi;

import com.plazoleta.plazoleta_microservice.domain.model.Dish;

import java.util.Optional;

public interface IDishPersistencePort {
    Dish saveDish(Dish dish);
}
