package com.plazoleta.plazoleta_microservice.domain.model;

import com.plazoleta.plazoleta_microservice.domain.exception.restaurant.InvalidNitException;
import com.plazoleta.plazoleta_microservice.domain.exception.restaurant.MissingNitException;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(properties = "spring.profiles.active=test")
class RestaurantNitTest {
    @Test
    void shouldCreateValidNit() {
        String nitValue = "123456789";
        RestaurantNit nit = new RestaurantNit(nitValue);
        assertEquals(nitValue, nit.getValue());
    }

    @Test
    void shouldThrowExceptionWhenNitIsNull() {
        MissingNitException ex = assertThrows(
                MissingNitException.class,
                () -> new RestaurantNit(null)
        );
        assertEquals("Nit must be provided", ex.getMessage());
    }

    @Test
    void shouldThrowExceptionWhenNitIsBlank() {
        MissingNitException ex = assertThrows(
                MissingNitException.class,
                () -> new RestaurantNit(" ")
        );
        assertEquals("Nit must be provided", ex.getMessage());
    }

    @Test
    void shouldThrowExceptionWhenNitIsNonNumeric() {
        InvalidNitException ex = assertThrows(
                InvalidNitException.class,
                () -> new RestaurantNit("ABC123")
        );
        assertEquals("Nit is not valid: ABC123", ex.getMessage());
    }

    @Test
    void shouldConsiderTwoSameValueNitsEqual() {
        RestaurantNit nit1 = new RestaurantNit("987654321");
        RestaurantNit nit2 = new RestaurantNit("987654321");
        assertEquals(nit1, nit2);
        assertEquals(nit1.hashCode(), nit2.hashCode());
    }

    @Test
    void shouldConsiderDifferentValueNitsNotEqual() {
        RestaurantNit nit1 = new RestaurantNit("111");
        RestaurantNit nit2 = new RestaurantNit("222");
        assertNotEquals(nit1, nit2);
    }

    @Test
    void shouldNotEqualWithDifferentClass() {
        RestaurantNit nit = new RestaurantNit("12345");
        String other = "12345";
        assertNotEquals(nit, other);
    }

    @Test
    void shouldBeEqualToItself() {
        RestaurantNit nit = new RestaurantNit("123456");
        assertEquals(nit, nit);
    }

    @Test
    void shouldNotEqualNull() {
        RestaurantNit nit = new RestaurantNit("123456");
        assertNotEquals(nit, null);
    }

}