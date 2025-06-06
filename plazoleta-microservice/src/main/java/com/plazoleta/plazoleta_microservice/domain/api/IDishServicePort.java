package com.plazoleta.plazoleta_microservice.domain.api;

import com.plazoleta.plazoleta_microservice.domain.model.Dish;

import java.util.List;

public interface IDishServicePort {
    Dish saveDish(Dish dish);

    void updateDishPriceAndDescription(Long dishId, Dish dish);

    Dish findDishById(Long id);

    void changeDishStatus(Long dishId, boolean activate);

    List<Dish> findAllDishesByRestaurantIdAndCategoryId(Long restaurantId,Long categoryId, int pageIndex, int elementsPerPage);
}
