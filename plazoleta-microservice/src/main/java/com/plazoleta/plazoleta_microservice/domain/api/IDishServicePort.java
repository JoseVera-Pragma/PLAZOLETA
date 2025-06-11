package com.plazoleta.plazoleta_microservice.domain.api;

import com.plazoleta.plazoleta_microservice.domain.model.Dish;
import com.plazoleta.plazoleta_microservice.domain.util.Page;

public interface IDishServicePort {
    Dish saveDish(Dish dish);

    void updateDishPriceAndDescription(Long dishId, Dish dish);

    Dish findDishById(Long id);

    void changeDishStatus(Long dishId, boolean activate);

    Page<Dish> findAllDishesByRestaurantIdAndCategoryId(Long restaurantId, Long categoryId, int pageIndex, int elementsPerPage);
}
