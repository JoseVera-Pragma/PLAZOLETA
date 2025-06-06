package com.plazoleta.plazoleta_microservice.domain.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class RestaurantTest {

    @Test
    void testBuilderAndGetters() {
        Long id = 1L;
        String name = "My Restaurant";
        String nit = "123456789";
        String address = "123 Main St";
        String phone = "555-1234";
        String urlLogo = "http://logo.url/image.png";
        Long ownerId = 10L;

        Restaurant restaurant = Restaurant.builder()
                .id(id)
                .name(name)
                .nit(nit)
                .address(address)
                .phoneNumber(phone)
                .urlLogo(urlLogo)
                .idOwner(ownerId)
                .build();

        assertEquals(id, restaurant.getId());
        assertEquals(name, restaurant.getName());
        assertEquals(nit, restaurant.getNit());
        assertEquals(address, restaurant.getAddress());
        assertEquals(phone, restaurant.getPhoneNumber());
        assertEquals(urlLogo, restaurant.getUrlLogo());
        assertEquals(ownerId, restaurant.getIdOwner());
    }

    @Test
    void testEqualsAndHashCode() {
        Restaurant r1 = Restaurant.builder().id(1L).build();
        Restaurant r2 = Restaurant.builder().id(1L).build();
        Restaurant r3 = Restaurant.builder().id(2L).build();

        assertEquals(r1, r2);
        assertEquals(r1.hashCode(), r2.hashCode());

        assertNotEquals(r1, r3);
        assertNotEquals(r1.hashCode(), r3.hashCode());
    }

    @Test
    void testEqualsWithNullAndDifferentClass() {
        Restaurant restaurant = Restaurant.builder().id(1L).build();

        assertNotEquals(null, restaurant);
        assertNotEquals(restaurant,null);
        assertNotEquals(restaurant, new Object());
        assertNotEquals(new Object(),restaurant);
    }

    @Test
    void testEqualsWithSameObject() {
        Restaurant restaurant = Restaurant.builder().id(1L).build();

        assertEquals(restaurant, restaurant);
    }
}