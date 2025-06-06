package com.plazoleta.plazoleta_microservice.domain.validator;

import com.plazoleta.plazoleta_microservice.domain.exception.dish.InvalidDishDataException;
import com.plazoleta.plazoleta_microservice.domain.model.Category;
import com.plazoleta.plazoleta_microservice.domain.model.Dish;

public class DishValidator {
    private DishValidator() {}

    public static void validate(Dish dish) {
        if (dish == null) {
            throw new InvalidDishDataException("Dish cannot be null.");
        }

        if (dish.getName() == null || dish.getName().isBlank()) {
            throw new InvalidDishDataException("Dish name is required.");
        }

        if (dish.getPrice() == null || dish.getPrice() <= 0) {
            throw new InvalidDishDataException("Dish price must be greater than 0.");
        }

        if (dish.getDescription() == null || dish.getDescription().isBlank()) {
            throw new InvalidDishDataException("Dish description is required.");
        }

        if (dish.getImageUrl() == null || dish.getImageUrl().isBlank()) {
            throw new InvalidDishDataException("Dish image URL is required.");
        }

        Category category = dish.getCategory();
        if (category == null || category.getName() == null || category.getName().isBlank()) {
            throw new InvalidDishDataException("Dish must be linked to a restaurant.");
        }

        if (dish.getRestaurantId() == null) {
            throw new InvalidDishDataException("The dish must be associated with a restaurant.");
        }
    }
}
