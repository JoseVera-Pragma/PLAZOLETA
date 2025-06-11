package com.plazoleta.plazoleta_microservice.domain.spi;

import com.plazoleta.plazoleta_microservice.domain.model.Restaurant;
import com.plazoleta.plazoleta_microservice.domain.util.Page;

import java.util.Optional;

public interface IRestaurantPersistencePort {
    Restaurant saveRestaurant(Restaurant restaurant);
    boolean existsRestaurantByNit(String nit);
    Optional<Restaurant> findRestaurantById(Long id);
    Page<Restaurant> findAllRestaurants(int pageIndex, int elementsPerPage);
}
