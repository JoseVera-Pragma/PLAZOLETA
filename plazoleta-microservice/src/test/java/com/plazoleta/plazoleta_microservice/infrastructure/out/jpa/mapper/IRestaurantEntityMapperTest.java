package com.plazoleta.plazoleta_microservice.infrastructure.out.jpa.mapper;

import com.plazoleta.plazoleta_microservice.domain.model.Restaurant;
import com.plazoleta.plazoleta_microservice.infrastructure.out.jpa.entity.RestaurantEntity;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class IRestaurantEntityMapperTest {

    private final IRestaurantEntityMapper mapper = Mappers.getMapper(IRestaurantEntityMapper.class);

    @Test
    void toRestaurantEntity_ShouldMapAllFields() {
        Restaurant restaurant = new Restaurant.Builder()
                .id(1L)
                .name("My Restaurant")
                .nit("123456789")
                .address("123 Main St")
                .phoneNumber("5551234")
                .urlLogo("logo-url")
                .idOwner(42L)
                .build();

        RestaurantEntity entity = mapper.toRestaurantEntity(restaurant);

        assertNotNull(entity);
        assertEquals(restaurant.getId(), entity.getId());
        assertEquals(restaurant.getName(), entity.getName());
        assertEquals(restaurant.getNit(), entity.getNit());
        assertEquals(restaurant.getAddress(), entity.getAddress());
        assertEquals(restaurant.getPhoneNumber(), entity.getPhoneNumber());
        assertEquals(restaurant.getUrlLogo(), entity.getUrlLogo());
        assertEquals(restaurant.getIdOwner(), entity.getIdOwner());
    }

    @Test
    void toRestaurant_ShouldMapAllFields() {
        RestaurantEntity entity = new RestaurantEntity();
        entity.setId(1L);
        entity.setName("My Restaurant");
        entity.setNit("123456789");
        entity.setAddress("123 Main St");
        entity.setPhoneNumber("5551234");
        entity.setUrlLogo("logo-url");
        entity.setIdOwner(42L);

        Restaurant restaurant = mapper.toRestaurant(entity);

        assertNotNull(restaurant);
        assertEquals(entity.getId(), restaurant.getId());
        assertEquals(entity.getName(), restaurant.getName());
        assertEquals(entity.getNit(), restaurant.getNit());
        assertEquals(entity.getAddress(), restaurant.getAddress());
        assertEquals(entity.getPhoneNumber(), restaurant.getPhoneNumber());
        assertEquals(entity.getUrlLogo(), restaurant.getUrlLogo());
        assertEquals(entity.getIdOwner(), restaurant.getIdOwner());
    }

    @Test
    void toRestaurant_ShouldReturnNullWhenEntityIsNull() {
        Restaurant restaurant = mapper.toRestaurant(null);
        assertNull(restaurant);
    }
}