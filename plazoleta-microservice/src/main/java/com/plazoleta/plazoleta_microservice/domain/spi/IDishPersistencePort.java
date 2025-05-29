package com.plazoleta.plazoleta_microservice.domain.spi;

import com.plazoleta.plazoleta_microservice.domain.model.Dish;

public interface IDishPersistencePort {
    Dish saveDish(Dish dish);
    void updateDish(Dish dish);
    Dish getById(Long id);
}
