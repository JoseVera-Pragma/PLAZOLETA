package com.plazoleta.plazoleta_microservice.domain.validator;

import com.plazoleta.plazoleta_microservice.domain.exception.restaurant.*;
import com.plazoleta.plazoleta_microservice.domain.model.Restaurant;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Constructor;

import static org.junit.jupiter.api.Assertions.*;
class RestaurantValidatorTest {

    private Restaurant buildValidRestaurant() {
        return Restaurant.builder()
                .id(1L)
                .name("Restaurante A")
                .nit("123456789")
                .address("Calle 123")
                .phoneNumber("+573001234567")
                .urlLogo("http://logo.com/logo.png")
                .idOwner(100L)
                .build();
    }

    @Test
    void constructorShouldBePrivateAndInvokableViaReflection() throws Exception {
        Constructor<RestaurantValidator> constructor = RestaurantValidator.class.getDeclaredConstructor();
        assertFalse(constructor.canAccess(null) || constructor.isAccessible(), "Constructor should be private");

        constructor.setAccessible(true);
        RestaurantValidator instance = constructor.newInstance();
        assertNotNull(instance);
    }

    @Test
    void validateRestaurant_validData_shouldPass() {
        assertDoesNotThrow(() -> RestaurantValidator.validateRestaurant(buildValidRestaurant()));
    }

    @Test
    void validateRestaurant_nullName_shouldThrowException() {
        Restaurant restaurant = buildValidRestaurant().builder().name(null).build();
        assertThrows(InvalidRestaurantNameException.class, () -> RestaurantValidator.validateRestaurant(restaurant));
    }

    @Test
    void validateRestaurant_blankName_shouldThrowException() {
        Restaurant restaurant = buildValidRestaurant().builder().name("   ").build();
        assertThrows(InvalidRestaurantNameException.class, () -> RestaurantValidator.validateRestaurant(restaurant));
    }

    @Test
    void validateRestaurant_numericName_shouldThrowException() {
        Restaurant restaurant = buildValidRestaurant().builder().name("123456").build();
        assertThrows(InvalidRestaurantNameException.class, () -> RestaurantValidator.validateRestaurant(restaurant));
    }

    @Test
    void validateRestaurant_nullNit_shouldThrowException() {
        Restaurant restaurant = Restaurant.builder()
                .id(1L)
                .name("Restaurante A")
                .nit(null)
                .address("Calle 123")
                .phoneNumber("+573001234567")
                .urlLogo("http://logo.com/logo.png")
                .idOwner(100L)
                .build();
        assertThrows(MissingNitException.class, () -> RestaurantValidator.validateRestaurant(restaurant));
    }

    @Test
    void validateRestaurant_blankNit_shouldThrowException() {
        Restaurant restaurant = Restaurant.builder()
                .id(1L)
                .name("Restaurante A")
                .nit("   ")
                .address("Calle 123")
                .phoneNumber("+573001234567")
                .urlLogo("http://logo.com/logo.png")
                .idOwner(100L)
                .build();
        assertThrows(MissingNitException.class, () -> RestaurantValidator.validateRestaurant(restaurant));
    }

    @Test
    void validateRestaurant_nonNumericNit_shouldThrowException() {
        Restaurant restaurant = Restaurant.builder()
                .id(1L)
                .name("Restaurante A")
                .nit("ABC123")
                .address("Calle 123")
                .phoneNumber("+573001234567")
                .urlLogo("http://logo.com/logo.png")
                .idOwner(100L)
                .build();
        assertThrows(InvalidRestaurantNitException.class, () -> RestaurantValidator.validateRestaurant(restaurant));
    }

    @Test
    void validateRestaurant_nullAddress_shouldThrowException() {
        Restaurant restaurant = Restaurant.builder()
                .id(1L)
                .name("Restaurante A")
                .nit("123456789")
                .address(null)
                .phoneNumber("+573001234567")
                .urlLogo("http://logo.com/logo.png")
                .idOwner(100L)
                .build();
        assertThrows(InvalidRestaurantAddressException.class, () -> RestaurantValidator.validateRestaurant(restaurant));
    }

    @Test
    void validateRestaurant_blankAddress_shouldThrowException() {
        Restaurant restaurant = Restaurant.builder()
                .id(1L)
                .name("Restaurante A")
                .nit("123456789")
                .address("   ")
                .phoneNumber("+573001234567")
                .urlLogo("http://logo.com/logo.png")
                .idOwner(100L)
                .build();
        assertThrows(InvalidRestaurantAddressException.class, () -> RestaurantValidator.validateRestaurant(restaurant));
    }

    @Test
    void validateRestaurant_nullPhoneNumber_shouldThrowException() {
        Restaurant restaurant = Restaurant.builder()
                .id(1L)
                .name("Restaurante A")
                .nit("123456789")
                .address("Calle 123")
                .phoneNumber(null)
                .urlLogo("http://logo.com/logo.png")
                .idOwner(100L)
                .build();
        assertThrows(MissingPhoneNumberException.class, () -> RestaurantValidator.validateRestaurant(restaurant));
    }

    @Test
    void validateRestaurant_blankPhoneNumber_shouldThrowException() {
        Restaurant restaurant = Restaurant.builder()
                .id(1L)
                .name("Restaurante A")
                .nit("123456789")
                .address("Calle 123")
                .phoneNumber("   ")
                .urlLogo("http://logo.com/logo.png")
                .idOwner(100L)
                .build();
        assertThrows(MissingPhoneNumberException.class, () -> RestaurantValidator.validateRestaurant(restaurant));
    }

    @Test
    void validateRestaurant_invalidPhoneNumber_shouldThrowException() {
        Restaurant restaurant = Restaurant.builder()
                .id(1L)
                .name("Restaurante A")
                .nit("123456789")
                .address("Calle 123")
                .phoneNumber("abc123")
                .urlLogo("http://logo.com/logo.png")
                .idOwner(100L)
                .build();
        assertThrows(InvalidPhoneNumberException.class, () -> RestaurantValidator.validateRestaurant(restaurant));
    }

    @Test
    void validateRestaurant_nullUrlLogo_shouldThrowException() {
        Restaurant restaurant = Restaurant.builder()
                .id(1L)
                .name("Restaurante A")
                .nit("123456789")
                .address("Calle 123")
                .phoneNumber("+573001234567")
                .urlLogo(null)
                .idOwner(100L)
                .build();
        assertThrows(InvalidRestaurantUrlLogoException.class, () -> RestaurantValidator.validateRestaurant(restaurant));
    }

    @Test
    void validateRestaurant_blankUrlLogo_shouldThrowException() {
        Restaurant restaurant = Restaurant.builder()
                .id(1L)
                .name("Restaurante A")
                .nit("123456789")
                .address("Calle 123")
                .phoneNumber("+573001234567")
                .urlLogo("  ")
                .idOwner(100L)
                .build();
        assertThrows(InvalidRestaurantUrlLogoException.class, () -> RestaurantValidator.validateRestaurant(restaurant));
    }

    @Test
    void validateRestaurant_nullIdOwner_shouldThrowException() {
        Restaurant restaurant = Restaurant.builder()
                .id(1L)
                .name("Restaurante A")
                .nit("123456789")
                .address("Calle 123")
                .phoneNumber("+573001234567")
                .urlLogo("http://logo.com/logo.png")
                .idOwner(null)
                .build();
        assertThrows(InvalidRestaurantOwnerIdException.class, () -> RestaurantValidator.validateRestaurant(restaurant));
    }
}