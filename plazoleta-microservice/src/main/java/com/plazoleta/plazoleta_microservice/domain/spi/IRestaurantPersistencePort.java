package com.plazoleta.plazoleta_microservice.domain.spi;

import com.plazoleta.plazoleta_microservice.domain.model.Restaurant;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface IRestaurantPersistencePort {
    void saveRestaurant(Restaurant restaurant);
    boolean existsByNit(String nit);
    Restaurant getById(Long id);
    Page<Restaurant> findAll(Pageable pageable);
}
