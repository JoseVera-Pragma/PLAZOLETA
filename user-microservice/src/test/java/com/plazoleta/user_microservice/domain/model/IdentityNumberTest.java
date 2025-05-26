package com.plazoleta.user_microservice.domain.model;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(properties = "spring.profiles.active=test")
class IdentityNumberTest {

    @Test
    void shouldCreateIdentityNumberWithValidInput() {
        IdentityNumber id = new IdentityNumber("123456789");
        assertEquals("123456789", id.getValue());
    }

    @Test
    void shouldThrowExceptionWhenValueIsNull() {
        assertThrows(IllegalArgumentException.class, () -> {
            new IdentityNumber(null);
        });
    }

    @Test
    void shouldThrowExceptionWhenValueIsBlank() {
        assertThrows(IllegalArgumentException.class, () -> {
            new IdentityNumber("   ");
        });
    }

    @Test
    void shouldThrowExceptionWhenValueIsNonNumeric() {
        assertThrows(IllegalArgumentException.class, () -> {
            new IdentityNumber("ABC123");
        });
    }

    @Test
    void shouldThrowExceptionWhenValueContainsSpaces() {
        assertThrows(IllegalArgumentException.class, () -> {
            new IdentityNumber("123 456");
        });
    }

    @Test
    void identityNumbersWithSameValueShouldBeEqual() {
        IdentityNumber id1 = new IdentityNumber("123456");
        IdentityNumber id2 = new IdentityNumber("123456");
        assertEquals(id1, id2);
    }

    @Test
    void identityNumbersWithDifferentValuesShouldNotBeEqual() {
        IdentityNumber id1 = new IdentityNumber("123456");
        IdentityNumber id2 = new IdentityNumber("654321");
        assertNotEquals(id1, id2);
    }

    @Test
    void identityNumberShouldBeEqualToItself() {
        IdentityNumber id = new IdentityNumber("123456");
        assertEquals(id, id);
    }

    @Test
    void identityNumberShouldNotBeEqualToNull() {
        IdentityNumber id = new IdentityNumber("123456");
        assertNotEquals(null, id);
    }

    @Test
    void identityNumberShouldNotBeEqualToDifferentClassObject() {
        IdentityNumber id = new IdentityNumber("123456");
        String other = "123456";
        assertNotEquals(id, other);
    }

    @Test
    void identityNumbersWithSameValueShouldHaveSameHashCode() {
        IdentityNumber id1 = new IdentityNumber("123456");
        IdentityNumber id2 = new IdentityNumber("123456");
        assertEquals(id1.hashCode(), id2.hashCode());
    }

    @Test
    void identityNumbersWithDifferentValuesShouldHaveDifferentHashCodes() {
        IdentityNumber id1 = new IdentityNumber("123456");
        IdentityNumber id2 = new IdentityNumber("654321");
        assertNotEquals(id1.hashCode(), id2.hashCode());
    }
}