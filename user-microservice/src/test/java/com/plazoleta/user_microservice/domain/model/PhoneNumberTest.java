package com.plazoleta.user_microservice.domain.model;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(properties = "spring.profiles.active=test")
class PhoneNumberTest {

    @Test
    void shouldCreatePhoneNumberWithValidNumber() {
        PhoneNumber phone = new PhoneNumber("+573001112233");
        assertEquals("+573001112233", phone.getValue());
    }

    @Test
    void shouldCreatePhoneNumberWithoutPlus() {
        PhoneNumber phone = new PhoneNumber("573001112233");
        assertEquals("573001112233", phone.getValue());
    }

    @Test
    void shouldThrowExceptionWhenPhoneNumberIsNull() {
        assertThrows(IllegalArgumentException.class, () -> new PhoneNumber(null));
    }

    @Test
    void shouldThrowExceptionWhenPhoneNumberIsBlank() {
        assertThrows(IllegalArgumentException.class, () -> new PhoneNumber("   "));
    }

    @Test
    void shouldThrowExceptionWhenPhoneNumberIsTooLong() {
        assertThrows(IllegalArgumentException.class, () -> new PhoneNumber("+12345678901234")); // 14 digits
    }

    @Test
    void shouldThrowExceptionWhenPhoneNumberContainsLetters() {
        assertThrows(IllegalArgumentException.class, () -> new PhoneNumber("+57abc123"));
    }

    @Test
    void shouldThrowExceptionWhenPhoneNumberContainsSymbols() {
        assertThrows(IllegalArgumentException.class, () -> new PhoneNumber("+57-3001112233"));
    }

    @Test
    void phoneNumbersWithSameValueShouldBeEqual() {
        PhoneNumber p1 = new PhoneNumber("+573001112233");
        PhoneNumber p2 = new PhoneNumber("+573001112233");
        assertEquals(p1, p2);
    }

    @Test
    void phoneNumbersWithDifferentValueShouldNotBeEqual() {
        PhoneNumber p1 = new PhoneNumber("+573001112233");
        PhoneNumber p2 = new PhoneNumber("+573001112234");
        assertNotEquals(p1, p2);
    }

    @Test
    void phoneNumberShouldBeEqualToItself() {
        PhoneNumber p = new PhoneNumber("+573001112233");
        assertEquals(p, p);
    }

    @Test
    void phoneNumberShouldNotBeEqualToNull() {
        PhoneNumber p = new PhoneNumber("+573001112233");
        assertNotEquals(null, p);
    }

    @Test
    void phoneNumberShouldNotBeEqualToDifferentClassObject() {
        PhoneNumber p = new PhoneNumber("+573001112233");
        String other = "+573001112233";
        assertNotEquals(p, other);
    }

    @Test
    void phoneNumbersWithSameValueShouldHaveSameHashCode() {
        PhoneNumber p1 = new PhoneNumber("573001112233");
        PhoneNumber p2 = new PhoneNumber("573001112233");
        assertEquals(p1.hashCode(), p2.hashCode());
    }

    @Test
    void phoneNumbersWithDifferentValueShouldHaveDifferentHashCode() {
        PhoneNumber p1 = new PhoneNumber("573001112233");
        PhoneNumber p2 = new PhoneNumber("573001112234");
        assertNotEquals(p1.hashCode(), p2.hashCode());
    }
}
