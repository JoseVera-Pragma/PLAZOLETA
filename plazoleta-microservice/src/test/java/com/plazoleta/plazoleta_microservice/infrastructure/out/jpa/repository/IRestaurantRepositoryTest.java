package com.plazoleta.plazoleta_microservice.infrastructure.out.jpa.repository;

import com.plazoleta.plazoleta_microservice.infrastructure.out.jpa.entity.RestaurantEntity;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.TestPropertySource;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@TestPropertySource(locations = "classpath:application-test.properties")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class IRestaurantRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private IRestaurantRepository restaurantRepository;

    @Test
    void shouldReturnTrueIfRestaurantExistsByNit() {
        RestaurantEntity restaurant = new RestaurantEntity();
        restaurant.setName("Restaurante Prueba");
        restaurant.setAddress("Calle 123");
        restaurant.setPhoneNumber("3000000000");
        restaurant.setUrlLogo("http://logo.test");
        restaurant.setNit("1234567890");
        restaurant.setIdOwner(1L);
        entityManager.persistAndFlush(restaurant);

        boolean exists = restaurantRepository.existsByNit("1234567890");

        assertTrue(exists);
    }

    @Test
    void shouldReturnFalseIfRestaurantDoesNotExistByNit() {
        boolean exists = restaurantRepository.existsByNit("0000000000");

        assertFalse(exists);
    }
}
