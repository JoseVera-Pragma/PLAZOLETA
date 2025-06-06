package com.plazoleta.plazoleta_microservice.infrastructure.out.jpa.adapter;

import com.plazoleta.plazoleta_microservice.domain.model.Restaurant;
import com.plazoleta.plazoleta_microservice.domain.spi.IRestaurantPersistencePort;
import com.plazoleta.plazoleta_microservice.infrastructure.out.jpa.entity.RestaurantEntity;
import com.plazoleta.plazoleta_microservice.infrastructure.out.jpa.mapper.IRestaurantEntityMapper;
import com.plazoleta.plazoleta_microservice.infrastructure.out.jpa.repository.IRestaurantRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class RestaurantJpaAdapter implements IRestaurantPersistencePort {

    private final IRestaurantRepository restaurantRepository;
    private final IRestaurantEntityMapper restaurantEntityMapper;

    @Override
    public Restaurant saveRestaurant(Restaurant restaurant) {
        RestaurantEntity entity = restaurantEntityMapper.toRestaurantEntity(restaurant);
        return restaurantEntityMapper.toRestaurant(restaurantRepository.save(entity));
    }

    @Override
    public boolean existsRestaurantByNit(String nit){
        return restaurantRepository.existsByNit(nit);
    }

    @Override
    public Optional<Restaurant> findRestaurantById(Long id) {
        return restaurantRepository.findById(id)
                .map(restaurantEntityMapper::toRestaurant);
    }

    @Override
    public List<Restaurant> findAllRestaurants(int pageIndex, int elementsPerPage) {
        Pageable pageable = PageRequest.of(pageIndex, elementsPerPage);
        Page<RestaurantEntity> page = restaurantRepository.findAll(pageable);
        return page.stream()
                .map(restaurantEntityMapper::toRestaurant)
                .toList();
    }
}
