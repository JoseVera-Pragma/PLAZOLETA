package com.plazoleta.plazoleta_microservice.infrastructure.out.jpa.adapter;


import com.plazoleta.plazoleta_microservice.domain.model.Restaurant;
import com.plazoleta.plazoleta_microservice.infrastructure.out.jpa.mapper.IRestaurantEntityMapper;
import com.plazoleta.plazoleta_microservice.infrastructure.out.jpa.mapper.IRestaurantEntityMapperImpl;
import com.plazoleta.plazoleta_microservice.infrastructure.out.jpa.repository.IRestaurantRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@Import({RestaurantJpaAdapter.class, IRestaurantEntityMapperImpl.class})
class RestaurantJpaAdapterIntegrationTest {

    @Autowired
    private IRestaurantRepository restaurantRepository;

    @Autowired
    private IRestaurantEntityMapper restaurantEntityMapper;

    @Autowired
    private RestaurantJpaAdapter restaurantJpaAdapter;

    @Test
    void testSaveAndExistsByNit() {
        Restaurant restaurant = new Restaurant.Builder()
                .id(null)
                .name("Name")
                .nit("123456789")
                .address("Address")
                .phoneNumber("+123456789")
                .urlLogo("http://logo.url")
                .idOwner(1L)
                .build();

        restaurantJpaAdapter.saveRestaurant(restaurant);

        boolean exists = restaurantJpaAdapter.existsByNit("123456789");
        assertThat(exists).isTrue();
    }
}
