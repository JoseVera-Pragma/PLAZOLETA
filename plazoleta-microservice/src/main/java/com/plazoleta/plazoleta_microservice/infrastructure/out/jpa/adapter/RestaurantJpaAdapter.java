package com.plazoleta.plazoleta_microservice.infrastructure.out.jpa.adapter;

import com.plazoleta.plazoleta_microservice.domain.model.Restaurant;
import com.plazoleta.plazoleta_microservice.domain.spi.IRestaurantPersistencePort;
import com.plazoleta.plazoleta_microservice.infrastructure.exception.RestaurantNotFoundException;
import com.plazoleta.plazoleta_microservice.infrastructure.out.jpa.entity.RestaurantEntity;
import com.plazoleta.plazoleta_microservice.infrastructure.out.jpa.mapper.IRestaurantEntityMapper;
import com.plazoleta.plazoleta_microservice.infrastructure.out.jpa.repository.IRestaurantRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class RestaurantJpaAdapter implements IRestaurantPersistencePort {

    private final IRestaurantRepository restaurantRepository;
    private final IRestaurantEntityMapper restaurantEntityMapper;

    @Override
    public void saveRestaurant(Restaurant restaurant) {
        RestaurantEntity entity = restaurantEntityMapper.toRestaurantEntity(restaurant);
        restaurantRepository.save(entity);
    }

    @Override
    public boolean existsByNit(String nit){
        return restaurantRepository.existsByNit(nit);
    }

    @Override
    public Restaurant getById(Long id) {
        return restaurantEntityMapper.toRestaurant(restaurantRepository.findById(id).orElseThrow(()->
                new RestaurantNotFoundException("Restaurant with ID " + id + " not found")
        ));
    }

    @Override
    public Page<Restaurant> findAll(Pageable pageable) {
        return restaurantRepository.findAll(pageable).map(restaurantEntityMapper::toRestaurant);
    }
}
