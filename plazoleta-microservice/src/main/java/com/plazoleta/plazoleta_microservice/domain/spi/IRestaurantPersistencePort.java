package com.plazoleta.plazoleta_microservice.domain.spi;

import com.plazoleta.plazoleta_microservice.domain.model.Restaurant;

import java.util.List;
import java.util.Optional;

public interface IRestaurantPersistencePort {
    Restaurant saveRestaurant(Restaurant restaurant);
    boolean existsRestaurantByNit(String nit);
    Optional<Restaurant> findRestaurantById(Long id);
    List<Restaurant> findAllRestaurants(int pageIndex, int elementsPerPage);
}
