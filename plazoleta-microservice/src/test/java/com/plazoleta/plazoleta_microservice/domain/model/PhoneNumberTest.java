package com.plazoleta.plazoleta_microservice.domain.model;

import com.plazoleta.plazoleta_microservice.domain.exception.restaurant.InvalidPhoneNumberException;
import com.plazoleta.plazoleta_microservice.domain.exception.restaurant.MissingPhoneNumberException;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(properties = "spring.profiles.active=test")
class PhoneNumberTest {

    @Test
    void shouldCreateValidPhoneNumber() {
        PhoneNumber number = new PhoneNumber("+573001234567");
        assertEquals("+573001234567", number.getValue());

        PhoneNumber numberWithoutPlus = new PhoneNumber("573001234567");
        assertEquals("573001234567", numberWithoutPlus.getValue());
    }

    @Test
    void shouldThrowWhenPhoneNumberIsNull() {
        assertThrows(
                MissingPhoneNumberException.class,
                () -> new PhoneNumber(null)
        );
    }

    @Test
    void shouldThrowWhenPhoneNumberIsBLank() {
        assertThrows(MissingPhoneNumberException.class, () -> new PhoneNumber("  "));
    }

    @Test
    void shouldThrowWhenPhoneNumberContainsLetters() {
        assertThrows(InvalidPhoneNumberException.class, () -> new PhoneNumber("+575451asfds"));
    }

    @Test
    void shouldThrowWhenPhoneNumberIsTooLong() {
        assertThrows(InvalidPhoneNumberException.class, () -> new PhoneNumber("+352574455152351"));
    }

    @Test
    void shouldConsiderEqualsPhoneNumberEqual() {
        PhoneNumber n1 = new PhoneNumber("3101234567");
        PhoneNumber n2 = new PhoneNumber("3101234567");
        assertEquals(n1, n2);
        assertEquals(n1.hashCode(), n2.hashCode());
    }

    @Test
    void shouldNotBeEqualTonNull() {
        PhoneNumber n1 = new PhoneNumber("3101234567");
        assertNotEquals(n1, null);
    }

    @Test
    void shouldNotBeEqualToDifferentClass() {
        PhoneNumber n1 = new PhoneNumber("3101234567");
        String other = "3101234567";
        assertNotEquals(n1, other);
    }

    @Test
    void shouldNotBeEqualToItself() {
        PhoneNumber n1 = new PhoneNumber("3101234567");
        assertEquals(n1, n1);
    }

    @Test
    void shouldNotBeEqualIfDifferrentNumber() {
        PhoneNumber n1 = new PhoneNumber("3101234567");
        PhoneNumber n2 = new PhoneNumber("31012351515");
        assertNotEquals(n1, n2);
    }

}