package com.plazoleta.plazoleta_microservice.domain.api;

import com.plazoleta.plazoleta_microservice.domain.model.Dish;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface IDishServicePort {
    Dish save(Long ownerId, Dish dish);

    void update(Long ownerId, Dish dish);

    Dish getById(Long id);

    void changeDishStatus(Long ownerId, Long dishId, boolean activate);

    Page<Dish> findAllByRestaurantIdAndCategoryId(Long restaurantId, Long categoryId, Pageable pageable);

    List<Dish> getDishesByRestaurantId(Long restaurantId);
}
