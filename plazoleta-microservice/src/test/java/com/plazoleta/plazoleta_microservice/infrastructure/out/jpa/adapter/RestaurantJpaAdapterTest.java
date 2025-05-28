package com.plazoleta.plazoleta_microservice.infrastructure.out.jpa.adapter;

import com.plazoleta.plazoleta_microservice.domain.model.Restaurant;
import com.plazoleta.plazoleta_microservice.infrastructure.out.jpa.entity.RestaurantEntity;
import com.plazoleta.plazoleta_microservice.infrastructure.out.jpa.mapper.IRestaurantEntityMapper;
import com.plazoleta.plazoleta_microservice.infrastructure.out.jpa.repository.IRestaurantRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)

class RestaurantJpaAdapterTest {
    private IRestaurantRepository restaurantRepository;
    private IRestaurantEntityMapper restaurantEntityMapper;
    private RestaurantJpaAdapter adapter;

    @BeforeEach
    void setUp() {
        restaurantRepository = mock(IRestaurantRepository.class);
        restaurantEntityMapper = mock(IRestaurantEntityMapper.class);
        adapter = new RestaurantJpaAdapter(restaurantRepository, restaurantEntityMapper);
    }

    @Test
    void testSaveRestaurant() {
        Restaurant restaurant = new Restaurant.Builder()
                .id(1L)
                .name("Name")
                .nit("123456")
                .address("Address")
                .phoneNumber("+123456789")
                .urlLogo("http://logo.url")
                .idOwner(1L)
                .build();
        RestaurantEntity restaurantEntity = new RestaurantEntity();

        when(restaurantEntityMapper.toRestaurantEntity(restaurant)).thenReturn(restaurantEntity);

        adapter.saveRestaurant(restaurant);

        verify(restaurantEntityMapper).toRestaurantEntity(restaurant);
        verify(restaurantRepository).save(restaurantEntity);
    }

    @Test
    void testExistsByNit() {
        String nit = "123456";
        when(restaurantRepository.existsByNit(nit)).thenReturn(true);

        boolean exists = adapter.existsByNit(nit);

        verify(restaurantRepository).existsByNit(nit);
        assert(exists);
    }

}