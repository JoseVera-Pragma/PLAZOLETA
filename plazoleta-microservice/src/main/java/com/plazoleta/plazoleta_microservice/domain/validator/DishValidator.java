package com.plazoleta.plazoleta_microservice.domain.validator;

import com.plazoleta.plazoleta_microservice.domain.exception.dish.InvalidDishDataException;
import com.plazoleta.plazoleta_microservice.domain.model.Category;

public class DishValidator {
    public static void validate(String name, Double price, String description, String imageUrl, Category category, Long restaurantId) {
        if (name == null || name.isBlank()) {
            throw new InvalidDishDataException("The name of the dish is mandatory.");
        }

        if (price <= 0) {
            throw new InvalidDishDataException("The price of the dish must be a positive integer greater than 0.");
        }

        if (description == null || description.isBlank()) {
            throw new InvalidDishDataException("The description of the dish is mandatory.");
        }

        if (imageUrl == null || imageUrl.isBlank()) {
            throw new InvalidDishDataException("The URL of the image of the plate is mandatory.");
        }

        if (category == null || category.getName().isBlank()) {
            throw new InvalidDishDataException("The plate category is mandatory.");
        }

        if (restaurantId == null) {
            throw new InvalidDishDataException("The dish must be associated with a restaurant.");
        }
    }
}
