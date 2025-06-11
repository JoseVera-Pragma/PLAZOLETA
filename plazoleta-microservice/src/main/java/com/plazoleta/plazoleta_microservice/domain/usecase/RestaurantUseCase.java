package com.plazoleta.plazoleta_microservice.domain.usecase;

import com.plazoleta.plazoleta_microservice.domain.api.IRestaurantServicePort;
import com.plazoleta.plazoleta_microservice.domain.exception.restaurant.DuplicateNitException;
import com.plazoleta.plazoleta_microservice.domain.exception.restaurant.InvalidUserRoleException;
import com.plazoleta.plazoleta_microservice.domain.util.Page;
import com.plazoleta.plazoleta_microservice.domain.model.Restaurant;
import com.plazoleta.plazoleta_microservice.domain.model.User;
import com.plazoleta.plazoleta_microservice.domain.spi.IRestaurantPersistencePort;
import com.plazoleta.plazoleta_microservice.domain.spi.IUserServiceClientPort;
import com.plazoleta.plazoleta_microservice.domain.validator.RestaurantValidator;

public class RestaurantUseCase implements IRestaurantServicePort {

    private final IRestaurantPersistencePort restaurantPersistencePort;
    private final IUserServiceClientPort userServiceClientPort;

    public RestaurantUseCase(IRestaurantPersistencePort restaurantPersistencePort, IUserServiceClientPort userSecurityPort) {
        this.restaurantPersistencePort = restaurantPersistencePort;
        this.userServiceClientPort = userSecurityPort;
    }

    @Override
    public Restaurant createRestaurant(Restaurant restaurant) {
        User owner = userServiceClientPort.findUserById(restaurant.getIdOwner());

        if (!"ROLE_OWNER".equalsIgnoreCase(owner.getRole())) {
            throw new InvalidUserRoleException("User does not have the required role");
        }

        if (restaurantPersistencePort.existsRestaurantByNit(restaurant.getNit())){
            throw new DuplicateNitException("A restaurant with the NIT '" + restaurant.getNit() + "' already exists.");
        }

        RestaurantValidator.validateRestaurant(restaurant);

        return restaurantPersistencePort.saveRestaurant(restaurant);
    }

    @Override
    public Page<Restaurant> findAllRestaurants(int pageIndex, int elementsPerPage){
        return restaurantPersistencePort.findAllRestaurants(pageIndex, elementsPerPage);
    }
}
