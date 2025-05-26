package com.plazoleta.user_microservice.domain.model;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(properties = "spring.profiles.active=test")
class EmailTest {

    private final String validEmail = "test@email.com";
    private final String invalidEmail = "test@email";

    @Test
    void shouldCreateEmail() {
        Email email = new Email(validEmail);
        assertEquals(validEmail, email.getValue());
    }

    @Test
    void shouldGetNewEmail() {
        Email email = new Email(validEmail);
        String value = email.getValue();
        assertEquals(validEmail, value);
    }

    @Test
    void shoulThrowExceptionWhenEmailIsInvalid() {
        Exception e = assertThrows(IllegalArgumentException.class, () -> {
            new Email(invalidEmail);
        });
        assertEquals("Email not is valid: test@email",e.getMessage());
    }

    @Test
    void shoulThrowExceptionWhenEmailIsBlank() {
        Exception e = assertThrows(IllegalArgumentException.class, () -> {
            new Email("");
        });
        assertEquals("Email not is valid: ",e.getMessage());
    }

    @Test
    void shoulThrowExceptionWhenEmailIsNull() {
        Exception e = assertThrows(IllegalArgumentException.class, () -> {
            new Email(null);
        });
        assertEquals("Email not is valid: null",e.getMessage());
    }

    @Test
    void sameEmailShouldHaveSameHashCode() {
        Email e1 = new Email("test@example.com");
        Email e2 = new Email("test@example.com");

        assertEquals(e1, e2);
        assertEquals(e1.hashCode(), e2.hashCode(), "HashCodes must be equal for equal objects");
    }

    @Test
    void differentEmailsShouldHaveDifferentHashCodes() {
        Email e1 = new Email("first@example.com");
        Email e2 = new Email("second@example.com");

        assertNotEquals(e1, e2);
        assertNotEquals(e1.hashCode(), e2.hashCode(), "Different emails should ideally have different hashCodes");
    }

    @Test
    void hashCodeShouldBeConsistent() {
        Email e = new Email("stable@example.com");
        int hash1 = e.hashCode();
        int hash2 = e.hashCode();
        int hash3 = e.hashCode();

        assertEquals(hash1, hash2);
        assertEquals(hash2, hash3);
    }

    @Test
    void emailShouldNotBeEqualToNull() {
        Email email = new Email("test@example.com");
        assertNotEquals(null, email);
    }

    @Test
    void emailShouldNotBeEqualToDifferentClass() {
        Email email = new Email("test@example.com");
        assertNotEquals("test@example.com", email);
    }

    @Test
    void emailShouldNotBeEqualToObjectOfDifferentClass() {
        Email email = new Email("test@example.com");
        Object other = new Object();

        assertNotEquals(email, other);
    }
}
