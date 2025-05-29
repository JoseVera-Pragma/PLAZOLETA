package com.plazoleta.plazoleta_microservice.domain.usecase;

import com.plazoleta.plazoleta_microservice.domain.api.IDishServicePort;
import com.plazoleta.plazoleta_microservice.domain.exception.category.CategoryNotFoundException;
import com.plazoleta.plazoleta_microservice.domain.exception.dish.DishNotFoundException;
import com.plazoleta.plazoleta_microservice.domain.exception.dish.UnauthorizedOwnerException;
import com.plazoleta.plazoleta_microservice.domain.exception.restaurant.RestaurantNotFoundException;
import com.plazoleta.plazoleta_microservice.domain.model.Category;
import com.plazoleta.plazoleta_microservice.domain.model.Dish;
import com.plazoleta.plazoleta_microservice.domain.model.Restaurant;
import com.plazoleta.plazoleta_microservice.domain.spi.ICategoryPersistencePort;
import com.plazoleta.plazoleta_microservice.domain.spi.IDishPersistencePort;
import com.plazoleta.plazoleta_microservice.domain.spi.IRestaurantPersistencePort;

public class DishUseCase implements IDishServicePort {
    private final IDishPersistencePort dishPersistencePort;
    private final IRestaurantPersistencePort restaurantPersistencePort;
    private final ICategoryPersistencePort categoryPersistencePort;

    public DishUseCase(ICategoryPersistencePort categoryPersistencePort, IDishPersistencePort dishPersistencePort, IRestaurantPersistencePort restaurantPersistencePort) {
        this.categoryPersistencePort = categoryPersistencePort;
        this.dishPersistencePort = dishPersistencePort;
        this.restaurantPersistencePort = restaurantPersistencePort;
    }

    @Override
    public Dish save(Long ownerId, Dish dish) {
        Restaurant restaurant = restaurantPersistencePort
                .getById(dish.getRestaurantId());

        if (restaurant == null) {
            throw new RestaurantNotFoundException("Restaurant with ID " + dish.getRestaurantId() + " not found");
        }

        if (!ownerId.equals(restaurant.getIdOwner())) {
            throw new UnauthorizedOwnerException("Owner ID " + ownerId + " is not authorized to create dishes for restaurant with ID " + dish.getRestaurantId());
        }

        Category category = categoryPersistencePort.findByName(dish.getCategory().getName()).orElseThrow(() ->
                new CategoryNotFoundException("Category with name '" + dish.getCategory().getName() + "' not found"));

        Dish dishToSave = Dish.builder()
                .id(dish.getId())
                .name(dish.getName())
                .description(dish.getDescription())
                .price(dish.getPrice())
                .restaurantId(dish.getRestaurantId())
                .imageUrl(dish.getImageUrl())
                .category(category)
                .build();
        return dishPersistencePort.saveDish(dishToSave);
    }

    @Override
    public Dish getById(Long id) {
        return dishPersistencePort.getById(id);
    }

    @Override
    public void update(Long ownerId, Dish dish) {

        Dish existingDish = dishPersistencePort.getById(dish.getId());

        if (existingDish == null) {
            throw new DishNotFoundException("Dish with ID " + dish.getId() + " not found");
        }

        Restaurant restaurant = restaurantPersistencePort
                .getById(dish.getRestaurantId());

        if (restaurant == null) {
            throw new RestaurantNotFoundException("Restaurant with ID " + dish.getRestaurantId() + " not found");
        }

        if (!ownerId.equals(restaurant.getIdOwner())) {
            throw new UnauthorizedOwnerException("Owner ID " + ownerId + " is not authorized to update dishes for restaurant with ID " + dish.getRestaurantId());
        }

        Dish updatedDish = Dish.builder()
                .id(existingDish.getId())
                .name(existingDish.getName())
                .description(dish.getDescription())
                .price(dish.getPrice())
                .restaurantId(existingDish.getRestaurantId())
                .imageUrl(existingDish.getImageUrl())
                .category(existingDish.getCategory())
                .build();
        dishPersistencePort.updateDish(updatedDish);
    }
}
