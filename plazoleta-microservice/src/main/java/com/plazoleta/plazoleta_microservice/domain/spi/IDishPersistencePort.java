package com.plazoleta.plazoleta_microservice.domain.spi;

import com.plazoleta.plazoleta_microservice.domain.model.Dish;

import java.util.List;
import java.util.Optional;

public interface IDishPersistencePort {
    Dish saveDish(Dish dish);

    void updateDish(Dish dish);

    Optional<Dish> findDishById(Long id);

    List<Dish> findAllDishesByRestaurantIdAndCategoryId(Long restaurantId, Long categoryId, int pageIndex, int elementsPerPage);

    List<Dish> findDishesByRestaurantId(Long restaurantId);
}
