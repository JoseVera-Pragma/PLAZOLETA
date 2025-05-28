package com.plazoleta.plazoleta_microservice.domain.model;

import com.plazoleta.plazoleta_microservice.domain.exception.restaurant.InvalidNitException;
import com.plazoleta.plazoleta_microservice.domain.exception.restaurant.InvalidRestaurantNameException;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(properties = "spring.profiles.active=test")
class RestaurantNameTest {
    @Test
    void shouldCreateValidRestaurantName() {
        String name = "Burger Palace";
        RestaurantName restaurantName = new RestaurantName(name);
        assertEquals(name, restaurantName.getValue());
    }

    @Test
    void shouldThrowWhenNameIsNull() {
        InvalidRestaurantNameException ex = assertThrows(
                InvalidRestaurantNameException.class,
                () -> new RestaurantName(null)
        );
        assertEquals("Name must not be null or empty", ex.getMessage());
    }

    @Test
    void shouldThrowWhenNameIsBlank() {
        InvalidRestaurantNameException ex = assertThrows(
                InvalidRestaurantNameException.class,
                () -> new RestaurantName("  ")
        );
        assertEquals("Name must not be null or empty", ex.getMessage());
    }

    @Test
    void shouldThrowWhenNameIsOnlyDigits() {
        InvalidNitException ex = assertThrows(
                InvalidNitException.class,
                () -> new RestaurantName("123456")
        );
        assertEquals("Name must not contain only numbers", ex.getMessage());
    }

    @Test
    void shouldConsiderEqualNamesAsEqual() {
        RestaurantName name1 = new RestaurantName("Sushi Bar");
        RestaurantName name2 = new RestaurantName("Sushi Bar");
        assertEquals(name1, name2);
        assertEquals(name1.hashCode(), name2.hashCode());
    }

    @Test
    void shouldNotBeEqualIfDifferentNames() {
        RestaurantName name1 = new RestaurantName("Taco Town");
        RestaurantName name2 = new RestaurantName("Pizza Planet");
        assertNotEquals(name1, name2);
    }

    @Test
    void shouldNotBeEqualToNull() {
        RestaurantName name = new RestaurantName("Pasta House");
        assertNotEquals(name, null);
    }

    @Test
    void shouldNotBeEqualToDifferentClass() {
        RestaurantName name = new RestaurantName("BBQ Grill");
        String other = "BBQ Grill";
        assertNotEquals(name, other);
    }

    @Test
    void shouldBeEqualToItself() {
        RestaurantName name = new RestaurantName("Waffle World");
        assertEquals(name, name);
    }
}