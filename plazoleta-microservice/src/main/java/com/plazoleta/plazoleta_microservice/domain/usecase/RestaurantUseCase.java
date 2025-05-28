package com.plazoleta.plazoleta_microservice.domain.usecase;

import com.plazoleta.plazoleta_microservice.domain.api.IRestaurantServicePort;
import com.plazoleta.plazoleta_microservice.domain.exception.restaurant.DuplicateNitException;
import com.plazoleta.plazoleta_microservice.domain.exception.restaurant.InvalidUserRoleException;
import com.plazoleta.plazoleta_microservice.domain.exception.restaurant.UserNotFoundException;
import com.plazoleta.plazoleta_microservice.domain.model.Restaurant;
import com.plazoleta.plazoleta_microservice.domain.model.User;
import com.plazoleta.plazoleta_microservice.domain.spi.IRestaurantPersistencePort;
import com.plazoleta.plazoleta_microservice.domain.spi.IUserSecurityPort;

public class RestaurantUseCase implements IRestaurantServicePort {

    private final IRestaurantPersistencePort restaurantPersistencePort;
    private final IUserSecurityPort userSecurityPort;

    public RestaurantUseCase(IRestaurantPersistencePort restaurantPersistencePort, IUserSecurityPort userSecurityPort) {
        this.restaurantPersistencePort = restaurantPersistencePort;
        this.userSecurityPort = userSecurityPort;
    }

    @Override
    public void createRestaurant(Restaurant restaurant) {
        User owner = userSecurityPort.getUserById(restaurant.getIdOwner());
        if (owner == null) {
            throw new UserNotFoundException("User not found");
        }

        if (!"ROLE_OWNER".equalsIgnoreCase(owner.getRole())) {
            throw new InvalidUserRoleException("User does not have the required role");
        }

        if (restaurantPersistencePort.existsByNit(restaurant.getNit())){
            throw new DuplicateNitException("A restaurant with the NIT '" + restaurant.getNit() + "' already exists.");
        }

        restaurantPersistencePort.saveRestaurant(restaurant);
    }
}
