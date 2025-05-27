package com.plazoleta.plazoleta_microservice.domain.api;

import com.plazoleta.plazoleta_microservice.domain.model.Restaurant;

public interface IRestaurantServicePort {
    void createRestaurant(Restaurant restaurant);
}
