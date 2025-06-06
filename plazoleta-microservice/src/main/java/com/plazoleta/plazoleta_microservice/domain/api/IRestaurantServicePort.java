package com.plazoleta.plazoleta_microservice.domain.api;

import com.plazoleta.plazoleta_microservice.domain.model.Restaurant;

import java.util.List;

public interface IRestaurantServicePort {
    Restaurant createRestaurant(Restaurant restaurant);
    List<Restaurant> findAllRestaurants(int pageIndex, int elementsPerPage);
}
