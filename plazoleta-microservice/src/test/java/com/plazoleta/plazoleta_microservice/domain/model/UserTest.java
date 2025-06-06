package com.plazoleta.plazoleta_microservice.domain.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class UserTest {

    @Test
    void testBuilderAndGetters() {
        String firstName = "Jose";
        String lastName = "Vera";
        String identityNumber = "1093854586";
        String phoneNumber = "3001234567";
        String dateOfBirth = "1990-01-01";
        String email = "jose@example.com";
        String password = "securePass123";
        String role = "EMPLOYEE";
        Long restaurantId = 1L;

        User user = User.builder()
                .firstName(firstName)
                .lastName(lastName)
                .identityNumber(identityNumber)
                .phoneNumber(phoneNumber)
                .dateOfBirth(dateOfBirth)
                .email(email)
                .password(password)
                .role(role)
                .restaurantId(restaurantId)
                .build();

        assertEquals(firstName, user.getFirstName());
        assertEquals(lastName, user.getLastName());
        assertEquals(identityNumber, user.getIdentityNumber());
        assertEquals(phoneNumber, user.getPhoneNumber());
        assertEquals(dateOfBirth, user.getDateOfBirth());
        assertEquals(email, user.getEmail());
        assertEquals(password, user.getPassword());
        assertEquals(role, user.getRole());
        assertEquals(restaurantId, user.getRestaurantId());
    }

    @Test
    void testNoArgsConstructor() {
        User user = new User();

        assertNull(user.getFirstName());
        assertNull(user.getLastName());
        assertNull(user.getIdentityNumber());
        assertNull(user.getPhoneNumber());
        assertNull(user.getDateOfBirth());
        assertNull(user.getEmail());
        assertNull(user.getPassword());
        assertNull(user.getRole());
        assertNull(user.getRestaurantId());
    }
}