package com.plazoleta.plazoleta_microservice.domain.model;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(properties = "spring.profiles.active=test")
class UserTest {

    @Test
    void testBuilderCreatesUserCorrectly() {
        User user = new User.Builder()
                .firstName("John")
                .lastName("Doe")
                .identityNumber("123456789")
                .phoneNumber("555-1234")
                .dateOfBirth("1990-01-01")
                .email("john.doe@example.com")
                .password("securePassword123")
                .role("admin")
                .build();

        assertEquals("John", user.getFirstName());
        assertEquals("Doe", user.getLastName());
        assertEquals("123456789", user.getIdentityNumber());
        assertEquals("555-1234", user.getPhoneNumber());
        assertEquals("1990-01-01", user.getDateOfBirth());
        assertEquals("john.doe@example.com", user.getEmail());
        assertEquals("securePassword123", user.getPassword());
        assertEquals("admin", user.getRole());
    }

    @Test
    void testEmptyConstructorAndSetters() {
        User user = new User();

        assertNull(user.getFirstName());
        assertNull(user.getLastName());
        assertNull(user.getIdentityNumber());
        assertNull(user.getPhoneNumber());
        assertNull(user.getDateOfBirth());
        assertNull(user.getEmail());
        assertNull(user.getPassword());
        assertNull(user.getRole());
    }

    @Test
    void testPartialBuilder() {
        User user = new User.Builder()
                .firstName("Alice")
                .email("alice@example.com")
                .build();

        assertEquals("Alice", user.getFirstName());
        assertEquals("alice@example.com", user.getEmail());

        assertNull(user.getLastName());
        assertNull(user.getIdentityNumber());
        assertNull(user.getPhoneNumber());
        assertNull(user.getDateOfBirth());
        assertNull(user.getPassword());
        assertNull(user.getRole());
    }

}