package com.plazoleta.plazoleta_microservice.domain.usecase;

import com.plazoleta.plazoleta_microservice.domain.api.IDishServicePort;
import com.plazoleta.plazoleta_microservice.domain.exception.category.CategoryNotFoundException;
import com.plazoleta.plazoleta_microservice.domain.exception.dish.DishNotFoundException;
import com.plazoleta.plazoleta_microservice.domain.exception.dish.UnauthorizedOwnerException;
import com.plazoleta.plazoleta_microservice.domain.exception.restaurant.RestaurantNotFoundException;
import com.plazoleta.plazoleta_microservice.domain.exception.restaurant.UserNotFoundException;
import com.plazoleta.plazoleta_microservice.domain.model.Category;
import com.plazoleta.plazoleta_microservice.domain.model.Dish;
import com.plazoleta.plazoleta_microservice.domain.model.Restaurant;
import com.plazoleta.plazoleta_microservice.domain.spi.*;
import com.plazoleta.plazoleta_microservice.domain.util.Page;
import com.plazoleta.plazoleta_microservice.domain.validator.DishValidator;

import java.util.List;

public class DishUseCase implements IDishServicePort {
    private final IDishPersistencePort dishPersistencePort;
    private final IRestaurantPersistencePort restaurantPersistencePort;
    private final ICategoryPersistencePort categoryPersistencePort;
    private final IAuthenticatedUserPort authenticatedUserPort;

    public DishUseCase(ICategoryPersistencePort categoryPersistencePort, IDishPersistencePort dishPersistencePort, IRestaurantPersistencePort restaurantPersistencePort, IAuthenticatedUserPort authenticatedUserPort) {
        this.categoryPersistencePort = categoryPersistencePort;
        this.dishPersistencePort = dishPersistencePort;
        this.restaurantPersistencePort = restaurantPersistencePort;
        this.authenticatedUserPort = authenticatedUserPort;
    }

    @Override
    public Dish saveDish(Dish dish) {
        Restaurant restaurant = restaurantPersistencePort.findRestaurantById(dish.getRestaurantId()).orElseThrow(
                () -> new RestaurantNotFoundException("Restaurant not found."));

        Long ownerId = authenticatedUserPort.getCurrentUserId().orElseThrow(
                () -> new UserNotFoundException("User not found."));

        if (!ownerId.equals(restaurant.getIdOwner())) {
            throw new UnauthorizedOwnerException("Owner ID " + ownerId + " is not authorized to create dishes for restaurant with ID " + dish.getRestaurantId());
        }

        Category category = categoryPersistencePort.findCategoryByName(dish.getCategory().getName()).orElseThrow(() ->
                new CategoryNotFoundException("Category with name '" + dish.getCategory().getName() + "' not found"));

        Dish dishToSave = dish.withCategory(category).activate();

        DishValidator.validate(dishToSave);

        return dishPersistencePort.saveDish(dishToSave);
    }

    @Override
    public Dish findDishById(Long id) {
        return dishPersistencePort.findDishById(id).orElseThrow(() -> new DishNotFoundException("Dish not found."));
    }

    @Override
    public void updateDishPriceAndDescription(Long dishId, Dish dish) {

        Dish existingDish = dishPersistencePort.findDishById(dishId).orElseThrow(
                () -> new DishNotFoundException("Dish with ID " + dishId + " not found")
        );

        Restaurant restaurant = restaurantPersistencePort
                .findRestaurantById(existingDish.getRestaurantId()).orElseThrow(
                        () -> new RestaurantNotFoundException("Restaurant with ID " + existingDish.getRestaurantId() + " not found")
                );

        Long ownerId = authenticatedUserPort.getCurrentUserId().orElseThrow(
                () -> new UserNotFoundException("User not found."));

        if (!ownerId.equals(restaurant.getIdOwner())) {
            throw new UnauthorizedOwnerException("Owner ID " + ownerId + " is not authorized to updateDishPriceAndDescription dishes for restaurant with ID " + dish.getRestaurantId());
        }

        Dish updatedDish = existingDish.withPriceAndDescription(dish.getPrice(), dish.getDescription());

        DishValidator.validate(updatedDish);

        dishPersistencePort.updateDish(updatedDish);
    }

    @Override
    public void changeDishStatus(Long dishId, boolean activate) {

        Dish dish = dishPersistencePort.findDishById(dishId).orElseThrow(
                () -> new DishNotFoundException("Dish with ID " + dishId + " not found")
        );

        Restaurant restaurant = restaurantPersistencePort
                .findRestaurantById(dish.getRestaurantId()).orElseThrow(
                        () -> new RestaurantNotFoundException("Restaurant with ID " + dish.getRestaurantId() + " not found")
                );

        Long ownerId = authenticatedUserPort.getCurrentUserId().orElseThrow(
                () -> new UserNotFoundException("User not found."));
        if (!ownerId.equals(restaurant.getIdOwner())) {
            throw new UnauthorizedOwnerException("Owner ID " + ownerId + " is not authorized to updateDishPriceAndDescription dishes for restaurant with ID " + dish.getRestaurantId());
        }

        Dish updatedDish = activate ? dish.activate() : dish.deactivate();

        dishPersistencePort.updateDish(updatedDish);
    }

    @Override
    public Page<Dish> findAllDishesByRestaurantIdAndCategoryId(Long restaurantId, Long categoryId, int pageIndex, int elementsPerPage) {

        categoryPersistencePort.findCategoryById(categoryId).orElseThrow(() ->
                new CategoryNotFoundException("Category with Id '" + categoryId + "' not found"));


        restaurantPersistencePort.findRestaurantById(restaurantId).orElseThrow(
                ()-> new RestaurantNotFoundException("Restaurant with ID " + restaurantId + " not found.")
        );

        return dishPersistencePort.findAllDishesByRestaurantIdAndCategoryId(restaurantId, categoryId, pageIndex, elementsPerPage );
    }
}
