package com.plazoleta.plazoleta_microservice.domain.spi;

import com.plazoleta.plazoleta_microservice.domain.model.Dish;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface IDishPersistencePort {
    Dish saveDish(Dish dish);
    void updateDish(Dish dish);
    Dish getById(Long id);
    Page<Dish> findAllByRestaurantIdAndCategoryId(Long restaurantId, Long categoryId, Pageable pageable);
}
