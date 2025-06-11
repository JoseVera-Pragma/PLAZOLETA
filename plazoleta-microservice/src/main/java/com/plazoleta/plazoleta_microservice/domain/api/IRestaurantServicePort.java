package com.plazoleta.plazoleta_microservice.domain.api;

import com.plazoleta.plazoleta_microservice.domain.model.Restaurant;
import com.plazoleta.plazoleta_microservice.domain.util.Page;

public interface IRestaurantServicePort {
    Restaurant createRestaurant(Restaurant restaurant);
    Page<Restaurant> findAllRestaurants(int pageIndex, int elementsPerPage);
}
